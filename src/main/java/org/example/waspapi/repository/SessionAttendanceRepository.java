package org.example.waspapi.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.waspapi.model.SessionAttendance;
import org.example.waspapi.model.SessionAttendanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionAttendanceRepository
    extends JpaRepository<SessionAttendance, SessionAttendanceId> {

  List<SessionAttendance> findBySessionId(UUID sessionId);

  boolean existsByUserIdAndSessionId(UUID userId, UUID sessionId);

  Optional<SessionAttendance> findByUserIdAndSessionId(UUID userId, UUID sessionId);
}
