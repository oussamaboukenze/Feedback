package com.example.feedback.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.feedback.entity.User;

@Service
public class JwtService {

    @Value("${jwt.secret:feedback-dev-secret-change-in-prod}")
    private String secret;

    private static final long EXPIRATION_SECONDS = 24 * 60 * 60;

    public String generateToken(User user) {
        String header  = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        long expiresAt = Instant.now().plusSeconds(EXPIRATION_SECONDS).getEpochSecond();
        String payload = encode(String.format(
            "{\"sub\":\"%s\",\"uid\":%d,\"role\":\"%s\",\"exp\":%d}",
            escape(user.getEmail()), user.getId(), user.getRole().name(), expiresAt));
        String unsigned = header + "." + payload;
        return unsigned + "." + hmac(unsigned);
    }

    public boolean isTokenValid(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;
            if (!hmac(parts[0] + "." + parts[1]).equals(parts[2])) return false;
            String json = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            long exp = Long.parseLong(json.replaceAll(".*\"exp\":(\\d+).*", "$1"));
            return Instant.now().getEpochSecond() < exp;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        String[] parts = token.split("\\.");
        String json = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        return json.replaceAll(".*\"sub\":\"([^\"]+)\".*", "$1");
    }

    private String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding()
            .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String hmac(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de générer le token", e);
        }
    }

    private String escape(String v) {
        return v.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}