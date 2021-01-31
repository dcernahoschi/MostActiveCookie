package com.quantcast.cookieprocess;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CookieInfoTest {

  @Test
  public void testCreateFromGoodInput() {
    Instant instant = LocalDateTime.of(2018, 12, 7, 23, 30, 0)
        .atZone(ZoneOffset.UTC).toInstant();
    String rawInput = "abc,2018-12-07T23:30:00+00:00";
    CookieInfo cookieInfo = CookieInfo.from(rawInput);

    assertEquals(cookieInfo.getId(), "abc");
    assertEquals(cookieInfo.getTimestamp(), instant);
  }

  @Test
  public void testCreateFromInputWithoutSeparator() {
    String rawInput = "abc2018-12-07T23:30:00+00:00";
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> CookieInfo.from(rawInput));

    assertEquals("Could not parse cookie id and date from input " + rawInput, exception.getMessage());
  }

  @Test
  public void testCreateFromInputWithBadDate() {
    String rawInput = "abc,2018-12-0777T23:30:00+00:00";
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> CookieInfo.from(rawInput));

    assertEquals("Could not parse cookie date from input " + rawInput, exception.getMessage());
  }

  @ParameterizedTest
  @MethodSource("testIsWithin24hAfterProvider")
  public void testIsWithin24hAfter(Instant cookieFromDate, Instant inputDate, Boolean testResult) {
    CookieInfo cookieInfo = new CookieInfo("abc", cookieFromDate);
    assertEquals(testResult, cookieInfo.isWithin24hAfter(inputDate));
  }

  private static Stream<Arguments> testIsWithin24hAfterProvider() {
    Instant inputDate = LocalDate.of(2018, 12, 7).atStartOfDay().toInstant(ZoneOffset.UTC);

    return Stream.of(
        Arguments.of(
            LocalDateTime.of(2018, 12, 7, 23, 59, 59).atZone(ZoneOffset.UTC).toInstant(),
            inputDate,
            true
        ),
        Arguments.of(
            LocalDateTime.of(2018, 12, 7, 0, 0, 0).atZone(ZoneOffset.UTC).toInstant(),
            inputDate,
            true
        ),
        Arguments.of(
            LocalDateTime.of(2018, 12, 8, 0, 0, 0).atZone(ZoneOffset.UTC).toInstant(),
            inputDate,
            false
        ),
        Arguments.of(
            LocalDateTime.of(2018, 12, 6, 23, 59, 59).atZone(ZoneOffset.UTC).toInstant(),
            inputDate,
            false
        )
    );
  }

  @ParameterizedTest
  @MethodSource("testIsNotFromSameDateBeforeProvider")
  public void testIsNotFromSameDateBefore(Instant cookieFromDate, Instant inputDate, Boolean testResult) {
    CookieInfo cookieInfo = new CookieInfo("abc", cookieFromDate);
    assertEquals(testResult, cookieInfo.isNotFromSomeDateBefore(inputDate));
  }

  private static Stream<Arguments> testIsNotFromSameDateBeforeProvider() {
    Instant inputDate = LocalDate.of(2018, 12, 7).atStartOfDay().toInstant(ZoneOffset.UTC);

    return Stream.of(
        Arguments.of(
            LocalDateTime.of(2018, 12, 7, 23, 59, 59).atZone(ZoneOffset.UTC).toInstant(),
            inputDate,
            true
        ),
        Arguments.of(
            LocalDateTime.of(2018, 12, 7, 0, 0, 0).atZone(ZoneOffset.UTC).toInstant(),
            inputDate,
            true
        ),
        Arguments.of(
            LocalDateTime.of(2018, 12, 5, 21, 22, 0).atZone(ZoneOffset.UTC).toInstant(),
            inputDate,
            false
        ),
        Arguments.of(
            LocalDateTime.of(2018, 12, 6, 23, 59, 59).atZone(ZoneOffset.UTC).toInstant(),
            inputDate,
            false
        )
    );
  }
}
