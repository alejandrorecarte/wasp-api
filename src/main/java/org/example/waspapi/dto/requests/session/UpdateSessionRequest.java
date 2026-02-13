package org.example.waspapi.dto.requests.session;

import java.time.LocalDateTime;

public class UpdateSessionRequest {

  private String name;

  private Boolean isPresential;

  private LocalDateTime datetime;

  private String place;

  private String observations;

  public UpdateSessionRequest() {}

  public UpdateSessionRequest(
      String name,
      Boolean isPresential,
      LocalDateTime datetime,
      String place,
      String observations) {
    this.name = name;
    this.isPresential = isPresential;
    this.datetime = datetime;
    this.place = place;
    this.observations = observations;
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
}
