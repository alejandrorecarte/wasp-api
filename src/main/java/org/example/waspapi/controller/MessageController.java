package org.example.waspapi.controller;

import static org.example.waspapi.Constants.INVALID_FILE_TYPE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.UUID;
import javax.validation.Valid;
import org.example.waspapi.dto.requests.message.SendMessageRequest;
import org.example.waspapi.dto.responses.message.GetMessageResponse;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.Message;
import org.example.waspapi.service.MessageService;
import org.example.waspapi.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "Messages", description = "Endpoints for managing chat messages within a game")
@RequestMapping("/games/{gameId}/messages")
public class MessageController {

  private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
  private final MessageService messageService;
  private final SubscriptionService subscriptionService;

  public MessageController(MessageService messageService, SubscriptionService subscriptionService) {
    this.messageService = messageService;
    this.subscriptionService = subscriptionService;
  }

  @Operation(
      summary = "Send a message",
      description = "Sends a message to the game chat. Requires subscription.",
      operationId = "sendMessage")
  @PostMapping
  public ResponseEntity<GetMessageResponse> sendMessage(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @Valid @RequestBody SendMessageRequest request) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} sending message to game {}", userId, gameId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    Message message = messageService.send(gameId, userId, request);
    return ResponseEntity.ok(toResponse(message));
  }

  @Operation(
      summary = "Get messages",
      description =
          "Returns paginated messages for the game chat, ordered by newest first. Requires subscription.",
      operationId = "getMessages")
  @GetMapping
  public ResponseEntity<Page<GetMessageResponse>> getMessages(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info(
        "User {} fetching messages for game {}, page={}, size={}", userId, gameId, page, size);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    Page<GetMessageResponse> messages =
        messageService.getByGameId(gameId, PageRequest.of(page, size)).map(this::toResponse);
    return ResponseEntity.ok(messages);
  }

  @Operation(
      summary = "Send a message with an image",
      description =
          "Sends a message with an image to the game chat. Text content is optional. Requires subscription.",
      operationId = "sendMessageWithImage")
  @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<GetMessageResponse> sendMessageWithImage(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @RequestParam("file") MultipartFile file,
      @RequestParam(required = false) String content)
      throws IOException {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} sending image message to game {}", userId, gameId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new HandledException(INVALID_FILE_TYPE, HttpStatus.BAD_REQUEST);
    }

    Message message =
        messageService.sendWithImage(gameId, userId, content, file.getBytes(), contentType);
    return ResponseEntity.ok(toResponse(message));
  }

  private GetMessageResponse toResponse(Message message) {
    return new GetMessageResponse(
        message.getId(),
        message.getContent(),
        messageService.resolveImageUrl(message.getImageUrl()),
        message.getCreatedAt(),
        message.getUser().getId(),
        message.getUser().getNickname(),
        message.getUser().getProfilePhoto());
  }
}
