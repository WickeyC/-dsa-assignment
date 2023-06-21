package report;

import java.time.LocalDate;

import account.Admin;
import account.AdminRepository;
import account.AuthService;
import account.Customer;
import account.CustomerRepository;
import adt.ListInterface;
import adt.MapInterface;
import checkout.Order;
import checkout.OrderRepository;
import food.FoodItem;
import food.MainDish;
import food.SideDish;
import food.Stock;
import main.Input;
import sorting.SortArray;
import utility.Font;
import utility.Screen;

public class ReportPage {
  public static void showReportMenu() {
    int choice = 0;

    do {
      Screen.clear();
      String currentUsername = AuthService.getLoggedInAccount().getUsername();
      String usernameText = "| Your username: " + currentUsername + " |";
      Font.print("-", usernameText.length());
      System.out.println(usernameText);
      Font.print("-", usernameText.length());
      System.out.printf("\n%47s\n", "+-------------------------------+");
      System.out.printf("%47s\n", "|          Reports Menu         |");
      System.out.printf("%47s\n", "|-------------------------------|");
      System.out.printf("%47s\n", "| 1. Customer Report            |");
      System.out.printf("%47s\n", "| 2. Customer Orders Report     |");
      System.out.printf("%47s\n", "| 3. Admin Report               |");
      System.out.printf("%47s\n", "| 4. Stock Report               |");
      System.out.printf("%47s\n", "|-------------------------------|");
      System.out.printf("%47s\n", "| 5. Go back                    |");
      System.out.printf("%47s\n", "+-------------------------------+");

      choice = Input.getMenuChoice(5);

      switch (choice) {
        case 1:
          displayCustomerReport();
          break;
        case 2:
          displayCustomerOrdersReport();
          break;
        case 3:
          displayAdminReport();
          break;
        case 4:
          displayStockReport();
          break;
        default:
          break;
      }
    } while (choice != 5);
  }

  private static void displayCustomerReport() {
    Font.print(Font.ANSI_CYAN, "               <<< Loading customer list... >>>");
    ListInterface<Customer> customerList = CustomerRepository.findAll();
    // Convert ArrayList to array
    Customer[] customerArr = customerList.toArray(new Customer[0]);
    // Sort the array
    SortArray.quickSort(customerArr, customerArr.length);

    Screen.clear();
    System.out.println(
        "+-----------------------------------------------------------------------------------------------------------------------+");
    System.out.println(
        "|                                                    " + Font.getStr(Font.ANSI_CYAN, "Customer Report")
            + "                                                    |");
    System.out.println(
        "|-----------------------------------------------------------------------------------------------------------------------|");
    System.out.println(
        "| #  | Full Name               | Email                       | Username         | Phone Number | Points | Date of Birth |");
    System.out.println(
        "|-----------------------------------------------------------------------------------------------------------------------|");
    for (int i = 0; i < customerArr.length; i++) {
      System.out.println(String.format("| %-2s ", (i + 1)) + customerArr[i]);
      if (i != customerArr.length - 1) {
        System.out.println(
            "|-----------------------------------------------------------------------------------------------------------------------|");
      }
    }
    System.out.println(
        "+-----------------------------------------------------------------------------------------------------------------------+");
    System.out.printf("| Total customers: %-41s | New customers in last 30 days: %-25s |\n",
        customerList.size(), getLast30DaysNewCustomers(customerList));
    System.out.println(
        "+-----------------------------------------------------------------------------------------------------------------------+");

    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();
  }

  private static void displayCustomerOrdersReport() {
    Font.print(Font.ANSI_CYAN, "           <<< Loading customers and orders... >>>");
    ListInterface<Customer> customerList = CustomerRepository.findAll();

    Screen.clear();
    System.out.println(
        "+-------------------------------------------------------------------------------------------------------------------+");
    System.out.println(
        "|                                              " + Font.getStr(Font.ANSI_CYAN, "Customer Orders Report")
            + "                                               |");
    System.out.println(
        "|-------------------------------------------------------------------------------------------------------------------|");
    System.out.println(
        "| #  | Order ID | Status     | Order Date | Delivery Date | Customer Name          | Username         | Amount (RM) |");
    System.out.println(
        "|-------------------------------------------------------------------------------------------------------------------|");

    int i = 1;
    for (Customer customer : customerList) {      
      MapInterface<Integer, Order> orders = OrderRepository.findAllByCustomerEmail(customer.getEmail());
      if (orders.isEmpty()) {
        continue;
      }
      customer.setOrders(orders);
      boolean firstRowPrinted = false;
      double totalAmount = 0;

      for (Order order : customer.getOrders().values()) {
        totalAmount += order.getFinalPrice();
        System.out.println(
            String.format("| %-2s | %-8s | %-10s | %-10s | %-13s | %-22s | %-16s | %-11s |", i, order.getId(),
                order.getStatus(),
                order.getOrderTime().toLocalDate(), order.getDeliveryDate(),
                !firstRowPrinted ? customer.getName().getFullName() : "",
                !firstRowPrinted ? customer.getUsername() : "", String.format("%.2f", order.getFinalPrice())));
        i++;
        firstRowPrinted = true;
      }

      System.out.println(
          "+-------------------------------------------------------------------------------------------------------------------+");
      System.out.printf(
          "|                                                                                  | Total amount     : RM%-9s |\n",
          String.format("%.2f", totalAmount));

      System.out.println(
          "+-------------------------------------------------------------------------------------------------------------------+");
    }

    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();
  }

