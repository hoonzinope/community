package home.example.board.service.bot;

import home.example.board.DTO.BotUserDTO;
import home.example.board.DTO.botApiDTO.BotLoginRequestDTO;
import home.example.board.dao.UserDAO;
import home.example.board.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BotLoginService {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public BotLoginService(UserDAO userDAO, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String generateLoginToken(BotLoginRequestDTO botLoginRequestDTO) throws IllegalAccessException {
        // Generate a login token for the user
        String user_name = botLoginRequestDTO.getUser_name();
        String user_pw = botLoginRequestDTO.getUser_pw();
        // check if the user is a bot
        BotUserDTO botUser = userDAO.getBotUserByName(user_name);
        if(botUser == null || botUser.getIs_bot().equalsIgnoreCase("N")) {
            throw new IllegalAccessException("User is not a bot");
        }
        // check if the password is correct
        if(!passwordEncoder.matches(user_pw, botUser.getUser_pw())) {
            throw new IllegalAccessException("Invalid password");
        }
        // Generate a JWT token
        String token = jwtTokenProvider.generateToken(botUser);
        return token;
    }

}
