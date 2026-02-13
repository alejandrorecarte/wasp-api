package org.example.waspapi.service;

import static org.example.waspapi.Constants.NOT_FRIENDS;
import static org.example.waspapi.Constants.PHOTO_UPLOAD_FAILED;
import static org.example.waspapi.Constants.PRIVATE_MESSAGE_CONTENT_EMPTY;
import static org.example.waspapi.Constants.USER_NOT_FOUND;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.example.waspapi.dto.responses.privatemessage.GetConversationResponse;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.PrivateMessage;
import org.example.waspapi.model.User;
import org.example.waspapi.repository.PrivateMessageRepository;
import org.example.waspapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PrivateMessageService {

  private static final Logger logger = LoggerFactory.getLogger(PrivateMessageService.class);
  private static final String PRIVATE_MESSAGE_PHOTOS_BUCKET = "private-message-photos";

  private final PrivateMessageRepository privateMessageRepository;
  private final UserRepository userRepository;
  private final FriendRequestService friendRequestService;
  private final NotificationService notificationService;
  private final SupabaseStorageService storageService;

  public PrivateMessageService(
      PrivateMessageRepository privateMessageRepository,
      UserRepository userRepository,
      FriendRequestService friendRequestService,
      NotificationService notificationService,
      SupabaseStorageService storageService) {
    this.privateMessageRepository = privateMessageRepository;
    this.userRepository = userRepository;
    this.friendRequestService = friendRequestService;
    this.notificationService = notificationService;
    this.storageService = storageService;
  }

  public PrivateMessage send(UUID senderId, UUID receiverId, String content) {
    logger.debug("User {} sending private message to {}", senderId, receiverId);

    if (content == null || content.trim().isEmpty()) {
      throw new HandledException(PRIVATE_MESSAGE_CONTENT_EMPTY, HttpStatus.BAD_REQUEST);
    }

    if (!friendRequestService.areFriends(senderId, receiverId)) {
      throw new HandledException(NOT_FRIENDS, HttpStatus.FORBIDDEN);
    }

    User sender =
        userRepository
            .findById(senderId)
            .orElseThrow(() -> new HandledException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    User receiver =
        userRepository
            .findById(receiverId)
            .orElseThrow(() -> new HandledException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));

    PrivateMessage message = new PrivateMessage(sender, receiver, content);
    PrivateMessage saved = privateMessageRepository.save(message);
    logger.info("Private message sent from {} to {}", senderId, receiverId);
    notificationService.createIfNotExists(receiverId, "UNREAD_PRIVATE_MESSAGES", senderId);
    return saved;
  }

  public PrivateMessage sendWithImage(
      UUID senderId, UUID receiverId, String content, byte[] imageData, String contentType) {
    logger.debug("User {} sending image private message to {}", senderId, receiverId);

    if (!friendRequestService.areFriends(senderId, receiverId)) {
      throw new HandledException(NOT_FRIENDS, HttpStatus.FORBIDDEN);
    }

    User sender =
        userRepository
            .findById(senderId)
            .orElseThrow(() -> new HandledException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    User receiver =
        userRepository
            .findById(receiverId)
            .orElseThrow(() -> new HandledException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));

    String path = senderId + "/" + UUID.randomUUID();
    try {
      storageService.upload(PRIVATE_MESSAGE_PHOTOS_BUCKET, path, imageData, contentType);
    } catch (Exception e) {
      logger.error(
          "Failed to upload private message image from {} to {}: {}",
          senderId,
          receiverId,
          e.getMessage());
      throw new HandledException(PHOTO_UPLOAD_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    PrivateMessage message = new PrivateMessage(sender, receiver, content);
    message.setImageUrl(path);
    PrivateMessage saved = privateMessageRepository.save(message);
    logger.info("Image private message sent from {} to {}", senderId, receiverId);
    notificationService.createIfNotExists(receiverId, "UNREAD_PRIVATE_MESSAGES", senderId);
    return saved;
  }

  public String resolveImageUrl(String imageUrl) {
    if (imageUrl == null) {
      return null;
    }
    return storageService.getPublicUrl(PRIVATE_MESSAGE_PHOTOS_BUCKET, imageUrl);
  }

  public Page<PrivateMessage> getConversation(UUID userId, UUID friendUserId, Pageable pageable) {
    logger.debug("Fetching conversation between {} and {}", userId, friendUserId);

    if (!friendRequestService.areFriends(userId, friendUserId)) {
      throw new HandledException(NOT_FRIENDS, HttpStatus.FORBIDDEN);
    }

    return privateMessageRepository.findConversation(userId, friendUserId, pageable);
  }

  public List<GetConversationResponse> getConversations(UUID userId) {
    logger.debug("Fetching conversations for user {}", userId);

    List<PrivateMessage> latestMessages =
        privateMessageRepository.findLatestMessagePerConversation(userId);

    return latestMessages.stream()
        .map(
            msg -> {
              User friend =
                  msg.getSender().getId().equals(userId) ? msg.getReceiver() : msg.getSender();
              return new GetConversationResponse(
                  friend.getId(),
                  friend.getNickname(),
                  friend.getProfilePhoto(),
                  msg.getContent(),
                  msg.getCreatedAt(),
                  msg.getSender().getId());
            })
        .collect(Collectors.toList());
  }
}
