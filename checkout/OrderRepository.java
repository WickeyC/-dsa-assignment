package checkout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;

import account.AddressRepository;
import adt.LinkedHashMap;
import adt.MapInterface;
import food.Drink;
import food.FoodItem;
import food.MainDish;
import food.SideDish;
import ordermenu.AlaCarte;
import ordermenu.Cart;
import ordermenu.CartItem;
import ordermenu.SetMeal;
import utility.Database;

public class OrderRepository {
  private static final String TABLE_NAME = "customer_order";

  // Add the order record to the database
  public static void create(Order order, String customerEmail) {

    // Set order time to current time
    order.setOrderTime(LocalDateTime.now());
    Connection connection = Database.getConnection();
    try {
      String fields = "voucher_applied, voucher_discount, points_used, " +
          "final_price, points_earned, payment_method, " +
          "delivery_date, status, order_time, address_id, customer_email";
      String values = "VALUES (?, ?, ?, ?, ?, ?, ?::date, ?, ?, ?, ?)";

      String sql = "INSERT INTO " + TABLE_NAME + " (" + fields + ") " + values + " RETURNING id;";

      // Prepare the order data to insert into the customer_order table
      PreparedStatement order_pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      order_pstmt.setString(1, order.getVoucherApplied());
      order_pstmt.setDouble(2, order.getVoucherDiscount());
      order_pstmt.setInt(3, order.getPointsUsed());
      order_pstmt.setDouble(4, order.getFinalPrice());
      order_pstmt.setInt(5, order.getPointsEarned());
      order_pstmt.setString(6, order.getPaymentMethod());
      order_pstmt.setString(7, order.getDeliveryDate().toString());
      order_pstmt.setString(8, order.getStatus());
      order_pstmt.setObject(9, order.getOrderTime());
      order_pstmt.setInt(10, order.getDeliveryAddress().getId());
      order_pstmt.setString(11, customerEmail);

      // Execute the query
      order_pstmt.executeUpdate();

      // Get the primary key (orderId) of the inserted record
      ResultSet rs = order_pstmt.getGeneratedKeys();
      if (rs.next()) {
        // Retrieve the auto generated key
        int key = rs.getInt(1);
        // Set the id to the generated key
        order.setId(key);
      }
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }

    try {
      String cart_item_fields = "is_set_meal, quantity, order_id";
      String cart_item_values = "VALUES (?, ?, ?)";
      String cart_item_sql = "INSERT INTO cart_item ("
          + cart_item_fields + ") "
          + cart_item_values + ";";
      PreparedStatement cart_item_pstmt = connection.prepareStatement(cart_item_sql, Statement.RETURN_GENERATED_KEYS);

      // Prepare the cartItem data to insert into the cart_item table
      for (CartItem cartItem : Cart.getCartItems()) {
        if (cartItem instanceof SetMeal) {
          SetMeal setMealCartItem = (SetMeal) cartItem;
          cart_item_pstmt.setBoolean(1, true);
          cart_item_pstmt.setInt(2, setMealCartItem.getQuantity());
        } else {
          AlaCarte alaCarteCartItem = (AlaCarte) cartItem;
          cart_item_pstmt.setBoolean(1, false);
          cart_item_pstmt.setInt(2, alaCarteCartItem.getQuantity());
        }
        cart_item_pstmt.setInt(3, order.getId());

        // Execute the query
        cart_item_pstmt.executeUpdate();

        // Get the primary key (cart_item_id) of the inserted record
        ResultSet rs = cart_item_pstmt.getGeneratedKeys();

        if (rs.next()) {
          // Retrieve the auto generated key(s).
          int cartItemId = rs.getInt(1);
          try {
            String cart_food_item_fields = "cart_item_id, food_id, food_item_type, food_name, unit_price";
            String cart_food_item_values = "VALUES (?, ?, ?, ?, ?)";
            String cart_food_item_sql = "INSERT INTO cart_food_item ("
                + cart_food_item_fields + ") "
                + cart_food_item_values + ";";
            PreparedStatement cart_food_item_pstmt = connection.prepareStatement(cart_food_item_sql);

            // Prepare the food item data to insert into the cart_food_item table
            if (cartItem instanceof SetMeal) {
              // Insert the 3 FoodItem records (MainDish, SideDish, Drink) into
              // the cart_food_item table when the CartItem is a SetMeal
              SetMeal setMealCartItem = (SetMeal) cartItem;
              cart_food_item_pstmt.setInt(1, cartItemId);
              cart_food_item_pstmt.setInt(2, setMealCartItem.getMainDishFoodItem().getFoodID());
              cart_food_item_pstmt.setString(3, "MainDish");
              cart_food_item_pstmt.setString(4, setMealCartItem.getMainDishFoodItem().getFoodName());
              cart_food_item_pstmt.setDouble(5, setMealCartItem.getMainDishFoodItem().getUnitPrice());
              cart_food_item_pstmt.executeUpdate();
              cart_food_item_pstmt.setInt(1, cartItemId);
              cart_food_item_pstmt.setInt(2, setMealCartItem.getSideDishFoodItem().getFoodID());
              cart_food_item_pstmt.setString(3, "SideDish");
              cart_food_item_pstmt.setString(4, setMealCartItem.getSideDishFoodItem().getFoodName());
              cart_food_item_pstmt.setDouble(5, setMealCartItem.getSideDishFoodItem().getUnitPrice());
              cart_food_item_pstmt.executeUpdate();
              cart_food_item_pstmt.setInt(1, cartItemId);
              cart_food_item_pstmt.setInt(2, setMealCartItem.getDrinkFoodItem().getFoodID());
              cart_food_item_pstmt.setString(3, "Drink");
              cart_food_item_pstmt.setString(4, setMealCartItem.getDrinkFoodItem().getFoodName());
              cart_food_item_pstmt.setDouble(5, setMealCartItem.getDrinkFoodItem().getUnitPrice());
              cart_food_item_pstmt.executeUpdate();
            } else {
              // Insert the 1 FoodItem record into
              // the cart_food_item table when the CartItem is an AlaCarte cart item
              AlaCarte alaCarteCartItem = (AlaCarte) cartItem;
              cart_food_item_pstmt.setInt(1, cartItemId);
              cart_food_item_pstmt.setInt(2, alaCarteCartItem.getAlaCarteFoodItem().getFoodID());
              if (alaCarteCartItem.getAlaCarteFoodItem() instanceof MainDish) {
                cart_food_item_pstmt.setString(3, "MainDish");
              } else if (alaCarteCartItem.getAlaCarteFoodItem() instanceof SideDish) {
                cart_food_item_pstmt.setString(3, "SideDish");
              } else {
                cart_food_item_pstmt.setString(3, "Drink");
              }
              cart_food_item_pstmt.setString(4, alaCarteCartItem.getAlaCarteFoodItem().getFoodName());
              cart_food_item_pstmt.setDouble(5, alaCarteCartItem.getAlaCarteFoodItem().getUnitPrice());
              cart_food_item_pstmt.executeUpdate();
            }
            cart_food_item_pstmt.close();
          } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
          }
        }
      }

      // Close the connection
      cart_item_pstmt.close();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }

