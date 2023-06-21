package utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
  // Supabase DB credentials (Cloud DB)
  private static final String DATABASE_URL = "jdbc:postgresql://db.xqpvsbaybsxunctztfri.supabase.co/postgres";
  private static final String USER = "postgres";
  private static final String PASSWORD = "sybBK@w93qryyXj"; // Supabase

  // Our local DB credentials for local development purpose
  // private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/postgres";
  // private static final String PASSWORD = "postgres";

  private static Connection connection;

  public static Connection getConnection() {
    return connection == null ? initConnection() : connection;
  }

  public static Connection initConnection() {
    // [1] Load the driver (jar file must be on class path)
    try {
      Class.forName("org.postgresql.Driver");
    } catch (Exception e) {
      System.err.println("Couldn't find driver");
      System.err.println(e);
      System.exit(1);
    }

    // [2] Set up connection properties
    Properties props = new Properties();
    props.setProperty("user", USER);
    props.setProperty("password", PASSWORD);
    props.setProperty("tcpKeepAlive", "true");

    // [3] Open the connection to the database
    try {
      connection = DriverManager.getConnection(DATABASE_URL, props);
      return connection;
    } catch (Exception e) {
      System.err.println("SQL operations failed");
      System.err.println(e);
      System.exit(2);
    }
    return null;
  }

  public static void createTables() {
    String CREATE_CUSTOMER_ACCOUNT_TABLE_SQL = "CREATE TABLE IF NOT EXISTS customer_account (" +
        " email         VARCHAR(255) PRIMARY KEY NOT NULL," +
        " username      VARCHAR(30) UNIQUE NOT NULL, " +
        " password      VARCHAR(50) NOT NULL, " +
        " first_name    VARCHAR(50) NOT NULL, " +
        " last_name     VARCHAR(50) NOT NULL, " +
        " date_created  DATE NOT NULL, " +
        " phone_num     VARCHAR(12), " +
        " points INT    DEFAULT 0 NOT NULL, " +
        " date_of_birth DATE" +
        " );";

    String CREATE_ADMIN_ACCOUNT_TABLE_SQL = "CREATE TABLE IF NOT EXISTS admin_account (" +
        " email        VARCHAR(255) PRIMARY KEY NOT NULL," +
        " username     VARCHAR(20) UNIQUE NOT NULL, " +
        " password     VARCHAR(50) NOT NULL, " +
        " first_name   VARCHAR(50) NOT NULL, " +
        " last_name    VARCHAR(50) NOT NULL, " +
        " date_created DATE NOT NULL, " +
        " position     VARCHAR(50) NOT NULL " +
        " );";

    String CREATE_ADDRESS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS address (" +
        " id             SERIAL PRIMARY KEY NOT NULL, " +
        " address_line   VARCHAR(100) UNIQUE NOT NULL, " +
        " city           VARCHAR(50) NOT NULL, " +
        " state          VARCHAR(15) NOT NULL, " +
        " postcode       CHAR(5) NOT NULL, " +
        " customer_email VARCHAR(255) NOT NULL, " +
        " FOREIGN KEY(customer_email) REFERENCES customer_account(email)  " +
        " );";

    String CREATE_CARD_TABLE_SQL = "CREATE TABLE IF NOT EXISTS card (" +
        " card_number      VARCHAR(16) PRIMARY KEY NOT NULL, " +
        " name_on_card     VARCHAR(100) NOT NULL, " +
        " expiration_month INT NOT NULL, " +
        " expiration_year  INT NOT NULL, " +
        " cvc              CHAR(3) NOT NULL, " +
        " customer_email   VARCHAR(255) NOT NULL, " +
        " FOREIGN KEY(customer_email) REFERENCES customer_account(email)  " +
        " );";

    String CREATE_FOOD_ITEM_TABLE_SQL = "CREATE TABLE IF NOT EXISTS food_item (" +
        " food_id        SERIAL PRIMARY KEY NOT NULL, " +
        " food_item_type VARCHAR(8), " +
        " main_dish_type VARCHAR(7), " +
        " food_name      VARCHAR(25) NOT NULL, " +
        " unit_price     DOUBLE PRECISION NOT NULL, " +
        " in_stock_qty   INT NOT NULL" +
        " );";

    String CREATE_ORDER_TABLE_SQL = "CREATE TABLE IF NOT EXISTS customer_order (" +
        " id               SERIAL PRIMARY KEY NOT NULL, " +        
        " voucher_applied  VARCHAR(7), " +
        " voucher_discount DOUBLE PRECISION DEFAULT 0 NOT NULL, " +
        " points_used      INT DEFAULT 0 NOT NULL, " +
        " final_price      DOUBLE PRECISION NOT NULL, " +
        " points_earned    INT NOT NULL," +
        " payment_method   VARCHAR(20) NOT NULL, " +
        " delivery_date    DATE NOT NULL, " +
        " status           VARCHAR(10) NOT NULL," +
        " order_time       TIMESTAMP NOT NULL, " +
        " address_id       INT NOT NULL, " +
        " customer_email   VARCHAR(255) NOT NULL, " +
        " FOREIGN KEY(address_id) REFERENCES address(id), " +
        " FOREIGN KEY(customer_email) REFERENCES customer_account(email) " +
        " );";

    String CREATE_CART_ITEM_TABLE_SQL = "CREATE TABLE IF NOT EXISTS cart_item (" +
        " cart_item_id SERIAL PRIMARY KEY NOT NULL, " +
        " is_set_meal BOOLEAN NOT NULL, " +
        " quantity     INT NOT NULL, " +
        " order_id     INT NOT NULL, " +
        " FOREIGN KEY(order_id) REFERENCES customer_order(id) " +
        " );";

    String CREATE_CART_FOOD_ITEM_TABLE_SQL = "CREATE TABLE IF NOT EXISTS cart_food_item (" +
        " cart_item_id INT NOT NULL, " +
        " food_id      INT NOT NULL, " +
        " food_item_type VARCHAR(8), " +
        " food_name      VARCHAR(25) NOT NULL, " +
        " unit_price     DOUBLE PRECISION NOT NULL, " +
        " PRIMARY KEY(cart_item_id, food_id), " +
        " FOREIGN KEY(cart_item_id) REFERENCES cart_item(cart_item_id) " +
        " );";

    String[] sqlCreateTables = new String[] {
        CREATE_CUSTOMER_ACCOUNT_TABLE_SQL,
        CREATE_ADMIN_ACCOUNT_TABLE_SQL,
        CREATE_ADDRESS_TABLE_SQL,
        CREATE_CARD_TABLE_SQL,
        CREATE_FOOD_ITEM_TABLE_SQL,
        CREATE_ORDER_TABLE_SQL,
        CREATE_CART_ITEM_TABLE_SQL,
        CREATE_CART_FOOD_ITEM_TABLE_SQL,
      };

    for (String sql : sqlCreateTables) {
      try {
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.execute();
        pstmt.close();
      } catch (SQLException e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
      }
    }
  }
}
