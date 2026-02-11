package org.example.waspapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.example.waspapi.dto.requests.event.CreateEventRequest;
import org.example.waspapi.dto.requests.event.UpdateEventRequest;
import org.example.waspapi.dto.responses.event.GetEventAttendanceResponse;
import org.example.waspapi.dto.responses.event.GetEventResponse;
import org.example.waspapi.model.Event;
import org.example.waspapi.model.EventAttendance;
import org.example.waspapi.service.EventService;
import org.example.waspapi.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Events", description = "Endpoints for managing events within a game")
@RequestMapping("/games/{gameId}/events")
public class EventController {

  private static final Logger logger = LoggerFactory.getLogger(EventController.class);
  private final EventService eventService;
  private final SubscriptionService subscriptionService;

  public EventController(EventService eventService, SubscriptionService subscriptionService) {
    this.eventService = eventService;
    this.subscriptionService = subscriptionService;
  }

  @Operation(
      summary = "Create an event",
      description = "Creates a new event for the specified game. Requires admin privileges.",
      operationId = "createEvent")
  @PostMapping
  public ResponseEntity<GetEventResponse> createEvent(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @Valid @RequestBody CreateEventRequest request) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} creating event for game {}", userId, gameId);
    if (!subscriptionService.isAdmin(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    Event event = eventService.create(request, gameId);
    return ResponseEntity.ok(toResponse(event));
  }

  @Operation(
      summary = "List events of a game",
      description = "Returns all events for the specified game. Requires subscription.",
      operationId = "getEventsByGame")
  @GetMapping
  public ResponseEntity<List<GetEventResponse>> getEventsByGame(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt, @PathVariable UUID gameId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} listing events for game {}", userId, gameId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    List<GetEventResponse> events =
        eventService.getByGameId(gameId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    return ResponseEntity.ok(events);
  }

  @Operation(
      summary = "Get an event by ID",
      description = "Returns a single event. Requires subscription to the game.",
      operationId = "getEvent")
  @GetMapping("/{eventId}")
  public ResponseEntity<GetEventResponse> getEvent(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID eventId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} fetching event {} for game {}", userId, eventId, gameId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    Event event = eventService.getById(eventId);
    return ResponseEntity.ok(toResponse(event));
  }

  @Operation(
      summary = "Update an event",
      description = "Updates an existing event. Requires admin privileges.",
      operationId = "updateEvent")
  @PutMapping("/{eventId}")
  public ResponseEntity<GetEventResponse> updateEvent(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID eventId,
      @Valid @RequestBody UpdateEventRequest request) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} updating event {} for game {}", userId, eventId, gameId);
    if (!subscriptionService.isAdmin(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    Event event = eventService.update(eventId, request);
    return ResponseEntity.ok(toResponse(event));
  }

  @Operation(
      summary = "Delete an event",
      description = "Deletes an event. Requires admin privileges.",
      operationId = "deleteEvent")
  @DeleteMapping("/{eventId}")
  public ResponseEntity<Void> deleteEvent(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID eventId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} deleting event {} for game {}", userId, eventId, gameId);
    if (!subscriptionService.isAdmin(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    eventService.delete(eventId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Confirm attendance",
      description =
          "Confirms the authenticated user's attendance to an event. Requires subscription.",
      operationId = "confirmAttendance")
  @PostMapping("/{eventId}/attendance")
  public ResponseEntity<GetEventAttendanceResponse> confirmAttendance(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID eventId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} confirming attendance for event {}", userId, eventId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    EventAttendance attendance = eventService.confirmAttendance(userId, eventId);
    return ResponseEntity.ok(toAttendanceResponse(attendance));
  }

  @Operation(
      summary = "Cancel attendance",
      description =
          "Cancels the authenticated user's attendance to an event. Requires subscription.",
      operationId = "cancelAttendance")
  @DeleteMapping("/{eventId}/attendance")
  public ResponseEntity<GetEventAttendanceResponse> cancelAttendance(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID eventId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} cancelling attendance for event {}", userId, eventId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    EventAttendance attendance = eventService.cancelAttendance(userId, eventId);
    return ResponseEntity.ok(toAttendanceResponse(attendance));
  }

  @Operation(
      summary = "List attendees of an event",
      description = "Returns all attendees for the specified event. Requires subscription.",
      operationId = "getAttendees")
  @GetMapping("/{eventId}/attendance")
  public ResponseEntity<List<GetEventAttendanceResponse>> getAttendees(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID eventId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} listing attendees for event {}", userId, eventId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    List<GetEventAttendanceResponse> attendees =
        eventService.getAttendees(eventId).stream()
            .map(this::toAttendanceResponse)
            .collect(Collectors.toList());
    return ResponseEntity.ok(attendees);
  }

  private GetEventResponse toResponse(Event event) {
    return new GetEventResponse(
        event.getId(),
        event.getName(),
        event.getIsPresential(),
        event.getDatetime(),
        event.getPlace(),
        event.getObservations(),
        event.getGame().getId(),
        eventService.countConfirmed(event.getId()));
  }

  private GetEventAttendanceResponse toAttendanceResponse(EventAttendance attendance) {
    return new GetEventAttendanceResponse(
        attendance.getUser().getId(),
        attendance.getUser().getNickname(),
        attendance.getUser().getProfilePhoto(),
        attendance.getConfirmAssist());
  }
}
