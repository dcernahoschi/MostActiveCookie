package com.quantcast.cookieprocess;

public class ConsoleOutputSink implements OutputMessageSink {

  @Override
  public void write(String message) {
    System.out.println(message);
  }
}
