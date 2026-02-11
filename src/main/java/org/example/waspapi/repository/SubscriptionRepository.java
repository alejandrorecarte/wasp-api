package org.example.waspapi.repository;

import java.util.List;
import java.util.UUID;
import org.example.waspapi.model.Subscription;
import org.example.waspapi.model.SubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {

  boolean existsByUserIdAndGameId(UUID userId, UUID gameId);

  boolean existsByUserIdAndGameIdAndIsAdminTrue(UUID userId, UUID gameId);

  List<Subscription> findByUserId(UUID userId);

  long countByGameId(UUID gameId);

  List<Subscription> findByGameId(UUID gameId);
}
