package org.example.waspapi.controller;

import static org.example.waspapi.Constants.SUPABASE_EMAIL_CLAIM;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import javax.validation.Valid;
import org.example.waspapi.dto.requests.users.RegisterUserRequest;
import org.example.waspapi.dto.requests.users.UpdateUserRequest;
import org.example.waspapi.dto.responses.users.GetUserResponse;
import org.example.waspapi.model.User;
import org.example.waspapi.service.SupabaseAuthService;
import org.example.waspapi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Users", description = "Endpoints for managing users")
@RequestMapping("/users")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);
  private final UserService userService;
  private final SupabaseAuthService supabaseAuthService;

  public UserController(UserService userService, SupabaseAuthService supabaseAuthService) {
    this.userService = userService;
    this.supabaseAuthService = supabaseAuthService;
  }

  @Operation(
      summary = "User login",
      description =
          "Endpoint for user login using JWT authentication. Retrieves or creates user information based on the JWT token.",
      operationId = "userLogin")
  @GetMapping("/login")
  public ResponseEntity<User> login(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getSubject());
    String email = jwt.getClaim(SUPABASE_EMAIL_CLAIM).toString();
    logger.info("User login attempt for email: {}", email);
    User user = userService.getOrCreate(userId, email);

    logger.info("User logged in successfully: {}", user.getEmail());
    return ResponseEntity.ok(user);
  }

  @Operation(
      summary = "Register a new user",
      description =
          "Endpoint for registering a new user with a chosen nickname and optional profile info. "
              + "Fails if the user already exists.",
      operationId = "registerUser")
  @PostMapping("/register")
  public ResponseEntity<User> register(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @Valid @RequestBody RegisterUserRequest request) {
    UUID userId = UUID.fromString(jwt.getSubject());
    String email = jwt.getClaim(SUPABASE_EMAIL_CLAIM).toString();
    logger.info("User registration attempt for email: {}", email);
    try {
      User user = userService.register(userId, email, request);
      logger.info("User registered successfully: {}", user.getEmail());
      return ResponseEntity.ok(user);
    } catch (Exception e) {
      logger.error("Registration failed for email: {}. Deleting Supabase Auth user.", email);
      supabaseAuthService.deleteUser(userId.toString());
      throw e;
    }
  }

  @Operation(
      summary = "Update user information",
      description =
          "Endpoint for updating user information using JWT authentication. Updates the user's data based on the provided request.",
      operationId = "updateUser")
  @PutMapping("/update")
  public ResponseEntity<User> updateUser(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @RequestBody UpdateUserRequest request) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("Updating user with id: {}", userId);
    User updated = userService.update(userId, request);

    logger.info("User updated successfully: {}", updated.getEmail());
    return ResponseEntity.ok(updated);
  }

  @Operation(
      summary = "Get authenticated user information",
      description =
          "Endpoint to retrieve the authenticated user's information using JWT authentication.",
      operationId = "getAuthenticatedUser")
  @GetMapping("/me")
  public ResponseEntity<User> getMe(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("Fetch me with id: {}", userId);
    User user = userService.get(userId);
    if (user == null) {
      logger.error("Me not found for id: {}", userId);
      return ResponseEntity.notFound().build();
    }

    logger.info("Me fetched successfully: {}", user.getEmail());
    return ResponseEntity.ok(user);
  }

  @Operation(
      summary = "Get user by nickname",
      description = "Endpoint to retrieve a user's ID and profile photo by their nickname.",
      operationId = "getUserByNickname")
  @GetMapping("/{nickname}")
  public ResponseEntity<GetUserResponse> getUser(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt, @PathVariable String nickname) {
    logger.info("Fetching user with nickname: {}", nickname);
    User user = userService.getByNickname(nickname);
    if (user == null) {
      logger.error("User not found for nickname: {}", nickname);
      return ResponseEntity.notFound().build();
    }
    GetUserResponse response = new GetUserResponse(user.getId(), user.getProfilePhoto());
    return ResponseEntity.ok(response);
  }
}
