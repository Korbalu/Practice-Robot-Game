package com.robotgame.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTProcessor {

    private static final String SECRET_KEY = "7b8bf18c2561f17ff7274299ab90be987103048f90c87cd0f327ab5e387b44ba1938fe678b216a8757e31c5e4445437d078fee1ce21e210b4ef9ff98688dd192";

    public String getUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction){
        Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }
    private Claims extractAllClaims (String token){
        return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
    }
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }
    public String generateToken(Map<String, Object> claims, UserDetails userDetails){
        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis()+1000*60*60))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
    }
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public boolean isTokenValid(String token, UserDetails userDetails){
        String username = getUserName(token);
        return (username.equals((userDetails.getUsername())) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date date = extractClaim(token, Claims::getExpiration);
        return date.before(new Date());
    }
}
