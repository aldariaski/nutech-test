package com.nutech.nutech.service;


import com.nutech.nutech.entity.*;
import com.nutech.nutech.repository.*;
//import com.nutech.nutech.config.*;
//import com.nutech.nutech.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import  org.springframework.web.multipart.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String uploadDir = "uploads/";

    @Value("${jwt.secret}")
    private String secretKey; // Inject the key from configuration

    public User registerUser(User user) {
    //public User registerUser(String email, String first_name, String last_name, String password) {    
        /*User user = new User();
        user.setEmail(email);
        user.setFirstName(first_name);
        user.setLastName(last_name);
        user.setPassword(passwordEncoder.encode(password));*/
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public User getProfile() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userRepository.findByEmail(email);
    }

    public User updateProfile(User updatedUser) {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email);
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        return userRepository.save(user);
    }

    public User updateProfileImage(MultipartFile file) throws IOException {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email);

        if (!file.isEmpty()) {
            String contentType = file.getContentType();
            if (contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path path = Paths.get(uploadDir + fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());
                user.setProfileImage("https://yoururlapi.com/" + fileName);
                return userRepository.save(user);
            } else {
                throw new IllegalArgumentException("Format Image tidak sesuai");
            }
        } else {
            throw new IllegalArgumentException("File tidak boleh kosong");
        }
    }
}