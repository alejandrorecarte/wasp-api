package org.example.waspapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.example.waspapi.dto.requests.joinrequest.CreateJoinRequestRequest;
import org.example.waspapi.dto.responses.joinrequest.GetJoinRequestResponse;
import org.example.waspapi.model.JoinRequest;
import org.example.waspapi.service.JoinRequestService;
import org.example.waspapi.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Join Requests", description = "Endpoints for managing game join requests")
@RequestMapping("/games/{gameId}/join-requests")
public class JoinRequestController {

  private static final Logger logger = LoggerFactory.getLogger(JoinRequestController.class);
  private final JoinRequestService joinRequestService;
  private final SubscriptionService subscriptionService;

  public JoinRequestController(
      JoinRequestService joinRequestService, SubscriptionService subscriptionService) {
    this.joinRequestService = joinRequestService;
    this.subscriptionService = subscriptionService;
  }

  @Operation(
      summary = "Create a join request",
      description = "Creates a join request for the authenticated user to join the specified game.",
      operationId = "createJoinRequest")
  @PostMapping
  public ResponseEntity<UUID> createJoinRequest(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @RequestBody(required = false) CreateJoinRequestRequest request) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} requesting to join game {}", userId, gameId);

    String message = request != null ? request.getMessage() : null;
    JoinRequest joinRequest = joinRequestService.create(userId, gameId, message);

    logger.info("Join request created with ID: {}", joinRequest.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(joinRequest.getId());
  }

  @Operation(
      summary = "Check if user has a join request",
      description =
          "Returns whether the authenticated user has already sent a join request for the specified game.",
      operationId = "hasJoinRequest")
  @GetMapping("/me")
  public ResponseEntity<Boolean> hasJoinRequest(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt, @PathVariable UUID gameId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("Checking join request existence for user {} on game {}", userId, gameId);
    boolean exists = joinRequestService.existsByUserAndGame(userId, gameId);
    return ResponseEntity.ok(exists);
  }

  @Operation(
      summary = "Get pending join requests",
      description =
          "Returns all pending join requests for the specified game. Only game admins can access this.",
      operationId = "getPendingJoinRequests")
  @GetMapping
  public ResponseEntity<List<GetJoinRequestResponse>> getPendingJoinRequests(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt, @PathVariable UUID gameId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("Fetching pending join requests for game {}", gameId);

    if (!subscriptionService.isAdmin(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    List<GetJoinRequestResponse> responses =
        joinRequestService.getPendingByGameId(gameId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());

    return ResponseEntity.ok(responses);
  }

  @Operation(
      summary = "Accept a join request",
      description =
          "Accepts a join request and creates a subscription for the user. Only game admins can do this.",
      operationId = "acceptJoinRequest")
  @PostMapping("/{requestId}/accept")
  public ResponseEntity<GetJoinRequestResponse> acceptJoinRequest(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID requestId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("Accepting join request {} for game {}", requestId, gameId);

    if (!subscriptionService.isAdmin(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    JoinRequest joinRequest = joinRequestService.accept(requestId, userId);
    return ResponseEntity.ok(toResponse(joinRequest));
  }

  @Operation(
      summary = "Reject a join request",
      description = "Rejects a join request. Only game admins can do this.",
      operationId = "rejectJoinRequest")
  @PostMapping("/{requestId}/reject")
  public ResponseEntity<GetJoinRequestResponse> rejectJoinRequest(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID requestId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("Rejecting join request {} for game {}", requestId, gameId);

    if (!subscriptionService.isAdmin(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    JoinRequest joinRequest = joinRequestService.reject(requestId);
    return ResponseEntity.ok(toResponse(joinRequest));
  }

  private GetJoinRequestResponse toResponse(JoinRequest joinRequest) {
    return new GetJoinRequestResponse(
        joinRequest.getId(),
        joinRequest.getUser().getId(),
        joinRequest.getUser().getNickname(),
        joinRequest.getMessage(),
        joinRequest.getStatus(),
        joinRequest.getCreatedAt());
  }
}
