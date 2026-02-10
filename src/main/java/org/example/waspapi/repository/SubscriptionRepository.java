package org.example.waspapi.repository;

import java.util.List;
import java.util.UUID;
import org.example.waspapi.model.Subscription;
import org.example.waspapi.model.SubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {

  boolean existsByUserEmailAndGameId(String userEmail, UUID gameId);

  boolean existsByUserEmailAndGameIdAndIsAdminTrue(String userEmail, UUID gameId);

  List<Subscription> findByUserEmail(String userEmail);
}
