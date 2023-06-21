package ordermenu;

import java.util.InputMismatchException;

import account.AuthService;
import adt.LinkedHashMap;
import adt.MapInterface;
import food.Drink;
import food.FoodItem;
import food.MainDish;
import food.SideDish;
import food.Stock;
import food.MainDish.MainDishType;
import main.Input;
import utility.Font;
import utility.Screen;

/* this OrderMenu class is quite a static class as it is a order menu for
   the customer to order food items 
   or used to list the food items in admin's manage stock page.
   All the data members and methods are static, so there is no constructor
   because there is no need to create many instances of OrderMenu. */

public class OrderMenu {
    // ******************************
    // --- --- Data members
    // ******************************
    private static MapInterface<Integer, FoodItem> mainDishChoice;
    private static MapInterface<Integer, FoodItem> sideDishChoice;
    private static MapInterface<Integer, FoodItem> drinkChoice;
    private static int index;

    // ******************************
    // --- --- Getters
    // ******************************
    public static MapInterface<Integer, FoodItem> getMainDishChoice() {
        return mainDishChoice;
    }

    public static MapInterface<Integer, FoodItem> getSideDishChoice() {
        return sideDishChoice;
    }

    public static MapInterface<Integer, FoodItem> getDrinkChoice() {
        return drinkChoice;
    }

    public static int getIndex() {
        return index;
    }

    // ******************************
    // --- --- Methods
    // ******************************
    // Order Menu for customer to choose which food item type they want to order
    public static void orderMenu() {
        int choice = 0;

    do {
      Screen.clear();
      String currentUsername = AuthService.getLoggedInAccount().getUsername();
      String usernameText = "| Your username: " + currentUsername + " |";
      Font.print("-", usernameText.length());
      System.out.println(usernameText);
      Font.print("-", usernameText.length());
      System.out.printf("\n%47s\n", "+-------------------------------+");
      System.out.printf("%47s\n", "|   Select Food Type to Order   |");
      System.out.printf("%47s\n", "|-------------------------------|");
      System.out.printf("%47s\n", "| 1. Main Dish                  |");
      System.out.printf("%47s\n", "| 2. Side Dish                  |");
      System.out.printf("%47s\n", "| 3. Drink                      |");
      System.out.printf("%47s\n", "|-------------------------------|");
      System.out.printf("%47s\n", "| 4. Back                       |");
      System.out.printf("%47s\n", "+-------------------------------+");

      choice = Input.getMenuChoice(4);
      boolean displayZeroStock = false;

      switch (choice) {
        case 1:
          // Order Main Dish
          showMainDishTitle();
          displayMainDish(displayZeroStock);
          orderMainDish();
          break;
        case 2:
          // Order Side Dish
          showSideDishTitle();
          displaySideDish(displayZeroStock);
          orderSideDish();
          break;
        case 3:
          // Order Drink
          showDrinkTitle();
          displayDrink(displayZeroStock);
          orderDrink();
          break;
        case 4:
          // Go Back
          break;
        default:
          break;
      }
    } while (choice != 4);
    }

    // util method to prompt for customer's choice of food item
    public static int menuChoice(String promptMsg) {
        boolean validChoice = false; int choice = 0;
        do {
        try {
            System.out.print(promptMsg);
            choice = Input.SCANNER.nextInt();
            if (choice < 1 || choice > index) {
            Font.print(Font.ANSI_RED, "<<< Invalid Choice! >>>");
            validChoice = false;
            } else {
                validChoice = true;
            }
        } catch (InputMismatchException inputMismatchException) {
            System.out.printf("%65s", Font.getStr(Font.ANSI_RED, "<<< Invalid choice, please enter integers only. >>>\n"));
        }
        Input.SCANNER.nextLine();
        } while (!validChoice);

        return choice;
    }

