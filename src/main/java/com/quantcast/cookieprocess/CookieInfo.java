package com.quantcast.cookieprocess;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

class CookieInfo {
  private static final String COLUMN_SEPARATOR = ",";
  private static final Duration DURATION_OF_24H = Duration.ofHours(24);

  private final String id;
  private final Instant timestamp;

  public static CookieInfo from(String rawInput) {
    String[] idAndTimestamp = rawInput.split(COLUMN_SEPARATOR);
    if (idAndTimestamp.length >= 2) {
      try {
        var timestamp = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(idAndTimestamp[1], Instant::from);
        return new CookieInfo(idAndTimestamp[0], timestamp);
      } catch (DateTimeParseException e) {
        throw new IllegalArgumentException("Could not parse cookie date from input " + rawInput);
      }
    }
    throw new IllegalArgumentException("Could not parse cookie id and date from input " + rawInput);
  }

  CookieInfo(String id, Instant timestamp) {
    this.id = id;
    this.timestamp = timestamp;
  }

  public String getId() {
    return id;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public boolean isWithin24hAfter(Instant date) {
    var duration = Duration.between(date, this.timestamp);
    return duration.compareTo(DURATION_OF_24H) < 0 && !duration.isNegative();
  }

  public boolean isNotFromSomeDateBefore(Instant date) {
    return !Duration.between(date, this.timestamp).isNegative();
  }
}
