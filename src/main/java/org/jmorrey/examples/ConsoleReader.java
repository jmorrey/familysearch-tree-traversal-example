package org.jmorrey.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Utility class for reading passwords from console.  Attempts first to use the System.console (which rarely seems to
 * work).  If that doesn't work, it falls back on a System.in reader.
 */
class ConsoleReader {

  public char[] readPassword(String format, Object... args)
          throws IOException {
    if (System.console() != null)
      return System.console().readPassword(format, args);
    return this.readLine(format, args).toCharArray();
  }

  public String readLine(String format, Object... args) throws IOException {
    if (System.console() != null) {
      return System.console().readLine(format, args);
    }
    System.out.print(String.format(format, args));
    BufferedReader reader = new BufferedReader(new InputStreamReader(
            System.in));
    return reader.readLine();
  }
}
