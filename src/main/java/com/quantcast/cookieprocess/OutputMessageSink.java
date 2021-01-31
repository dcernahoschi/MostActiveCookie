package com.quantcast.cookieprocess;

/**
 * Interface that abstracts where an application output message is written
 */
public interface OutputMessageSink {
  void write(String message);
}
