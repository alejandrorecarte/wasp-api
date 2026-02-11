package org.example.waspapi.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.waspapi.model.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {

  List<FriendRequest> findByReceiverIdAndStatus(UUID receiverId, String status);

  @Query(
      "SELECT fr FROM FriendRequest fr WHERE fr.status = :status"
          + " AND (fr.sender.id = :userId OR fr.receiver.id = :userId)")
  List<FriendRequest> findByUserIdAndStatus(
      @Param("userId") UUID userId, @Param("status") String status);

  Optional<FriendRequest> findBySenderIdAndReceiverId(UUID senderId, UUID receiverId);

  boolean existsBySenderIdAndReceiverId(UUID senderId, UUID receiverId);
}
