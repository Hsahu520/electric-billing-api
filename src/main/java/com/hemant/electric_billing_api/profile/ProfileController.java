package com.hemant.electric_billing_api.profile;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    // temporary fixed user until auth is added
    private static final UUID DEV_USER_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final ProfileRepository repo;

    public ProfileController(ProfileRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Profile> list() {
        return repo.findAllByUserIdOrderByCreatedAtDesc(DEV_USER_ID);
    }

    @PostMapping
    public ResponseEntity<Profile> create(@Valid @RequestBody CreateProfileRequest req) {
        // simple validation
        String name = req.name() == null ? "" : req.name().trim();
        if (name.isEmpty()) {
            return ResponseEntity.badRequest().build(); // 400 - Bad Server
        }

        // conflict if same user + name already exists
        if (repo.existsByUserIdAndName(DEV_USER_ID, name)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 - User already exists
        }

        Profile p = Profile.builder()
                .id(UUID.randomUUID())
                .userId(DEV_USER_ID)
                .name(name)
                .createdAt(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(p)); // 201 - User Created
    }
}
