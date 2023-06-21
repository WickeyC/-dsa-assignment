package utility;

public class StringUtils {
  public static String center(int width, String s) {
    return String.format("%-" + width + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
  }
}