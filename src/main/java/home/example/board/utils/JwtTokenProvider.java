package home.example.board.utils;

import home.example.board.DTO.BotUserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private final long validityInMilliseconds = 5 * 60 * 1000; // 5분

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
}
