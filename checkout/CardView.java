package checkout;

import java.time.LocalDate;
import java.util.InputMismatchException;

import adt.ListInterface;
import adt.MapInterface;
import main.Input;
import utility.Font;
import utility.Screen;

public class CardView {
  // Get input for card number
  public static String getCardNumberInput() {
    String cardNumber;
    boolean validCardNumber;
    do {
      System.out.print("> Enter card number: ");
      cardNumber = Input.SCANNER.nextLine().trim();

      // Validate card number input
      if (!passLuhnCheck(cardNumber)) {
        Font.print(Font.ANSI_RED, "<<< Invalid card number, please enter again. >>>");
        validCardNumber = false;
      } else if (cardNumber.length() == 0) {
        Font.print(Font.ANSI_RED, "<<< Card number is required, please enter it. >>>");
        validCardNumber = false;
      } else if (cardNumber.length() != 16 || !cardNumber.matches("[0-9]+")) {
        Font.print(Font.ANSI_RED, "<<< Card number must be a 16-digit number without spaces. >>>");
        validCardNumber = false;
      } else if (CardRepository.findOneByCardNumber(cardNumber) != null) {
        Font.print(Font.ANSI_RED, "<<< This card already exists in the system. >>>");
        validCardNumber = false;
      } else {
        validCardNumber = true;
      }
    } while (!validCardNumber);
    return cardNumber;
  }

  // Get input for name on card
  public static String getNameOnCardInput() {
    String nameOnCard;
    boolean validNameOnCard;
    do {
      System.out.print("> Enter name on card: ");
      nameOnCard = Input.SCANNER.nextLine().trim().toUpperCase();

      // Validate name on card input
      if (nameOnCard.length() == 0) {
        Font.print(Font.ANSI_RED, "<<< Name on card is required, please enter it. >>>");
        validNameOnCard = false;
      } else if (nameOnCard.length() > 20) {
        Font.print(Font.ANSI_RED, "<<< Name on card is too long. >>>");
        validNameOnCard = false;
      } else {
        validNameOnCard = true;
      }
    } while (!validNameOnCard);
    return nameOnCard;
  }

  // Get input for expiration month
  public static int getExpirationMonthInput() {
    int expirationMonth = 0;
    boolean validExpirationMonth = false;
    do {
      try {
        System.out.print("> Enter expiration month (MM): ");
        expirationMonth = Input.SCANNER.nextInt();

        // Validate expiration month
        if (expirationMonth < 1 || expirationMonth > 12) {
          Font.print(Font.ANSI_RED, "<<< Invalid month, please enter again. >>>");
          validExpirationMonth = false;
        } else {
          validExpirationMonth = true;
        }
      } catch (InputMismatchException inputMismatchException) {
        Font.print(Font.ANSI_RED, "<<< Invalid month, please enter integers only. >>>");
      }
      Input.SCANNER.nextLine();
    } while (!validExpirationMonth);
    return expirationMonth;
  }

  // Get input for expiration year
  public static int getExpirationYearInput() {
    int expirationYear = 0;
    boolean validExpirationYear = false;
    do {
      try {
        System.out.print("> Enter expiration year (YY): ");
        expirationYear = Input.SCANNER.nextInt();

        // Validate expiration year
        if (expirationYear < LocalDate.now().getYear() % 1000 || expirationYear > 99) {
          Font.print(Font.ANSI_RED, "<<< Invalid year, please enter again. >>>");
          validExpirationYear = false;
        } else {
          validExpirationYear = true;
        }
      } catch (InputMismatchException inputMismatchException) {
        Font.print(Font.ANSI_RED, "<<< Invalid year, please enter integers only. >>>");
      }
      Input.SCANNER.nextLine();
    } while (!validExpirationYear);
    return expirationYear;
  }

