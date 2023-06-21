package checkout;

import java.time.LocalDate;
import java.time.LocalDateTime;

import main.Input;
import utility.CalendarUtils;
import utility.Font;
import utility.Screen;

public class OrderPlacedPage {
  // Display the order placed UI
  public static void showOrderPlacedUI() {
    Screen.clear();
    // The minimum and maximum arrival time is about 20 to 30 minutes.
    int orderId = Order.getCurrentOrder().getId();
    LocalDateTime orderTime = Order.getCurrentOrder().getOrderTime();
    LocalDate deliveryDate = Order.getCurrentOrder().getDeliveryDate();
    System.out.println("              +-----------------------------------+");
    System.out.println("              | " + Font.getStr(Font.ANSI_GREEN, "Your Order is Placed Successfully") + " |");
    System.out.println("              +-----------------------------------+");
    System.out.println();
    System.out.println("+--------------------------------------------------------------+");
    System.out.println("|       We will deliver your order on the date specified       |");
    System.out.println("|--------------------------------------------------------------|");
    System.out.println("| Order ID                : " + String.format("%-34s", orderId) + " |");
    System.out.println("| Order time              : " + String.format("%-34s", orderTime.format(CalendarUtils.DATE_TIME_FORMATTER)) + " |");
    System.out.println("| Scheduled delivery date : " + String.format("%-34s", deliveryDate.toString()) + " |");
    System.out.println("+--------------------------------------------------------------+");
    System.out.print("\nPress enter to go to main menu...");
    Input.SCANNER.nextLine();
  }
}