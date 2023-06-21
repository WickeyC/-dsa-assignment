package account;

import java.util.regex.Pattern;

import account.AuthInput.PromptForContinueOption;
import account.AuthService.SignupResult;
import main.Display;
import main.Input;
import utility.Font;
import utility.Screen;

public class CustomerView implements Authenticatable {
  private static final Pattern USERNAME_PATTERN = Pattern
      .compile("^[a-z0-9]([._-](?![._-])|[a-zA-Z0-9]){0,28}[a-zA-Z0-9]$");
      
  // Display login UI for customer account
  @Override
  public void showLoginUI() {
    Customer customer = new Customer();

    Screen.clear();
    System.out.println("--------------------------------------------");
    System.out.println("|         Login to your BW account         |");
    System.out.println("--------------------------------------------");

    // ---------- Get email input
    String emailOrUsernameInput;
    boolean validEmailOrUsernameInput = true;
    do {
      validEmailOrUsernameInput = true;
      System.out.print("> Enter your email or username: ");
      emailOrUsernameInput = Input.SCANNER.nextLine().toLowerCase().trim();

      // Validate email input
      if (emailOrUsernameInput.length() == 0) {
        Font.print(Font.ANSI_RED, "<<< This field is required, please enter it. >>>");
        validEmailOrUsernameInput = false;
      } else if (AuthInput.validateEmailFormat(emailOrUsernameInput)) {
        Font.print(Font.ANSI_CYAN, "<<< Checking for the existence of this email in the database... >>>");
        boolean isEmailExists = CustomerController.isEmailExists(emailOrUsernameInput);
        if (!isEmailExists) {
          Font.print(Font.ANSI_RED, "<<< This email is not signed up yet. >>>");
          validEmailOrUsernameInput = false;
          if (!AuthInput.promptForContinue(PromptForContinueOption.LOGIN_EMAIL)) {
            return;
          }
        } else {
          Font.print(Font.ANSI_GREEN, "<<< Great! There's an account associated with this email. >>>");
          validEmailOrUsernameInput = true;
          customer.setEmail(emailOrUsernameInput);
        }
      } else if (validateUsernameFormat(emailOrUsernameInput)) {
        Font.print(Font.ANSI_CYAN, "<<< Checking for the existence of this username in the database... >>>");
        boolean isUsernameExists = CustomerController.isUsernameExists(emailOrUsernameInput);
        if (!isUsernameExists) {
          Font.print(Font.ANSI_RED, "<<< This username does not exist. >>>");
          validEmailOrUsernameInput = false;
          if (!AuthInput.promptForContinue(PromptForContinueOption.LOGIN_USERNAME)) {
            return;
          }
        } else {
          Font.print(Font.ANSI_GREEN, "<<< Great! There's an account associated with this username. >>>");
          validEmailOrUsernameInput = true;
          customer.setUsername(emailOrUsernameInput);
        }
      } else {
        Font.print(Font.ANSI_RED, "<<< Invalid email or username, please try again. >>>");
        validEmailOrUsernameInput = false;
      }
    } while (!validEmailOrUsernameInput);

    customer.setPassword(AuthInput.getLoginPassword(false));
    Font.print(Font.ANSI_CYAN, "<<< Logging in... >>>");

    boolean isLoginSuccessful = CustomerController.login(customer);

    if (isLoginSuccessful) {
      Font.print(Font.ANSI_GREEN, "\n<<< Successfully logged in! >>>");

      System.out.print("\nPress enter to continue...");
      Input.SCANNER.nextLine();
      Display.displayCustomerMenu();
    } else {
      Font.print(Font.ANSI_RED, "\n<<< Incorrect password. >>>");
      if (AuthInput.promptForContinue(PromptForContinueOption.LOGIN_REPEAT)) {
        this.showLoginUI();
      }
    }
  }

