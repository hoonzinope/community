package home.example.board.DTO;

import home.example.board.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetail implements UserDetails {

    private final User user;

    public CustomUserDetail(User user) {
        this.user = user;
    }

    public boolean getTempPassword() { return this.user.getForce_password_change() == 1;}

    public String getUserNickName() {
        return this.user.getUser_nickname();
    }

    public Long getUserSeq() {
        return this.user.getUser_seq();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_"+user.getRole());
        return Collections.singletonList(authority);
    }

    public boolean isAdmin() {
        return this.user.getRole().equals("ADMIN");
    }

    @Override
    public String getPassword() {
        return this.user.getUser_pw();
    }

    @Override
    public String getUsername() {
        return this.user.getUser_name();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getDelete_flag() == 0;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getDelete_flag() == 0;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.getDelete_flag() == 0;
    }

    @Override
    public boolean isEnabled() {
        return user.getDelete_flag() == 0;
    }
}
