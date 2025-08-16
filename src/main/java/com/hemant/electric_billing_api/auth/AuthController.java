package com.hemant.electric_billing_api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public record RegisterReq(@Email String email,
                              @NotBlank String password,
                              String firstName,
                              String lastName,
                              String phone,
                              String username) {}

    public record LoginReq(@Email String email, @NotBlank String password) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterReq req) {
        if (users.existsByEmail(req.email())) {
            return ResponseEntity.status(409).body(Map.of("error", "Email already exists"));
        }
        var u = User.builder()
                .email(req.email().toLowerCase())
                .username(req.username())
                .firstName(req.firstName())
                .lastName(req.lastName())
                .phone(req.phone())
                .passwordHash(encoder.encode(req.password()))
                .build();
        users.save(u);
        String token = jwt.issue(u.getId(), u.getEmail());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        var u = users.findByEmail(req.email().toLowerCase())
                .orElse(null);
        if (u == null || !encoder.matches(req.password(), u.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        String token = jwt.issue(u.getId(), u.getEmail());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
