package org.example.waspapi.model;

import javax.persistence.*;

@Entity
@Table(name = "users_sessions")
@IdClass(SessionAttendanceId.class)
public class SessionAttendance {

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "session_id", referencedColumnName = "session_id")
  private Session session;

  @Column(name = "confirm_assist")
  private Boolean confirmAssist;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Session getSession() {
    return session;
  }

  public void setSession(Session session) {
    this.session = session;
  }

  public Boolean getConfirmAssist() {
    return confirmAssist;
  }

  public void setConfirmAssist(Boolean confirmAssist) {
    this.confirmAssist = confirmAssist;
  }
}
