package account;

import java.util.regex.Pattern;

import account.AuthInput.PromptForContinueOption;
import account.AuthService.SignupResult;
import main.Display;
import main.Input;
import utility.Font;
import utility.Screen;

public class AdminView implements Authenticatable {
  private static final Pattern USERNAME_PATTERN = Pattern
      .compile("^[a-z0-9]([._-](?![._-])|[a-zA-Z0-9]){2,18}[a-zA-Z0-9]$"); 
  private static final Pattern PASSWORD_PATTERN = Pattern
      .compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[.!@#&()-[{}]:;',?/*~$^+=<>]).{6,50}$");
  public static final String SECRET_SIGNUP_CODE = "DSA";

  // Display login UI for admin account
  @Override
  public void showLoginUI() {
    Admin admin = new Admin();

    Screen.clear();
    System.out.println("---------------------------------------------");
    System.out.println("|        Login to your admin account        |");
    System.out.println("---------------------------------------------");

    // ---------- Get email input
    String emailOrUsernameInput;
    boolean validEmailOrUsernameInput = true;
    do {
      validEmailOrUsernameInput = true;
      System.out.print("> Enter your email or username: ");
      emailOrUsernameInput = Input.SCANNER.nextLine().toLowerCase().trim();

      // Validate emailOrUsernameInput
      if (emailOrUsernameInput.length() == 0) {
        Font.print(Font.ANSI_RED, "<<< This field is required, please enter it. >>>");
        validEmailOrUsernameInput = false;
      } else if (AuthInput.validateEmailFormat(emailOrUsernameInput)) {
        Font.print(Font.ANSI_CYAN, "<<< Checking for the existence of this email in the database... >>>");
        boolean isEmailExists = AdminController.isEmailExists(emailOrUsernameInput);
        if (!isEmailExists) {
          Font.print(Font.ANSI_RED, "<<< This email is not signed up yet. >>>");
          validEmailOrUsernameInput = false;
          if (!AuthInput.promptForContinue(PromptForContinueOption.LOGIN_EMAIL)) {
            return;
          }
        } else {
          Font.print(Font.ANSI_GREEN, "<<< Great! There's an account associated with this email. >>>");
          validEmailOrUsernameInput = true;
          admin.setEmail(emailOrUsernameInput);
        }
      } else if (validateUsernameFormat(emailOrUsernameInput)) {
        Font.print(Font.ANSI_CYAN, "<<< Checking for the existence of this username in the database... >>>");
        boolean isUsernameExists = AdminController.isUsernameExists(emailOrUsernameInput);
        if (!isUsernameExists) {
          Font.print(Font.ANSI_RED, "<<< This username does not exist. >>>");
          validEmailOrUsernameInput = false;
          if (!AuthInput.promptForContinue(PromptForContinueOption.LOGIN_USERNAME)) {
            return;
          }
        } else {
          Font.print(Font.ANSI_GREEN, "<<< Great! There's an account associated with this username. >>>");
          validEmailOrUsernameInput = true;
          admin.setUsername(emailOrUsernameInput);
        }
      } else {
        Font.print(Font.ANSI_RED, "<<< Invalid email or username, please try again. >>>");
        validEmailOrUsernameInput = false;
      }
    } while (!validEmailOrUsernameInput);

    admin.setPassword(AuthInput.getLoginPassword(false));
    Font.print(Font.ANSI_CYAN, "<<< Logging in... >>>");

    // Polymorphism
    boolean isLoginSuccessful = AdminController.login(admin);

    if (isLoginSuccessful) {
      Font.print(Font.ANSI_GREEN, "\n<<< Successfully logged in! >>>");

      System.out.print("\nPress enter to continue...");
      Input.SCANNER.nextLine();
      Display.displayAdminMenu();
    } else {
      Font.print(Font.ANSI_RED, "\n<<< Incorrect password. >>>");
      if (AuthInput.promptForContinue(PromptForContinueOption.LOGIN_REPEAT)) {
        this.showLoginUI();
      }
    }
  }

  // Display signup UI for admin account
  @Override
  public void showSignupUI() {
    Admin admin = new Admin();

    Screen.clear();
    System.out.println("---------------------------------------------");
    System.out.println("|          Create an admin account          |");
    System.out.println("---------------------------------------------");

    // ---------- Get secret signup code input
    String secretSignupCodeInput;
    boolean validSecretSignupCodeInput;
    do {
      System.out.print("> Enter secret signup code: ");
      secretSignupCodeInput = Input.SCANNER.nextLine();

      // Validate secretSignupCodeInput
      if (secretSignupCodeInput.length() == 0) {
        Font.print(Font.ANSI_RED, "<<< Secret signup code is required, please enter it. >>>");
        validSecretSignupCodeInput = false;
      } else if (!secretSignupCodeInput.equals(SECRET_SIGNUP_CODE)) {
        Font.print(Font.ANSI_RED, "<<< Wrong secret signup code. >>>");
        validSecretSignupCodeInput = false;
        if (!AuthInput.promptForContinue(PromptForContinueOption.SIGNUP_CODE)) {
          return;
        }
      } else {
        Font.print(Font.ANSI_GREEN, "<<< Great! The secret signup code is correct. >>>");
        validSecretSignupCodeInput = true;
      }
    } while (!validSecretSignupCodeInput);

    // ---------- Get email input
    boolean validEmailInput = true;
    do {
      validEmailInput = true;
      System.out.print("> Enter email: ");
      admin.setEmail(Input.SCANNER.nextLine().toLowerCase().trim());

      // Validate email input
      if (admin.getEmail().length() == 0) {
        Font.print(Font.ANSI_RED, "<<< Email is required, please enter your email. >>>");
        validEmailInput = false;
      } else if (!AuthInput.validateEmailFormat(admin.getEmail())) {
        Font.print(Font.ANSI_RED, "<<< Invalid email format, please try again. >>>");
        validEmailInput = false;
      }

      if (validEmailInput) {
        Font.print(Font.ANSI_CYAN, "<<< Checking if the email is already registered or not... >>>");
        boolean isEmailExists = AdminController.isEmailExists(admin.getEmail());
        if (isEmailExists) {
          Font.print(Font.ANSI_RED, "<<< Email already registered, please use another email. >>>");
          validEmailInput = false;
          if (!AuthInput.promptForContinue(PromptForContinueOption.LOGIN_EMAIL)) {
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
      admin.setUsername(Input.SCANNER.nextLine().toLowerCase().trim());

      // Validate username input
      if (admin.getUsername().length() == 0) {
        Font.print(Font.ANSI_RED, "<<< Username is required, please enter your username. >>>");
        validUsernameInput = false;
      } else if (!validateUsernameFormat(admin.getUsername())) {
        Font.print(Font.ANSI_RED, "<<< Invalid username format, please try again. >>>");
        System.out.println(getUsernameRequirementMsg());
        validUsernameInput = false;
      }

      if (validUsernameInput) {
        Font.print(Font.ANSI_CYAN, "<<< Checking the availability of username... >>>");
        boolean isUsernameExists = AdminController.isUsernameExists(admin.getUsername());
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

    admin.setPassword(AuthInput.getSignupPassword(admin, false));
    AuthInput.confirmPassword(admin.getPassword(), false);
    admin.setName(AuthInput.getFirstName(), AuthInput.getLastName());
    admin.setPosition(AuthInput.getPosition());

    Font.print(Font.ANSI_CYAN, "<<< Signing up... >>>");

    SignupResult signupResult = AdminController.signup(admin);

    if (signupResult == AuthService.SignupResult.SUCCESS) {
      // Successfully signed up
      Font.print(Font.ANSI_GREEN, "\n<<< Successfully signed up! >>>");
      Font.print(Font.ANSI_GREEN, "Please check your email for our welcome message.");

      System.out.print("\nPress enter to continue...");
      Input.SCANNER.nextLine();
      Display.displayAdminMenu();
    } else if (signupResult == AuthService.SignupResult.SUCCESS_BUT_EMAIL_ERROR) {
      // Successfully signed up but somehow email does not get delivered
      Font.print(Font.ANSI_GREEN, "\n<<< Successfully signed up! >>>");
      Font.print(Font.ANSI_YELLOW, "However, welcome email is not sent successfully due to Internet or server error.");

      System.out.print("\nPress enter to continue...");
      Input.SCANNER.nextLine();
      Display.displayAdminMenu();
    } else {
      // Signup was not successful
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
    return PASSWORD_PATTERN.matcher(password).matches();
  }

  public static String getUsernameRequirementMsg() {
    return Font.getStr(Font.ANSI_YELLOW, "Username Requirements: \n"
        + "- Only alphanumeric characters, dot (.), underscore (_) and hypen (-) are allowed.\n"
        + "- The dot (.), underscore (_), or hyphen (-) must not be the first or last character.\n"
        + "- The dot (.), underscore (_), or hyphen (-) does not appear consecutively.\n"
        + "- The number of characters must be between 4 to 20.");
  }

  public static String getPasswordRequirementMsg() {
    return Font.getStr(Font.ANSI_YELLOW, "Password Requirements: \n"
        + "- Password must have at least 6 characters and maximum 50 characters.\n"
        + "- Password must contain at least 1 number.\n"
        + "- Password must at least one lowercase and one uppercase alphabet.\n"
        + "- Password must contain at least one special character like ! @ # & ( ).");
  }
}
