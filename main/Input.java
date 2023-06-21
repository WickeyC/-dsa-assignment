package main;

import java.util.InputMismatchException;
import java.util.Scanner;

import utility.Font;

public class Input {
  public static final Scanner SCANNER = new Scanner(System.in);
  
  public static int getMenuChoice(int choiceLength) {
    int choice = 0;
    boolean validChoice = false;

    do {
      try {
        System.out.printf("\n%47s", "> Please enter your choice (1-" + choiceLength + "): ");
        choice = SCANNER.nextInt();
        if (choice < 1 || choice > choiceLength) {
          throw new InvalidMenuChoiceException(choiceLength);
        } else {
          validChoice = true;
        }
      } catch (InvalidMenuChoiceException invalidMenuChoiceException) {
        System.out.printf("%64s", invalidMenuChoiceException.getErrorMsg());
      } catch (InputMismatchException inputMismatchException) {
        System.out.printf("%65s", Font.getStr(Font.ANSI_RED, "<<< Invalid choice, please enter integers only. >>>"));
      }
      SCANNER.nextLine();
    } while (!validChoice);

    return choice;
  }

  // Exception for handling invalid menu choice
  public static class InvalidMenuChoiceException extends Exception {
    private final String errorMsg;

    public InvalidMenuChoiceException(int choiceLength) {
      this.errorMsg = Font.getStr(Font.ANSI_RED, "<<< Invalid choice, only 1 to " + choiceLength + " are accepted. >>>");     
    }

    public String getErrorMsg() {
      return this.errorMsg;
    }
  }

  public static boolean getYesNoInput(String message, int msgLeftPadding) {
    boolean validOption = false;
    boolean isContinue = true;
    do {
      try {
        System.out.print(message);

        String continueInput = SCANNER.nextLine().toUpperCase();
        if (continueInput.equals("YES")
            || continueInput.equals("Y")) {
          validOption = true;
          isContinue = true;
        } else if (continueInput.equals("NO")
            || continueInput.equals("N")) {
          validOption = true;
          isContinue = false;
        } else {
          throw new InvalidYesNoInputException(msgLeftPadding);
        }
      } catch (InvalidYesNoInputException invalidYesNoInputException) {
        System.out.println(invalidYesNoInputException.getErrorMsg());
      }
    } while (!validOption);
    return isContinue;
  }

  // Exception for handling invalid continue play input
  public static class InvalidYesNoInputException extends Exception {
    private final String errorMsg;

    InvalidYesNoInputException(int msgLeftPadding) {
      String leftPadding = "";
      for (int i = 0; i < msgLeftPadding; i++) {
        leftPadding += " ";
      }
      this.errorMsg = Font.getStr(Font.ANSI_RED,
          leftPadding + "<<< Invalid input, please enter Y / N only >>>");
    }

    public String getErrorMsg() {
      return errorMsg;
    }
  }
}