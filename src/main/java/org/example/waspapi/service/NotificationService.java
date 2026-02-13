package org.example.waspapi.service;

import static org.example.waspapi.Constants.NOTIFICATION_NOT_FOUND;

import java.util.List;
import java.util.UUID;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.Notification;
import org.example.waspapi.model.User;
import org.example.waspapi.repository.NotificationRepository;
import org.example.waspapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

  private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

  public NotificationService(
      NotificationRepository notificationRepository, UserRepository userRepository) {
    this.notificationRepository = notificationRepository;
    this.userRepository = userRepository;
  }

  public Notification create(UUID userId, String type, UUID referenceId) {
    logger.debug("Creating notification type {} for user {}", type, userId);
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> {
                  logger.warn("Cannot create notification: user {} not found", userId);
                  return new HandledException("User not found", HttpStatus.NOT_FOUND);
                });

    Notification notification = new Notification(user, type, referenceId);
    Notification saved = notificationRepository.save(notification);
    logger.info("Notification created: type={}, user={}, ref={}", type, userId, referenceId);
    return saved;
  }

  public void createIfNotExists(UUID userId, String type, UUID referenceId) {
    if (notificationRepository.existsByUserIdAndTypeAndReferenceIdAndIsReadFalse(
        userId, type, referenceId)) {
      logger.debug(
          "Notification type {} already exists for user {} ref {}", type, userId, referenceId);
      return;
    }
    create(userId, type, referenceId);
  }

  public void createForMany(List<UUID> userIds, String type, UUID referenceId) {
    logger.debug("Creating notification type {} for {} users", type, userIds.size());
    for (UUID userId : userIds) {
      try {
        create(userId, type, referenceId);
      } catch (HandledException e) {
        logger.warn("Skipping notification for user {}: {}", userId, e.getMessage());
      }
    }
  }

  public Page<Notification> getNotifications(UUID userId, Pageable pageable) {
    logger.debug("Fetching notifications for user {}", userId);
    return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
  }

  public Page<Notification> getUnreadNotifications(UUID userId, Pageable pageable) {
    logger.debug("Fetching unread notifications for user {}", userId);
    return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable);
  }

  public long getUnreadCount(UUID userId) {
    logger.debug("Counting unread notifications for user {}", userId);
    return notificationRepository.countByUserIdAndIsReadFalse(userId);
  }

  @Transactional
  public Notification markAsRead(UUID notificationId, UUID userId) {
    logger.debug("Marking notification {} as read for user {}", notificationId, userId);
    Notification notification =
        notificationRepository
            .findById(notificationId)
            .orElseThrow(() -> new HandledException(NOTIFICATION_NOT_FOUND, HttpStatus.NOT_FOUND));

    if (!notification.getUser().getId().equals(userId)) {
      throw new HandledException(NOTIFICATION_NOT_FOUND, HttpStatus.FORBIDDEN);
    }

    notification.setIsRead(true);
    Notification saved = notificationRepository.save(notification);
    logger.info("Notification {} marked as read", notificationId);
    return saved;
  }

  @Transactional
  public void markMessagesAsRead(UUID userId, UUID gameId) {
    logger.debug("Marking UNREAD_MESSAGES as read for user {} game {}", userId, gameId);
    notificationRepository.markAsReadByUserIdAndTypeAndReferenceId(
        userId, "UNREAD_MESSAGES", gameId);
    logger.info("UNREAD_MESSAGES marked as read for user {} game {}", userId, gameId);
  }

  @Transactional
  public void markPrivateMessagesAsRead(UUID userId, UUID friendUserId) {
    logger.debug(
        "Marking UNREAD_PRIVATE_MESSAGES as read for user {} friend {}", userId, friendUserId);
    notificationRepository.markAsReadByUserIdAndTypeAndReferenceId(
        userId, "UNREAD_PRIVATE_MESSAGES", friendUserId);
    logger.info(
        "UNREAD_PRIVATE_MESSAGES marked as read for user {} friend {}", userId, friendUserId);
  }

  @Transactional
  public void markAllAsRead(UUID userId) {
    logger.debug("Marking all notifications as read for user {}", userId);
    notificationRepository.markAllAsReadByUserId(userId);
    logger.info("All notifications marked as read for user {}", userId);
  }
}
