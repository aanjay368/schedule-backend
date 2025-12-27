package id.my.schedule.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.schedule.model.user.UserResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRETKEY = "eqwewqmwksmaskdknkansdalwkeqiwenqwnekqnkqlwkneqlwdasdaliewqwieqkneqlwkneksdas";
    private final long EXPIRATION = Duration.ofDays(1).toMillis();
    private final Key key = Keys.hmacShaKeyFor(SECRETKEY.getBytes());

    @Autowired
    private ObjectMapper objectMapper;

    public String generateToken(UserResponse user){
        try {

            return Jwts.builder()
                    .setSubject(objectMapper.writeValueAsString(user))
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Terjadi kesalahan pada server");
        }
    }

    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public UserResponse extractUser(String token) {
        try {
            return objectMapper.readValue(getClaims(token).getSubject(), new TypeReference<UserResponse>() {
            });
        }catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Terjadi kesalahan pada server");
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


}
