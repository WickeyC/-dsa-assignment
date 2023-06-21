package account;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import account.AuthService.SignupResult;
import utility.Email;
import utility.PasswordUtils;

public class CustomerController {
  // Check if the email already exist in the database or not
  public static boolean isEmailExists(String email) {
    return CustomerRepository.findBy("email", email) != null;
  }

  // Check if the username already exist in the database or not
  public static boolean isUsernameExists(String username) {
    return CustomerRepository.findBy("username", username) != null;
  }

  public static boolean login(Customer customer) {
    Customer foundCustomer = CustomerRepository.findByEmailOrUsername(customer);
    if (foundCustomer == null) {
      return false;
    }

    String providedPassword = customer.getPassword();
    String storedPassword = foundCustomer.getPassword();
    boolean passwordMatch = PasswordUtils.verifyUserPassword(providedPassword, storedPassword);

    if (passwordMatch) {
      // Set the currently logged in account and send the login email
      AuthService.setLoggedInAccount(foundCustomer);
      // Send login email asynchronously
      new Thread(() -> {
        generateLoginEmail(foundCustomer.getEmail(), foundCustomer.getUsername());
      }).start();
    }

    return passwordMatch;
  }

  public static SignupResult signup(Customer customer) {
    boolean isSignupSuccessful = CustomerRepository.create(customer);

    if (isSignupSuccessful) {
      // Set the currently logged in account and send the welcome email
      AuthService.setLoggedInAccount(customer);
      if (generateWelcomeEmail(customer.getEmail(), customer.getUsername())) {
        return SignupResult.SUCCESS;
      } else {
        return SignupResult.SUCCESS_BUT_EMAIL_ERROR;
      }
    } else {
      return SignupResult.ERROR;
    }
  }

  public static void update(Customer customer) {
    CustomerRepository.update(customer);
  }

  public static boolean checkPassword(Customer customer) {
    Customer foundCustomer = CustomerRepository.findByEmailOrUsername(customer);
    if (foundCustomer == null) {
      return false;
    }

    String providedPassword = customer.getPassword();
    String storedPassword = foundCustomer.getPassword();
    boolean passwordMatch = PasswordUtils.verifyUserPassword(providedPassword, storedPassword);

    return passwordMatch;
  }

  public static void updatePassword(Customer customer) {
    CustomerRepository.updatePassword(customer);
  }

  // Send a customized login email for customer user
  private static boolean generateLoginEmail(String email, String username) {
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    String toEmail = email;
    String subject = "BW Account Login Notification";
    String text = "Your email: " + toEmail + "\n"
        + "Your username: " + username + "\n"
        + "Login time: " + dateFormat.format(date);
    return Email.sendEmail(toEmail, subject, text);
  }

  // Send a customized welcome email for customer user
  private static boolean generateWelcomeEmail(String email, String username) {
    String toEmail = email;
    String subject = "Welcome to BW App";
    String text = "Account type: customer\n"
        + "Your email: " + toEmail + "\n"
        + "Your username: " + username + "\n"
        + "New user voucher: NEWUSER";
    return Email.sendEmail(toEmail, subject, text);
  }
}
