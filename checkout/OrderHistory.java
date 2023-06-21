package checkout;

import java.util.InputMismatchException;

import account.AuthService;
import account.Customer;
import adt.ListInterface;
import adt.MapInterface;
import main.Input;
import ordermenu.Cart;
import ordermenu.CartItem;
import utility.CalendarUtils;
import utility.Font;
import utility.Screen;

public class OrderHistory {  
  public static void showOrderHistory() {
    Customer customer = (Customer) AuthService.getLoggedInAccount();
    int choice;
    do {
      Screen.clear();
      System.out.println("===============================================================");
      System.out.println("|                        Order History                        |");
      System.out.println("===============================================================");
      // Display a list of previous orders
      printOrders(customer.getOrders());
      System.out.println("===============================================================");
      if (customer.getOrders().isEmpty()) {
        System.out.print("\nPress enter to continue...");
        Input.SCANNER.nextLine();
        break;
      } else {
        System.out.printf("%48s\n", "+-------------------------------+");
        System.out.printf("%48s\n", "|       ENTER YOUR OPTION       |");
        System.out.printf("%48s\n", "|-------------------------------|");
        System.out.printf("%48s\n", "| 1. View more details          |");
        System.out.printf("%48s\n", "|-------------------------------|");
        System.out.printf("%48s\n", "| 2. Go back                    |");
        System.out.printf("%48s\n", "+-------------------------------+");

        choice = Input.getMenuChoice(2);

        switch (choice) {
          case 1:
            // View more information
            viewMoreDetails(customer);
            break;
          default:
            break;
        }
      }
    } while (choice != 2);
  }

  private static void viewMoreDetails(Customer customer) {
    // When the user choose to view more details about a particular order,
    // we will let them choose the order ID of the order they want to view more about.
    int orderId = 0;
    boolean validOrderId = false;
    do {
      try {
        System.out.print("       > Enter the order ID of order to view details: ");
        orderId = Input.SCANNER.nextInt();
        validOrderId = true;
      } catch (InputMismatchException inputMismatchException) {
        Font.print(Font.ANSI_RED, "<<< Invalid order ID, please enter integers only. >>>");
      }
      Input.SCANNER.nextLine();
    } while (!validOrderId);

    // Find an order from the customer's orders based on the orderId
    Order order = customer.getOrders().get(orderId);

    if (order == null) {
      // Order does not exist
      Font.print(Font.ANSI_RED, "                <<< Order does not exists. >>>");
      System.out.print("                 Press enter to continue...");
      Input.SCANNER.nextLine();
    } else {
      // Order exists, and continue to display the full details of that order
      displayOrderDetails(order);
    }
  }

  // Display a list of orders belong to this customer
  private static void printOrders(MapInterface<Integer, Order> orders) {
    if (orders.isEmpty()) {
      System.out.println("| No orders                                                   |");
    } else {
      int i = 0;

      for (Order order : orders.values()) {
        System.out.format("|           Order ID : %-38s |\n", order.getId());
        System.out.format("|     Payment Method : %-38s |\n", order.getPaymentMethod());
        System.out.format("|        Amount paid : RM%-36s |\n",
            String.format("%.2f", order.getFinalPrice()));
        System.out.format("|       Order status : %-38s |\n", order.getStatus());
        System.out.format("|         Order time : %-38s |\n",
            order.getOrderTime().format(CalendarUtils.DATE_TIME_FORMATTER));
        if (i != orders.size() - 1) {
          System.out.println("|-------------------------------------------------------------|");
        }
        i++;
      }
    }
  }

  // Display the order details UI
  private static void displayOrderDetails(Order order) {
    String voucherApplied = order.getVoucherApplied() == null ? "None" : order.getVoucherApplied();

    Screen.clear();
    System.out.println("===============================================================");
    System.out.println("|                        Order Details                        |");
    System.out.println("===============================================================");
    System.out.printf("| Order ID : %-13s | Order Time : %-19s | \n", order.getId(),
        order.getOrderTime().format(CalendarUtils.DATE_TIME_FORMATTER));
    System.out.println("|-------------------------------------------------------------|");
    System.out.printf("| Payment Method   : %-40s | \n", order.getPaymentMethod());
    System.out.printf("| Points Earned    : %-40s | \n", order.getPointsEarned());
    System.out.printf("| Delivery Address : %-40s | \n", order.getDeliveryAddress().getAddressLine());
    System.out.printf("|                    %-5s %-34s | \n", order.getDeliveryAddress().getPostcode(),
        order.getDeliveryAddress().getCity());
    System.out.printf("|                    %-40s | \n", order.getDeliveryAddress().getState());
    System.out.println("|-------------------------------------------------------------|");
    System.out.printf("| Voucher Applied  : %-7s | Voucher Discount : - RM%-7s | \n", voucherApplied,
        String.format("%.2f", order.getVoucherDiscount()));
    System.out.printf("| Points Used      : %-7s | Points Discount  : - RM%-7s | \n", order.getPointsUsed(),
        String.format("%.2f", (double) order.getPointsUsed() / 100));
    System.out.printf("|                            | Amount Paid      : RM%-9s | \n",
        String.format("%.2f", order.getFinalPrice()));
    System.out.println("|-------------------------------------------------------------|");
    ListInterface<CartItem> cartItems = order.getItems();
    Cart.displayCartItems(cartItems);
    System.out.println("===============================================================");
    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();
  }
}