package checkout;

import account.Address;
import account.AddressManagement;
import account.AddressRepository;
import account.AddressView;
import account.AuthService;
import account.Customer;
import main.Input;
import utility.Font;
import utility.Screen;

public class ChooseAddressPage {
  public static void showChooseAddressUI() {
    Screen.clear();
    System.out.println("===============================================================");
    System.out.println("|                   Choose Delivery Address                   |");
    System.out.println("===============================================================");

    Customer customer = (Customer) AuthService.getLoggedInAccount();
    // Display a list of addresses to choose from
    AddressManagement.printAddresses(customer.getAddresses());

    System.out.println("===============================================================");

    Address chosenAddress = null;
    if (customer.getAddresses().isEmpty()) {
      // No addresses exist in the account
      Font.print(Font.ANSI_CYAN, "\n<<< There's no address in your account, you have to create one. >>>");
      chosenAddress = createNewAddress();
    } else {
      // Ask the user whether he wants to choose an existing address or create a new
      // one.
      boolean toChooseExistingAddress = Input
          .getYesNoInput("\n> Do you want to choose an existing address? Or no to create a new one? (y/n): ", 0);
      if (toChooseExistingAddress) {
        do {
          int chosenAddressId = AddressView
              .getAddressIdInput("> Please choose your delivery address by entering the ID: ");
          // Check if the address with the ID entered exist or not
          chosenAddress = customer.getAddresses().get(chosenAddressId);
          // Display appropriate message based on the existence of the address
          if (chosenAddress == null) {
            Font.print(Font.ANSI_RED, "<<< This address does not exist, please enter again. >>>");
          } else {
            Font.print(Font.ANSI_GREEN, "<<< Address chosen successfully. >>>");
          }
        } while (chosenAddress == null);
      } else {
        // Let the user create a new address on the spot.
        Font.print(Font.ANSI_CYAN, "\n<<< Fill in the fields below to create a new address. >>>");
        chosenAddress = createNewAddress();
      }
    }

    // Update current order's delivery address
    Order.getCurrentOrder().setDeliveryAddress(chosenAddress);

    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();

    // Go to Delivery Date page
    DeliveryDatePage.showDeliveryDateUI();
  }

  private static Address createNewAddress() {
    Customer customer = (Customer) AuthService.getLoggedInAccount();
    Address address = AddressView.getNewAddress();
    // Then, add it to the database
    AddressRepository.create(address, customer.getEmail());
    customer.getAddresses().put(address.getId(), address);
    Font.print(Font.ANSI_GREEN, "\n<<< Address is created successfully! >>>");
    return address;
  }
}