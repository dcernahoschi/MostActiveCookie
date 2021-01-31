package com.quantcast.cookieprocess;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The command that search for the cookie(s) that appear most times in the provided log file in the given date
 */
public class MostActiveCookieCommand implements Runnable {

  private static final int NUMBER_OF_ROWS_TO_SKIP = 1;
  private static final ZoneOffset DEFAULT_TIME_ZONE = ZoneOffset.UTC;

  private final LocalDate fromDate;
  private final Path inputFilePath;
  private final ZoneOffset timeZone = DEFAULT_TIME_ZONE;

  private final OutputMessageSink outputSink;

  public MostActiveCookieCommand(LocalDate fromDate, Path inputFilePath, OutputMessageSink outputSink) {
    this.fromDate = fromDate;
    this.inputFilePath = inputFilePath;
    this.outputSink = outputSink;
  }

  @Override
  public void run() {
    var fromInstant = fromDate.atStartOfDay().toInstant(timeZone);

    Map<String, Long> cookieToOccurrenceInAGivenDate;
    try (Stream<String> lines = Files.lines(inputFilePath)) {
      cookieToOccurrenceInAGivenDate = lines
          .skip(NUMBER_OF_ROWS_TO_SKIP)
          .map(this::tryParseCookie)
          .flatMap(Optional::stream)
          .filter(cookieInfo -> cookieInfo.isWithin24hAfter(fromInstant))
          .takeWhile(cookieInfo -> cookieInfo.isNotFromSomeDateBefore(fromInstant))
          .collect(Collectors.groupingBy(CookieInfo::getId, Collectors.counting()));
    } catch (IOException|UncheckedIOException e) {
      outputSink.write(String.format("The provided input file %s could not be found or read.", inputFilePath));
      throw new IllegalAppArgumentException(e);
    }

    var cookiesSortedByActivity = cookieToOccurrenceInAGivenDate.entrySet().stream()
        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
        .collect(Collectors.toList());

    outputMostActiveCookiesIfAny(cookiesSortedByActivity);
  }

  private void outputMostActiveCookiesIfAny(List<Map.Entry<String, Long>> cookiesSortedByActivity) {
    if (cookiesSortedByActivity.isEmpty()) {
      outputSink.write(String.format("No active cookie for the provided date %s", fromDate));
      return;
    }

    cookiesSortedByActivity.stream()
        .takeWhile(entry -> entry.getValue().equals(cookiesSortedByActivity.get(0).getValue()))
        .forEach(entry -> outputSink.write(entry.getKey()));
  }

  private Optional<CookieInfo> tryParseCookie(String rawInput) {
    try {
      return Optional.of(CookieInfo.from(rawInput));
    } catch (IllegalArgumentException e) {
      outputSink.write(String.format("Invalid cookie data, could not parse cookie id and date from input %s. " +
          "Cookie will be ignored. Detail: %s", rawInput, e.getMessage()));
      return Optional.empty();
    }
  }
}