    // util method to prompt for customer's desired food item quantity to be ordered
    // (Parametric polymorphism)
    private static int getQuantity(FoodItem foodItem) {
        int quantity = 0;
        boolean validQuantity = false; 
        do {
            try {
                System.out.print("\n> Order Quantity: ");
                quantity = Input.SCANNER.nextInt();
                if (quantity < 0)  {
                    Font.print(Font.ANSI_RED, "<<< Invalid Quantity! >>>");
                    validQuantity = false;
                } else if (quantity > foodItem.getInStockQty()) {
                    Font.print(Font.ANSI_RED, "<<< Insufficient Stock! >>>");
                    validQuantity = false;
                } else {
                validQuantity = true;
                }
            } catch (InputMismatchException inputMismatchException) {
                System.out.printf("%65s", Font.getStr(Font.ANSI_RED, "<<< Invalid quantity, please enter integers only. >>>\n"));
            }
            Input.SCANNER.nextLine();
        } while (!validQuantity);
        return quantity;
    }

    // ***********************************
    // --- --- Methods for Main Dish Menu
    // ***********************************
    private static void showMainDishTitle() {
        Screen.clear();
        System.out.println("==================================================");
        System.out.println("|                    Main Dish                   |");
        System.out.println("==================================================");
    }

    // Display all the main dish
    // (can choose whether to display zero stock food item or not)
    public static void displayMainDish(boolean displayZeroStock) {
        System.out.printf("\n%-4s%-28s%-10s%-13s%s\n", 
        "No.","Food Name", "Type", "Unit Price", "Stock Qty");

        mainDishChoice = new LinkedHashMap<Integer, FoodItem>();
        index = 0;

        displayMainDishType(MainDishType.BURGER, displayZeroStock);
        displayMainDishType(MainDishType.RICE, displayZeroStock);
        displayMainDishType(MainDishType.NOODLE, displayZeroStock);

        //If there is no main dish item OR 
        if (index == 0) {
            System.out.printf("%s", Font.getStr(Font.ANSI_RED, "<<< Waiting for new items to be added to stock. >>>\n\n"));
            System.out.printf("Press enter to continue...");
            Input.SCANNER.nextLine();
            return;
        }
    }
    
    // Display a type of main dish according to the parameter
    // For example, only display BURGER type of main dish
    /* to be used in displayMainDish() method above
       to display all the maindish */
    private static void displayMainDishType(
    MainDishType mainDishType, boolean displayZeroStock) {
        if (displayZeroStock){
            for (FoodItem foodItem : Stock.getFoodItems()){
                if (foodItem instanceof MainDish) {
                  MainDish mainDishFoodItem = (MainDish) foodItem;
                  if (mainDishFoodItem.getType() == mainDishType) {
                    System.out.printf("%2d. %s\n", ++index, mainDishFoodItem);
                    mainDishChoice.put(index, foodItem);
                  }
                }
            }
        } else {
            for (FoodItem foodItem : Stock.getFoodItems()){
                if (foodItem instanceof MainDish) {
                  MainDish mainDishFoodItem = (MainDish) foodItem;
                  if (mainDishFoodItem.getType() == mainDishType
                  && foodItem.getInStockQty() > 0) {
                    System.out.printf("%2d. %s\n", ++index, mainDishFoodItem);
                    mainDishChoice.put(index, foodItem);
                  }
                }
            }
        }
    }

    //***** To know more about SetMeal / AlaCarte Cart Item, check out their class source code. *****/
    // Check whether the Main Dish can be ordered as a set meal
    // (by checking two condition: 
    // 1. must has at least one side dish food item's quantity >= the ordered quantity
    // 2. must has at least one drink food item's quantity >= the ordered quantity)
    private static boolean checkSetMealAvailability(int quantity) {
        boolean sideDishAvailable = false;
        for (FoodItem foodItem : Stock.getFoodItems()){
            if (foodItem instanceof SideDish
            && foodItem.getInStockQty() >= quantity) {
                sideDishAvailable = true;
            }
        }

        boolean drinkAvailable = false;
        for (FoodItem foodItem : Stock.getFoodItems()){
            if (foodItem instanceof Drink
            && foodItem.getInStockQty() >= quantity) {
                drinkAvailable = true;
            }
        }

        if (sideDishAvailable && drinkAvailable) {
            return true;
        } else {
            return false;
        }
    }

