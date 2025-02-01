package com.nutech.nutech.controller;

import com.nutech.nutech.entity.*;
import com.nutech.nutech.service.*;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import  org.springframework.web.multipart.*;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 102);
            response.put("message", "Parameter email tidak sesuai format");
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }

        if (user.getPassword() == null || user.getPassword().length() < 8) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 103);
            response.put("message", "Password harus minimal 8 karakter");
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }

        userService.registerUser(user);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 0);
        response.put("message", "Registrasi berhasil silahkan login");
        response.put("data", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 102);
            response.put("message", "Parameter email tidak sesuai format");
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }

        User user = userService.authenticateUser(email, password);
        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 103);
            response.put("message", "Username atau password salah");
            response.put("data", null);
            return ResponseEntity.status(401).body(response);
        }

        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 12 * 60 * 60 * 1000)) // 12 jam
                .signWith(SignatureAlgorithm.HS512, "secret")
                .compact();

        Map<String, Object> response = new HashMap<>();
        response.put("status", 0);
        response.put("message", "Login Sukses");
        response.put("data", Map.of("token", token));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        User user = userService.getProfile();
        Map<String, Object> response = new HashMap<>();
        response.put("status", 0);
        response.put("message", "Sukses");
        response.put("data", Map.of(
                "email", user.getEmail(),
                "first_name", user.getFirstName(),
                "last_name", user.getLastName(),
                "profile_image", user.getProfileImage()
        ));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile/update")
    public ResponseEntity<?> updateProfile(@RequestBody User updatedUser) {
        User user = userService.updateProfile(updatedUser);
        Map<String, Object> response = new HashMap<>();
        response.put("status", 0);
        response.put("message", "Update Profile berhasil");
        response.put("data", Map.of(
                "email", user.getEmail(),
                "first_name", user.getFirstName(),
                "last_name", user.getLastName(),
                "profile_image", user.getProfileImage()
        ));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile/image")
    public ResponseEntity<?> updateProfileImage(@RequestParam("file") MultipartFile file) {
        try {
            User user = userService.updateProfileImage(file);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 0);
            response.put("message", "Update Profile Image berhasil");
            response.put("data", Map.of(
                    "email", user.getEmail(),
                    "first_name", user.getFirstName(),
                    "last_name", user.getLastName(),
                    "profile_image", user.getProfileImage()
            ));
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 102);
            response.put("message", "Format Image tidak sesuai");
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 102);
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
    }
}