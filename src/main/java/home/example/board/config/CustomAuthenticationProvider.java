package home.example.board.config;

import home.example.board.DTO.CustomUserDetail;
import home.example.board.service.login.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private CustomUserDetailService userDetailService;

    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;

    public CustomAuthenticationProvider(
            PasswordEncoder passwordEncoder, SessionRegistry sessionRegistry) {
        this.passwordEncoder = passwordEncoder;
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();


        // 사용자 검증
        UserDetails userDetails = userDetailService.loadUserByUsername(username);
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // 계정이 삭제된 경우
        if (!userDetails.isEnabled()) {
            throw new BadCredentialsException("계정이 삭제되었습니다.");
        }

        // 유효한 계정 여부 확인
        if (!userDetails.isAccountNonExpired()) {
            throw new BadCredentialsException("계정이 만료되었습니다.");
        }
        if (!userDetails.isAccountNonLocked()) {
            throw new BadCredentialsException("계정이 잠겨있습니다.");
        }
        if (!userDetails.isCredentialsNonExpired()) {
            throw new BadCredentialsException("비밀번호가 만료되었습니다.");
        }


        // 동시 로그인 체크
        List<Object> principals = sessionRegistry.getAllPrincipals();
        for (Object principal : principals) {
            if (principal instanceof CustomUserDetail) {
                CustomUserDetail checkUserDetails = (CustomUserDetail) principal;
                if (checkUserDetails.getUsername().equals(username)) {
                    List<SessionInformation> sessions =
                            sessionRegistry.getAllSessions(principal, false);
                    for (SessionInformation session : sessions) {
                        session.expireNow();
                    }
                }
            }
        }

        // 인증 완료된 토큰 생성
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                password,
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
