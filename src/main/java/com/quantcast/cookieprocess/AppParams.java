package com.quantcast.cookieprocess;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * A parser object for the arguments provided to the tool startup
 */
public class AppParams {

  public static final String FILE_PARAM = "file";
  public static final Class<Path> FILE_PARAM_CLASS = Path.class;

  public static final String DATE_PARAM = "date";
  public static final Class<LocalDate> DATE_PARAM_CLASS = LocalDate.class;

  private static final Options options = new Options();

  static {
    var dateOption = new Option("d", DATE_PARAM, true, "Cookie date in UTC time, e.g.: 2018-12-09");
    dateOption.setRequired(true);
    options.addOption(dateOption);

    var fileOption = new Option("f", FILE_PARAM, true, "Input file");
    fileOption.setRequired(true);
    options.addOption(fileOption);
  }

  public static Map<Class<?>, Object> from(String[] args, OutputMessageSink outputSink) {
    var parser = new DefaultParser();
    var formatter = new HelpFormatter();

    try {
      var commandLine = parser.parse(options, args);

      Map<Class<?>, Object> paramNameToValue = new HashMap<>();
      String dateValue = commandLine.getOptionValue(DATE_PARAM);
      paramNameToValue.put(DATE_PARAM_CLASS, convertFromDateParam(dateValue, outputSink));

      String inputFileValue = commandLine.getOptionValue(FILE_PARAM);
      paramNameToValue.put(FILE_PARAM_CLASS, convertInputFileParam(inputFileValue, outputSink));
      return paramNameToValue;
    } catch (ParseException e) {
      outputSink.write(String.format("Could not parse input args: %s", e.getMessage()));

      var stringWriter = new StringWriter();
      var writer = new PrintWriter(stringWriter);
      formatter.printHelp(writer, 100,  "$ ./macookie -f file -d date",
          "", options, 0, 0, "");
      outputSink.write(stringWriter.toString());
      throw new IllegalAppArgumentException(e);
    }
  }

  static Path convertInputFileParam(String file, OutputMessageSink outputSink) {
    try {
      return Path.of(file);
    } catch (InvalidPathException e) {
      outputSink.write(String.format("The provided input file %s could not be converted to a file path", file));
      throw new IllegalAppArgumentException(e);
    }
  }

  static LocalDate convertFromDateParam(String fromDate, OutputMessageSink outputSink) {
    try {
      return DateTimeFormatter.ISO_LOCAL_DATE.parse(fromDate, LocalDate::from);
    } catch (DateTimeParseException e) {
      outputSink.write(
          String.format("Could not parse the provided date %s. It should be in a format like, e.g.: 2018-12-24", fromDate));
      throw new IllegalAppArgumentException(e);
    }
  }
}