  public static MapInterface<Integer, Order> findAllProcessingOrders() {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    MapInterface<Integer, Order> ordersMap = new LinkedHashMap<Integer, Order>(); 

    // Reading from customer_order table
    try {
      String sql = "SELECT id, voucher_applied, voucher_discount, points_used, "
          + "final_price, points_earned, payment_method, " 
          + "delivery_date, status, order_time, address_id, customer_email FROM "
          + TABLE_NAME
          + " WHERE status = 'PROCESSING';";

      pstmt = connection.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        // Read order data and put the order object into the map
        int orderId = rs.getInt(1);
        Order order = new Order(
            orderId,
            rs.getString(2),
            rs.getDouble(3),
            rs.getInt(4),
            rs.getDouble(5),
            AddressRepository.findById(rs.getInt(11)),
            rs.getString(7),
            rs.getInt(6),
            LocalDate.parse(rs.getString(8)),
            rs.getString(9),
            rs.getObject(10, LocalDateTime.class));
        ordersMap.put(orderId, order);

        // Read the associated CartItem record from cart_item table
        String cart_item_sql = "SELECT cart_item_id, is_set_meal, quantity, order_id FROM cart_item"
            + " WHERE order_id = " + order.getId() + ";";
        pstmt = connection.prepareStatement(cart_item_sql);
        ResultSet cart_item_rs = pstmt.executeQuery();
        while (cart_item_rs.next()) {
          // Read the associated CartFoodItem record from cart_food_item table
          // One cart_item has many cart_food_item because one CartItem can have
          // many food (because of one SetMeal cart item can have 3 food)
          String cart_food_item_sql = "SELECT cart_item_id, food_id, food_item_type, food_name, unit_price FROM cart_food_item"
              + " WHERE cart_item_id = " + cart_item_rs.getInt(1) + ";";
          pstmt = connection.prepareStatement(cart_food_item_sql);
          ResultSet cart_food_item_rs = pstmt.executeQuery();
          MainDish mainDishFoodItem = null;
          SideDish sideDishFoodItem = null;
          Drink drinkFoodItem = null;
          CartItem cartItem = null;
          while (cart_food_item_rs.next()) {
            if (cart_item_rs.getBoolean(2)) {
              if (cart_food_item_rs.getString(3).equals("MainDish")) {
                mainDishFoodItem = new MainDish(cart_food_item_rs.getInt(2), cart_food_item_rs.getString(4),
                    cart_food_item_rs.getDouble(5), 0, null);
              } else if (cart_food_item_rs.getString(3).equals("SideDish")) {
                sideDishFoodItem = new SideDish(cart_food_item_rs.getInt(2), cart_food_item_rs.getString(4),
                    cart_food_item_rs.getDouble(5), 0);
              } else {
                drinkFoodItem = new Drink(cart_food_item_rs.getInt(2), cart_food_item_rs.getString(4),
                    cart_food_item_rs.getDouble(5), 0);
              }

              cartItem = new SetMeal(mainDishFoodItem, sideDishFoodItem, drinkFoodItem,
                  cart_item_rs.getInt(3));
            } else {
              FoodItem alaCarteFoodItem;
              if (cart_food_item_rs.getString(3).equals("MainDish")) {
                alaCarteFoodItem = new MainDish(cart_food_item_rs.getInt(2), cart_food_item_rs.getString(4),
                    cart_food_item_rs.getDouble(5), 0, null);
              } else if (cart_food_item_rs.getString(3).equals("SideDish")) {
                alaCarteFoodItem = new SideDish(cart_food_item_rs.getInt(2), cart_food_item_rs.getString(4),
                    cart_food_item_rs.getDouble(5), 0);
              } else {
                alaCarteFoodItem = new Drink(cart_food_item_rs.getInt(2), cart_food_item_rs.getString(4),
                    cart_food_item_rs.getDouble(5), 0);
              }
              cartItem = new AlaCarte(alaCarteFoodItem, cart_item_rs.getInt(3));
            }
          }
          order.getItems().add(cartItem);
        }
      }
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
    return ordersMap;
  }

