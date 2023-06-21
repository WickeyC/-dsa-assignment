package ordermenu;

// CartItem is objects that can be added to Cart
public abstract class CartItem {
    // ******************************
    // --- --- Data members
    // ******************************
    protected int quantity;

    // ******************************
    // --- --- Getters & Setters
    // ******************************
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    // ******************************
    // --- --- Abstract Methods
    // ******************************
    
    // For each cart item to calculate their price (subtotal)
    public abstract double calcPrice();

    // For each cart item to display their details
    @Override
    public abstract String toString();
}