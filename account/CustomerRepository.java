package account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import adt.ArrayList;
import adt.MapInterface;
import checkout.Card;
import checkout.CardRepository;
import checkout.Order;
import checkout.OrderRepository;
import utility.Database;
import utility.PasswordUtils;

// CustomerRepository is a class that is focused on interacting with the database table
public class CustomerRepository {
  private static final String TABLE_NAME = "customer_account";

  public static boolean create(Customer customer) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;

    try {
      String fields = "email, username, password, first_name, last_name, date_created, ";
      String values = "VALUES (?, ?, ?, ?, ?, ?::date, ";

      fields += "phone_num, points, date_of_birth";
      values += "?, ?, ?::date";

      String sql = "INSERT INTO " + TABLE_NAME + " (" + fields + ") " + values + ");";

      // Prepare the data to insert to DB
      pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pstmt.setString(1, customer.getEmail());
      pstmt.setString(2, customer.getUsername());
      String password = customer.getPassword();
      String securePassword = PasswordUtils.generateSecurePassword(password);
      customer.setPassword(securePassword);
      pstmt.setString(3, securePassword);
      pstmt.setString(4, customer.getName().getFirstName());
      pstmt.setString(5, customer.getName().getLastName());
      pstmt.setString(6, customer.getDateCreated().toString());
      pstmt.setString(7, customer.getPhoneNum());
      pstmt.setInt(8, customer.getPoints());
      if (customer.getDateOfBirth() == null) {
        pstmt.setNull(9, java.sql.Types.NULL);
      } else {
        pstmt.setString(9, customer.getDateOfBirth().toString());
      }

      pstmt.executeUpdate();
      pstmt.close();

      return true;
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
    return false;
  }

  // Return an arraylist of all Customer objects in the database
  public static ArrayList<Customer> findAll() {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    ArrayList<Customer> allCustomers = new ArrayList<Customer>();
    try {
      String sql = "SELECT * FROM " + TABLE_NAME + ";";
      pstmt = connection.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        Customer customer = new Customer(
            rs.getString(1),
            rs.getString(2),
            rs.getString(3),
            rs.getString(4),
            rs.getString(5),
            rs.getString(7),
            null);
        customer.setDateCreated(LocalDate.parse(rs.getString(6)));
        customer.setPoints(rs.getInt(8));
        if (rs.getString(9) != null) {
          customer.setDateOfBirth(LocalDate.parse(rs.getString(9)));
        }
        MapInterface<Integer, Order> ordersMap = OrderRepository.findAllByCustomerEmail(customer.getEmail());
        customer.setOrders(ordersMap);
        MapInterface<String, Card> cardsMap = CardRepository.findAllByCustomerEmail(customer.getEmail());
        customer.setCards(cardsMap);
        MapInterface<Integer, Address> addressesMap = AddressRepository.findAllByCustomerEmail(customer.getEmail());
        customer.setAddresses(addressesMap);
        allCustomers.add(customer);
      }
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
    return allCustomers;
  }

  public static Customer findBy(String columnName, String value) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    Customer customer = null;

    try {
      String sql = "SELECT * FROM " + TABLE_NAME
          + " WHERE " + columnName + " = '" + value + "';";

      pstmt = connection.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        customer = new Customer(
            rs.getString(1),
            rs.getString(2),
            rs.getString(3),
            rs.getString(4),
            rs.getString(5),
            rs.getString(7),
            null);
        customer.setDateCreated(LocalDate.parse(rs.getString(6)));
        customer.setPoints(rs.getInt(8));
        if (rs.getString(9) != null) {
          customer.setDateOfBirth(LocalDate.parse(rs.getString(9)));
        }
        MapInterface<Integer, Order> ordersMap = OrderRepository.findAllByCustomerEmail(customer.getEmail());
        customer.setOrders(ordersMap);
        MapInterface<String, Card> cardsMap = CardRepository.findAllByCustomerEmail(customer.getEmail());
        customer.setCards(cardsMap);
        MapInterface<Integer, Address> addressesMap = AddressRepository.findAllByCustomerEmail(customer.getEmail());
        customer.setAddresses(addressesMap);
        return customer;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return customer;
  }

  public static Customer findByEmailOrUsername(Customer customer) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    Customer foundCustomer = null;

    try {
      String sql = "SELECT * FROM " + TABLE_NAME
          + " WHERE email = '" + customer.getEmail() + "' OR "
          + " username = '" + customer.getUsername() + "';";

      pstmt = connection.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        foundCustomer = new Customer(
            rs.getString(1),
            rs.getString(2),
            rs.getString(3),
            rs.getString(4),
            rs.getString(5),
            rs.getString(7),
            null);
        foundCustomer.setDateCreated(LocalDate.parse(rs.getString(6)));
        foundCustomer.setPoints(rs.getInt(8));
        if (rs.getString(9) != null) {
          foundCustomer.setDateOfBirth(LocalDate.parse(rs.getString(9)));
        }
        MapInterface<Integer, Order> ordersMap = OrderRepository.findAllByCustomerEmail(foundCustomer.getEmail());
        foundCustomer.setOrders(ordersMap);
        MapInterface<String, Card> cardsMap = CardRepository.findAllByCustomerEmail(foundCustomer.getEmail());
        foundCustomer.setCards(cardsMap);
        MapInterface<Integer, Address> addressesMap = AddressRepository.findAllByCustomerEmail(foundCustomer.getEmail());
        foundCustomer.setAddresses(addressesMap);
        return foundCustomer;
      }
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
    return foundCustomer;
  }

  // Update the customer data (except password) in database to the customer data
  // passed in
  public static void update(Customer customer) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    try {
      String[] fieldsToUpdate = { "first_name", "last_name", "phone_num", "points", "date_of_birth" };

      String sql = "UPDATE " + TABLE_NAME + " SET ";

      for (int i = 0; i < fieldsToUpdate.length; i++) {
        if (i != fieldsToUpdate.length - 1) {
          sql += fieldsToUpdate[i] + " = ?";
          if (fieldsToUpdate[i].equals("date_of_birth")) {
            sql += "::date, ";
          } else {
            sql += ", ";
          }
        } else {
          sql += fieldsToUpdate[i] + " = ?";
          if (fieldsToUpdate[i].equals("date_of_birth")) {
            sql += "::date ";
          } else {
            sql += " ";
          }
        }
      }

      sql += " WHERE email = '" + customer.getEmail() + "';";

      pstmt = connection.prepareStatement(sql);

      pstmt.setString(1, customer.getName().getFirstName());
      pstmt.setString(2, customer.getName().getLastName());
      pstmt.setString(3, customer.getPhoneNum());
      pstmt.setInt(4, customer.getPoints());
      if (customer.getDateOfBirth() == null) {
        pstmt.setNull(5, java.sql.Types.NULL);
      } else {
        pstmt.setString(5, customer.getDateOfBirth().toString());
      }

      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }

  public static void updatePassword(Customer customer) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    try {
      String sql = "UPDATE " + TABLE_NAME + " SET password = ?"
          + " WHERE email = '" + customer.getEmail() + "';";

      pstmt = connection.prepareStatement(sql);

      String password = customer.getPassword();
      String securePassword = PasswordUtils.generateSecurePassword(password);

      pstmt.setString(1, securePassword);

      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }
}