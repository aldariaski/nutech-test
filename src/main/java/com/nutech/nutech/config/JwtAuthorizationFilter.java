package com.nutech.nutech.config;

import com.nutech.nutech.*;
import com.nutech.nutech.controller.*;
import com.nutech.nutech.entity.*;
import com.nutech.nutech.repository.*;
import com.nutech.nutech.service.*;
import com.nutech.nutech.service.CustomUserDetailsService;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    @Value("${jwt.secret}")
    private String secretKey; // Inject the key from configuration

    public JwtAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        /*String token = request.getHeader("Authorization");
        System.out.println("Hi this is UsernamePasswordAuthenticationToken ");
        if (token != null) {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody();

            String user = claims.getSubject();

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }
        return null;*/

        String token = request.getHeader("Authorization");
        if (token != null) {
            Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String email = claims.getSubject();

        if (email != null) {
            // Load the user from the database
            //UserDetails userDetails = CustomUserDetailsService.loadUserByUsername(email);
            //return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            return new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
            
        }
        return null;
    }
    return null;

    }
}