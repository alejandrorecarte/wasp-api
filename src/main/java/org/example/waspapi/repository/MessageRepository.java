package org.example.waspapi.repository;

import java.util.UUID;
import org.example.waspapi.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  Page<Message> findByGameIdOrderByCreatedAtDesc(UUID gameId, Pageable pageable);
}
