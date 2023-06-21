package food;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;

import account.AuthService;
import adt.LinkedList;
import adt.ListInterface;
import food.MainDish.MainDishType;
import main.Input;
import main.Input.InvalidMenuChoiceException;
import ordermenu.OrderMenu;
import report.ReportPage;
import utility.Database;
import utility.Font;
import utility.Screen;

/* this Stock class is quite a static class as the system has only one stock
  All the data members and methods are static, so there is no constructor
  because there is no need to create many instances of Stock. */
public class Stock {
  // ******************************
  // --- --- Data members
  // ******************************
  /* Polymorphic ArrayList of FoodItem
     to store all the food items in the system at one place (stock) */
  private static ListInterface<FoodItem> foodItems = new LinkedList<FoodItem>();
  private static FoodItem foodItemToAdd, foodItemToUpdate, foodItemToDelete;

  // ******************************
  // --- --- Getters
  // ******************************
  public static ListInterface<FoodItem> getFoodItems() {
      return foodItems;
  }

  // ******************************
  // --- --- Methods
  // ******************************
  /* Admins can use this stock menu to choose whether they want to 
     view the Stock Report or manage the 
     (Add, Update and Delete) Food Items in Stock */
  public static void manageStockMenu() {
    int choice = 0;

    do {
      Screen.clear();
      String currentUsername = AuthService.getLoggedInAccount().getUsername();
      String usernameText = "| Your username: " + currentUsername + " |";
      Font.print("-", usernameText.length());
      System.out.println(usernameText);
      Font.print("-", usernameText.length());
      System.out.printf("\n%47s\n", "+-------------------------------+");
      System.out.printf("%47s\n", "|          Manage Stock         |");
      System.out.printf("%47s\n", "|-------------------------------|");
      System.out.printf("%47s\n", "| 1. Stock Report               |");
      System.out.printf("%47s\n", "| 2. Add Food Item              |");
      System.out.printf("%47s\n", "| 3. Update Food Item           |");
      System.out.printf("%47s\n", "| 4. Delete Food Item           |");
      System.out.printf("%47s\n", "|-------------------------------|");
      System.out.printf("%47s\n", "| 5. Back                       |");
      System.out.printf("%47s\n", "+-------------------------------+");

      choice = Input.getMenuChoice(5);

      switch (choice) {
        case 1:
          // Stock Report
          ReportPage.displayStockReport();
          break;
        case 2:
          // Add New Food Item
          addFoodItem();
          break;
        case 3:
          // Update Food Item
          updateFoodItem();
          break;
        case 4:
          // Delete Food Item
          deleteFoodItem();
          break;
        case 5:
          // Go Back
          break;
        default:
          break;
      }
    } while (choice != 5);
  }

  // Add a new Food Item into the stock
  private static void addFoodItem() {
    Screen.clear();
    System.out.println("==================================================");
    System.out.println("|                 Add New Food Item              |");
    System.out.println("==================================================");
    FoodItemTypeChoice choice = chooseFoodItem();

    switch (choice) {
      case MAINDISH:
        foodItemToAdd = new MainDish();
        break;
      case SIDEDISH:
        foodItemToAdd = new SideDish();
        break;
      case DRINK:
        foodItemToAdd = new Drink();
        break;
      default:
        break;
    }
    
    // Polymorphism
    foodItemToAdd.getInputs();

    Font.print(Font.ANSI_CYAN, "\n<<< Adding the new food item in the database... >>>");
    foodItemToAdd.addToDB();
    Stock.getFoodItems().add(foodItemToAdd);
    
    Font.print(Font.ANSI_GREEN, "\n<<< New food item is added successfully! >>>");
    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();
  }
  