  // Get input for CVC
  public static String getCvcInput() {
    String cvc;
    boolean validCvc;
    do {
      System.out.print("> Enter card CVC: ");
      cvc = Input.SCANNER.nextLine().trim();

      // Validate CVC input
      if (cvc.length() == 0) {
        Font.print(Font.ANSI_RED, "<<< CVC is required, please enter it. >>>");
        validCvc = false;
      } else if (cvc.length() != 3 || !cvc.matches("[0-9]+")) {
        Font.print(Font.ANSI_RED, "<<< CVC must be a 3-digit number. >>>");
        validCvc = false;
      } else {
        validCvc = true;
      }
    } while (!validCvc);
    return cvc;
  }

  // The Luhn algorithm to validate credit card numbers
  public static boolean passLuhnCheck(String cardNumber) {
    int[] ints = new int[cardNumber.length()];
    for (int i = 0; i < cardNumber.length(); i++) {
      ints[i] = Integer.parseInt(cardNumber.substring(i, i + 1));
    }
    for (int i = ints.length - 2; i >= 0; i = i - 2) {
      int j = ints[i];
      j = j * 2;
      if (j > 9) {
        j = j % 10 + 1;
      }
      ints[i] = j;
    }
    int sum = 0;
    for (int i = 0; i < ints.length; i++) {
      sum += ints[i];
    }
    return sum % 10 == 0;
  }

  // Print the card details
  public static void printCardDetailsInput(Card card) {
    String cardNumber = card.getCardNumber() == null ? "#### #### #### ####" : card.getCardNumber();
    String nameOnCard = card.getNameOnCard() == null ? "?" : card.getNameOnCard();
    String expirationMonthStr = "";
    if (card.getExpirationMonth() >= 10) {
      expirationMonthStr = Integer.toString(card.getExpirationMonth());
    } else {
      expirationMonthStr = "0" + Integer.toString(card.getExpirationMonth());
    }
    String expirationYearStr = "";
    if (card.getExpirationYear() >= 10) {
      expirationYearStr = Integer.toString(card.getExpirationYear());
    } else {
      expirationYearStr = "0" + Integer.toString(card.getExpirationYear());
    }
    String expirationMonth = card.getExpirationMonth() == 0 ? "MM" : expirationMonthStr;
    String expirationYear = card.getExpirationYear() == 0 ? "YY" : expirationYearStr;
    String cvc = card.getCvc() == null ? "***" : card.getCvc();
    System.out.println("| Card Number  : " + String.format("%-43s", cardNumber) + " |");
    System.out.println("| Name on Card : " + String.format("%-43s", nameOnCard) + " |");
    System.out.println("| Expiry Date  : " + String.format("%-43s", expirationMonth + "/" + expirationYear) + " |");
    System.out.println("| CVC          : " + String.format("%-43s", cvc) + " |");
  }

  public static void printCards(ListInterface<String> cardList, MapInterface<String, Card> cards) {
    if (cards.isEmpty()) {
      System.out.println("| No cards                                                   |");
    } else {
      System.out.println("| NO | Card Details                                          |");
      System.out.println("|------------------------------------------------------------|");

      int i = 0;
      for (String cardNumber : cardList) {
        System.out.println("| " + String.format("%-2s", (i + 1)) + " | Card Number  : "
            + String.format("%-38s", cards.get(cardNumber).getCardNumber()) + " |");
        System.out
            .println("|    | Name on Card : " + String.format("%-38s", cards.get(cardNumber).getNameOnCard()) + " |");
        System.out
            .println("|    | Expiry Date  : "
                + String.format("%-38s",
                    cards.get(cardNumber).getExpirationMonth() + "/" + cards.get(cardNumber).getExpirationYear())
                + " |");
        System.out.println("|    | CVC          : " + String.format("%-38s", cards.get(cardNumber).getCvc()) + " |");
        if (i != cards.size() - 1) {
          System.out.println("|------------------------------------------------------------|");
        }
        i++;
      }
    }
  }

  // Display the Credit/Debit Card Payment Form
  public static void printHeading(Card card) {
    Screen.clear();
    System.out.println("==============================================================");
    System.out.println("|                   Credit/Debit Card Form                   |");
    System.out.println("==============================================================");
    // Print card details
    CardView.printCardDetailsInput(card);
    System.out.println("==============================================================");
    if (card.getCvc() == null) {
      System.out.println("\n<<< Please specify your card details below >>>");
    }
  }
}
