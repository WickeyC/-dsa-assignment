package main;

import account.CustomerView;
import food.Stock;
import utility.Database;
import utility.Font;
import utility.Screen;

public class Main {
  public static void main(String[] args) {
    initApp();

    String selection = null;
    boolean validSelection;

    do {
      displayMenu();

      do {
        try {
          System.out.print("\n             > Please enter your choice (a-c): ");
          selection = Input.SCANNER.nextLine().toLowerCase();
          if (!selection.equals("a")
              && !selection.equals("b")
              && !selection.equals("c")
              && !selection.equals("x")) {
            throw new InvalidSelectionException();
          }
          validSelection = true;
        } catch (InvalidSelectionException e) {
          System.out.printf("%67s", e.getErrorMsg());
          validSelection = false;
        }
      } while (!validSelection);

      Screen.clear();
      switch (selection) {
        case "a":
          new CustomerView().showLoginUI();
          break;
        case "b":
          new CustomerView().showSignupUI();
          break;
        case "c":
          displayEnding();
          System.exit(0);
          break;
        case "x":
          Display.displayAdminPortalMenu();
          break;
      }
    } while (true);
  }

  private static void initApp() {
    Screen.clear();
    Font.print(Font.ANSI_CYAN, "<<< Initializing application... >>>");
    Database.initConnection();
    Database.createTables();
    Stock.hydrateData();
    Font.print(Font.ANSI_GREEN, "<<< Initialization completed >>>");
    Screen.pause(1);
  }

  private static void displayLogo() {
    System.out.println("                                               ");
    System.out.println("                   + ------------------ +      ");
    System.out.println("                   |  BW Food Catering  |      ");
    System.out.println("                   + ------------------ +      ");
  }

  private static void displayMenu() {
    Screen.clear();
    System.out.printf("%61s\n", "+-----------------+");
    System.out.printf("%61s\n", "| x. Admin Portal |");
    System.out.printf("%61s\n", "+-----------------+");
    displayLogo();
    System.out.printf("\n%47s\n", "+-------------------------------+");
    System.out.printf("%47s\n", "|            WELCOME            |");
    System.out.printf("%47s\n", "|-------------------------------|");
    System.out.printf("%47s\n", "| a. Login                      |");
    System.out.printf("%47s\n", "| b. Signup                     |");
    System.out.printf("%47s\n", "|-------------------------------|");
    System.out.printf("%47s\n", "| c. Quit                       |");
    System.out.printf("%47s\n", "+-------------------------------+");
  }

  private static void displayEnding() {
    Screen.clear();
    System.out.printf("%47s\n", "+----------------------------------+");
    System.out.printf("%47s\n", "| Thank for using the application! |");
    System.out.printf("%47s\n", "+----------------------------------+");
    System.out.printf("%47s\n", "        We hope you like it.        ");
    System.out.printf("\n%47s\n", "           >> Good Bye <<           ");
    System.out.println();
    Screen.pause(5);
  }

  // Exception for handling invalid selection input
  private static class InvalidSelectionException extends Exception {
    private final String errorMsg;

    InvalidSelectionException() {
      this.errorMsg = Font.getStr(Font.ANSI_RED, "<<< Invalid choice, please enter a correct choice. >>>");
    }

    public String getErrorMsg() {
      return this.errorMsg;
    }
  }
}