package com.gussbom.auth.security;

import com.gussbom.auth.entities.Token;
import com.gussbom.auth.exceptions.BadRequestException;
import com.gussbom.auth.repositories.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    private final long tokenExpiration = 60000;
    private final long refreshExpiration = 604800000;
    private final TokenRepository tokenRepository;

    @Value("${jwt.secret}")
    private String secret;

    private Claims getAllClaimsFromToken(String token){

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            Token newToken = tokenRepository.findByToken(token)
                    .orElseThrow(()-> new BadRequestException("Token Not Found"));
            newToken.setExpired(true);
            tokenRepository.save(newToken);
            throw new BadRequestException("Session expired. Log in again");
        }
//         boolean result = claims.isEmpty();
    }
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver){
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }
    public String generateToken(Map<String, Object> claims, UserDetails userDetails){
        claims.put("role", userDetails.getAuthorities());
        return buildToken(claims, userDetails, tokenExpiration);
    }
    public String generateRefreshToken(UserDetails userDetails){
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }
    public String buildToken(Map<String, Object> claims, UserDetails userDetails, Long expiration){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    public String getUsernameFromToken(String token){
        return getClaimFromToken(token, Claims::getSubject);
    }
    public Date getExpirationFromToken(String token){
        return getClaimFromToken(token, Claims::getExpiration);
    }
    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
//    public String generateRefreshToken(UserDetails userDetails){
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("role", userDetails.getAuthorities());
//        return doGenerateRefreshToken(claims, userDetails.getUsername());
//    }


    private Key getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private Boolean isTokenExpired(String token){
        final Date expiration = getExpirationFromToken(token);
        return expiration.before(new Date());
    }

    private String getString(Map<String, Object> claims, String subject) {
        Header header = Jwts.header();
        header.setType("JWT");
        return Jwts.builder()
                .setHeader((Map<String, Object>) header)
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("rbacspring")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration * 2 * 100))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }
}
