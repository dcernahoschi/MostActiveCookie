package com.quantcast.cookieprocess;

import java.io.File;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AppTest {

  @Test
  public void testApp() {
    var app = new App();

    String testsUserDir = System.getProperty("user.dir");
    String inputFile = testsUserDir + File.separator + "src" + File.separator +
        "test" + File.separator + "resources" + File.separator + "cookie_log_1.csv";
    var outputSink = mock(OutputMessageSink.class);

    app.run(new String[]{"-d", "2018-12-09", "-f", inputFile}, outputSink);

    verify(outputSink, times(1)).write("AtY0laUfhglK3lC7");
  }
}