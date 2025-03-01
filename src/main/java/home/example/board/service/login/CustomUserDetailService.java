package home.example.board.service.login;

import home.example.board.DTO.CustomUserDetail;
import home.example.board.domain.User;
import home.example.board.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailService  implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String user_name) throws UsernameNotFoundException {
        User user = userMapper.getUser(user_name);
        if(user == null){
            throw new UsernameNotFoundException("user_"+user_name+" not found");
        }
        return new CustomUserDetail(user);
    }
}