  // Update details of a food item in the stock
  private static void updateFoodItem() {
    Screen.clear();
    System.out.println("==================================================");
    System.out.println("|                  Update Food Item              |");
    System.out.println("==================================================");
    

    foodItemToUpdate = assignFoodItem("\n> Choose No. to Update: ");

    if (OrderMenu.getIndex()== 0) {
      return;
    }

    int choice = 0;
    do {
      Screen.clear();
      System.out.println("<<< Updating " + foodItemToUpdate.getFoodName()
      + " (Food ID: " + foodItemToUpdate.getFoodID() + ") >>>\n");
      if (foodItemToUpdate instanceof MainDish) {
        System.out.printf("Current Main Dish Type: %s\n", 
        ((MainDish)foodItemToUpdate).getType().getType());
      }
      System.out.printf("Current Unit Price: RM%.2f\n", foodItemToUpdate.getUnitPrice());
      System.out.printf("Current In Stock Quantity: %d\n\n", foodItemToUpdate.getInStockQty());

      System.out.println("-----------------------------");
      System.out.println("|  Select Detail to Update  |");
      System.out.println("-----------------------------");
      System.out.println("|     1. Food Name          |");
      System.out.println("|     2. Unit Price         |");
      System.out.println("|     3. In Stock Quantity  |");
      System.out.println("|     4. Reset Whole Item   |");
      System.out.println("-----------------------------");
      System.out.println("|     5. Done Update        |");
      System.out.println("-----------------------------");

      boolean validChoice = false;
      do {
        try {
          System.out.printf("\n%s", "> Please enter your choice (1-5): ");
          choice = Input.SCANNER.nextInt();
          if (choice < 1 || choice > 5) {
            throw new InvalidMenuChoiceException(5);
          } else {
            validChoice = true;
          }
        } catch (InvalidMenuChoiceException invalidMenuChoiceException) {
          System.out.printf("%s", invalidMenuChoiceException.getErrorMsg());
        } catch (InputMismatchException inputMismatchException) {
          System.out.printf("%s", Font.getStr(Font.ANSI_RED, "<<< Invalid choice, please enter integers only. >>>"));
        }
        Input.SCANNER.nextLine();
      } while (!validChoice);

      switch (choice) {
        case 1:
          // Update Food Name
          foodItemToUpdate.promptFoodName();
          Font.print(Font.ANSI_CYAN, "\n<<< Updating the new food name in the database... >>>");
          foodItemToUpdate.updateFoodNameInDB();
          Font.print(Font.ANSI_GREEN, "\n<<< The food name is updated successfully! >>>");
          System.out.print("\nPress enter to continue...");
          Input.SCANNER.nextLine();
          break;
        case 2:
          // Update Unit Price
          foodItemToUpdate.promptUnitPrice();
          Font.print(Font.ANSI_CYAN, "\n<<< Updating the new unit price in the database... >>>");
          foodItemToUpdate.updateUnitPriceInDB();
          Font.print(Font.ANSI_GREEN, "\n<<< The unit price is updated successfully! >>>");
          System.out.print("\nPress enter to continue...");
          Input.SCANNER.nextLine();
          break;
        case 3:
          // Update In Stock Quantity
          foodItemToUpdate.promptInStockQty();
          Font.print(Font.ANSI_CYAN, "\n<<< Updating the new in stock quantity in the database... >>>");
          foodItemToUpdate.updateInStockQtyInDB();
          Font.print(Font.ANSI_GREEN, "\n<<< The in stock quantity is updated successfully! >>>");
          System.out.print("\nPress enter to continue...");
          Input.SCANNER.nextLine();
          break;
        case 4:
          // Reset Food Item
          // Polymorphism
          foodItemToUpdate.getInputs();
          Font.print(Font.ANSI_CYAN, "\n<<< Updating the new food item in the database... >>>");
          foodItemToUpdate.updateInDB();
          Font.print(Font.ANSI_GREEN, "\n<<< The food item is updated successfully! >>>");
          System.out.print("\nPress enter to continue...");
          Input.SCANNER.nextLine();
          break;
        case 5:
          break;
        default:
          break;
      }
    } while (choice != 5);
  }

  // Delete a food item from the stock
  private static void deleteFoodItem() {
    Screen.clear();
    System.out.println("==================================================");
    System.out.println("|                 Delete Food Item               |");
    System.out.println("==================================================");

    foodItemToDelete = assignFoodItem(
    "\n> Choose No. to Delete: ");

    if (OrderMenu.getIndex() == 0) {
      return;
    }

    //Delete Confirmation
    if (
      !(Input.getYesNoInput(
      Font.getStr(Font.ANSI_RED, 
      String.format("\n> Are you sure to delete %s? (y/n): ", 
      foodItemToDelete.getFoodName())), 0))
    ) {
      return;
    }
    
    Font.print(Font.ANSI_CYAN, "\n<<< Removing the food item in the database... >>>");
    foodItemToDelete.deleteFromDB();
    for (int i = 0; i < Stock.getFoodItems().size(); i++) {
      if (Stock.getFoodItems().get(i).equals(foodItemToDelete)) {
        Stock.getFoodItems().remove(i);
      }
    }

    Font.print(Font.ANSI_GREEN, "\n<<< The food item is deleted successfully! >>>");
    System.out.print("\nPress enter to continue...");
    Input.SCANNER.nextLine();
  }

