package checkout;

import java.util.InputMismatchException;

import account.AuthService;
import account.Customer;
import main.Display;
import main.Input;
import utility.Font;
import utility.Screen;

public class CardPaymentPage {
  public static void showCardPaymentUI() {
    // Set the payment method to Credit/Debit Card
    Order.getCurrentOrder().setPaymentMethod("Credit/Debit Card");

    Screen.clear();
    System.out.println("+---------------------------------------------------------------+");
    System.out.println("|             Credit/Debit Card Payment Information             |");
    System.out.println("|---------------------------------------------------------------|");
    System.out.println("| + Zero fee for Visa card, 2% for other issuers                |");
    System.out.println("| + Earn 200 points for Visa card, 50 for other issuers         |");
    System.out.println("+---------------------------------------------------------------+");
    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();

    Screen.clear();
    System.out.println("==============================================================");
    System.out.println("|                  Your Credit/Debit Cards                   |");
    System.out.println("==============================================================");

    Customer customer = (Customer) AuthService.getLoggedInAccount();
    CardManagement.loadCardList(customer.getCards());
    CardView.printCards(CardManagement.getCardList(), customer.getCards());

    System.out.println("==============================================================");

    Card chosenCard = null;
    if (customer.getCards().isEmpty()) {
      // No cards exist in the account
      chosenCard = createNewCard();
    } else {
      // Ask the user whether he wants to choose an existing card or create a new one.
      boolean toChooseExistingCard = Input
          .getYesNoInput("\n> Do you want to choose an existing card? Or no to enter a new one? (y/n): ", 0);
      if (toChooseExistingCard) {
        int no = 0;
        do {
          try {
            System.out.print("> Please choose your card by entering the NO: ");
            no = Input.SCANNER.nextInt();

            String cardNumber = CardManagement.getCardList().get(no - 1);
            chosenCard = customer.getCards().get(cardNumber);

            if (chosenCard == null) {
              Font.print(Font.ANSI_RED, "             <<< Invalid NO, please enter again. >>>");
            } else {
              Font.print(Font.ANSI_GREEN, "<<< Card chosen successfully. >>>");
            }
          } catch (InputMismatchException inputMismatchException) {
            Font.print(Font.ANSI_RED, "<<< Invalid NO, please enter integers only. >>>");
          }
          Input.SCANNER.nextLine();
        } while (chosenCard == null);
      } else {
        // Let the user create a new card on the spot.
        chosenCard = createNewCard();
      }
    }

    chosenCard.setAmount(Order.getCurrentOrder().getFinalPrice());
    Order.getCurrentOrder().setFinalPrice(chosenCard.calcFinalAmountToPay());

    // Display transaction details and process the payment
    chosenCard.displayTransactionDetails();
    showPaymentProcessingUI(chosenCard);
  }

  private static Card createNewCard() {
    Customer customer = (Customer) AuthService.getLoggedInAccount();
    Card card = new Card(Order.getCurrentOrder().getFinalPrice());
    // Prompt inputs for card information
    CardView.printHeading(card);
    card.setCardNumber(CardView.getCardNumberInput());
    CardView.printHeading(card);
    card.setNameOnCard(CardView.getNameOnCardInput());
    CardView.printHeading(card);
    card.setExpirationMonth(CardView.getExpirationMonthInput());
    CardView.printHeading(card);
    card.setExpirationYear(CardView.getExpirationYearInput());
    CardView.printHeading(card);
    card.setCvc(CardView.getCvcInput());

    CardView.printHeading(card);

    CardRepository.create(card, customer.getEmail());
    customer.getCards().put(card.getCardNumber(), card);
    Font.print(Font.ANSI_GREEN, "\n<<< New card is added successfully! >>>");

    return card;
  }

  public static void showPaymentProcessingUI(Card card) {
    Font.print(Font.ANSI_CYAN, "<<< Trying to charge your card... >>>");
    String paymentResult = card.pay();
    if (paymentResult.equals("CARD_SUCCESSFUL")) {
      // Display successful message when payment is sucessful
      Font.print(Font.ANSI_GREEN,
          String.format("\n<<< RM%.2f is paid using your card successfully! >>>", card.calcFinalAmountToPay()));
      System.out.print("\nPress enter to continue...");
      Input.SCANNER.nextLine();
      OrderPlacedPage.showOrderPlacedUI();
    } else {
      // Display unsuccessful message and prompt whether to pay again when payment is
      // not sucessful
      Font.print(Font.ANSI_RED, "\n<<< Payment is not successful. >>>");
      boolean payAgain = Input.getYesNoInput("> Do you want to pay again? Or no to go back to main menu? (y/n): ", 0);
      if (payAgain) {
        CardPaymentPage.showCardPaymentUI();
      } else {
        // Go back to main customer menu
        Display.displayCustomerMenu();
      }
    }
  }
}