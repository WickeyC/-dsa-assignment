package checkout;

import java.time.LocalDate;
import java.util.InputMismatchException;

import main.Input;
import utility.CalendarUtils;
import utility.Font;
import utility.Screen;

public class DeliveryDatePage {
  public static void showDeliveryDateUI() {
    Screen.clear();
    System.out.println("==================================================");
    System.out.println("|              Choose Delivery Date              |");
    System.out.println("==================================================");

    System.out.println("\n<<< Please enter the delivery date below >>>");

    LocalDate deliveryDate = getDeliveryDate();

    Font.print(Font.ANSI_GREEN, "\n<<< Delivery date is selected successfully! >>>");

    // Update current order's delivery date
    Order.getCurrentOrder().setDeliveryDate(deliveryDate);

    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();

    // Go to Card Payment page
    CardPaymentPage.showCardPaymentUI();
  }

  public static LocalDate getDeliveryDate() {
    // Get year input
    int year = 0;
    boolean validYear = false;
    do {
      try {
        System.out.print("> Enter year: ");
        year = Input.SCANNER.nextInt();

        // Validate year input
        if (year < LocalDate.now().getYear() || year > LocalDate.now().getYear() + 1) {
          Font.print(Font.ANSI_RED, "<<< Invalid year, please enter again. >>>");
          validYear = false;
        } else {
          validYear = true;
        }
      } catch (InputMismatchException inputMismatchException) {
        Font.print(Font.ANSI_RED, "<<< Invalid year, please enter integers only. >>>");
      }
      Input.SCANNER.nextLine();
    } while (!validYear);

    // Get month input
    int month = 0;
    boolean validMonth = false;
    do {
      try {
        System.out.print("> Enter month: ");
        month = Input.SCANNER.nextInt();

        // Validate month input
        if (year == LocalDate.now().getYear() && month < LocalDate.now().getMonthValue()) {
          Font.print(Font.ANSI_RED, "<<< Invalid month, please enter again. >>>");
          validMonth = false;
        } else if (month < 1 || month > 12) {
          Font.print(Font.ANSI_RED, "<<< Invalid month, please enter again. >>>");
          validMonth = false;
        } else {
          validMonth = true;
        }
      } catch (InputMismatchException inputMismatchException) {
        Font.print(Font.ANSI_RED, "<<< Invalid month, please enter integers only. >>>");
      }
      Input.SCANNER.nextLine();
    } while (!validMonth);

    String monthStr = "";
    if (month >= 10) {
      monthStr = Integer.toString(month);
    } else {
      monthStr = "0" + Integer.toString(month);
    }

    // Get day input
    int day = 0;
    boolean validDay = false;
    do {
      try {
        System.out.print("> Enter day: ");
        day = Input.SCANNER.nextInt();

        // Validate day input
        if (year == LocalDate.now().getYear() && month == LocalDate.now().getMonthValue()
            && day <= LocalDate.now().getDayOfMonth()) {
          Font.print(Font.ANSI_RED, "<<< Invalid day, please enter again. >>>");
          validDay = false;
        } else if (day < 1 || day > 31) {
          Font.print(Font.ANSI_RED, "<<< Invalid day, please enter again. >>>");
          validDay = false;
        } else if ((month == 4 || month == 6 || month == 9 || month == 11) && day == 31) {
          Font.print(Font.ANSI_RED, "<<< Day must be less than or equal to 30, please enter again. >>>");
          validDay = false;
        } else if (month == 2 && CalendarUtils.isLeapYear(year) && day > 29) {
          Font.print(Font.ANSI_RED, "<<< Day must be less than or equal to 29, please enter again. >>>");
          validDay = false;
        } else if (month == 2 && !CalendarUtils.isLeapYear(year) && day > 28) {
          Font.print(Font.ANSI_RED, "<<< Day must be less than or equal to 28, please enter again. >>>");
          validDay = false;
        } else {
          validDay = true;
        }
      } catch (InputMismatchException inputMismatchException) {
        Font.print(Font.ANSI_RED, "<<< Invalid day, please enter integers only. >>>");
      }
      Input.SCANNER.nextLine();
    } while (!validDay);

    String dayStr = "";
    if (day >= 10) {
      dayStr = Integer.toString(day);
    } else {
      dayStr = "0" + Integer.toString(day);
    }

    return LocalDate.parse(year + "-" + monthStr + "-" + dayStr);
  }
}
