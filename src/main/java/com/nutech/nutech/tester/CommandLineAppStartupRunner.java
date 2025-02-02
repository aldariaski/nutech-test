package com.nutech.nutech.tester;

import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import javax.crypto.SecretKey;

import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import io.jsonwebtoken.SignatureAlgorithm;



import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;


@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    @Override
    public void run(String...args) throws Exception {
               
        // Generate a secure key for HS512
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String secretString = Encoders.BASE64.encode(key.getEncoded()); // Encode the key to a Base64 string
        System.out.println("Just key " + key.toString());
        System.out.println("Random generated Secret Key: " + secretString);

    }
}
