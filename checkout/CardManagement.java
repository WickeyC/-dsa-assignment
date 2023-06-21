package checkout;

import java.util.InputMismatchException;

import account.AuthService;
import account.Customer;
import adt.ArrayList;
import adt.ListInterface;
import adt.MapInterface;
import main.Input;
import utility.Font;
import utility.Screen;

public class CardManagement {
  // Why convert to list instead of map?
  // The main issue here is that the key of the map is the card number.
  // Thus, to select a card, we cannot let the user to select by typing the
  // card number because it is too long. 
  // To let the users to select the card, we can let them to select
  // by the NO/position in the UI.
  // * If we use map, then we need to iterate through the map 2 times, 
  //  * first loop to print the card to users, and when the user has
  //  * chosen the NO, we will have a second loop to identify the entry.
  // * The second alternative is that we first create a list of card number from the 
  //  * the map. Then, after the user enters the NO, and whenever we
  //  * want to access the entry, the directly access the list using (NO - 1)
  //  * altogether accessing the map using the card number from the list.
  // * Also, the main reason is that this approach is more intuitive

  // A list by definition is ordered. 
  // You add items and then you are able to iterate back through
  // the list in the order that you inserted the items. 
  // When you add items to a HashMap, you are not guaranteed 
  // to retrieve the items in the same order you put them in. 
  // There are subclasses of HashMap like LinkedHashMap that will maintain the order, 
  // but in general order is not guaranteed with a Map.

  private static ListInterface<String> cardList = new ArrayList<String>();

  public static ListInterface<String> getCardList() {
    return cardList;
  }

  public static void loadCardList(MapInterface<String, Card> cardsMap) {
    ListInterface<String> cardList = new ArrayList<String>();
    for (String cardNumber : cardsMap.keys()) {
      cardList.add(cardNumber);
    }

    CardManagement.cardList = cardList;
  }

  public static void showUI() {
    int choice;
    do {
      Screen.clear();
      System.out.println("==============================================================");
      System.out.println("|                  Your Credit/Debit Cards                   |");
      System.out.println("==============================================================");

      Customer customer = (Customer) AuthService.getLoggedInAccount();
      loadCardList(customer.getCards());
      CardView.printCards(cardList, customer.getCards());

      System.out.println("==============================================================");
      System.out.printf("%48s\n", "+-------------------------------+");
      System.out.printf("%48s\n", "|       ENTER YOUR OPTION       |");
      System.out.printf("%48s\n", "|-------------------------------|");
      System.out.printf("%48s\n", "| 1. Add new card               |");
      System.out.printf("%48s\n", "| 2. Update card                |");
      System.out.printf("%48s\n", "| 3. Delete card                |");
      System.out.printf("%48s\n", "| 4. Delete all cards           |");
      System.out.printf("%48s\n", "|-------------------------------|");
      System.out.printf("%48s\n", "| 5. Go back                    |");
      System.out.printf("%48s\n", "+-------------------------------+");

      choice = Input.getMenuChoice(5);

      switch (choice) {
        case 1:
          // Add new card
          addCard();
          break;
        case 2:
          // Update card
          updateCard();
          break;
        case 3:
          // Delete card
          deleteCard();
          break;
        case 4:
          // Delete all cards
          deleteAllCards();
          break;
        case 5:
          // Go back
          break;
        default:
          break;
      }
    } while (choice != 5);
  }

  private static void addCard() {
    Customer customer = (Customer) AuthService.getLoggedInAccount();

    Screen.clear();
    System.out.println("==================================================");
    System.out.println("|                  Add New Card                  |");
    System.out.println("==================================================");
    System.out.println("\n<<< Please specify your new card below >>>");

    Card card = new Card();
    // Prompt inputs for card information
    card.setCardNumber(CardView.getCardNumberInput());
    card.setNameOnCard(CardView.getNameOnCardInput());
    card.setExpirationMonth(CardView.getExpirationMonthInput());
    card.setExpirationYear(CardView.getExpirationYearInput());
    card.setCvc(CardView.getCvcInput());

    Font.print(Font.ANSI_CYAN, "<<< Adding the new card in the database... >>>");
    CardRepository.create(card, customer.getEmail());
    customer.getCards().put(card.getCardNumber(), card);

    Font.print(Font.ANSI_GREEN, "\n<<< New card is added successfully! >>>");
    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();
  }