  // Display signup UI for customer account
  @Override
  public void showSignupUI() {
    Customer customer = new Customer();

    Screen.clear();
    System.out.println("--------------------------------------------");
    System.out.println("|          Create your BW account          |");
    System.out.println("--------------------------------------------");

    // ---------- Get email input
    boolean validEmailInput = true;
    do {
      validEmailInput = true;
      System.out.print("> Enter email: ");
      customer.setEmail(Input.SCANNER.nextLine().toLowerCase().trim());

      // Validate email input
      if (customer.getEmail().length() == 0) {
        Font.print(Font.ANSI_RED, "<<< Email is required, please enter your email. >>>");
        validEmailInput = false;
      } else if (!AuthInput.validateEmailFormat(customer.getEmail())) {
        Font.print(Font.ANSI_RED, "<<< Invalid email format, please try again. >>>");
        validEmailInput = false;
      }

      if (validEmailInput) {
        Font.print(Font.ANSI_CYAN, "<<< Checking if the email is already registered or not... >>>");
        boolean isEmailExists = CustomerController.isEmailExists(customer.getEmail());
        if (isEmailExists) {
          Font.print(Font.ANSI_RED, "<<< Email already registered, please use another email. >>>");
          validEmailInput = false;
          if (!AuthInput.promptForContinue(PromptForContinueOption.SIGNUP_EMAIL)) {
            return;
          }
        } else {
          Font.print(Font.ANSI_GREEN, "<<< Great! This email is valid for new registration. >>>");
          validEmailInput = true;
        }
      }
    } while (!validEmailInput);

    // ---------- Get username input
    boolean validUsernameInput = true;
    do {
      validUsernameInput = true;
      System.out.print("> Enter username: ");
      customer.setUsername(Input.SCANNER.nextLine().toLowerCase().trim());

      // Validate username input
      if (customer.getUsername().length() == 0) {
        Font.print(Font.ANSI_RED, "<<< Username is required, please enter your username. >>>");
        validUsernameInput = false;
      } else if (!validateUsernameFormat(customer.getUsername())) {
        Font.print(Font.ANSI_RED, "<<< Invalid username format, please try again. >>>");
        System.out.println(getUsernameRequirementMsg());
        validUsernameInput = false;
      }

      if (validUsernameInput) {
        Font.print(Font.ANSI_CYAN, "<<< Checking the availability of username... >>>");
        boolean isUsernameExists = CustomerController.isUsernameExists(customer.getUsername());
        if (isUsernameExists) {
          Font.print(Font.ANSI_RED, "<<< Username already exists, please try another username. >>>");
          validUsernameInput = false;
          if (!AuthInput.promptForContinue(PromptForContinueOption.SIGNUP_USERNAME)) {
            return;
          }
        } else {
          Font.print(Font.ANSI_GREEN, "<<< Great! This username is available. >>>");
          validUsernameInput = true;
        }
      }
    } while (!validUsernameInput);

    customer.setPassword(AuthInput.getSignupPassword(customer, false));
    AuthInput.confirmPassword(customer.getPassword(), false);
    customer.setName(AuthInput.getFirstName(), AuthInput.getLastName());

    Font.print(Font.ANSI_CYAN, "<<< Signing up... >>>");
    
    SignupResult signupResult = CustomerController.signup(customer);

    if (signupResult == SignupResult.SUCCESS) {
      Font.print(Font.ANSI_GREEN, "\n<<< Successfully signed up! >>>");
      Font.print(Font.ANSI_GREEN, "Please check your email for new user voucher.");

      System.out.print("\nPress enter to continue...");
      Input.SCANNER.nextLine();
      Display.displayCustomerMenu();
    } else if (signupResult == SignupResult.SUCCESS_BUT_EMAIL_ERROR) {
      Font.print(Font.ANSI_GREEN, "\n<<< Successfully signed up! >>>");
      Font.print(Font.ANSI_YELLOW, "However, welcome email is not sent successfully due to Internet or server error.");
      System.out.println("It's okay, here's the new user voucher code: NEWUSER");

      System.out.print("\nPress enter to continue...");
      Input.SCANNER.nextLine();
      Display.displayCustomerMenu();
    } else {
      Font.print(Font.ANSI_RED, "\n<<< Error signing up. >>>");

      System.out.print("\nPress enter to continue...");
      Input.SCANNER.nextLine();
    }
  }

  // Check if the username fulfills the format requirements or not
  public static boolean validateUsernameFormat(String username) {
    return USERNAME_PATTERN.matcher(username).matches();
  }

  // Check if the password fulfills the format requirements or not
  public static boolean validatePasswordFormat(String password) {
    return (password.length() >= 4 && password.length() <= 50)
        && password.matches(".*[a-zA-Z]+.*")
        && password.matches(".*[0-9].*");
  }

  public static String getUsernameRequirementMsg() {
    return Font.getStr(Font.ANSI_YELLOW, "Username Requirements: \n"
        + "- Only alphanumeric characters, dot (.), underscore (_) and hypen (-) are allowed.\n"
        + "- The dot (.), underscore (_), or hyphen (-) must not be the first or last character.\n"
        + "- The dot (.), underscore (_), or hyphen (-) does not appear consecutively.\n"
        + "- The number of characters must be between 2 to 30.");
  }

  public static String getPasswordRequirementMsg() {
    return Font.getStr(Font.ANSI_YELLOW, "Password Requirements: \n"
        + "- Password must have at least 4 characters and maximum 50 characters.\n"
        + "- Password must contain at least 1 number and 1 letter.");
  }
}
