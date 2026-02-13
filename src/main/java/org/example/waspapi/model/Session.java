package org.example.waspapi.model;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.*;

@Entity
@Table(name = "sessions", schema = "public")
public class Session {

  @Id
  @GeneratedValue
  @Column(name = "session_id", updatable = false, nullable = false)
  private UUID id;

  @Column private String name;

  @Column(name = "is_presential")
  private Boolean isPresential;

  @Column(columnDefinition = "timestamptz")
  private LocalDateTime datetime;

  @Column(columnDefinition = "text")
  private String place;

  @Column(columnDefinition = "text")
  private String observations;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_id", foreignKey = @ForeignKey(name = "sessions_game_id_fkey"))
  private Game game;

  public Session() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getIsPresential() {
    return isPresential;
  }

  public void setIsPresential(Boolean isPresential) {
    this.isPresential = isPresential;
  }

  public LocalDateTime getDatetime() {
    return datetime;
  }

  public void setDatetime(LocalDateTime datetime) {
    this.datetime = datetime;
  }

  public String getPlace() {
    return place;
  }

  public void setPlace(String place) {
    this.place = place;
  }

  public String getObservations() {
    return observations;
  }

  public void setObservations(String observations) {
    this.observations = observations;
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }
}