  public static MapInterface<Integer, Order> findAllByCustomerEmail(String customerEmail) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    MapInterface<Integer, Order> ordersMap = new LinkedHashMap<Integer, Order>();

    // Reading from customer_order table
    try {
      String sql = "SELECT id, voucher_applied, voucher_discount, points_used, "
          + "final_price, points_earned, payment_method, " 
          + "delivery_date, status, order_time, address_id, customer_email FROM "
          + TABLE_NAME
          + " WHERE customer_email = '" + customerEmail + "';";

      pstmt = connection.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        // Read order data and put the order object into the map
        int orderId = rs.getInt(1);
        Order order = new Order(
            orderId,
            rs.getString(2),
            rs.getDouble(3),
            rs.getInt(4),
            rs.getDouble(5),
            AddressRepository.findById(rs.getInt(11)),
            rs.getString(7),
            rs.getInt(6),
            LocalDate.parse(rs.getString(8)),
            rs.getString(9),
            rs.getObject(10, LocalDateTime.class));
        ordersMap.put(orderId, order);

        // Read the associated CartItem record from cart_item table
        String cart_item_sql = "SELECT cart_item_id, is_set_meal, quantity, order_id FROM cart_item"
            + " WHERE order_id = " + order.getId() + ";";
        pstmt = connection.prepareStatement(cart_item_sql);
        ResultSet cart_item_rs = pstmt.executeQuery();
        while (cart_item_rs.next()) {
          // Read the associated CartFoodItem record from cart_food_item table
          // One cart_item has many cart_food_item because one CartItem can have
          // many food (because of one SetMeal cart item can have 3 food)
          String cart_food_item_sql = "SELECT cart_item_id, food_id, food_item_type, food_name, unit_price FROM cart_food_item"
              + " WHERE cart_item_id = " + cart_item_rs.getInt(1) + ";";
          pstmt = connection.prepareStatement(cart_food_item_sql);
          ResultSet cart_food_item_rs = pstmt.executeQuery();
          MainDish mainDishFoodItem = null;
          SideDish sideDishFoodItem = null;
          Drink drinkFoodItem = null;
          CartItem cartItem = null;
          while (cart_food_item_rs.next()) {
            if (cart_item_rs.getBoolean(2)) {
              if (cart_food_item_rs.getString(3).equals("MainDish")) {
                mainDishFoodItem = new MainDish(cart_food_item_rs.getInt(2), cart_food_item_rs.getString(4),
                    cart_food_item_rs.getDouble(5), 0, null);
              } else if (cart_food_item_rs.getString(3).equals("SideDish")) {
                sideDishFoodItem = new SideDish(cart_food_item_rs.getInt(2), cart_food_item_rs.getString(4),
                    cart_food_item_rs.getDouble(5), 0);
              } else {
                drinkFoodItem = new Drink(cart_food_item_rs.getInt(2), cart_food_item_rs.getString(4),
                    cart_food_item_rs.getDouble(5), 0);
              }

              cartItem = new SetMeal(mainDishFoodItem, sideDishFoodItem, drinkFoodItem,
                  cart_item_rs.getInt(3));
            } else {
              FoodItem alaCarteFoodItem;
              if (cart_food_item_rs.getString(3).equals("MainDish")) {
                alaCarteFoodItem = new MainDish(cart_food_item_rs.getInt(2), cart_food_item_rs.getString(4),
                    cart_food_item_rs.getDouble(5), 0, null);
              } else if (cart_food_item_rs.getString(3).equals("SideDish")) {
                alaCarteFoodItem = new SideDish(cart_food_item_rs.getInt(2), cart_food_item_rs.getString(4),
                    cart_food_item_rs.getDouble(5), 0);
              } else {
                alaCarteFoodItem = new Drink(cart_food_item_rs.getInt(2), cart_food_item_rs.getString(4),
                    cart_food_item_rs.getDouble(5), 0);
              }
              cartItem = new AlaCarte(alaCarteFoodItem, cart_item_rs.getInt(3));
            }
          }
          order.getItems().add(cartItem);
        }
      }
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
    return ordersMap;
  }

  public static void updateStatus(Order order) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    try {
      String sql = "UPDATE " + TABLE_NAME + " SET status = ?"
          + " WHERE id = '" + order.getId() + "';";

      pstmt = connection.prepareStatement(sql);

      pstmt.setString(1, order.getStatus());

      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }

  public static void updateDeliveryDate(Order order) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    try {
      String sql = "UPDATE " + TABLE_NAME + " SET delivery_date = ?::date"
          + " WHERE id = '" + order.getId() + "';";

      pstmt = connection.prepareStatement(sql);

      pstmt.setString(1, order.getDeliveryDate().toString());

      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }
}
