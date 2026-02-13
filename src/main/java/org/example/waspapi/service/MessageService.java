package org.example.waspapi.service;

import static org.example.waspapi.Constants.GAME_NOT_FOUND;
import static org.example.waspapi.Constants.MESSAGE_CONTENT_EMPTY;
import static org.example.waspapi.Constants.PHOTO_UPLOAD_FAILED;
import static org.example.waspapi.Constants.USER_NOT_FOUND;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.example.waspapi.dto.requests.message.SendMessageRequest;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.Game;
import org.example.waspapi.model.Message;
import org.example.waspapi.model.User;
import org.example.waspapi.repository.GameRepository;
import org.example.waspapi.repository.MessageRepository;
import org.example.waspapi.repository.SubscriptionRepository;
import org.example.waspapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

  private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
  private static final String MESSAGE_PHOTOS_BUCKET = "message-photos";

  private final MessageRepository messageRepository;
  private final GameRepository gameRepository;
  private final UserRepository userRepository;
  private final SupabaseStorageService storageService;
  private final SubscriptionRepository subscriptionRepository;
  private final NotificationService notificationService;

  public MessageService(
      MessageRepository messageRepository,
      GameRepository gameRepository,
      UserRepository userRepository,
      SupabaseStorageService storageService,
      SubscriptionRepository subscriptionRepository,
      NotificationService notificationService) {
    this.messageRepository = messageRepository;
    this.gameRepository = gameRepository;
    this.userRepository = userRepository;
    this.storageService = storageService;
    this.subscriptionRepository = subscriptionRepository;
    this.notificationService = notificationService;
  }

  public Message send(UUID gameId, UUID userId, SendMessageRequest request) {
    logger.debug("User {} sending message to game {}", userId, gameId);

    if (request.getContent() == null || request.getContent().trim().isEmpty()) {
      throw new HandledException(MESSAGE_CONTENT_EMPTY, HttpStatus.BAD_REQUEST);
    }

    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(() -> new HandledException(GAME_NOT_FOUND, HttpStatus.NOT_FOUND));
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new HandledException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));

    Message message = new Message(game, user, request.getContent());
    Message saved = messageRepository.save(message);
    logger.info("Message sent by user {} in game {}", userId, gameId);
    notifySubscribers(gameId, userId);
    return saved;
  }

  public Message sendWithImage(
      UUID gameId, UUID userId, String content, byte[] imageData, String contentType) {
    logger.debug("User {} sending image message to game {}", userId, gameId);
    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(() -> new HandledException(GAME_NOT_FOUND, HttpStatus.NOT_FOUND));
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new HandledException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));

    String path = gameId + "/" + UUID.randomUUID();
    try {
      storageService.upload(MESSAGE_PHOTOS_BUCKET, path, imageData, contentType);
    } catch (Exception e) {
      logger.error("Failed to upload message image for game {}: {}", gameId, e.getMessage());
      throw new HandledException(PHOTO_UPLOAD_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    Message message = new Message(game, user, content);
    message.setImageUrl(path);
    Message saved = messageRepository.save(message);
    logger.info("Image message sent by user {} in game {}", userId, gameId);
    notifySubscribers(gameId, userId);
    return saved;
  }

  public String resolveImageUrl(String imageUrl) {
    if (imageUrl == null) {
      return null;
    }
    return storageService.getPublicUrl(MESSAGE_PHOTOS_BUCKET, imageUrl);
  }

  private void notifySubscribers(UUID gameId, UUID senderId) {
    List<UUID> subscriberIds =
        subscriptionRepository.findByGameIdAndIsActiveTrue(gameId).stream()
            .map(s -> s.getUser().getId())
            .filter(id -> !id.equals(senderId))
            .collect(Collectors.toList());
    for (UUID subscriberId : subscriberIds) {
      notificationService.createIfNotExists(subscriberId, "UNREAD_MESSAGES", gameId);
    }
  }

  public Page<Message> getByGameId(UUID gameId, Pageable pageable) {
    logger.debug("Fetching messages for game: {}", gameId);
    return messageRepository.findByGameIdOrderByCreatedAtDesc(gameId, pageable);
  }
}