  private static void updateCard() {
    Customer customer = (Customer) AuthService.getLoggedInAccount();

    if (customer.getCards().isEmpty()) {
      Font.print(Font.ANSI_CYAN, "             <<< There's no card in your account. >>>");
      System.out.print("\n             Press enter to continue...");
      Input.SCANNER.nextLine();
      return;
    }

    int no = 0;
    boolean validNo = false;

    do {
      try {
        System.out.print("             > Enter card NO to update: ");
        no = Input.SCANNER.nextInt();

        // Validate NO input
        if (no < 1 || no > cardList.size()) {
          Font.print(Font.ANSI_RED, "             <<< Invalid NO, please enter again. >>>");
          validNo = false;
        } else {
          validNo = true;
        }
      } catch (InputMismatchException inputMismatchException) {
        Font.print(Font.ANSI_RED, "<<< Invalid NO, please enter integers only. >>>");
      }
      Input.SCANNER.nextLine();
    } while (!validNo);

    String cardNumberToUpdate = cardList.get(no - 1);

    Card cardToUpdate = customer.getCards().get(cardNumberToUpdate);
    // Prompt inputs for card information
    CardView.printHeading(cardToUpdate);
    cardToUpdate.setNameOnCard(CardView.getNameOnCardInput());
    CardView.printHeading(cardToUpdate);
    cardToUpdate.setExpirationMonth(CardView.getExpirationMonthInput());
    CardView.printHeading(cardToUpdate);
    cardToUpdate.setExpirationYear(CardView.getExpirationYearInput());
    CardView.printHeading(cardToUpdate);
    cardToUpdate.setCvc(CardView.getCvcInput());

    Font.print(Font.ANSI_CYAN, "\n<<< Updating card in the database... >>>");
    CardRepository.update(cardToUpdate);
    Font.print(Font.ANSI_GREEN, "<<< Card is updated successfully! >>>");

    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();
  }

  private static void deleteCard() {
    Customer customer = (Customer) AuthService.getLoggedInAccount();

    if (customer.getCards().isEmpty()) {
      Font.print(Font.ANSI_CYAN, "             <<< There's no card in your account. >>>");
      System.out.print("\n             Press enter to continue...");
      Input.SCANNER.nextLine();
      return;
    }

    int no = 0;
    boolean validNo = false;

    do {
      try {
        System.out.print("             > Enter card NO to delete: ");
        no = Input.SCANNER.nextInt();

        // Validate NO input
        if (no < 1 || no > cardList.size()) {
          Font.print(Font.ANSI_RED, "             <<< Invalid NO, please enter again. >>>");
          validNo = false;
        } else {
          validNo = true;
        }
      } catch (InputMismatchException inputMismatchException) {
        Font.print(Font.ANSI_RED, "<<< Invalid NO, please enter integers only. >>>");
      }
      Input.SCANNER.nextLine();
    } while (!validNo);
    
    String cardNumberToDelete = cardList.get(no - 1);

    Font.print(Font.ANSI_CYAN, "             <<< Deleting card from the database... >>>");
    CardRepository.deleteByCardNumber(cardNumberToDelete);
    // Map Usage
    customer.getCards().remove(cardNumberToDelete);
    Font.print(Font.ANSI_GREEN, "             <<< Card is deleted successfully! >>>");

    System.out.print("\n             Press enter to continue...");
    Input.SCANNER.nextLine();
  }

  private static void deleteAllCards() {
    Customer customer = (Customer) AuthService.getLoggedInAccount();

    if (customer.getCards().isEmpty()) {
      Font.print(Font.ANSI_CYAN, "             <<< There's no card in your account. >>>");
      System.out.print("\n             Press enter to continue...");
      Input.SCANNER.nextLine();
      return;
    }
    
    boolean confirmDelete = Input.getYesNoInput("             > Are you sure to delete all cards? (y/n): ", 0);

    if (!confirmDelete) {
      return;
    }

    Font.print(Font.ANSI_CYAN, "             <<< Deleting all cards from the database... >>>");
    CardRepository.deleteAll();
    // Map Usage
    customer.getCards().clear();
    Font.print(Font.ANSI_GREEN, "             <<< All cards are deleted successfully! >>>");

    System.out.print("\n             Press enter to continue...");
    Input.SCANNER.nextLine();
  }
}
