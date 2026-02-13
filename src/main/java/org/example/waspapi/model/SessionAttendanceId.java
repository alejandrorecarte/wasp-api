package org.example.waspapi.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class SessionAttendanceId implements Serializable {

  private UUID user;
  private UUID session;

  public SessionAttendanceId() {}

  public SessionAttendanceId(UUID user, UUID session) {
    this.user = user;
    this.session = session;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SessionAttendanceId that = (SessionAttendanceId) o;
    return Objects.equals(user, that.user) && Objects.equals(session, that.session);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, session);
  }
}
