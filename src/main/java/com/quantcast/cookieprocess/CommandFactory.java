package com.quantcast.cookieprocess;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

/**
 * The command factory constructs commands that the application can run, based on the provided app parameters
 */
public class CommandFactory {

  public static Runnable createCommand(Map<Class<?>, Object> paramNameToValue, OutputMessageSink outputSink) {

    var inputFile = paramNameToValue.get(AppParams.FILE_PARAM_CLASS);
    Path inputFilePath = AppParams.FILE_PARAM_CLASS.cast(inputFile);

    var fromDate = paramNameToValue.get(AppParams.DATE_PARAM_CLASS);
    LocalDate fromLocalDate = AppParams.DATE_PARAM_CLASS.cast(fromDate);

    return new MostActiveCookieCommand(fromLocalDate, inputFilePath, outputSink);
  }
}