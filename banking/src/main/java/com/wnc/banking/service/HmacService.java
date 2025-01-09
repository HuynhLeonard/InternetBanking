package com.wnc.banking.service;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class HmacService {
    public String generateHmac(String data, String secretKey) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    public boolean isHmacValid(String data, String receivedHmac, String secretKey) throws Exception {
        String calculatedHmac = generateHmac(data, secretKey);
        System.out.println(calculatedHmac);
        return calculatedHmac.equals(receivedHmac);
    }
}
