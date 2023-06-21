package food;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;

import main.Input;
import utility.Database;
import utility.Font;

public abstract class FoodItem implements Comparable<FoodItem> {
  // ******************************
  // --- --- Data members
  // ******************************
    // Table name in the cloud database 
    public static final String TABLE_NAME = "food_item";
    protected int foodID;
    protected String foodName;
    protected double unitPrice;
    protected int inStockQty;
  
  // ******************************
  // --- --- Constructors
  // ******************************
    protected FoodItem() {
    }
    
    protected FoodItem(
    int foodID, String foodName, double unitPrice, int inStockQty) {
        this.foodID = foodID;
        this.foodName = foodName;
        this.unitPrice = unitPrice;
        this.inStockQty = inStockQty;
    }

  // ******************************
  // --- --- Getters
  // ******************************
    public int getFoodID() {
        return foodID;
    }

    public String getFoodName() {
        return foodName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getInStockQty() {
        return inStockQty;
    }

  // ******************************
  // --- --- Methods
  // ******************************
    public void addToCart(int quantity) {
      if (this.inStockQty >= quantity) {
        this.inStockQty -= quantity;
      }
    }

    public void removeFromCart(int quantity) {
        this.inStockQty += quantity;
    }
    
    // methods to prompt input from the users
    // to set values for food name, unit price, and in stock qty
    public abstract void getInputs();
    protected void getInputsGeneral() {
        promptFoodName();
        promptUnitPrice();
        promptInStockQty();
    }

    // prompt for food name
    public void promptFoodName() {
      boolean validFoodItemName;
      do {
        System.out.print("> Enter Food Name: ");
        this.foodName = Input.SCANNER.nextLine().trim();
        if (this.foodName.length() == 0) {
          Font.print(Font.ANSI_RED, "<<< Food name is required, please enter it. >>>");
          validFoodItemName = false;
        } else if (this.foodName.length() > 25) {
          Font.print(Font.ANSI_RED, "<<< Food name is too long, please enter again. >>>");
          validFoodItemName = false;
        } else {
          validFoodItemName = true;
        }
      } while (!validFoodItemName);
    }

    // prompt for unit price
    public void promptUnitPrice() {
      boolean validUnitPrice;
      do {
        try {
          System.out.print("> Enter Unit Price: ");
          this.unitPrice = Input.SCANNER.nextDouble();
          if (this.unitPrice <= 0) {
            Font.print(Font.ANSI_RED, "<<< Unit price must be more than 0. >>>");
            validUnitPrice = false;
          } else if (this.unitPrice > 999.99) {
            Font.print(Font.ANSI_RED, "<<< The maximum unit price is RM999.99. >>>");
            validUnitPrice = false;
          } else {
            Input.SCANNER.nextLine(); //To consume the '\n'
            validUnitPrice = true;
          }
        } catch (InputMismatchException inputMismatchException) {
          Input.SCANNER.nextLine(); //To consume the '\n'
          System.out.printf("%s", Font.getStr(Font.ANSI_RED, "<<< Invalid choice, please enter number value only. >>>\n"));
          validUnitPrice = false;
        }
      } while (!validUnitPrice);
    }

    // prompt for in stock qty
    public void promptInStockQty() {
      boolean validInStockQty;
      do {
        try {
          System.out.print("> Enter In Stock Quantity: ");
          this.inStockQty = Input.SCANNER.nextInt();
          if (this.inStockQty <= 0) {
            Font.print(Font.ANSI_RED, "<<< In stock quantity must be more than 0. >>>");
            validInStockQty = false;
          } else if (this.inStockQty > 1000) {
            Font.print(Font.ANSI_RED, "<<< Maximum in stock quantity is 1000. >>>");
            validInStockQty = false;
          } else {
            Input.SCANNER.nextLine(); //To consume the '\n'
            validInStockQty = true;
          }
        } catch (InputMismatchException inputMismatchException) {
          Input.SCANNER.nextLine(); //To consume the '\n'
          System.out.printf("%s", Font.getStr(Font.ANSI_RED, "<<< Invalid choice, please enter integers only. >>>\n"));
          validInStockQty = false;
        }
      } while (!validInStockQty);
    }

    // methods for adding a new food item to the database
    public abstract void addToDB();
    protected void addToDBGeneral() {
        Connection connection = Database.getConnection();
        PreparedStatement pstmt = null;
        try {
          String fields = "food_name, unit_price, in_stock_qty";
          String values = "VALUES (?, ?, ?)";
    
          String sql = "INSERT INTO " + TABLE_NAME + " (" + fields + ") " + values + " RETURNING food_id;";
    
          pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          pstmt.setString(1, this.foodName);
          pstmt.setDouble(2, this.unitPrice);
          pstmt.setInt(3, this.inStockQty);
    
          pstmt.executeUpdate();

          ResultSet rs = pstmt.getGeneratedKeys();
          if (rs.next()) {
            // Retrieve the auto generated key(s).
            int key = rs.getInt(1);
            this.foodID = key;
          }

          pstmt.close();
        } catch (SQLException e) {
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    // methods for updating data fields in the database
    public abstract void updateInDB();
    public void updateInDBGeneral() {
      updateFoodNameInDB();
      updateUnitPriceInDB();
      updateInStockQtyInDB();
    };

    public void updateFoodNameInDB() {
      Connection connection = Database.getConnection();
      PreparedStatement pstmt = null;
      try {
        String sql = "UPDATE " + TABLE_NAME 
        + " SET food_name = ? " 
        + "WHERE food_id = " + foodID + ";";
  
        pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, this.foodName);
  
        pstmt.executeUpdate();

        pstmt.close();
      } catch (SQLException e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
      }
    }

    public void updateUnitPriceInDB() {
      Connection connection = Database.getConnection();
      PreparedStatement pstmt = null;
      try {
        String sql = "UPDATE " + TABLE_NAME 
        + " SET unit_price = ? " 
        + "WHERE food_id = " + foodID + ";";
  
        pstmt = connection.prepareStatement(sql);
        pstmt.setDouble(1, this.unitPrice);
  
        pstmt.executeUpdate();

        pstmt.close();
      } catch (SQLException e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
      }
    };

    public void updateInStockQtyInDB() {
      Connection connection = Database.getConnection();
      PreparedStatement pstmt = null;
      try {
        String sql = "UPDATE " + TABLE_NAME 
        + " SET in_stock_qty = ? " 
        + "WHERE food_id = " + foodID + ";";
  
        pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, this.inStockQty);
  
        pstmt.executeUpdate();

        pstmt.close();
      } catch (SQLException e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
      }
    };

    // method for deleting a food item in the database
    public void deleteFromDB() {
      Connection connection = Database.getConnection();
      PreparedStatement pstmt = null;
      try {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE food_id = ?;";
  
        pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstmt.setInt(1, this.foodID);
        pstmt.executeUpdate();
  
        pstmt.close();
      } catch (SQLException e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
      }
    }

    // toString() method to print out details of a food item
    // to be utilized in customer order menu
    @Override
    public String toString() {
      if (inStockQty > 0) {
        return String.format("%-30sRM%6.2f%9d", 
        foodName, unitPrice, inStockQty);
      } else {
        return String.format("%-30sRM%6.2f%s", 
        foodName, unitPrice,
        Font.getStr(Font.ANSI_RED, String.format("%9d", inStockQty)));
      }
    }

    // Compare by Food Item Type & In Stock Quantity
    @Override
    public int compareTo(FoodItem foodItem) {
      Integer thisInStockQty = this.inStockQty;
      int compareInStockQtyResult = thisInStockQty.compareTo(foodItem.getInStockQty());
      if (this instanceof MainDish){
        if (foodItem instanceof MainDish) {
          return compareInStockQtyResult;
        } else {
          return -1;
        }
      } else if (this instanceof SideDish) {
        if (foodItem instanceof MainDish) {
          return 1;
        } else if (foodItem instanceof Drink) {
          return -1;
        } else {
          return compareInStockQtyResult;
        }
      } else {
        if (foodItem instanceof Drink) {
          return compareInStockQtyResult;
        } else {
          return 1;
        }
      }

    }

    // Selection sort algorithm for sorting the array of food items
    public static FoodItem[] sortFoodItems(FoodItem[] arr) {
      for (int i = 0; i < arr.length; ++i) {
        int indexOfSmallest = i;
        for (int j = i + 1; j < arr.length; ++j) {
          if (arr[j].compareTo(arr[indexOfSmallest]) < 0)
            indexOfSmallest = j;
        }
        FoodItem tempArr = arr[indexOfSmallest];
        arr[indexOfSmallest] = arr[i];
        arr[i] = tempArr;
      }
      return arr;
    }
}