    // Order the main dish as an ala carte item
    // (Parametric polymorphism)
    private static void orderMainDishAlaCarte(FoodItem orderedMainDish, int quantity) {
        boolean itemsInCart = false;
        for (CartItem cartItem : Cart.getCartItems()) {
            if (cartItem instanceof AlaCarte &&
            ((AlaCarte)cartItem).getAlaCarteFoodItem() == orderedMainDish) {
                itemsInCart = true;
                cartItem.setQuantity(((AlaCarte)cartItem).getQuantity() + quantity);
                break;
            }
        }
        if (itemsInCart == false) {
            Cart.getCartItems().add(new AlaCarte(orderedMainDish, quantity));
        }
        orderedMainDish.addToCart(quantity);
        System.out.printf("%s", Font.getStr(Font.ANSI_GREEN, "\n<<< Sucessfully added to cart! >>>\n"));
    }

    // Selecting and ordering a main dish
    private static void orderMainDish() {
        if (index == 0) {
            return;
        }
        FoodItem orderedMainDish; int quantity = 0;

        orderedMainDish = mainDishChoice.get
        (menuChoice("\n> Choose No. to Order (1-"+ index +"): "));

        quantity = getQuantity(orderedMainDish);

        if (checkSetMealAvailability(quantity)) {
            if (Input.getYesNoInput("\nDo you want to order "
            + orderedMainDish.getFoodName() +" as a set meal?\n> (Set Meal has " +
            SetMeal.getSetMealDiscountPCT() + "% discount) (y/n): ", 0)) {
                FoodItem orderedSideDish; FoodItem orderedDrink;
                
                //Side Dish
                showSetMealSideDishTitle();
                System.out.printf("\n%-4s%-28s%-13s%s\n", 
                "No.","Food Name", "Unit Price", "Stock Qty");

                sideDishChoice = new LinkedHashMap<Integer, FoodItem>();
                index = 0;
                for (FoodItem foodItem : Stock.getFoodItems()){
                    if (foodItem instanceof SideDish
                    && foodItem.getInStockQty() >= quantity) {
                      System.out.printf("%2d. %s\n", ++index, foodItem);
                      sideDishChoice.put(index, foodItem);
                    }
                }
    
                boolean validChoice_SD = false; int choice = 0;
                do {
                try {
                    System.out.print("\n> Choose No. to Order (1-"+ index +"): ");
                    choice = Input.SCANNER.nextInt();
                    if (choice < 1 || choice > index) {
                        Font.print(Font.ANSI_RED, "<<< Invalid Choice! >>>");
                        validChoice_SD = false;
                    } else if (quantity < 0) {
                        Font.print(Font.ANSI_RED, "<<< Invalid Quantity! >>>");
                        validChoice_SD = false; 
                    } else if (quantity > sideDishChoice.get(choice).getInStockQty()) {
                        Font.print(Font.ANSI_RED, "<<< Insufficient Stock! >>>");
                        validChoice_SD = false; 
                    } else {
                        validChoice_SD = true;
                    }
                } catch (InputMismatchException inputMismatchException) {
                    System.out.printf("%65s", Font.getStr(Font.ANSI_RED, "<<< Invalid choice, please enter integers only. >>>\n"));
                }
                Input.SCANNER.nextLine();
                } while (!validChoice_SD);
    
                orderedSideDish = sideDishChoice.get(choice);
    
                //Drink
                showSetMealDrinkTitle();
                System.out.printf("\n%-4s%-28s%-13s%s\n", 
                "No.","Food Name", "Unit Price", "Stock Qty");

                drinkChoice = new LinkedHashMap<Integer, FoodItem>();
                index = 0;
                for (FoodItem foodItem : Stock.getFoodItems()){
                    if (foodItem instanceof Drink
                    && foodItem.getInStockQty() >= quantity) {
                      System.out.printf("%2d. %s\n", ++index, foodItem);
                      drinkChoice.put(index, foodItem);
                    }
                }
    
                boolean validChoice_D = false;
                do {
                try {
                    System.out.print("\n> Choose No. to Order (1-"+ index +"): ");
                    choice = Input.SCANNER.nextInt();
                    if (choice < 1 || choice > index) {
                    Font.print(Font.ANSI_RED, "<<< Invalid Choice! >>>");
                    validChoice_D = false;
                    } else if (quantity < 0) {
                        Font.print(Font.ANSI_RED, "<<< Invalid Quantity! >>>");
                        validChoice_D = false; 
                    } else if (quantity > drinkChoice.get(choice).getInStockQty()) {
                        Font.print(Font.ANSI_RED, "<<< Insufficient Stock! >>>");
                        validChoice_D = false; 
                    } else {
                    validChoice_D = true;
                    }
                } catch (InputMismatchException inputMismatchException) {
                    System.out.printf("%65s", Font.getStr(Font.ANSI_RED, "<<< Invalid choice, please enter integers only. >>>\n"));
                }
                Input.SCANNER.nextLine();
                } while (!validChoice_D);
    
                orderedDrink = drinkChoice.get(choice);
    
                boolean itemsInCart = false;
                for (CartItem cartItem : Cart.getCartItems()) {
                    if (
                        cartItem instanceof SetMeal && 
                        ((SetMeal)cartItem).getMainDishFoodItem() == orderedMainDish &&
                        ((SetMeal)cartItem).getSideDishFoodItem() == orderedSideDish&&
                        ((SetMeal)cartItem).getDrinkFoodItem() == orderedDrink
                    ) {
                        itemsInCart = true;
                        cartItem.setQuantity(((SetMeal)cartItem).getQuantity() + quantity);
                        break;
                    }
                }
    
                if (itemsInCart == false) {
                    Cart.getCartItems().add(
                    new SetMeal(orderedMainDish, orderedSideDish, orderedDrink, quantity));
                }
                FoodItem[] orderedFoodItems = {
                    orderedMainDish, orderedSideDish, orderedDrink
                };
                for (FoodItem foodItem : orderedFoodItems) {
                    foodItem.addToCart(quantity);
                }
                System.out.printf("%65s", Font.getStr(Font.ANSI_GREEN, "\n<<< Sucessfully added to cart! >>>\n"));
            } else {
                orderMainDishAlaCarte(orderedMainDish, quantity);
            }
        } else {
            System.out.println("\n(No Set Meal available because of insufficient stock of side dish / drink)");
            if (!(Input.getYesNoInput("> Add as Ala Carte? (y/n): ", 0))) {
                return;
            }
            orderMainDishAlaCarte(orderedMainDish, quantity);
        }
        
        System.out.printf("%s", "Press enter to continue...");
        Input.SCANNER.nextLine();
    }

