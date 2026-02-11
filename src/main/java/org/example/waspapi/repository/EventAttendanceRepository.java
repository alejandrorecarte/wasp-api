package org.example.waspapi.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.waspapi.model.EventAttendance;
import org.example.waspapi.model.EventAttendanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventAttendanceRepository
    extends JpaRepository<EventAttendance, EventAttendanceId> {

  List<EventAttendance> findByEventId(UUID eventId);

  boolean existsByUserIdAndEventId(UUID userId, UUID eventId);

  Optional<EventAttendance> findByUserIdAndEventId(UUID userId, UUID eventId);
}
