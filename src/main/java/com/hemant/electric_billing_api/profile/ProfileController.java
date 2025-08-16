package com.hemant.electric_billing_api.profile;

import com.hemant.electric_billing_api.auth.JwtAuthFilter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    // Fallback user for non-auth flows (dev/local)
    private static final UUID DEV_USER_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final ProfileRepository repo;

    public ProfileController(ProfileRepository repo) {
        this.repo = repo;
    }

    /** Resolve the current user:
     *  - If Authentication has a JwtAuthFilter.AuthUser principal, use its id
     *  - Otherwise fall back to DEV_USER_ID (for dev/no-auth scenarios)
     */
    private UUID resolveUserId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof JwtAuthFilter.AuthUser u) {
            return u.id();
        }
        return DEV_USER_ID;
    }

    @GetMapping
    public List<Profile> list(Authentication auth) {
        UUID uid = resolveUserId(auth);
        return repo.findAllByUserIdOrderByCreatedAtDesc(uid);
    }

    @PostMapping
    public ResponseEntity<Profile> create(@Valid @RequestBody CreateProfileRequest req,
                                          Authentication auth) {
        String name = req.name() == null ? "" : req.name().trim();
        if (name.isEmpty()) {
            // 400 - Bad Request (empty/blank name)
            return ResponseEntity.badRequest().build();
        }

        UUID uid = resolveUserId(auth);

        // 409 - Conflict if same user + name already exists
        if (repo.existsByUserIdAndName(uid, name)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Profile p = Profile.builder()
                .id(UUID.randomUUID())
                .userId(uid)
                .name(name)
                .createdAt(Instant.now())
                .build();

        // 201 - Created
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(p));
    }
}