    // ***********************************
    // --- --- Methods for Side Dish Menu
    // ***********************************
    private static void showSideDishTitle() {
        Screen.clear();
        System.out.println("==================================================");
        System.out.println("|                    Side Dish                   |");
        System.out.println("==================================================");
    }
    
    private static void showSetMealSideDishTitle() {
        System.out.println("\n==================================================");
        System.out.println("|           Select Set Meal Side Dish            |");
        System.out.println("==================================================");
    }

    // Display all the side dish
    // (can choose whether to display zero stock food item or not)
    public static void displaySideDish(boolean displayZeroStock) {
        System.out.printf("\n%-4s%-28s%-13s%s\n", 
        "No.","Food Name", "Unit Price", "Stock Qty");

        sideDishChoice = new LinkedHashMap<Integer, FoodItem>();
        index = 0;
        if (displayZeroStock) {
            for (FoodItem foodItem : Stock.getFoodItems()){
              if (foodItem instanceof SideDish) {
                System.out.printf("%2d. %s\n", ++index, foodItem);
                sideDishChoice.put(index, foodItem);
              }
            }
        } else {
            for (FoodItem foodItem : Stock.getFoodItems()){
                if (foodItem instanceof SideDish
                && foodItem.getInStockQty() > 0) {
                  System.out.printf("%2d. %s\n", ++index, foodItem);
                  sideDishChoice.put(index, foodItem);
                }
            }
        }

        //If there is no side dish item
        if (index == 0) {
            System.out.printf("%s", Font.getStr(Font.ANSI_RED, "<<< Waiting for new items to be added to stock. >>>\n\n"));
            System.out.printf("Press enter to continue...");
            Input.SCANNER.nextLine();
            return;
        }
    }

