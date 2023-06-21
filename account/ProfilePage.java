package account;

import account.AuthInput.PromptForContinueOption;
import main.Input;
import utility.Font;
import utility.Screen;

public class ProfilePage {
  // Display customer profile information UI
  public static void showCustomerProfileUI() {
    int choice;
    do {
      Screen.clear();
      System.out.println("=============================================================");
      System.out.println("|                    Profile Information                    |");
      System.out.println("=============================================================");

      Account account = AuthService.getLoggedInAccount();
      // Polymorphism
      System.out.println(getProfileInfo(account));

      System.out.println("=============================================================");
      System.out.printf("%47s\n", "+-------------------------------+");
      System.out.printf("%47s\n", "|       ENTER YOUR OPTION       |");
      System.out.printf("%47s\n", "|-------------------------------|");
      System.out.printf("%47s\n", "| 1. Edit name                  |");
      System.out.printf("%47s\n", "| 2. Edit phone number          |");
      System.out.printf("%47s\n", "| 3. Edit date of birth         |");
      System.out.printf("%47s\n", "| 4. Change password            |");
      System.out.printf("%47s\n", "|-------------------------------|");
      System.out.printf("%47s\n", "| 5. Go back                    |");
      System.out.printf("%47s\n", "+-------------------------------+");

      choice = Input.getMenuChoice(5);

      switch (choice) {
        case 1:
          // Edit name
          editName();
          break;
        case 2:
          // Edit phone number
          editPhoneNum();
          break;
        case 3:
          // Edit date of birth
          editDateOfBirth();
          break;
        case 4:
          // Change password
          changePassword();
          break;
        case 5:
          // Go back
        default:
          break;
      }
    } while (choice != 5);
  }

  // Display admin profile information UI
  public static void showAdminProfileUI() {
    int choice;
    do {
      Screen.clear();
      System.out.println("=============================================================");
      System.out.println("|                    Profile Information                    |");
      System.out.println("=============================================================");

      Account account = AuthService.getLoggedInAccount();
      // Polymorphism
      System.out.println(getProfileInfo(account));

      System.out.println("=============================================================");
      System.out.printf("%47s\n", "+-------------------------------+");
      System.out.printf("%47s\n", "|       ENTER YOUR OPTION       |");
      System.out.printf("%47s\n", "|-------------------------------|");
      System.out.printf("%47s\n", "| 1. Edit name                  |");
      System.out.printf("%47s\n", "| 2. Edit position              |");
      System.out.printf("%47s\n", "| 3. Change password            |");
      System.out.printf("%47s\n", "|-------------------------------|");
      System.out.printf("%47s\n", "| 4. Go back                    |");
      System.out.printf("%47s\n", "+-------------------------------+");

      choice = Input.getMenuChoice(4);

      switch (choice) {
        case 1:
          // Edit name
          editName();
          break;
        case 2:
          // Edit phone number
          editPosition();
          break;
        case 3:
          // Change password
          changePassword();
          break;
        case 4:
          // Go back
        default:
          break;
      }
    } while (choice != 4);
  }

  private static String getProfileInfo(Account account) {
    String profileInfo = String.format("|         Email : %-41s |\n", account.getEmail()) +
        String.format("|      Username : %-41s |\n", account.getUsername()) +
        String.format("|          Name : %-41s |\n", account.getName().getFullName()) +
        String.format("|  Sign Up Date : %-41s |", account.getDateCreated().toString());

    if (account instanceof Admin) {
      Admin admin = (Admin) account;
      profileInfo += "\n" +
          String.format("|      Position : %-41s |", admin.getPosition() == null ? "Unspecified" : admin.getPosition());
    } else {
      Customer customer = (Customer) account;

      profileInfo += "\n" +
          String.format("|  Phone Number : %-41s |\n",
              customer.getPhoneNum() == null ? "Unspecified" : customer.getPhoneNum())
          + String.format("|        Points : %-41s |\n", customer.getPoints())
          + String.format("| Date Of Birth : %-41s |",
              customer.getDateOfBirth() == null ? "Unspecified" : customer.getDateOfBirth().toString());
    }

    return profileInfo;
  }

  // Display UI for editing name for both admin and customer
  private static void editName() {
    Account account = AuthService.getLoggedInAccount();
    Screen.clear();
    System.out.println("===================================================");
    System.out.println("|                    Edit Name                    |");
    System.out.println("===================================================");
    System.out.printf("| Your current first name : %-21s |\n", account.getName().getFirstName());
    System.out.printf("| Your current last name  : %-21s |\n", account.getName().getLastName());
    System.out.println("===================================================");
    System.out.println("\n<<< Please enter your new name below >>>");
    account.setName(AuthInput.getFirstName(), AuthInput.getLastName());
    Font.print(Font.ANSI_CYAN, "<<< Updating the name in the database... >>>");

    if (account instanceof Customer) {
      CustomerController.update((Customer) account);
    } else {
      AdminController.update((Admin) account);
    }

    Font.print(Font.ANSI_GREEN, "\n<<< Name is updated successfully! >>>");
    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();
  }

