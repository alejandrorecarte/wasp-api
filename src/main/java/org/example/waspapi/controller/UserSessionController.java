package org.example.waspapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.example.waspapi.dto.responses.session.GetSessionResponse;
import org.example.waspapi.model.Session;
import org.example.waspapi.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Sessions", description = "Endpoints for managing sessions within a game")
@RequestMapping("/sessions")
public class UserSessionController {

  private static final Logger logger = LoggerFactory.getLogger(UserSessionController.class);
  private final SessionService sessionService;

  public UserSessionController(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @Operation(
      summary = "Get my sessions by month",
      description =
          "Returns all sessions for the authenticated user's games in the specified month.",
      operationId = "getMySessions")
  @GetMapping("/me")
  public ResponseEntity<List<GetSessionResponse>> getMySessions(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @RequestParam int year,
      @RequestParam int month) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} fetching sessions for {}-{}", userId, year, month);

    List<GetSessionResponse> sessions =
        sessionService.getByUserAndMonth(userId, year, month).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    return ResponseEntity.ok(sessions);
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
}
