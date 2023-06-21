package utility;

public class Font {
  // Regular
  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_BLACK = "\u001B[30m";
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_YELLOW = "\u001B[33m";
  public static final String ANSI_BLUE = "\u001B[34m";
  public static final String ANSI_PURPLE = "\u001B[35m";
  public static final String ANSI_CYAN = "\u001B[36m";
  public static final String ANSI_WHITE = "\u001B[37m";

  // Bold High Intensity
  public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
  public static final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
  public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
  public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
  public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
  public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
  public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";  // CYAN
  public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

  public static void print(String color, String message) {
    System.out.println(color + message + ANSI_RESET);
  }

  public static String getStr(String color, String message) {
    return color + message + ANSI_RESET;
  }

  public static void print(String ch, int repeat) {
    String line = "";
    for (int i = 0; i < repeat; i++) {
      line += ch;
    }
    System.out.println(line);
  }
}
