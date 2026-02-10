package org.example.waspapi.repository;

import java.util.UUID;
import org.example.waspapi.model.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, UUID> {

  Page<Game> findByIsPublicTrueAndIsDeletedFalse(Pageable pageable);
}
