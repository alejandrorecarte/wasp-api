package org.example.waspapi.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class EventAttendanceId implements Serializable {

  private UUID user;
  private UUID event;

  public EventAttendanceId() {}

  public EventAttendanceId(UUID user, UUID event) {
    this.user = user;
    this.event = event;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EventAttendanceId that = (EventAttendanceId) o;
    return Objects.equals(user, that.user) && Objects.equals(event, that.event);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, event);
  }
}
