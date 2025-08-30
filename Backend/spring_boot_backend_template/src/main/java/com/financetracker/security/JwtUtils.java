package com.financetracker.security;

import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret.key}")
    private String jwtSecret;

    @Value("${jwt.exp.time}")
    private int jwtExpirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateJwtToken(Authentication authentication) {
        String username = authentication.getName();
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(username)
                .claim("authorities", authorities)
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    public Claims validateJwtToken(String token) {
        return Jwts.parser()
        	    .verifyWith(key)
        	    .build()
        	    .parseSignedClaims(token)
        	    .getPayload();
        //alternate patterns
        /*
         *  return Jwts.parserBuilder()
                .setSigningKey(key) // must match signWith(key)
                .build()
                .parseClaimsJws(token)
                .getBody(); // verified, returns claims
                
         * */

        /*
         * return Jwts.parser()
	    	.setSigningKey(secretBytes)
	    	.parseClaimsJws(token)
	    	.getBody();

         * */
    }

    @SuppressWarnings("unchecked")
    public java.util.List<GrantedAuthority> getAuthoritiesFromClaims(Claims claims) {
        java.util.List<String> auths = (java.util.List<String>) claims.get("authorities");
        return auths.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public Authentication buildAuthenticationFromToken(String jwt) {
        Claims claims = validateJwtToken(jwt);
        String username = claims.getSubject();
        java.util.List<GrantedAuthority> authorities = getAuthoritiesFromClaims(claims);
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
}
