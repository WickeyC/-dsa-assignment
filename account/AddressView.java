package account;

import java.util.InputMismatchException;

import main.Input;
import utility.Font;

public class AddressView {
  public static int getAddressIdInput(String message) {
    int addressId = 0;
    boolean validAddressId = false;

    do {
      try {
        System.out.print(message);
        addressId = Input.SCANNER.nextInt();
        validAddressId = true;
      } catch (InputMismatchException inputMismatchException) {
        Font.print(Font.ANSI_RED, "<<< Invalid address ID, please enter integers only. >>>");
      }
      Input.SCANNER.nextLine();
    } while (!validAddressId);
    return addressId;
  }

  // Prompt inputs for addressLine, city, state, and postcode data fields
  public static Address getNewAddress() {
    Address address = new Address();

    boolean validAddressLine;
    do {
      System.out.print("> Enter address line: ");
      address.setAddressLine(Input.SCANNER.nextLine().trim());
      if (address.getAddressLine().length() == 0) {
        Font.print(Font.ANSI_RED, "<<< Address line is required, please enter it. >>>");
        validAddressLine = false;
      } else if (address.getAddressLine().length() > 100) {
        Font.print(Font.ANSI_RED, "<<< Address line is too long, please enter again. >>>");
        validAddressLine = false;
      } else {
        validAddressLine = true;
      }
    } while (!validAddressLine);

    boolean validCity;
    do {
      System.out.print("> Enter city: ");
      address.setCity(Input.SCANNER.nextLine().trim());
      if (address.getCity().length() == 0) {
        Font.print(Font.ANSI_RED, "<<< City is required, please enter it. >>>");
        validCity = false;
      } else if (address.getCity().length() > 50) {
        Font.print(Font.ANSI_RED, "<<< City is too long, please enter again. >>>");
        validCity = false;
      } else {
        validCity = true;
      }
    } while (!validCity);

    boolean validState;
    do {
      System.out.print("> Enter state: ");
      address.setState(Input.SCANNER.nextLine().trim());
      if (address.getState().length() == 0) {
        Font.print(Font.ANSI_RED, "<<< State is required, please enter it. >>>");
        validState = false;
      } else if (address.getState().length() > 15) {
        Font.print(Font.ANSI_RED, "<<< State is too long, please enter again. >>>");
        validState = false;
      } else {
        validState = true;
      }
    } while (!validState);

    boolean validPostcode;
    do {
      System.out.print("> Enter postcode: ");
      address.setPostcode(Input.SCANNER.nextLine().trim());
      if (address.getPostcode().length() == 0) {
        Font.print(Font.ANSI_RED, "<<< Postcode is required, please enter it. >>>");
        validPostcode = false;
      } else if (address.getPostcode().length() > 5) {
        Font.print(Font.ANSI_RED, "<<< Postcode is too long, please enter again. >>>");
        validPostcode = false;
      } else if (!address.getPostcode().matches("[0-9]+")
          || address.getPostcode().length() < 5) {
        Font.print(Font.ANSI_RED, "<<< Invalid postcode format, please enter again. >>>");
        validPostcode = false;
      } else {
        validPostcode = true;
      }
    } while (!validPostcode);

    return address;
  }
}