  /* This function is to grab data from the DB and "hydrate" the object's data.
     It will read and grab data from the food_item table
     and "hydrate" them as FoodItem objects to the foodItems ArrayList in stock */
  public static void hydrateData() {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    try {
      String sql = "SELECT * FROM " + FoodItem.TABLE_NAME + ";";

      pstmt = connection.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        FoodItem foodItem;
        String foodItemTypeStr = rs.getString(2);
        if (foodItemTypeStr.equals("MainDish")) {
          MainDishType mainDishType;
          String mainDishTypeStr = rs.getString(3);
          if (mainDishTypeStr.equals("Burger")) {
            mainDishType = MainDishType.BURGER;
          } else if (mainDishTypeStr.equals("Rice")) {
            mainDishType = MainDishType.RICE;
          } else {
            mainDishType = MainDishType.NOODLE;
          }
          foodItem = new MainDish(
            rs.getInt(1),
            rs.getString(4),
            rs.getDouble(5),
            rs.getInt(6),
            mainDishType 
          );
        } else if (foodItemTypeStr.equals("SideDish")) {
          foodItem = new SideDish(
            rs.getInt(1),
            rs.getString(4),
            rs.getDouble(5),
            rs.getInt(6) 
          );
        } else {
          foodItem = new Drink(
            rs.getInt(1),
            rs.getString(4),
            rs.getDouble(5),
            rs.getInt(6) 
          );
        }

        Stock.foodItems.add(foodItem);
      }
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }

  // ***********************************
  // --- --- Util Methods / Enumeration
  // ***********************************
  private static enum FoodItemTypeChoice {
    MAINDISH,
    SIDEDISH,
    DRINK
  }

  private static FoodItemTypeChoice chooseFoodItem() {
    FoodItemTypeChoice result = FoodItemTypeChoice.MAINDISH;
    boolean validChoice = false; int choice = 0;

    System.out.println("\n<<< Specify the food item type >>>");

    System.out.println("----------------------");
    System.out.println("|     1. Main Dish   |");
    System.out.println("|     2. Side Dish   |");
    System.out.println("|     3. Drink       |");
    System.out.println("----------------------");

    do {
      try {
          System.out.print("> Choose Food Item Type(1-3): ");
        choice = Input.SCANNER.nextInt();
        if (choice < 1 || choice > 3) {
          Font.print(Font.ANSI_RED, "<<< Invalid Choice! >>>");
          validChoice = false;
        } else {
          validChoice = true;
        }
      } catch (InputMismatchException inputMismatchException) {
        System.out.printf("%s", Font.getStr(Font.ANSI_RED, "<<< Invalid choice, please enter integers only. >>>\n"));
      }
      Input.SCANNER.nextLine();
    } while (!validChoice);

    switch (choice) {
      case 1:
        result = FoodItemTypeChoice.MAINDISH;
        break;
      case 2:
        result = FoodItemTypeChoice.SIDEDISH;
        break;
      case 3:
        result = FoodItemTypeChoice.DRINK;
        break;
      default:
        break;
    }
    
    return result;
  }

  private static FoodItem assignFoodItem(String promptMsg) {
    FoodItem foodItemToAssign = null;
    
    FoodItemTypeChoice choice = chooseFoodItem();
    boolean displayZeroStock = true;
    
    switch (choice) {
      case MAINDISH:
        System.out.println("\n<<< Specify the Main Dish food item >>>");
        OrderMenu.displayMainDish(displayZeroStock);
        if (OrderMenu.getIndex() == 0) {
          break;
        }
        foodItemToAssign = OrderMenu.getMainDishChoice().get
        (OrderMenu.menuChoice(promptMsg));
        break;
      case SIDEDISH:
        System.out.println("\n<<< Specify the Side Dish food item >>>");
        OrderMenu.displaySideDish(displayZeroStock);
        if (OrderMenu.getIndex() == 0) {
          break;
        }
        foodItemToAssign = OrderMenu.getSideDishChoice().get
        (OrderMenu.menuChoice(promptMsg));
        break;
      case DRINK:
        System.out.println("\n<<< Specify the Drink food item >>>");
        OrderMenu.displayDrink(displayZeroStock);
        if (OrderMenu.getIndex() == 0) {
          break;
        }
        foodItemToAssign = OrderMenu.getDrinkChoice().get
        (OrderMenu.menuChoice(promptMsg));
        break;
      default:
        break;
    }

    return foodItemToAssign;
  }

}