package org.example.waspapi.repository;

import java.util.List;
import java.util.UUID;
import org.example.waspapi.model.PrivateMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrivateMessageRepository extends JpaRepository<PrivateMessage, UUID> {

  @Query(
      "SELECT pm FROM PrivateMessage pm"
          + " WHERE (pm.sender.id = :userA AND pm.receiver.id = :userB)"
          + " OR (pm.sender.id = :userB AND pm.receiver.id = :userA)"
          + " ORDER BY pm.createdAt DESC")
  Page<PrivateMessage> findConversation(
      @Param("userA") UUID userA, @Param("userB") UUID userB, Pageable pageable);

  @Query(
      value =
          "SELECT DISTINCT ON (LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id))"
              + " * FROM private_messages"
              + " WHERE sender_id = :userId OR receiver_id = :userId"
              + " ORDER BY LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id),"
              + " created_at DESC",
      nativeQuery = true)
  List<PrivateMessage> findLatestMessagePerConversation(@Param("userId") UUID userId);
}
