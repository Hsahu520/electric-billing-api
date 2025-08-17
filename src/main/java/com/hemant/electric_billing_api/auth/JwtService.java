//package com.hemant.electric_billing_api.auth;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;  // <- this class is in jjwt-api (subpackage "security")
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import javax.crypto.SecretKey;
//import java.time.Instant;
//import java.util.Date;
//import java.util.UUID;
//
//@Service
//public class JwtService {
//
//    private final SecretKey key;
//    private final long ttlSeconds;
//
//    public JwtService(
//            @Value("${app.jwt.secret:dev-insecure-secret-change-me}") String secret,
//            @Value("${app.jwt.ttlSeconds:604800}") long ttlSeconds // 7 days
//    ) {
//        this.key = Keys.hmacShaKeyFor(secret.getBytes());
//        this.ttlSeconds = ttlSeconds;
//    }
//
//    public String issue(UUID userId, String email) {
//        Instant now = Instant.now();
//        return Jwts.builder()
//                .subject(email)
//                .claim("uid", userId.toString())
//                .issuedAt(Date.from(now))
//                .expiration(Date.from(now.plusSeconds(ttlSeconds)))
//                .signWith(key, Jwts.SIG.HS256)
//                .compact();
//    }
//
//    public Jws<Claims> parse(String token) {
//        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
//    }
//}

package com.hemant.electric_billing_api.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey key;
    private final long ttlSeconds;

    public JwtService(
            @Value("${app.jwt.secret:dev-insecure-secret-change-me}") String secret,
            @Value("${app.jwt.ttlSeconds:604800}") long ttlSeconds // 7 days
    ) {
        // Allow plain text or base64-encoded secrets (prefix "base64:")
        byte[] keyBytes = secret.startsWith("base64:")
                ? Decoders.BASE64.decode(secret.substring(7))
                : secret.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("app.jwt.secret must be at least 32 bytes (256 bits).");
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.ttlSeconds = ttlSeconds;
    }

    public String issue(UUID userId, String email) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim("uid", userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }
}