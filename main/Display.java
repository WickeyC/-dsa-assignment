package main;

import account.AddressManagement;
import account.AdminView;
import account.AuthService;
import account.ProfilePage;
import checkout.CardManagement;
import checkout.OrderHistory;
import food.Stock;
import ordermenu.Cart;
import ordermenu.OrderMenu;
import report.ReportPage;
import kitchen.Kitchen;
import utility.Font;
import utility.Screen;

public class Display {
  public static void displayCustomerMenu() {
    int choice = 0;
    do {
      Screen.clear();
      String currentUsername = AuthService.getLoggedInAccount().getUsername();
      String usernameText = "| Your username: " + currentUsername + " |";
      Font.print("-", usernameText.length());
      System.out.println(usernameText);
      Font.print("-", usernameText.length());
      System.out.printf("\n%47s\n", "+-------------------------------+");
      System.out.printf("%47s\n", "|            WELCOME            |");
      System.out.printf("%47s\n", "|-------------------------------|");
      System.out.printf("%47s\n", "| 1. Explore menu               |");
      System.out.printf("%47s\n", "| 2. My cart                    |");
      System.out.printf("%47s\n", "| 3. My orders                  |");
      System.out.printf("%47s\n", "| 4. My profile                 |");
      System.out.printf("%47s\n", "| 5. Manage cards               |");
      System.out.printf("%47s\n", "| 6. Manage addresses           |");
      System.out.printf("%47s\n", "|-------------------------------|");
      System.out.printf("%47s\n", "| 7. Sign out                   |");
      System.out.printf("%47s\n", "+-------------------------------+");

      choice = Input.getMenuChoice(7);

      switch (choice) {
        case 1:
          // Explore Menu
          OrderMenu.orderMenu();
          break;
        case 2:
          // My Cart
          Cart.displayCart();
          break;
        case 3:
          // My Orders
          OrderHistory.showOrderHistory();
          break;
        case 4:
          // My Profile
          ProfilePage.showCustomerProfileUI();
          break;
        case 5:
          // My Cards
          CardManagement.showUI();
          break;
        case 6:
          // My Addresses
          AddressManagement.showUI();
          break;
        case 7:
          // Signout
          AuthService.signout();
          break;
        default:
          break;
      }
    } while (choice != 7);
  }

  public static void displayAdminPortalMenu() {
    int choice = 0;

    do {
      Screen.clear();
      System.out.printf("\n%47s\n", "+-------------------------------+");
      System.out.printf("%47s\n", "|          Admin Portal         |");
      System.out.printf("%47s\n", "|-------------------------------|");
      System.out.printf("%47s\n", "| 1. Login                      |");
      System.out.printf("%47s\n", "| 2. Signup                     |");
      System.out.printf("%47s\n", "|-------------------------------|");
      System.out.printf("%47s\n", "| 3. Go back                    |");
      System.out.printf("%47s\n", "+-------------------------------+");

      choice = Input.getMenuChoice(3);

      switch (choice) {
        case 1:
          new AdminView().showLoginUI();
          break;
        case 2:
          new AdminView().showSignupUI();
          break;
        default:
          break;
      }
    } while (choice != 3);
  }

  public static void displayAdminMenu() {
    int choice = 0;

    do {
      Screen.clear();
      String currentUsername = AuthService.getLoggedInAccount().getUsername();
      String usernameText = "| Your username: " + currentUsername + " |";
      Font.print("-", usernameText.length());
      System.out.println(usernameText);
      Font.print("-", usernameText.length());
      System.out.printf("\n%47s\n", "+-------------------------------+");
      System.out.printf("%47s\n", "|          Admin Portal         |");
      System.out.printf("%47s\n", "|-------------------------------|");
      System.out.printf("%47s\n", "| 1. Kitchen                    |");
      System.out.printf("%47s\n", "| 2. View reports               |");
      System.out.printf("%47s\n", "| 3. Manage stock               |");
      System.out.printf("%47s\n", "| 4. My profile                 |");
      System.out.printf("%47s\n", "|-------------------------------|");
      System.out.printf("%47s\n", "| 5. Sign out                   |");
      System.out.printf("%47s\n", "+-------------------------------+");

      choice = Input.getMenuChoice(5);

      switch (choice) {
        case 1:
          // Kitchen: Manage orders
          Kitchen.manageOrders();
          break;
        case 2:
          // View Reports
          ReportPage.showReportMenu();
          break;
        case 3:
          // Manage stock
          Stock.manageStockMenu();
          break;
        case 4:
          // My profile
          ProfilePage.showAdminProfileUI();
          break;
        case 5:
          // Sign out
          break;
        default:
          break;
      }
    } while (choice != 5);
  }
}