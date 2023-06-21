package checkout;

import adt.LinkedHashMap;
import adt.MapInterface;
import main.Input;
import ordermenu.Cart;
import utility.Font;
import utility.Screen;

public class ApplyVoucherPage {
  private static MapInterface<String, Integer> vouchersMap;
  static {
    vouchersMap = new LinkedHashMap<String, Integer>();
    vouchersMap.put("NEWUSER", 20);
    vouchersMap.put("BNW", 10);
    vouchersMap.put("JAVA", 8);
    vouchersMap.put("OOP", 5);
  }
  
  // Display UI for applying voucher
  public static void showVoucherUI() {
    Screen.clear();
    System.out.printf("%47s\n", "+---------------------------------+");
    System.out.printf("%47s\n", "|          Apply Voucher          |");
    System.out.printf("%47s\n", "+---------------------------------+");
    boolean wantToApplyVoucher = Input.getYesNoInput(
        String.format("%49s", "> Do you want to apply a voucher? (y/n): "),
        6);

    if (wantToApplyVoucher) {
      boolean enterAgain = false;
      do {
        enterAgain = false;
        System.out.print("        > Enter a voucher code: ");
        String voucherCodeInput = Input.SCANNER.nextLine().toUpperCase().trim();

        boolean applySuccessful = false;

        // ADT usage
        if (vouchersMap.containsKey(voucherCodeInput)) {
          applySuccessful = true;
          Order.getCurrentOrder().setVoucherApplied(voucherCodeInput);
          int discountPct = vouchersMap.get(voucherCodeInput);
          // Calculate voucher discount
          double discount = Cart.calcTotalPrice() * discountPct / 100;
          Order.getCurrentOrder().setVoucherDiscount(discount);
          Order.getCurrentOrder().setFinalPrice(Cart.calcTotalPrice() - discount);
          Font.print(Font.ANSI_GREEN, "\n        " + discountPct + "% discount voucher is applied successfully!");
          System.out.println("        -------------------------------------------");
          System.out.printf("        | Price before discount : %15s |",
              String.format("RM%.2f", Cart.calcTotalPrice()));
          System.out.printf("\n        | Discount              : %15s |", String.format("- RM%.2f", discount));
          System.out.printf("\n        | Price after discount  : %15s |",
              String.format("RM%.2f", Order.getCurrentOrder().getFinalPrice()));
          System.out.println("\n        -------------------------------------------");
        }

        // When the voucher is not successfully applied, ask the user whether to enter again or not
        if (!applySuccessful) {
          Font.print(Font.ANSI_YELLOW, "        Sorry, this voucher does not exist.");
          enterAgain = Input.getYesNoInput("        > Do you want to enter again? (y/n): ", 8);
        }
      } while (enterAgain);
    } else {
      Order.getCurrentOrder().setFinalPrice(Cart.calcTotalPrice());
    }

    System.out.print("\n                Press enter to continue...");
    Input.SCANNER.nextLine();

    UsePointsPage.showUsePointsUI();
  }
}