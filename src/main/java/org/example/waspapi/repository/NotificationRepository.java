package org.example.waspapi.repository;

import java.util.UUID;
import org.example.waspapi.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

  Page<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(UUID userId, Pageable pageable);

  long countByUserIdAndIsReadFalse(UUID userId);

  boolean existsByUserIdAndTypeAndReferenceIdAndIsReadFalse(
      UUID userId, String type, UUID referenceId);

  @Modifying
  @Query(
      "UPDATE Notification n SET n.isRead = true"
          + " WHERE n.user.id = :userId AND n.type = :type AND n.referenceId = :referenceId"
          + " AND n.isRead = false")
  void markAsReadByUserIdAndTypeAndReferenceId(
      @Param("userId") UUID userId,
      @Param("type") String type,
      @Param("referenceId") UUID referenceId);

  @Modifying
  @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
  void markAllAsReadByUserId(@Param("userId") UUID userId);
}
