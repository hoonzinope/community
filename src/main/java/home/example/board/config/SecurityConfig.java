package home.example.board.config;

import home.example.board.handler.CustomAuthenticationFailureHandler;
import home.example.board.handler.CustomAuthenticationSuccessHandler;
import home.example.board.handler.CustomLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.SessionManagementFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().antMatchers("/css/**","/js/**","/images/**"));
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        ConcurrentSessionControlAuthenticationStrategy sessionStrategy =
                new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
        sessionStrategy.setMaximumSessions(1);
        sessionStrategy.setExceptionIfMaximumExceeded(false);
        return sessionStrategy;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            CustomAuthenticationProvider customAuthenticationProvider,
            CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) throws Exception{
        httpSecurity
            // CSRF 보호 설정 (필요에 따라 disable 할 수 있음)
            .csrf(csrf -> csrf.disable())
            // URL별 접근 제어 설정
            .authorizeHttpRequests(auth -> auth
                    // 게시판 목록이나 상세 페이지는 누구나 접근 가능
                    .antMatchers("/","/login","/signup","/board/**", "/post/**","/auth/signup","/api/**","/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                    // 그 외의 URL은 인증 필요
                    .anyRequest().authenticated()
            )
            // 폼 로그인 설정
            .formLogin(form -> form
                    // 커스텀 로그인 페이지 URL 지정 (기본 제공 페이지도 사용 가능)
                    .loginPage("/login")
                    .loginProcessingUrl("/auth/login")
                    .usernameParameter("user_name")
                    .passwordParameter("user_pw")
                    // 로그인 성공 시 이동할 URL
                    //.defaultSuccessUrl("/", true)
                    // 로그인 실패 시 URL
                    // .failureUrl("/error") //login?error=true
                    .successHandler(customAuthenticationSuccessHandler)
                    .failureHandler(new CustomAuthenticationFailureHandler())
                    // 모든 사용자에게 로그인 페이지 접근 허용
                    .permitAll()
            )
            // 로그아웃 설정
            .logout(logout -> logout
                    .logoutUrl("/auth/logout")
                    .logoutSuccessUrl("/") ///login?logout
                    .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                    .deleteCookies("JSESSIONID") // 세션 쿠키 삭제
                    .invalidateHttpSession(true)  // 세션 무효화
                    .permitAll()
            )
                .authenticationProvider(customAuthenticationProvider)  // 추가
            // 세션 관리 및 동시 로그인 제한 설정
             .sessionManagement(session -> session
                     .invalidSessionUrl("/")  // 유효하지 않은 세션 리다이렉트
                     .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 필요한 경우에만 세션 생성
                     .maximumSessions(1) // 최대 세션 수
                     .maxSessionsPreventsLogin(false) // 기존 세션 만료
                     .expiredUrl("/login?error=true&message=expireLogin") // expiredSessionStrategy 대신 expiredUrl 사용
                     .sessionRegistry(sessionRegistry())
             )
            .headers(headers -> headers
                    .frameOptions().sameOrigin()
                    .cacheControl().disable()  // 캐시 비활성화
            )
            .addFilterBefore(new ConcurrentSessionFilter(sessionRegistry(), event -> {
                HttpServletResponse response = event.getResponse();
                response.sendRedirect("/login?expired=true&message=expiredLogin");
            }), SessionManagementFilter.class);

        return httpSecurity.build();
    }

}
