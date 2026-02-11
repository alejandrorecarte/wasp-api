package org.example.waspapi.repository;

import java.util.UUID;
import org.example.waspapi.model.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameRepository extends JpaRepository<Game, UUID> {

  Page<Game> findByIsPublicTrueAndIsDeletedFalse(Pageable pageable);

  @Query(
      "SELECT g FROM Game g WHERE g.isPublic = true AND g.isDeleted = false"
          + " AND (:name IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%')))"
          + " AND (:themeName IS NULL OR LOWER(g.theme.name) LIKE LOWER(CONCAT('%', :themeName, '%')))")
  Page<Game> findPublicGamesWithFilters(
      @Param("name") String name, @Param("themeName") String themeName, Pageable pageable);
}
