package org.example.waspapi.model;

import javax.persistence.*;

@Entity
@Table(name = "users_events")
@IdClass(EventAttendanceId.class)
public class EventAttendance {

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id", referencedColumnName = "event_id")
  private Event event;

  @Column(name = "confirm_assist")
  private Boolean confirmAssist;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Event getEvent() {
    return event;
  }

  public void setEvent(Event event) {
    this.event = event;
  }

  public Boolean getConfirmAssist() {
    return confirmAssist;
  }

  public void setConfirmAssist(Boolean confirmAssist) {
    this.confirmAssist = confirmAssist;
  }
}
