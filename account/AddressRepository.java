package account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import adt.LinkedHashMap;
import adt.MapInterface;
import utility.Database;

public class AddressRepository {
  private static final String TABLE_NAME = "address";

  // Add the address record to the database
  public static void create(Address address, String customerEmail) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    try {
      String fields = "address_line, city, state, postcode, customer_email";
      String values = "VALUES (?, ?, ?, ?, ?)";

      String sql = "INSERT INTO " + TABLE_NAME + " (" + fields + ") " + values + " RETURNING id;";

      pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pstmt.setString(1, address.getAddressLine());
      pstmt.setString(2, address.getCity());
      pstmt.setString(3, address.getState());
      pstmt.setString(4, address.getPostcode());
      pstmt.setString(5, customerEmail);

      pstmt.executeUpdate();

      ResultSet rs = pstmt.getGeneratedKeys();
      if (rs.next()) {
        // Retrieve the auto generated key(s).
        int key = rs.getInt(1);
        address.setId(key);
      }

      pstmt.close();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }

  // Find addresses belong a customer
  public static MapInterface<Integer, Address> findAllByCustomerEmail(String email) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    MapInterface<Integer, Address> addressesMap = new LinkedHashMap<Integer, Address>();

    try {
      String sql = "SELECT id, address_line, city, state, postcode FROM " + TABLE_NAME
          + " WHERE customer_email = '" + email + "';";

      pstmt = connection.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        // Read address data and put the address object into the map
        int addressId = rs.getInt(1);
        Address address = new Address(
            addressId,
            rs.getString(2),
            rs.getString(3),
            rs.getString(4),
            rs.getString(5));
        addressesMap.put(addressId, address);
      }
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
    return addressesMap;
  }

  public static Address findById(int id) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;

    try {
      String sql = "SELECT * FROM " + TABLE_NAME
          + " WHERE id = '" + id + "';";

      pstmt = connection.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        Address address = new Address(
            rs.getInt(1),
            rs.getString(2),
            rs.getString(3),
            rs.getString(4),
            rs.getString(5));
        return address;
      }
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
    return null;
  }

  public static void update(Address address) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    try {
      String sql = "UPDATE " + TABLE_NAME
          + " SET address_line = ?, city = ?, state = ?, postcode = ?";

      sql += " WHERE id = '" + address.getId() + "';";

      pstmt = connection.prepareStatement(sql);

      pstmt.setString(1, address.getAddressLine());
      pstmt.setString(2, address.getCity());
      pstmt.setString(3, address.getState());
      pstmt.setString(4, address.getPostcode());

      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }

  // Delete the address record from the database
  public static void deleteById(int id) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    try {
      String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?;";

      pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pstmt.setInt(1, id);
      pstmt.executeUpdate();

      pstmt.close();
    } catch (SQLException e) {
      System.out.println( e.getErrorCode());
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }

  // Delete the address record from the database
  public static void deleteAll() {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    try {
      String sql = "DELETE FROM " + TABLE_NAME + ";";

      pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pstmt.executeUpdate();

      pstmt.close();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }
}
