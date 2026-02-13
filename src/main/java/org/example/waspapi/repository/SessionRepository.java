package org.example.waspapi.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.example.waspapi.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {

  List<Session> findByGameId(UUID gameId);

  @Query(
      "SELECT s FROM Session s JOIN FETCH s.game g "
          + "WHERE g.id IN :gameIds "
          + "AND s.datetime >= :start AND s.datetime < :end "
          + "ORDER BY s.datetime")
  List<Session> findByGameIdsAndDateRange(
      @Param("gameIds") List<UUID> gameIds,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);
}
