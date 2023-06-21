package account;

import ordermenu.Cart;

public class AuthService {  
  private static Account loggedInAccount;

  public static enum SignupResult {
    SUCCESS,
    SUCCESS_BUT_EMAIL_ERROR,
    ERROR
  }

  public static Account getLoggedInAccount() {
    return AuthService.loggedInAccount;
  }

  public static void setLoggedInAccount(Account loggedInAccount) {
    AuthService.loggedInAccount = loggedInAccount;
  }

  public static void signout() {
    loggedInAccount = null;
    Cart.resetCartAndStock();
  }
}