package kr.co.kurtcobain.security;

import io.jsonwebtoken.*;
import kr.co.kurtcobain.config.AuthConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 * json 웹 토큰 생성 및 확인
 * 2020-06-17 PJS
 */
@Service
public class TokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    private AuthConfig authConfig;

    public TokenProvider(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    public Map<String, String> createToken(Authentication authentication) {
        Map<String, String> result = new HashMap<>();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // 만료기간 : 현재시간 + 10일
        Date now = new Date();
        long expires = now.getTime() + authConfig.getAuth().getTokenExpirationMsec();
//        Date expiryDate = new Date(now.getTime() + authConfig.getAuth().getTokenExpirationMsec());
        Date expiryDate = new Date(expires);
        String accessToken = Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, authConfig.getAuth().getTokenSecret())
                .compact();
        result.put("accessToken", accessToken);
        result.put("expires", String.valueOf(expires));
        return result;
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(authConfig.getAuth().getTokenSecret())
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(authConfig.getAuth().getTokenSecret()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }
}
