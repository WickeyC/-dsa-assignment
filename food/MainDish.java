package food;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.InputMismatchException;

import main.Input;
import utility.Database;
import utility.Font;

public class MainDish extends FoodItem {
    // ******************************
    // --- --- Data members
    // ******************************
    private MainDishType type;

    // ******************************
    // --- --- MainDishType enumeration
    // ******************************
    public static enum MainDishType {
        BURGER("Burger"),
        RICE("Rice"),
        NOODLE("Noodle");

        private final String type;

        private MainDishType(String type) {
            this.type = type;
        }

        public String getType() { return type; }
    } 

    // ******************************
    // --- --- Constructors
    // ******************************
    public MainDish() {}

    public MainDish(int foodID, String foodName, 
    double unitPrice, int inStockQty,
    MainDishType type) {
        super(
            foodID,
            foodName,
            unitPrice,
            inStockQty
        );
        this.type = type;
    }

    // ******************************
    // --- --- Getters
    // ******************************
    public MainDishType getType() {
        return type;
    }

    // ******************************
    // --- --- Methods
    // ******************************
    @Override
    public void getInputs() {
        boolean validChoice = false; int choice = 0;
        System.out.println("----------------------");
        System.out.println("|     1. Burger      |");
        System.out.println("|     2. Rice        |");
        System.out.println("|     3. Noodle      |");
        System.out.println("----------------------");
        do {
        try {
            System.out.print("> Choose Main Dish Type(1-3): ");
            choice = Input.SCANNER.nextInt();
            if (choice < 1 || choice > 3) {
            Font.print(Font.ANSI_RED, "<<< Invalid Choice! >>>");
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
            this.type = MainDishType.BURGER;
            break;
        case 2:
            this.type = MainDishType.RICE;
            break;
        case 3:
            this.type = MainDishType.NOODLE;
            break;
        default:
            break;
        }
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
          pstmt.setString(1, "MainDish");
          pstmt.setString(2, this.type.getType());
    
          pstmt.executeUpdate();

          pstmt.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public void updateInDB() {
        updateMainDishTypeInDB();
        super.updateInDBGeneral();
    }

    public void updateMainDishTypeInDB() {
        Connection connection = Database.getConnection();
        PreparedStatement pstmt = null;
        try {
          String sql = "UPDATE " + TABLE_NAME 
          + " SET main_dish_type = ? " 
          + "WHERE food_id = " + foodID + ";";
    
          pstmt = connection.prepareStatement(sql);
          pstmt.setString(1, this.type.getType());
    
          pstmt.executeUpdate();

          pstmt.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        if (inStockQty > 0) {
            return String.format("%-28s%-12sRM%6.2f%9d", 
            foodName, type.getType(), unitPrice, inStockQty);
        } else {
            return String.format("%-28s%-12sRM%6.2f%s", 
            foodName, type.getType(), unitPrice, 
            Font.getStr(Font.ANSI_RED, String.format("%9d", inStockQty)));
        }
    }
}