  // Display UI for editing phone number for customer
  private static void editPhoneNum() {
    Customer customer = (Customer) AuthService.getLoggedInAccount();

    Screen.clear();
    System.out.println("===================================================");
    System.out.println("|                Edit Phone Number                |");
    System.out.println("===================================================");
    System.out.printf("| Your current phone number : %-19s |\n",
        customer.getPhoneNum() == null ? "Unspecified" : customer.getPhoneNum());
    System.out.println("===================================================");
    System.out.println("\n<<< Please enter your new phone number below >>>");
    customer.setPhoneNum(AuthInput.getPhoneNum());

    Font.print(Font.ANSI_CYAN, "<<< Updating the phone number in the database... >>>");

    CustomerController.update(customer);

    Font.print(Font.ANSI_GREEN, "\n<<< Phone number is updated successfully! >>>");
    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();
  }

  // Display UI for editing date of birth for customer
  private static void editDateOfBirth() {
    Customer customer = (Customer) AuthService.getLoggedInAccount();

    Screen.clear();
    System.out.println("==================================================");
    System.out.println("|               Edit Date of Birth               |");
    System.out.println("==================================================");
    System.out.printf("| Your current date of birth : %-17s |\n",
        customer.getDateOfBirth() == null ? "Unspecified" : customer.getDateOfBirth().toString());
    System.out.println("==================================================");
    System.out.println("\n<<< Please enter your new date of birth below >>>");
    customer.setDateOfBirth(AuthInput.getDateOfBirth());

    Font.print(Font.ANSI_CYAN, "<<< Updating the date of birth in the database... >>>");

    CustomerController.update(customer);

    Font.print(Font.ANSI_GREEN, "\n<<< Date of birth is updated successfully! >>>");
    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();
  }

  // Display UI for changing password for both customer and admin
  private static void changePassword() {
    Account account = AuthService.getLoggedInAccount();
    boolean isCurrentPasswordCorrect;
    do {
      Screen.clear();
      System.out.println("===================================================");
      System.out.println("|                 Change Password                 |");
      System.out.println("===================================================");

      account.setPassword(AuthInput.getLoginPassword(true));

      Font.print(Font.ANSI_CYAN, "<<< Verifying password... >>>");

      // Check if the password is correct by reusing the AuthService.login() method
      if (account instanceof Customer) {
        isCurrentPasswordCorrect = CustomerController.checkPassword((Customer) account);
      } else {
        isCurrentPasswordCorrect = AdminController.checkPassword((Admin) account);
      }

      if (!isCurrentPasswordCorrect) {
        Font.print(Font.ANSI_RED, "\n<<< Incorrect password. >>>");
        if (!AuthInput.promptForContinue(PromptForContinueOption.CHANGE_PASSWORD)) {
          return;
        }
      } else {
        Font.print(Font.ANSI_GREEN, "\n<<< Correct password, you can now enter a new one! >>>");
      }
    } while (!isCurrentPasswordCorrect);

    account.setPassword(AuthInput.getSignupPassword(account, true));
    // Confirm password input
    AuthInput.confirmPassword(account.getPassword(), true);

    Font.print(Font.ANSI_CYAN, "<<< Changing the password in the database... >>>");

    if (account instanceof Customer) {
      CustomerController.updatePassword((Customer) account);
    } else {
      AdminController.updatePassword((Admin) account);
    }

    Font.print(Font.ANSI_GREEN, "\n<<< Password is changed successfully! >>>");
    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();
  }

  // Display UI for editing position for admin
  private static void editPosition() {
    Admin admin = (Admin) AuthService.getLoggedInAccount();

    Screen.clear();
    System.out.println("===================================================");
    System.out.println("|                  Edit Position                  |");
    System.out.println("===================================================");
    System.out.printf("| Your current position : %-23s |\n",
        admin.getPosition() == null ? "Unspecified" : admin.getPosition());
    System.out.println("===================================================");
    System.out.println("\n<<< Please enter your new position below >>>");
    admin.setPosition(AuthInput.getPosition());
    Font.print(Font.ANSI_CYAN, "<<< Updating the position in the database... >>>");

    AdminController.update(admin);

    Font.print(Font.ANSI_GREEN, "\n<<< Position is updated successfully! >>>");
    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();
  }
}