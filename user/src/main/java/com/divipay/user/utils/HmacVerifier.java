package com.divipay.user.utils;

import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacVerifier {

    public static boolean verify(String userId, String email, String hasPaid, String receivedSignature, String secretKey) {
        try {
            String dataToSign = userId + email + hasPaid;

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(dataToSign.getBytes());
            String expectedSignature = Base64.getEncoder().encodeToString(hmacBytes);

            return expectedSignature.equals(receivedSignature);
        } catch (Exception e) {
            return false;
        }
    }
}

