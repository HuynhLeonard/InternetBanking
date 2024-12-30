package com.wnc.banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class KeyConfig {
    private String removePemHeaders(String key) {
        return key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", ""); // Remove all whitespace
    }


    @Bean
    public PublicKey publicKey() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("publicB.pem")) {
            if (is == null) {
                throw new RuntimeException("Public key file not found in resources");
            }

            // Read the file content
            byte[] encodedKey = is.readAllBytes();
            String key = removePemHeaders(new String(encodedKey));
            byte[] decodedKey = Base64.getDecoder().decode(key);

            // Generate PublicKey instance
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
        }
    }

    @Bean
    public PrivateKey privateKey() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("privateA.pem")) {
            if (is == null) {
                throw new RuntimeException("Private key file not found in resources");
            }

            // Read the file content
            byte[] encodedKey = is.readAllBytes();
            String key = removePemHeaders(new String(encodedKey));
            byte[] decodedKey = Base64.getDecoder().decode(key);

            // Generate PrivateKey instance
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodedKey));
        }
    }
}
