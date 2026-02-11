package org.example.waspapi.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class SubscriptionId implements Serializable {

  private UUID user;
  private UUID game;

  public SubscriptionId() {}

  public SubscriptionId(UUID user, UUID game) {
    this.user = user;
    this.game = game;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SubscriptionId that = (SubscriptionId) o;
    return Objects.equals(user, that.user) && Objects.equals(game, that.game);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, game);
  }
}
