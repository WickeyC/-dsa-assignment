package checkout;

import account.AuthService;
import account.Customer;
import account.CustomerController;
import ordermenu.Cart;

public class Card {
  // ******************************
  // --- --- Data members
  // ******************************
  private static final int VISA_POINTS = 200;
  private static final int VISA_FEE_PCT = 0;
  private static final int OTHER_POINTS = 50;
  private static final int OTHER_FEE_PCT = 2;
  private double amount;
  private String cardNumber;
  private String nameOnCard;
  private int expirationMonth;
  private int expirationYear;
  private String cvc;

  // ******************************
  // --- --- Constructors
  // ******************************
  public Card() {
    this(0);
  }

  public Card(double amount) {
    this.amount = amount;
    this.cardNumber = null;
    this.nameOnCard = null;
    this.expirationMonth = 0;
    this.expirationYear = 0;
    this.cvc = null;
  }

  public Card(
      String cardNumber,
      String nameOnCard,
      int expirationMonth,
      int expirationYear,
      String cvc) {
    this.amount = 0.0;
    this.cardNumber = cardNumber;
    this.nameOnCard = nameOnCard;
    this.expirationMonth = expirationMonth;
    this.expirationYear = expirationYear;
    this.cvc = cvc;
  }

  // ******************************
  // --- --- Methods
  // ******************************
  public double getAmount() {
    return this.amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public String getCardNumber() {
    return this.cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public String getNameOnCard() {
    return this.nameOnCard;
  }

  public void setNameOnCard(String nameOnCard) {
    this.nameOnCard = nameOnCard;
  }

  public int getExpirationMonth() {
    return this.expirationMonth;
  }

  public void setExpirationMonth(int expirationMonth) {
    this.expirationMonth = expirationMonth;
  }

  public int getExpirationYear() {
    return this.expirationYear;
  }

  public void setExpirationYear(int expirationYear) {
    this.expirationYear = expirationYear;
  }

  public String getCvc() {
    return this.cvc;
  }

  public void setCvc(String cvc) {
    this.cvc = cvc;
  }

  // Check if the card is a Visa card
  private boolean isVisaCard() {
    return this.cardNumber.matches("^4[0-9]{6,}$");
  }

  private void onPaymentSuccess() {
    Customer customer = (Customer) AuthService.getLoggedInAccount();

    Order.getCurrentOrder().setStatus("PROCESSING");
    Order.getCurrentOrder().setPointsEarned(calcPointsToEarn());
    int currentPoints = customer.getPoints();
    currentPoints -= Order.getCurrentOrder().getPointsUsed();
    currentPoints += Order.getCurrentOrder().getPointsEarned();
    customer.setPoints(currentPoints);
    
    OrderController.generateOrder(Order.getCurrentOrder(), customer);
    CustomerController.update(customer);

    // Checkout done
    Cart.checkOutDone();
  }

  // Display transaction details
  public void displayTransactionDetails() {
    System.out.println("\n+----------------------------------+");
    System.out.println("| Points to earn      : " + String.format("%-11s", this.calcPointsToEarn()) + "|");
    System.out.println("| Transaction fee     : "
        + String.format("%-11s",
            (this.calcTransactionFee() == 0 ? "FREE" : String.format("RM%.2f", this.calcTransactionFee())))
        + "|");
    System.out.println("| Final amount to pay : RM"
        + String.format("%-9s", String.format("%.2f", this.calcFinalAmountToPay())) + "|");
    System.out.println("+----------------------------------+");
  }

  // Calculate the points that will be earned by the customer
  // based on the payment card type
  public int calcPointsToEarn() {
    if (isVisaCard()) {
      return VISA_POINTS;
    } else {
      return OTHER_POINTS;
    }
  }

  // Calculate the transaction fee
  // based on the payment card type
  public double calcTransactionFee() {
    if (isVisaCard()) {
      return this.getAmount() * ((double) VISA_FEE_PCT / 100);
    } else {
      return this.getAmount() * ((double) OTHER_FEE_PCT / 100);
    }
  }

  // Calculate the final amount required to pay
  // which is the transaction fee plus the latest amount
  public double calcFinalAmountToPay() {
    return calcTransactionFee() + this.getAmount();
  }

  public String pay() {
    // Check if the card is one of the 4 dummy cards
    if (CardView.passLuhnCheck(this.cardNumber)) {
      this.onPaymentSuccess();
      return "CARD_SUCCESSFUL";
    } else {
      return "CARD_UNSUCCESSFUL";
    }
  }
}