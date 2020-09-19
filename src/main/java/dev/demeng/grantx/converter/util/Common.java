package dev.demeng.grantx.converter.util;

public class Common {

  public static void report(Throwable t, String description, boolean exit) {

    if (t != null) {
      t.printStackTrace();
    }

    System.err.println(description);

    if (exit) {
      System.exit(1);
    }
  }
}
