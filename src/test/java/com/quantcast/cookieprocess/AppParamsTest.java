package com.quantcast.cookieprocess;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AppParamsTest {

  @Test
  public void testMethodFromWithGoodArgs() {
    var file = "/tmp/cookie_log_1.csv";
    var date = "2018-12-09";
    String[] args = {"-f", file, "-d", date};

    var expectedAppParamToValue = Map.of(
        AppParams.FILE_PARAM_CLASS, Path.of("/tmp/cookie_log_1.csv"),
        AppParams.DATE_PARAM_CLASS, LocalDate.of(2018, 12, 9));
    var sink = mock(OutputMessageSink.class);

    assertEquals(expectedAppParamToValue, AppParams.from(args, sink));
  }

  @Test
  public void testMethodFromWithUnrecognizedArgs() {
    var file = "/tmp/cookie_log_1.csv";
    var date = "2018-12-09";
    String[] args = {"-e", file, "-t", date};

    var sink = mock(OutputMessageSink.class);
    assertThrows(IllegalAppArgumentException.class, () -> AppParams.from(args, sink));

    verify(sink, times(1)).write("Could not parse input args: Unrecognized option: -e");
    verify(sink, times(1)).write("usage: $ ./macookie -f file -d date\n-d,--date <arg>Cookie date in UTC time, e.g.: 2018-12-09\n-f,--file <arg>Input file\n");
  }

  @Test
  public void testConvertInputFileToPathWithInconvertiblePath() {
    var sink = mock(OutputMessageSink.class);
    var file = "some\u0000path\u0000with\u0000strange\u0000characters";
    assertThrows(IllegalAppArgumentException.class, () -> AppParams.convertInputFileParam(file, sink));
    verify(sink, times(1)).write(
        String.format("The provided input file %s could not be converted to a file path", file));
  }

  @Test
  public void testConvertFromDateToLocalDateWithBadArg() {
    var sink = mock(OutputMessageSink.class);
    var date = "2018-12-99";
    assertThrows(IllegalAppArgumentException.class, () -> AppParams.convertFromDateParam(date, sink));
    verify(sink, times(1)).write(
        String.format("Could not parse the provided date %s. It should be in a format like, e.g.: 2018-12-24", date));
  }
}