package food;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import utility.Database;

public class Drink extends FoodItem {
    // ******************************
    // --- --- Constructors
    // ******************************
    public Drink() {}

    public Drink(int foodID, String foodName,
    double unitPrice, int inStockQty) {
        super(
            foodID,
            foodName,
            unitPrice,
            inStockQty
        );
    }

    // ******************************
    // --- --- Methods
    // ******************************
    @Override
    public void getInputs() {
        super.getInputsGeneral();
    }

    @Override
    public void addToDB() {
        super.addToDBGeneral();
        Connection connection = Database.getConnection();
        PreparedStatement pstmt = null;
        try {
          String sql = "UPDATE " + TABLE_NAME 
          + " SET food_item_type = ?, main_dish_type = ? " 
          + "WHERE food_id = " + foodID + ";";
    
          pstmt = connection.prepareStatement(sql);
          pstmt.setString(1, "Drink");
          pstmt.setNull(2, Types.NULL);
    
          pstmt.executeUpdate();

          pstmt.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public void updateInDB() {
        super.updateInDBGeneral();
    }
}