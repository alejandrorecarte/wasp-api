package org.example.waspapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.example.waspapi.dto.responses.notification.GetNotificationResponse;
import org.example.waspapi.model.Notification;
import org.example.waspapi.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Notifications", description = "Endpoints for managing user notifications")
@RequestMapping("/notifications")
public class NotificationController {

  private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @Operation(
      summary = "Get all notifications",
      description = "Returns paginated notifications for the authenticated user.",
      operationId = "getNotifications")
  @GetMapping
  public ResponseEntity<Page<GetNotificationResponse>> getNotifications(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} fetching notifications", userId);
    Page<GetNotificationResponse> notifications =
        notificationService
            .getNotifications(userId, PageRequest.of(page, size))
            .map(this::toResponse);
    return ResponseEntity.ok(notifications);
  }

  @Operation(
      summary = "Get unread notifications",
      description = "Returns paginated unread notifications for the authenticated user.",
      operationId = "getUnreadNotifications")
  @GetMapping("/unread")
  public ResponseEntity<Page<GetNotificationResponse>> getUnreadNotifications(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} fetching unread notifications", userId);
    Page<GetNotificationResponse> notifications =
        notificationService
            .getUnreadNotifications(userId, PageRequest.of(page, size))
            .map(this::toResponse);
    return ResponseEntity.ok(notifications);
  }

  @Operation(
      summary = "Get unread notification count",
      description = "Returns the number of unread notifications for the authenticated user.",
      operationId = "getUnreadNotificationCount")
  @GetMapping("/unread/count")
  public ResponseEntity<Long> getUnreadCount(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} fetching unread notification count", userId);
    return ResponseEntity.ok(notificationService.getUnreadCount(userId));
  }

  @Operation(
      summary = "Mark notification as read",
      description = "Marks a specific notification as read.",
      operationId = "markNotificationAsRead")
  @PostMapping("/{notificationId}/read")
  public ResponseEntity<GetNotificationResponse> markAsRead(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID notificationId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} marking notification {} as read", userId, notificationId);
    Notification notification = notificationService.markAsRead(notificationId, userId);
    return ResponseEntity.ok(toResponse(notification));
  }

  @Operation(
      summary = "Mark unread messages notification as read for a game",
      description =
          "Marks the UNREAD_MESSAGES notification as read for the specified game."
              + " Call this when the user opens a game chat.",
      operationId = "markMessagesAsRead")
  @PostMapping("/messages/{gameId}/read")
  public ResponseEntity<Void> markMessagesAsRead(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt, @PathVariable UUID gameId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} marking messages as read for game {}", userId, gameId);
    notificationService.markMessagesAsRead(userId, gameId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Mark unread private messages notification as read for a conversation",
      description =
          "Marks the UNREAD_PRIVATE_MESSAGES notification as read for the specified friend."
              + " Call this when the user opens a private conversation.",
      operationId = "markPrivateMessagesAsRead")
  @PostMapping("/private-messages/{friendUserId}/read")
  public ResponseEntity<Void> markPrivateMessagesAsRead(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt, @PathVariable UUID friendUserId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} marking private messages as read for friend {}", userId, friendUserId);
    notificationService.markPrivateMessagesAsRead(userId, friendUserId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Mark all notifications as read",
      description = "Marks all notifications as read for the authenticated user.",
      operationId = "markAllNotificationsAsRead")
  @PostMapping("/read-all")
  public ResponseEntity<Void> markAllAsRead(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} marking all notifications as read", userId);
    notificationService.markAllAsRead(userId);
    return ResponseEntity.noContent().build();
  }

  private GetNotificationResponse toResponse(Notification notification) {
    return new GetNotificationResponse(
        notification.getId(),
        notification.getType(),
        notification.getReferenceId(),
        notification.getIsRead(),
        notification.getCreatedAt());
  }
}
