package home.example.board.utils;

import home.example.board.DTO.BotUserDTO;
import home.example.board.DTO.CustomUserDetail;
import home.example.board.dao.UserDAO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private final long validityInMilliseconds = 5 * 60 * 1000; // 5분

    private final UserDAO userDAO;

    @Autowired
    public JwtTokenProvider(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public String generateToken(BotUserDTO user) {
        Claims claims = Jwts.claims().setSubject(user.getUser_name());
        claims.put("user_seq", user.getUser_seq());
        claims.put("is_bot", user.getIs_bot());

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // validate the token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // get the user_seq from the token
    public UserDetails getUserDetails(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        Long user_seq = claims.get("user_seq", Long.class);
        UserDetails userDetails = new CustomUserDetail(userDAO.getUserBySeq(user_seq));
        return userDetails;
    }
}
