package ordermenu;

import food.FoodItem;

public class SetMeal extends CartItem{
    // ******************************
    // --- --- Data members
    // ******************************
    final private static int SET_MEAL_DISCOUNT_PCT = 10; //10% discount for Set Meal Item
    // Set Meal Cart Item has one Main Dish, one Side Dish and one Drink
    private FoodItem mainDishFoodItem;
    private FoodItem sideDishFoodItem;
    private FoodItem drinkFoodItem;

    // ******************************
    // --- --- Constructors
    // ******************************
    public SetMeal(
        FoodItem mainDishFoodItem,
        FoodItem sideDishFoodItem,
        FoodItem drinkFoodItem,
        int quantity) {
            this.mainDishFoodItem = mainDishFoodItem;
            this.sideDishFoodItem = sideDishFoodItem;
            this.drinkFoodItem = drinkFoodItem;
            this.quantity = quantity;
    }

    // ******************************
    // --- --- Getters
    // ******************************
    public static int getSetMealDiscountPCT() {
        return SET_MEAL_DISCOUNT_PCT;
    }

    public FoodItem getMainDishFoodItem() {
        return mainDishFoodItem;
    }

    public FoodItem getSideDishFoodItem() {
        return sideDishFoodItem;
    }

    public FoodItem getDrinkFoodItem() {
        return drinkFoodItem;
    }

    // ******************************
    // --- --- Methods
    // ******************************
    // to calculate the discounted amount
    public double calcDiscount() {
        return (mainDishFoodItem.getUnitPrice() * quantity +
        sideDishFoodItem.getUnitPrice() * quantity +
        drinkFoodItem.getUnitPrice() * quantity) 
        * (SET_MEAL_DISCOUNT_PCT / 100.0);
    }

    @Override
    public double calcPrice() {
        return 
        (mainDishFoodItem.getUnitPrice() * quantity +
        sideDishFoodItem.getUnitPrice() * quantity +
        drinkFoodItem.getUnitPrice() * quantity) 
        - calcDiscount();
    }

    @Override
    public String toString() {
        return 
        String.format("| Main Dish : %-25s %21s | \n", mainDishFoodItem.getFoodName(),
        String.format("(RM%.2f / unit)", mainDishFoodItem.getUnitPrice())) +
        String.format("| Side Dish : %-25s %21s | \n", sideDishFoodItem.getFoodName(),
        String.format("(RM%.2f / unit)", sideDishFoodItem.getUnitPrice())) +
        String.format("|     Drink : %-25s %21s | \n", drinkFoodItem.getFoodName(),
        String.format("(RM%.2f / unit)", drinkFoodItem.getUnitPrice())) +
        String.format("|  Quantity : %-10s %36s | \n", quantity,
        String.format("10%% Discount : - RM%.2f", calcDiscount())) +
        String.format("| %59s | \n", String.format("Subtotal : RM%.2f", calcPrice()));
    }
}