package com.quantcast.cookieprocess;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MostActiveCookieCommandTest {

  @ParameterizedTest
  @MethodSource("testNormalCaseProvider")
  public void testWithGoodInput(LocalDate fromDate, String testFile, List<String> expectedResults) throws URISyntaxException {
    var inputFilePath = Paths.get(ClassLoader.getSystemResource(testFile).toURI());
    var outputSink = mock(OutputMessageSink.class);

    new MostActiveCookieCommand(fromDate, inputFilePath, outputSink).run();

    expectedResults.forEach(result -> verify(outputSink, times(1)).write(result));
  }

  private static Stream<Arguments> testNormalCaseProvider() {
    return Stream.of(
        Arguments.of(LocalDate.of(2018, 12, 9), "cookie_log_1.csv", List.of("AtY0laUfhglK3lC7")),
        Arguments.of(LocalDate.of(2018, 12, 9), "cookie_log_2.csv", List.of("AtY0laUfhglK3lC7", "SAZuXPGUrfbcn5UA")),
        Arguments.of(LocalDate.of(2018, 12, 1), "cookie_log_1.csv",
            List.of("No active cookie for the provided date 2018-12-01"))
    );
  }

  @Test
  public void testWithNotExistingFilePath() {
    var fromDate = LocalDate.now();
    var inputFilePath = Path.of("/Users/abc.txt");
    var outputSink = mock(OutputMessageSink.class);

    assertThrows(RuntimeException.class, () -> new MostActiveCookieCommand(fromDate, inputFilePath, outputSink).run());
    verify(outputSink, times(1)).write("The provided input file /Users/abc.txt could not be found or read.");
  }

  @Test
  public void testWithAFileContainingSomeBadInput() throws URISyntaxException {
    var fromDate = LocalDate.of(2018, 12, 9);
    var inputFilePath = Paths.get(ClassLoader.getSystemResource("cookie_log_3.csv").toURI());
    var outputSink = mock(OutputMessageSink.class);

    new MostActiveCookieCommand(fromDate, inputFilePath, outputSink).run();

    verify(outputSink, times(1)).write("Invalid cookie data, could not parse cookie id and date from " +
        "input SAZuXPGUrfbcn5UA2018-12-09T12:13:00+00:00. Cookie will be ignored. Detail: Could not parse cookie id and date from " +
        "input SAZuXPGUrfbcn5UA2018-12-09T12:13:00+00:00");
    verify(outputSink, times(1)).write("AtY0laUfhglK3lC7");
  }
}