package com.nutech.nutech.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Value("${jwt.secret}")
    private String secretKey; // Inject the key from configuration

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public String trySecretKey() {
        // Generate a secure key for HS512
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512); //coba HS512
        String secretString = Encoders.BASE64.encode(key.getEncoded()); // Key diencode ke Base64 string
        return secretString;
    }

    
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            User creds = new ObjectMapper().readValue(req.getInputStream(), User.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
    }
    
    
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException, ServletException {
        //SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        //String secretString = Encoders.BASE64.encode(key.getEncoded()); // Encode the key to a Base64 string   
        User user = (User) auth.getPrincipal(); 

        /*String token = Jwts.builder()
                .setSubject(((User) auth.getPrincipal()).getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + 12 * 60 * 60 * 1000)) // 12 hours
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
        res.addHeader("Authorization", "Bearer " + token);*/

        String token = Jwts.builder()
            .setSubject(user.getUsername())
            .setExpiration(new Date(System.currentTimeMillis() + 12 * 60 * 60 * 1000)) // 12 hours
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();
        res.addHeader("Authorization", "Bearer " + token);

        System.out.println("Hi this is succesful authentication " + token);
    }
}
