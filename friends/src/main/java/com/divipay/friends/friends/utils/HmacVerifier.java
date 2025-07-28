package com.divipay.friends.friends.utils;

import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HmacVerifier {

	@Value("${SECRET_SHA}")
	private String secretKey;
	
    public boolean verify(Long userId, String email, boolean hasPaid, String receivedSignature) {
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


