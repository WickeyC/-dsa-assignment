package ordermenu;

import food.FoodItem;

public class AlaCarte extends  CartItem{
    // ******************************
    // --- --- Data members
    // ******************************
    // Ala Carte Cart Item has only one food item
    private FoodItem alaCarteFoodItem;

    // ******************************
    // --- --- Constructors
    // ******************************
    public AlaCarte(FoodItem alaCarteFoodItem, int quantity) {
        this.alaCarteFoodItem = alaCarteFoodItem;
        this.quantity = quantity;
    }

    // ******************************
    // --- --- Getters
    // ******************************
    public FoodItem getAlaCarteFoodItem() {
        return alaCarteFoodItem;
    }

    // ******************************
    // --- --- Methods
    // ******************************
    @Override
    public double calcPrice(){
        return alaCarteFoodItem.getUnitPrice() * quantity;
    }

    @Override
    public String toString() {
        return 
        String.format("| Food: %-30s %22s | \n", alaCarteFoodItem.getFoodName(),
        String.format("(RM%.2f / unit)", alaCarteFoodItem.getUnitPrice())) +
        String.format("| Quantity: %-20s %28s | \n", quantity,
        String.format("Subtotal : RM%.2f", calcPrice()));
    }
}