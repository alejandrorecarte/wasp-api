package org.example.waspapi.repository;

import java.util.List;
import java.util.UUID;
import org.example.waspapi.model.CharacterSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacterSheetRepository extends JpaRepository<CharacterSheet, UUID> {

  List<CharacterSheet> findByGameIdOrderByCreatedAtDesc(UUID gameId);

  List<CharacterSheet> findByGameIdAndUserId(UUID gameId, UUID userId);
}
