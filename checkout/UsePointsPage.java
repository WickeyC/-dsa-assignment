package checkout;

import java.util.InputMismatchException;

import account.AuthService;
import account.Customer;
import main.Input;
import utility.Font;
import utility.Screen;

public class UsePointsPage {
  // Display UI for asking whether the user wants to use points or not
  public static void showUsePointsUI() {
    Customer customer = (Customer) AuthService.getLoggedInAccount();

    Screen.clear();
    System.out.println("===============================================================");
    System.out.println("|               Use Your Points to Get Discount               |");
    System.out.println("===============================================================");
    System.out.println("| Points you have : " + String.format("%-41s", customer.getPoints() + " points") + " |");
    System.out.println("===============================================================");

    if (customer.getPoints() == 0) {
      Font.print(Font.ANSI_CYAN, "\n<<< You don't have any points to use currently. >>>");
    } else {
      Font.print(Font.ANSI_GREEN, "\n<<< 1 Points = RM0.01 >>>");
      boolean wantToUsePoints = Input.getYesNoInput("> Do you want to use your points to get discount? (y/n): ", 0);
      if (wantToUsePoints) {
        double priceToPay = Order.getCurrentOrder().getFinalPrice();
        // Ask the user to input the points to use
        int pointsToUse = getPointsToUse((int) (priceToPay * 100));
        Order.getCurrentOrder().setPointsUsed(pointsToUse);
        Font.print(Font.ANSI_GREEN, "\n          " + pointsToUse + " points is used successfully!");
        System.out.println("          +-----------------------------------------+");
        System.out.printf("          | Price before discount : %15s |",
            String.format("RM%.2f", priceToPay));
        double discount = pointsToUse * 0.01;
        priceToPay -= discount;
        Order.getCurrentOrder().setFinalPrice(priceToPay);
        System.out.printf("\n          | Discount              : %15s |", String.format("- RM%.2f", discount));
        System.out.printf("\n          | Price after discount  : %15s |", String.format("RM%.2f", priceToPay));
        System.out.println("\n          +-----------------------------------------+");
      }
    }

    System.out.print("\n                   Press enter to continue...");
    Input.SCANNER.nextLine();

    // Proceed to choose delivery address page
    ChooseAddressPage.showChooseAddressUI();
  }

  // Get input for points to use
  private static int getPointsToUse(int maxPointsToUse) {
    Customer customer = (Customer) AuthService.getLoggedInAccount();
    int points = 0;
    boolean validPoints = false;

    do {
      try {
        System.out.print("> How many points you want to use?: ");
        points = Input.SCANNER.nextInt();

        // Validate points to use input
        if (points < 1 || points > customer.getPoints()) {
          Font.print(Font.ANSI_RED, "<<< Invalid points, please enter again. >>>");
          validPoints = false;
        } else if (points > maxPointsToUse) {
          Font.print(Font.ANSI_RED, "<<< Maximum points you can use is " + maxPointsToUse + " >>>");
          validPoints = false;
        } else {
          validPoints = true;
        }
      } catch (InputMismatchException inputMismatchException) {
        Font.print(Font.ANSI_RED, "<<< Invalid points, please enter integers only. >>>");
      }
      Input.SCANNER.nextLine();
    } while (!validPoints);

    return points;
  }
}