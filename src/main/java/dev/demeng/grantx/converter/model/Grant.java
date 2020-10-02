package dev.demeng.grantx.converter.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class Grant {

  @Getter private final int id;
  @Getter private final Status status;
  @Getter private final long time;
  @Getter private final UUID target;
  @Getter private final UUID issuer;
  @Getter private final UUID revoker;
  @Getter private final String rank;
  @Getter private final String server;
  @Getter private final String duration;
  @Getter private final String reason;
  @Getter private final long revokeTime;

  public enum Status {
    ACTIVE,
    EXPIRED,
    REVOKED
  }
}
