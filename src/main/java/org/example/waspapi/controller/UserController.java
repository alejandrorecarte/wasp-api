package org.example.waspapi.controller;

import org.example.waspapi.model.User;
import org.example.waspapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getEmail(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaim("https://wasp-api/email").toString();
        return userService.getOrCreateUser(email).toString();
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@AuthenticationPrincipal Jwt jwt, @RequestBody User user) {
        String email = jwt.getClaim("https://wasp-api/email").toString();
        if (!email.equals(user.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        try {
            User updated = userService.updateUser(user);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
