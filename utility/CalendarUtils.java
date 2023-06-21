package utility;

import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class CalendarUtils {
  public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); 
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd"); 
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
  public static boolean isLeapYear(int year) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
  }
}