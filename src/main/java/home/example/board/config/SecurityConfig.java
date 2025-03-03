package home.example.board.config;

import home.example.board.handler.CustomAuthenticationSuccessHandler;
import home.example.board.handler.CustomLogoutSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.servlet.http.HttpServletRequest;

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
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity
    ) throws Exception{
        httpSecurity
            // CSRF 보호 설정 (필요에 따라 disable 할 수 있음)
            .csrf(csrf -> csrf.disable())
            // URL별 접근 제어 설정
            .authorizeHttpRequests(auth -> auth
                    // 게시판 목록이나 상세 페이지는 누구나 접근 가능
                    .antMatchers("/","/board/**", "/post/**","/addUser","/api/**").permitAll()
                    // 그 외의 URL은 인증 필요
                    .anyRequest().authenticated()
            )
            // 폼 로그인 설정
            .formLogin(form -> form
                    // 커스텀 로그인 페이지 URL 지정 (기본 제공 페이지도 사용 가능)
                    //.loginPage("/auth/login")
                    .loginProcessingUrl("/auth/login")
                    .usernameParameter("user_name")
                    .passwordParameter("user_pw")
                    // 로그인 성공 시 이동할 URL
                    //.defaultSuccessUrl("/", true)
                    // 로그인 실패 시 URL
                    .failureUrl("/error") //login?error=true
                    .successHandler(new CustomAuthenticationSuccessHandler())
                    // 모든 사용자에게 로그인 페이지 접근 허용
                    .permitAll()
            )
            // 로그아웃 설정
            .logout(logout -> logout
                    .logoutUrl("/auth/logout")
                    .logoutSuccessUrl("/") ///login?logout
                    .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                    .permitAll()
            );
        return httpSecurity.build();
    }

}
