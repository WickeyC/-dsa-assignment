package account;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import account.AuthService.SignupResult;
import utility.Email;
import utility.PasswordUtils;

public class AdminController {
  // Check if the email already exist in the database or not
  public static boolean isEmailExists(String email) {
    return AdminRepository.findBy("email", email) != null;
  }

  // Check if the username already exist in the database or not
  public static boolean isUsernameExists(String username) {
    return AdminRepository.findBy("username", username) != null;
  }

  public static boolean login(Admin admin) {
    Admin foundAdmin = AdminRepository.findByEmailOrUsername(admin);
    if (foundAdmin == null) {
      return false;
    }

    String providedPassword = admin.getPassword();
    String storedPassword = foundAdmin.getPassword();
    boolean passwordMatch = PasswordUtils.verifyUserPassword(providedPassword, storedPassword);

    if (passwordMatch) {
      // Set the currently logged in account and send the login email
      AuthService.setLoggedInAccount(foundAdmin);
      new Thread(() -> {
        generateLoginEmail(foundAdmin.getEmail(), foundAdmin.getUsername());
      }).start();
    }

    return passwordMatch;
  }

  public static SignupResult signup(Admin admin) {
    boolean isSignupSuccessful = AdminRepository.create(admin);

    if (isSignupSuccessful) {
      // Set the currently logged in account and send the welcome email
      AuthService.setLoggedInAccount(admin);
      if (generateWelcomeEmail(admin.getEmail(), admin.getUsername(), admin.getPosition())) {
        return SignupResult.SUCCESS;
      } else {
        return SignupResult.SUCCESS_BUT_EMAIL_ERROR;
      }
    } else {
      return SignupResult.ERROR;
    }
  }

  public static void update(Admin admin) {
    AdminRepository.update(admin);
  }

  public static boolean checkPassword(Admin admin) {
    Admin foundAdmin = AdminRepository.findByEmailOrUsername(admin);
    if (foundAdmin == null) {
      return false;
    }

    String providedPassword = admin.getPassword();
    String storedPassword = foundAdmin.getPassword();
    boolean passwordMatch = PasswordUtils.verifyUserPassword(providedPassword, storedPassword);

    return passwordMatch;
  }

  public static void updatePassword(Admin admin) {
    AdminRepository.updatePassword(admin);
  }

  // Send a customized welcome email for admin user
  private static boolean generateWelcomeEmail(String email, String username, String position) {
    String toEmail = email;
    String subject = "Successful BW Admin Registration";
    String text = "Account type: admin\n"
        + "Your email: " + toEmail + "\n"
        + "Your username: " + username + "\n"
        + "Your position: " + position;
    return Email.sendEmail(toEmail, subject, text);
  }

  // Send a customized login email for admin user
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
}
