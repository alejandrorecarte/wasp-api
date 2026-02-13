package org.example.waspapi.dto.responses.session;

import java.time.LocalDateTime;
import java.util.UUID;

public class GetSessionResponse {

  private UUID id;
  private String name;
  private Boolean isPresential;
  private LocalDateTime datetime;
  private String place;
  private String observations;
  private UUID gameId;
  private String gameName;
  private long confirmedCount;

  public GetSessionResponse() {}

  public GetSessionResponse(
      UUID id,
      String name,
      Boolean isPresential,
      LocalDateTime datetime,
      String place,
      String observations,
      UUID gameId,
      String gameName,
      long confirmedCount) {
    this.id = id;
    this.name = name;
    this.isPresential = isPresential;
    this.datetime = datetime;
    this.place = place;
    this.observations = observations;
    this.gameId = gameId;
    this.gameName = gameName;
    this.confirmedCount = confirmedCount;
  }

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

  public UUID getGameId() {
    return gameId;
  }

  public void setGameId(UUID gameId) {
    this.gameId = gameId;
  }

  public String getGameName() {
    return gameName;
  }

  public void setGameName(String gameName) {
    this.gameName = gameName;
  }

  public long getConfirmedCount() {
    return confirmedCount;
  }

  public void setConfirmedCount(long confirmedCount) {
    this.confirmedCount = confirmedCount;
  }
}
