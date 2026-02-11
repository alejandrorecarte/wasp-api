package org.example.waspapi.repository;

import java.util.List;
import java.util.UUID;
import org.example.waspapi.model.JoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JoinRequestRepository extends JpaRepository<JoinRequest, UUID> {

  List<JoinRequest> findByGameIdAndStatus(UUID gameId, String status);

  boolean existsByUserIdAndGameId(UUID userId, UUID gameId);
}
