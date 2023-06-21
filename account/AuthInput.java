package account;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.regex.Pattern;

import main.Input;
import utility.CalendarUtils;
import utility.Font;

public class AuthInput {
  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
      Pattern.CASE_INSENSITIVE);
      
  public static boolean validateEmailFormat(String email) {
    return EMAIL_PATTERN.matcher(email).matches();
  }
  
  // Get input for login password, this function is reused for both
  // admin and customer login password input
  public static String getLoginPassword(boolean isCurrent) {
    String password;
    boolean validPassword;
    do {
      if (isCurrent) {
        System.out.print("> Enter current password: ");
      } else {
        System.out.print("> Enter password: ");
      }
      password = Input.SCANNER.nextLine();

      // Validate password input
      if (password.length() == 0) {
        if (isCurrent) {
          Font.print(Font.ANSI_RED, "<<< Current password is required, please enter your password. >>>");
        } else {
          Font.print(Font.ANSI_RED, "<<< Password is required, please enter your password. >>>");
        }        
        validPassword = false;
      } else {
        validPassword = true;
      }
    } while (!validPassword);
    return password;
  }

  // Get input for signup password, this function is reused for both
  // admin and customer signup password input
  // Parametric polymorphism
  public static String getSignupPassword(Account account, boolean isNew) {
    String signupPassword;
    boolean validSignupPassword;
    do {
      validSignupPassword = true;
      if (isNew) {
        System.out.print("> Enter new password: ");
      } else {
        System.out.print("> Enter password: ");
      }
      signupPassword = Input.SCANNER.nextLine();

      // Validate signup password input
      if (signupPassword.length() == 0) {
        Font.print(Font.ANSI_RED, "<<< Password is required, please enter your password. >>>");
        validSignupPassword = false;
      }

      if (validSignupPassword) {
        if (account instanceof Customer) {
          if (!CustomerView.validatePasswordFormat(signupPassword)) {
            Font.print(Font.ANSI_RED, "<<< Password is not strong enough, please try again. >>>");
            System.out.println(CustomerView.getPasswordRequirementMsg());
            validSignupPassword = false;
          } else {
            validSignupPassword = true;
          }
        } else {
          if (!AdminView.validatePasswordFormat(signupPassword)) {
            Font.print(Font.ANSI_RED, "<<< Password is not strong enough, please try again. >>>");
            System.out.println(AdminView.getPasswordRequirementMsg());
            validSignupPassword = false;
          } else {
            validSignupPassword = true;
          }
        }
      }
    } while (!validSignupPassword);
    return signupPassword;
  }

  // Get input for password confirmation
  public static void confirmPassword(String password, boolean isNew) {
    String confirmPassword;
    boolean validConfirmPassword;
    do {
      if (isNew) {
        System.out.print("> Confirm new password: ");
      } else {
        System.out.print("> Confirm password: ");
      }
      confirmPassword = Input.SCANNER.nextLine();

      if (confirmPassword.length() == 0) {
        Font.print(Font.ANSI_RED, "<<< It is required to confirm your password. >>>");
        validConfirmPassword = false;
      } else if (!confirmPassword.equals(password)) {
        Font.print(Font.ANSI_RED, "<<< Password does not match. >>>");
        validConfirmPassword = false;
      } else {
        validConfirmPassword = true;
      }
    } while (!validConfirmPassword);
  }

  // Get input for first name
  public static String getFirstName() {
    String firstName;
    boolean validfirstName;
    do {
      System.out.print("> Enter first name: ");
      firstName = Input.SCANNER.nextLine().trim();

      // Validate first name input
      if (firstName.length() == 0) {
        Font.print(Font.ANSI_RED, "<<< First name is required, please enter your first name. >>>");
        validfirstName = false;
      } else if (firstName.length() > 50) {
        Font.print(Font.ANSI_RED, "<<< First name is too long, please enter again. >>>");
        validfirstName = false;
      } else {
        validfirstName = true;
      }
    } while (!validfirstName);
    return firstName;
  }

  // Get input for last name
  public static String getLastName() {
    String lastName;
    boolean validLastName;
    do {
      System.out.print("> Enter last name: ");
      lastName = Input.SCANNER.nextLine().trim();

      // Validate last name input
      if (lastName.length() == 0) {
        Font.print(Font.ANSI_RED, "<<< Last name is required, please enter your last name. >>>");
        validLastName = false;
      } else if (lastName.length() > 50) {
        Font.print(Font.ANSI_RED, "<<< Last name is too long, please enter again. >>>");
        validLastName = false;
      } else {
        validLastName = true;
      }
    } while (!validLastName);
    return lastName;
  }

  // Get input for position
  public static String getPosition() {
    String position;
    boolean validPosition;
    do {
      System.out.print("> Enter position: ");
      position = Input.SCANNER.nextLine().trim();

      // Validate position input
      if (position.length() == 0) {
        Font.print(Font.ANSI_RED, "<<< Position is required, please enter your position. >>>");
        validPosition = false;
      } else if (position.length() > 50) {
        Font.print(Font.ANSI_RED, "<<< Position is too long, please enter again. >>>");
        validPosition = false;
      } else {
        validPosition = true;
      }
    } while (!validPosition);
    return position;
  }

  // Get input for phone number
  public static String getPhoneNum() {
    String phoneNum;
    boolean validPhoneNum;
    do {
      System.out.print("> Enter phone number: ");
      phoneNum = Input.SCANNER.nextLine().trim();

      // Validate phone number input
      if (phoneNum.length() == 0) {
        Font.print(Font.ANSI_RED, "<<< Phone number is required, please enter your phone number. >>>");
        validPhoneNum = false;
      } else if (phoneNum.length() > 12) {
        Font.print(Font.ANSI_RED, "<<< Phone number is too long, please enter again. >>>");
        validPhoneNum = false;
      } else {
        validPhoneNum = true;
      }
    } while (!validPhoneNum);
    return phoneNum;
  }

  // Get inputs for date of birth (year, month, day)
  public static LocalDate getDateOfBirth() {
    // Get year input
    int year = 0;
    boolean validYear = false;
    do {
      try {
        System.out.print("> Enter year: ");
        year = Input.SCANNER.nextInt();

        // Validate year input
        if (year < 1920 || year > LocalDate.now().getYear()) {
          Font.print(Font.ANSI_RED, "<<< Invalid year, please enter again. >>>");
          validYear = false;
        } else if (LocalDate.now().getYear() - year < 18) {
          Font.print(Font.ANSI_RED, "<<< You have to be at least 18 y/o. >>>");
          validYear = false;
        } else {
          validYear = true;
        }
      } catch (InputMismatchException inputMismatchException) {
        Font.print(Font.ANSI_RED, "<<< Invalid year, please enter integers only. >>>");
      }
      Input.SCANNER.nextLine();
    } while (!validYear);

    // Get month input
    int month = 0;
    boolean validMonth = false;
    do {
      try {
        System.out.print("> Enter month: ");
        month = Input.SCANNER.nextInt();

        // Validate month input
        if (month < 1 || month > 12) {
          Font.print(Font.ANSI_RED, "<<< Invalid month, please enter again. >>>");
          validMonth = false;
        } else {
          validMonth = true;
        }
      } catch (InputMismatchException inputMismatchException) {
        Font.print(Font.ANSI_RED, "<<< Invalid month, please enter integers only. >>>");
      }
      Input.SCANNER.nextLine();
    } while (!validMonth);

    String monthStr = "";
    if (month >= 10) {
      monthStr = Integer.toString(month);
    } else {
      monthStr = "0" + Integer.toString(month);
    }

    // Get day input
    int day = 0;
    boolean validDay = false;
    do {
      try {
        System.out.print("> Enter day: ");
        day = Input.SCANNER.nextInt();

        // Validate day input
        if (day < 1 || day > 31) {
          Font.print(Font.ANSI_RED, "<<< Invalid day, please enter again. >>>");
          validDay = false;
        } else if ((month == 4 || month == 6 || month == 9 || month == 11) && day == 31) {
          Font.print(Font.ANSI_RED, "<<< Day must be less than or equal to 30, please enter again. >>>");
          validDay = false;
        } else if (month == 2 && CalendarUtils.isLeapYear(year) && day > 29) {
          Font.print(Font.ANSI_RED, "<<< Day must be less than or equal to 29, please enter again. >>>");
          validDay = false;
        } else if (month == 2 && !CalendarUtils.isLeapYear(year) && day > 28) {
          Font.print(Font.ANSI_RED, "<<< Day must be less than or equal to 28, please enter again. >>>");
          validDay = false;
        } else {
          validDay = true;
        }
      } catch (InputMismatchException inputMismatchException) {
        Font.print(Font.ANSI_RED, "<<< Invalid day, please enter integers only. >>>");
      }
      Input.SCANNER.nextLine();
    } while (!validDay);

    String dayStr = "";
    if (day >= 10) {
      dayStr = Integer.toString(day);
    } else {
      dayStr = "0" + Integer.toString(day);
    }

    return LocalDate.parse(year + "-" + monthStr + "-" + dayStr);
  }

  public enum PromptForContinueOption {
    LOGIN_EMAIL,
    LOGIN_USERNAME,
    LOGIN_REPEAT,
    SIGNUP_EMAIL,
    SIGNUP_USERNAME,
    SIGNUP_CODE,
    CHANGE_PASSWORD
  }

  public static boolean promptForContinue(PromptForContinueOption option) {
    String promptText;
    if (option == PromptForContinueOption.LOGIN_EMAIL) {
      promptText = "> Do you want to enter an email again? Or no to quit login? (y/n): ";
    } else if (option == PromptForContinueOption.LOGIN_USERNAME) {
      promptText = "> Do you want to enter an username again? Or no to quit login? (y/n): ";
    } else if (option == PromptForContinueOption.LOGIN_REPEAT) {
      promptText = "> Do you want to continue logging in again? (y/n): ";
    } else if (option == PromptForContinueOption.SIGNUP_EMAIL) {
      promptText = "> Do you want to enter an email again? Or no to quit signup? (y/n): ";
    } else if (option == PromptForContinueOption.SIGNUP_USERNAME) {
      promptText = "> Do you want to enter an username again? Or no to quit signup? (y/n): ";
    } else if (option == PromptForContinueOption.SIGNUP_CODE) {
      promptText = "> Do you want to enter the code again? Or no to quit signup? (y/n): ";
    } else {
      promptText = "> Do you want to enter the current password again? Or no to stop? (y/n): ";
    }

    return Input.getYesNoInput(promptText, 0);
  }
}