  private static void displayAdminReport() {
    Font.print(Font.ANSI_CYAN, "                <<< Loading admin list... >>>");
    ListInterface<Admin> adminList = AdminRepository.findAll();
    // Convert ArrayList to array
    Admin[] adminArr = adminList.toArray(new Admin[0]);
    // Sort the array
    SortArray.quickSort(adminArr, adminArr.length);

    Screen.clear();
    System.out.println(
        "+-----------------------------------------------------------------------------------------------------------------------+");
    System.out.println(
        "|                                                     " + Font.getStr(Font.ANSI_CYAN, "Admin Report")
            + "                                                      |");
    System.out.println(
        "|-----------------------------------------------------------------------------------------------------------------------|");
    System.out.println(
        "| #  | Full Name                 | Email                         | Username          | Position          | Date Created |");
    System.out.println(
        "|-----------------------------------------------------------------------------------------------------------------------|");
    for (int i = 0; i < adminArr.length; i++) {
      System.out.println(String.format("| %-2s ", (i + 1)) + adminArr[i]);
      if (i != adminArr.length - 1) {
        System.out.println(
            "|-----------------------------------------------------------------------------------------------------------------------|");
      }
    }
    System.out.println(
        "+-----------------------------------------------------------------------------------------------------------------------+");
    System.out.printf("| Total admins: %-48s | New admins in last 30 days: %-24s |\n",
        adminList.size(), getLast30DaysNewAdmins(adminList));
    System.out.println(
        "+-----------------------------------------------------------------------------------------------------------------------+");

    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();
  }

  public static void displayStockReport() {
    Font.print(Font.ANSI_CYAN, "               <<< Loading stock report... >>>");
    FoodItem[] foodItems = new FoodItem[Stock.getFoodItems().size()];
    // Convert ArrayList to array
    foodItems = Stock.getFoodItems().toArray(foodItems);
    // Sort the Food Items
    FoodItem[] sortedFoodItems = FoodItem.sortFoodItems(foodItems);

    // Output Stock Report
    Screen.clear();
    System.out.println(
        "+----------------------------------------------------------------------------------------------+");
    System.out.println(
        "|                                           " + Font.getStr(Font.ANSI_CYAN, "Stock Report")
            + "                                       |");
    System.out.println(
        "|----------------------------------------------------------------------------------------------|");
    System.out.println(
        "| #  | Food Name                 | Food Type | Main Dish Type | In Stock Quantity | Unit Price |");
    System.out.println(
        "|----------------------------------------------------------------------------------------------|");
    for (int i = 0; i < sortedFoodItems.length; i++) {
      System.out.print(String.format("| %-2s ", (i + 1)));
      System.out.println(String.format("| %-25s | %-9s | %-14s | %s | RM%6.2f   |",
          sortedFoodItems[i].getFoodName(),
          returnFoodType(sortedFoodItems[i]),
          returnMainDishType(sortedFoodItems[i]),
          (sortedFoodItems[i].getInStockQty() > 5) ? Font.getStr(Font.ANSI_GREEN, String.format("%17d",
              sortedFoodItems[i].getInStockQty()))
              : Font.getStr(Font.ANSI_RED, String.format("%17d",
                  sortedFoodItems[i].getInStockQty())),
          sortedFoodItems[i].getUnitPrice()));
      if (i != sortedFoodItems.length - 1) {
        System.out.println(
            "|----------------------------------------------------------------------------------------------|");
      }
    }
    System.out.println(
        "+----------------------------------------------------------------------------------------------+");
    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();
  }

  // ******************************
  // --- --- Some Util Methods
  // ******************************

  private static String returnFoodType(FoodItem foodItem) {
    if (foodItem instanceof MainDish) {
      return "Main Dish";
    } else if (foodItem instanceof SideDish) {
      return "Side Dish";
    } else {
      return "Drink";
    }
  }

  private static String returnMainDishType(FoodItem foodItem) {
    if (foodItem instanceof MainDish) {
      return ((MainDish) foodItem).getType().getType();
    } else {
      return "-";
    }
  }

  private static int getLast30DaysNewCustomers(ListInterface<Customer> customerList) {
    int count = 0;
    for (int i = 0; i < customerList.size(); i++) {
      if (LocalDate.now().compareTo(customerList.get(i).getDateCreated().plusDays(30)) <= 0) {
        count++;
      }
    }
    return count;
  }

  private static int getLast30DaysNewAdmins(ListInterface<Admin> adminList) {
    int count = 0;
    for (int i = 0; i < adminList.size(); i++) {
      if (LocalDate.now().compareTo(adminList.get(i).getDateCreated().plusDays(30)) <= 0) {
        count++;
      }
    }
    return count;
  }
}