    // Selecting and ordering a side dish
    private static void orderSideDish() {
        if (index == 0) {
            return;
        }
        
        FoodItem orderedSideDish; int quantity = 0;

        orderedSideDish = sideDishChoice.get
        (menuChoice("\n> Choose No. to Order (1-"+ index +"): "));

        quantity = getQuantity(orderedSideDish);

        boolean itemsInCart = false;
            for (CartItem cartItem : Cart.getCartItems()) {
                if (cartItem instanceof AlaCarte &&
                ((AlaCarte)cartItem).getAlaCarteFoodItem() == orderedSideDish) {
                    itemsInCart = true;
                    cartItem.setQuantity(((AlaCarte)cartItem).getQuantity() + quantity);
                    break;
                }
            }
            if (itemsInCart == false) {
                Cart.getCartItems().add(new AlaCarte(orderedSideDish, quantity));
            }
        orderedSideDish.addToCart(quantity);
        System.out.printf("%s", Font.getStr(Font.ANSI_GREEN, "\n<<< Sucessfully added to cart! >>>\n"));
        System.out.printf("%s", "Press enter to continue...");
        Input.SCANNER.nextLine();
    }

    // ***********************************
    // --- --- Methods for Drink Menu
    // ***********************************
    private static void showDrinkTitle() {
        Screen.clear();
        System.out.println("==================================================");
        System.out.println("|                      Drink                     |");
        System.out.println("==================================================");
    }

    private static void showSetMealDrinkTitle() {
        System.out.println("\n==================================================");
        System.out.println("|             Select Set Meal Drink              |");
        System.out.println("==================================================");
    }
    
    // Display all the drink
    // (can choose whether to display zero stock food item or not)
    public static void displayDrink(boolean displayZeroStock) {
        System.out.printf("\n%-4s%-28s%-13s%s\n", 
        "No.","Food Name", "Unit Price", "Stock Qty");

        drinkChoice = new LinkedHashMap<Integer, FoodItem>();
        index = 0;

        if (displayZeroStock) {
            for (FoodItem foodItem : Stock.getFoodItems()){
              if (foodItem instanceof Drink) {
                System.out.printf("%2d. %s\n", ++index, foodItem);
                drinkChoice.put(index, foodItem);
              }
            }
        } else {
            for (FoodItem foodItem : Stock.getFoodItems()){
                if (foodItem instanceof Drink && foodItem.getInStockQty() > 0) {
                  System.out.printf("%2d. %s\n", ++index, foodItem);
                  drinkChoice.put(index, foodItem);
                }
            }
        }

        //If there is no drink item
        if (index == 0) {
            System.out.printf("%s", Font.getStr(Font.ANSI_RED, "<<< Waiting for new items to be added to stock. >>>\n\n"));
            System.out.printf("Press enter to continue...");
            Input.SCANNER.nextLine();
            return;
        }
    }

    // Selecting and ordering a drink
    private static void orderDrink() {
        if (index == 0) {
            return;
        }

        FoodItem orderedDrink; int quantity = 0;

        orderedDrink = drinkChoice.get
        (menuChoice("\n> Choose No. to Order (1-"+ index +"): "));

        quantity = getQuantity(orderedDrink);
            

        boolean itemsInCart = false;
            for (CartItem cartItem : Cart.getCartItems()) {
                if (cartItem instanceof AlaCarte &&
                ((AlaCarte)cartItem).getAlaCarteFoodItem() == orderedDrink) {
                    itemsInCart = true;
                    cartItem.setQuantity(((AlaCarte)cartItem).getQuantity() + quantity);
                    break;
                }
            }
            if (itemsInCart == false) {
                Cart.getCartItems().add(new AlaCarte(orderedDrink, quantity));
            }
            orderedDrink.addToCart(quantity);
            System.out.printf("%s", Font.getStr(Font.ANSI_GREEN, "\n<<< Sucessfully added to cart! >>>\n"));
        System.out.printf("%s", "Press enter to continue...");
        Input.SCANNER.nextLine();
    }

}