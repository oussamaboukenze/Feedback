package com.example.feedback.security;

import com.example.feedback.entity.User;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Service
public class JwtService {

    private static final String SECRET = "feedback-local-development-secret";
    private static final long EXPIRATION_SECONDS = 24 * 60 * 60;

    public String generateToken(User user) {
        String header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        long expiresAt = Instant.now().plusSeconds(EXPIRATION_SECONDS).getEpochSecond();
        String payload = encode(String.format(
                "{\"sub\":\"%s\",\"uid\":%d,\"role\":\"%s\",\"exp\":%d}",
                escape(user.getEmail()),
                user.getId(),
                user.getRole().name(),
                expiresAt
        ));
        String unsignedToken = header + "." + payload;
        return unsignedToken + "." + hmac(unsignedToken);
    }

    private String encode(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String hmac(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Impossible de generer le token", exception);
        }
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
