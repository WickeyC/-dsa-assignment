package ordermenu;

import java.util.InputMismatchException;

import adt.LinkedList;
import adt.ListInterface;
import food.FoodItem;
import main.Input;
import main.Input.InvalidMenuChoiceException;
import utility.Font;
import utility.Screen;
import checkout.ApplyVoucherPage;
import checkout.Order;

/* this Class class is quite a static class as the system has only one Cart.
   All the data members and methods are static, so there is no constructor
   because there is no need to create many instances of Cart. */
public class Cart {
    // ******************************
    // --- --- Data members
    // ******************************

    // total price of all the cart items
    private static double totalPrice;
    /* Polymorphic ArrayList of CartItem
     to store all the cart items in the system at one place (cart) */
    private static ListInterface<CartItem> cartItems = new LinkedList<CartItem>();

    // ******************************
    // --- --- Getters
    // ******************************
    public static ListInterface<CartItem> getCartItems() {
        return cartItems;
    }

    // ******************************
    // --- --- Methods
    // ******************************
    // Calculate and return the total price of all the cart items
    public static double calcTotalPrice() {
        totalPrice = 0;
        for (CartItem cartItem : cartItems) {
            totalPrice += cartItem.calcPrice();
        }
        return totalPrice;
    }

    // Display the whole cart UI
    public static void displayCart() {
        Screen.clear();
        System.out.println("===============================================================");
        System.out.println("|                              Cart                           |");
        System.out.println("===============================================================");
        if (cartItems.size() > 0){
            displayCartItems(cartItems);
            System.out.println("===============================================================");
            String str = String.format("%60s",  String.format("Total Price : RM%.2f", calcTotalPrice()));
            System.out.printf("%s%s%s","|", Font.getStr(Font.ANSI_GREEN, str)," |\n");
            System.out.println("===============================================================");
            cartOption();
        } else {
            Font.print(Font.ANSI_RED, "\n<<< Cart is Empty! >>>");
            System.out.print("\nPress enter to go back...");
            Input.SCANNER.nextLine();
        }
    }

    // Display all the cart items
    // (to be used in displayCart() method in Cart and OrderDetails class)
    public static void displayCartItems(ListInterface<CartItem> cartItems) {
        for (int i = 0; i < cartItems.size(); i++) {
            // Display different UI for AlaCarte and SetMeal
            if (cartItems.get(i) instanceof AlaCarte) {        
              System.out.println("| Item " + (i + 1) + " (Ala Carte)                                          |");
            } else {
              System.out.println("| Item " + (i + 1) + " (Set Meal)                                           |");
            }
            System.out.print(cartItems.get(i));
            if (i != cartItems.size() - 1) {
              System.out.println("|-------------------------------------------------------------|");
            }
        }
    }

    // Let users opt for available actions associated with Cart
    private static void cartOption() {
        int choice = 0;
        System.out.println("\n                    -----------------------");
        System.out.println("                    |   1. Check Out Now  |");
        System.out.println("                    |   2. Reset Cart     |");
        System.out.println("                    -----------------------");
        System.out.println("                    |   3. Go Back        |");
        System.out.println("                    -----------------------");

        boolean validChoice = false;
        do {
            try {
            System.out.printf("\n%48s", "> Please enter your choice (1-3): ");
            choice = Input.SCANNER.nextInt();
            if (choice < 1 || choice > 3) {
                throw new InvalidMenuChoiceException(3);
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
                // Check Out
                Order.setCurrentOrder(new Order());
                ApplyVoucherPage.showVoucherUI();
                break;
            case 2:
                // Reset Cart
                resetCartAndStock();
                displayCart();
                break;
            case 3:
                break;
            default:
                break;
        }
    }

    // Reset the cart items (remove them from cart) and price
    private static void resetCart(){
        cartItems = new LinkedList<CartItem>();
        totalPrice = 0;
    }

    // FOR : logout / user choose to reset
    // add back the in stock quantity with the ordered quantity
    public static void resetCartAndStock() {
        
        for (CartItem cartItem : cartItems) {
            if (cartItem instanceof AlaCarte) {
                FoodItem foodItemInstance = ((AlaCarte)cartItem).getAlaCarteFoodItem();
                foodItemInstance.removeFromCart(cartItem.getQuantity());
            } else {
                FoodItem[] foodItemInstances = {
                    ((SetMeal)cartItem).getMainDishFoodItem(),
                    ((SetMeal)cartItem).getSideDishFoodItem(),
                    ((SetMeal)cartItem).getDrinkFoodItem()
                };
                for (FoodItem foodItemInstance : foodItemInstances) {
                    foodItemInstance.removeFromCart(cartItem.getQuantity());
                }
            }
        }
        resetCart();
    }

    // FOR: check out done
    // update the new in stock quantity in Database
    public static void checkOutDone() {
        
        for (CartItem cartItem : cartItems) {
            if (cartItem instanceof AlaCarte) {
                ((AlaCarte)cartItem).getAlaCarteFoodItem().updateInStockQtyInDB();
            } else {
                ((SetMeal)cartItem).getMainDishFoodItem().updateInStockQtyInDB();
                ((SetMeal)cartItem).getSideDishFoodItem().updateInStockQtyInDB();
                ((SetMeal)cartItem).getDrinkFoodItem().updateInStockQtyInDB();
            }
        }
        resetCart();
    }
}