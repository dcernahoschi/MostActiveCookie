package com.quantcast.cookieprocess;

public class App {

  void run(String[] args, OutputMessageSink outputSink) {
    var appParamToValue = AppParams.from(args, outputSink);
    var command = CommandFactory.createCommand(appParamToValue, outputSink);
    command.run();
  }

  public static void main(String[] args) {
    try {
      var outputSink = new ConsoleOutputSink();
      new App().run(args, outputSink);
    } catch (IllegalAppArgumentException allReadyDealtWith) {
    } catch (Throwable t) {
      System.err.printf("Unexpected err %s%n", t.getMessage());
      t.printStackTrace(System.err);
    }
  }
}