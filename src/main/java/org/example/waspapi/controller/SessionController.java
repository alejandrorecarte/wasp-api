package org.example.waspapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.example.waspapi.dto.requests.session.CreateSessionRequest;
import org.example.waspapi.dto.requests.session.UpdateSessionRequest;
import org.example.waspapi.dto.responses.session.GetSessionAttendanceResponse;
import org.example.waspapi.dto.responses.session.GetSessionResponse;
import org.example.waspapi.model.Session;
import org.example.waspapi.model.SessionAttendance;
import org.example.waspapi.service.SessionService;
import org.example.waspapi.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Sessions", description = "Endpoints for managing sessions within a game")
@RequestMapping("/games/{gameId}/sessions")
public class SessionController {

  private static final Logger logger = LoggerFactory.getLogger(SessionController.class);
  private final SessionService sessionService;
  private final SubscriptionService subscriptionService;

  public SessionController(SessionService sessionService, SubscriptionService subscriptionService) {
    this.sessionService = sessionService;
    this.subscriptionService = subscriptionService;
  }

  @Operation(
      summary = "Create a session",
      description = "Creates a new session for the specified game. Requires admin privileges.",
      operationId = "createSession")
  @PostMapping
  public ResponseEntity<GetSessionResponse> createSession(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @Valid @RequestBody CreateSessionRequest request) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} creating session for game {}", userId, gameId);
    if (!subscriptionService.isAdmin(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    Session session = sessionService.create(request, gameId);
    return ResponseEntity.ok(toResponse(session));
  }

  @Operation(
      summary = "List sessions of a game",
      description = "Returns all sessions for the specified game. Requires subscription.",
      operationId = "getSessionsByGame")
  @GetMapping
  public ResponseEntity<List<GetSessionResponse>> getSessionsByGame(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt, @PathVariable UUID gameId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} listing sessions for game {}", userId, gameId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    List<GetSessionResponse> sessions =
        sessionService.getByGameId(gameId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    return ResponseEntity.ok(sessions);
  }

  @Operation(
      summary = "Get a session by ID",
      description = "Returns a single session. Requires subscription to the game.",
      operationId = "getSession")
  @GetMapping("/{sessionId}")
  public ResponseEntity<GetSessionResponse> getSession(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID sessionId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} fetching session {} for game {}", userId, sessionId, gameId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    Session session = sessionService.getById(sessionId);
    return ResponseEntity.ok(toResponse(session));
  }

  @Operation(
      summary = "Update a session",
      description = "Updates an existing session. Requires admin privileges.",
      operationId = "updateSession")
  @PutMapping("/{sessionId}")
  public ResponseEntity<GetSessionResponse> updateSession(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID sessionId,
      @Valid @RequestBody UpdateSessionRequest request) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} updating session {} for game {}", userId, sessionId, gameId);
    if (!subscriptionService.isAdmin(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    Session session = sessionService.update(sessionId, request);
    return ResponseEntity.ok(toResponse(session));
  }

  @Operation(
      summary = "Delete a session",
      description = "Deletes a session. Requires admin privileges.",
      operationId = "deleteSession")
  @DeleteMapping("/{sessionId}")
  public ResponseEntity<Void> deleteSession(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID sessionId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} deleting session {} for game {}", userId, sessionId, gameId);
    if (!subscriptionService.isAdmin(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    sessionService.delete(sessionId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Confirm attendance",
      description =
          "Confirms the authenticated user's attendance to a session. Requires subscription.",
      operationId = "confirmAttendance")
  @PostMapping("/{sessionId}/attendance")
  public ResponseEntity<GetSessionAttendanceResponse> confirmAttendance(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID sessionId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} confirming attendance for session {}", userId, sessionId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    SessionAttendance attendance = sessionService.confirmAttendance(userId, sessionId);
    return ResponseEntity.ok(toAttendanceResponse(attendance));
  }

  @Operation(
      summary = "Cancel attendance",
      description =
          "Cancels the authenticated user's attendance to a session. Requires subscription.",
      operationId = "cancelAttendance")
  @DeleteMapping("/{sessionId}/attendance")
  public ResponseEntity<GetSessionAttendanceResponse> cancelAttendance(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID sessionId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} cancelling attendance for session {}", userId, sessionId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    SessionAttendance attendance = sessionService.cancelAttendance(userId, sessionId);
    return ResponseEntity.ok(toAttendanceResponse(attendance));
  }

  @Operation(
      summary = "Reset attendance to pending",
      description =
          "Resets the authenticated user's attendance to pending (undecided). Requires subscription.",
      operationId = "resetAttendance")
  @PutMapping("/{sessionId}/attendance")
  public ResponseEntity<GetSessionAttendanceResponse> resetAttendance(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID sessionId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} resetting attendance for session {}", userId, sessionId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    SessionAttendance attendance = sessionService.resetAttendance(userId, sessionId);
    return ResponseEntity.ok(toAttendanceResponse(attendance));
  }

  @Operation(
      summary = "List attendees of a session",
      description = "Returns all attendees for the specified session. Requires subscription.",
      operationId = "getAttendees")
  @GetMapping("/{sessionId}/attendance")
  public ResponseEntity<List<GetSessionAttendanceResponse>> getAttendees(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID sessionId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} listing attendees for session {}", userId, sessionId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    List<GetSessionAttendanceResponse> attendees =
        sessionService.getAttendees(sessionId).stream()
            .map(this::toAttendanceResponse)
            .collect(Collectors.toList());
    return ResponseEntity.ok(attendees);
  }

  private GetSessionResponse toResponse(Session session) {
    return new GetSessionResponse(
        session.getId(),
        session.getName(),
        session.getIsPresential(),
        session.getDatetime(),
        session.getPlace(),
        session.getObservations(),
        session.getGame().getId(),
        session.getGame().getName(),
        sessionService.countConfirmed(session.getId()));
  }

  private GetSessionAttendanceResponse toAttendanceResponse(SessionAttendance attendance) {
    return new GetSessionAttendanceResponse(
        attendance.getUser().getId(),
        attendance.getUser().getNickname(),
        attendance.getUser().getProfilePhoto(),
        attendance.getConfirmAssist());
  }
}
