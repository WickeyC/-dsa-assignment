package account;

import adt.MapInterface;
import main.Input;
import utility.Font;
import utility.Screen;

public class AddressManagement {
  // Display the Address Management UI
  public static void showUI() {
    int choice;
    do {
      Screen.clear();
      System.out.println("===============================================================");
      System.out.println("|                       Your Addresses                        |");
      System.out.println("===============================================================");

      Customer customer = (Customer) AuthService.getLoggedInAccount();
      printAddresses(customer.getAddresses());

      System.out.println("===============================================================");
      System.out.printf("%48s\n", "+-------------------------------+");
      System.out.printf("%48s\n", "|       ENTER YOUR OPTION       |");
      System.out.printf("%48s\n", "|-------------------------------|");
      System.out.printf("%48s\n", "| 1. Add new address            |");
      System.out.printf("%48s\n", "| 2. Update address             |");
      System.out.printf("%48s\n", "| 3. Delete address             |");
      System.out.printf("%48s\n", "| 4. Delete all address         |");
      System.out.printf("%48s\n", "|-------------------------------|");
      System.out.printf("%48s\n", "| 5. Go back                    |");
      System.out.printf("%48s\n", "+-------------------------------+");

      choice = Input.getMenuChoice(5);

      switch (choice) {
        case 1:
          // Add new address
          addAddress();
          break;
        case 2:
          // Update address
          updateAddress();
          break;
        case 3:
          // Delete address
          deleteAddress();
          break;
        case 4:
          // Delete all addresses
          deleteAllAddresses();
          break;
        case 5:
          // Go back
          break;
        default:
          break;
      }
    } while (choice != 5);
  }

  // Display a list of addresses
  public static void printAddresses(MapInterface<Integer, Address> addresses) {
    if (addresses.isEmpty()) {
      System.out.println("| No addresses                                                |");
    } else {
      int i = 0;
      for (Address address : addresses.values()) {
        System.out.format("|      Address ID : %-41s |\n", address.getId());
        System.out.format("|    Address line : %-41s |\n", address.getAddressLine());
        System.out.format("| Postcode & city : %-5s %-35s |\n", address.getPostcode(),
            address.getCity());
        System.out.format("|           State : %-41s |\n", address.getState());
        if (i != addresses.size() - 1) {
          System.out.println("|-------------------------------------------------------------|");
        }
        i++;
      }
    }
  }

  // Display the Add New Address UI
  private static void addAddress() {
    Customer customer = (Customer) AuthService.getLoggedInAccount();

    Screen.clear();
    System.out.println("==================================================");
    System.out.println("|                 Add New Address                |");
    System.out.println("==================================================");
    System.out.println("\n<<< Please specify your new address below >>>");

    Address address = AddressView.getNewAddress();

    Font.print(Font.ANSI_CYAN, "<<< Adding the new address in the database... >>>");
    AddressRepository.create(address, customer.getEmail());
    customer.getAddresses().put(address.getId(), address);

    Font.print(Font.ANSI_GREEN, "\n<<< New address is added successfully! >>>");
    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();
  }

  private static void updateAddress() {
    Customer customer = (Customer) AuthService.getLoggedInAccount();

    if (customer.getAddresses().isEmpty()) {
      Font.print(Font.ANSI_CYAN, "             <<< There's no address in your account. >>>");
      System.out.print("\n             Press enter to continue...");
      Input.SCANNER.nextLine();
      return;
    }

    int idToUpdate = AddressView.getAddressIdInput("             > Please enter the ID of address to update: ");
    // ADT Usage
    Address addressToUpdate = customer.getAddresses().get(idToUpdate);

    // Check if the address exists or not
    if (addressToUpdate == null) {
      Font.print(Font.ANSI_RED, "             <<< This address does not exist. >>>");
      System.out.print("\n             Press enter to continue...");
      Input.SCANNER.nextLine();
      return;
    }

    // If it exists, update process starts
    Screen.clear();
    System.out.println("===============================================================");
    System.out.println("|                   Update Delivery Address                   |");
    System.out.println("===============================================================");
    System.out.format("|      Address ID : %-41s |\n", addressToUpdate.getId());
    System.out.format("|    Address line : %-41s |\n", addressToUpdate.getAddressLine());
    System.out.format("| Postcode & city : %-5s %-35s |\n", addressToUpdate.getPostcode(),
        addressToUpdate.getCity());
    System.out.format("|           State : %-41s |\n", addressToUpdate.getState());
    System.out.println("===============================================================");

    Font.print(Font.ANSI_CYAN, "\n<<< Fill in the fields below to update the address. >>>");

    Address updatedAddress = AddressView.getNewAddress();
    updatedAddress.setId(addressToUpdate.getId());
    Font.print(Font.ANSI_CYAN, "<<< Updating address in the database... >>>");
    AddressRepository.update(updatedAddress);
    customer.getAddresses().put(updatedAddress.getId(), updatedAddress);
    Font.print(Font.ANSI_GREEN, "\n<<< Address is updated successfully! >>>");

    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();
  }

  // Display the Delete Address UI
  private static void deleteAddress() {
    Customer customer = (Customer) AuthService.getLoggedInAccount();

    Screen.clear();
    System.out.println("==================================================");
    System.out.println("|                 Delete Address                 |");
    System.out.println("==================================================");

    int idToDelete = AddressView.getAddressIdInput("> Please enter the ID of address to delete: ");

    // ADT Usage
    Address addressToDelete = customer.getAddresses().remove(idToDelete);

    // Check whether there's an address with the ID entered exist or not
    if (addressToDelete == null) {
      Font.print(Font.ANSI_RED, "<<< This address does not exist. >>>");
    } else {
      // If it exists, delete it from database
      Font.print(Font.ANSI_CYAN, "<<< Deleting address from the database... >>>");
      AddressRepository.deleteById(addressToDelete.getId());
      Font.print(Font.ANSI_GREEN, "\n<<< Address is deleted successfully! >>>");
    }

    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();
  }

  private static void deleteAllAddresses() {
    Customer customer = (Customer) AuthService.getLoggedInAccount();

    // ADT Usage
    if (customer.getAddresses().isEmpty()) {
      Font.print(Font.ANSI_CYAN, "             <<< There's no address in your account. >>>");
      System.out.print("\n             Press enter to continue...");
      Input.SCANNER.nextLine();
      return;
    }
    
    boolean confirmDelete = Input.getYesNoInput("             > Are you sure to delete all addresses? (y/n): ", 0);

    if (!confirmDelete) {
      return;
    }

    Font.print(Font.ANSI_CYAN, "             <<< Deleting all addresses from the database... >>>");
    AddressRepository.deleteAll();
    // Map Usage
    customer.getAddresses().clear();
    Font.print(Font.ANSI_GREEN, "             <<< All addresses are deleted successfully! >>>");

    System.out.print("\n             Press enter to continue...");
    Input.SCANNER.nextLine();
  }
}