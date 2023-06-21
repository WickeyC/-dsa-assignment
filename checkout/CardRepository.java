package checkout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import adt.LinkedHashMap;
import adt.MapInterface;
import utility.Database;

public class CardRepository {
  private static final String TABLE_NAME = "card";

  // Add the card record to the database
  public static void create(Card card, String customerEmail) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    try {
      String fields = "card_number, name_on_card, expiration_month, expiration_year, cvc, customer_email";
      String values = "VALUES (?, ?, ?, ?, ?, ?)";

      String sql = "INSERT INTO " + TABLE_NAME + " (" + fields + ") " + values + ";";

      pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pstmt.setString(1, card.getCardNumber());
      pstmt.setString(2, card.getNameOnCard());
      pstmt.setInt(3, card.getExpirationMonth());
      pstmt.setInt(4, card.getExpirationYear());
      pstmt.setString(5, card.getCvc());
      pstmt.setString(6, customerEmail);

      pstmt.executeUpdate();
      pstmt.close();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }

  public static MapInterface<String, Card> findAllByCustomerEmail(String customerEmail) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    MapInterface<String, Card> cardsMap = new LinkedHashMap<String, Card>();

    try {
      String sql = "SELECT card_number, name_on_card, expiration_month, expiration_year, cvc FROM " + TABLE_NAME
          + " WHERE customer_email = '" + customerEmail + "';";

      pstmt = connection.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        // Read card data and put the card object into the map
        String cardNumber = rs.getString(1);
        Card card = new Card(
            cardNumber,
            rs.getString(2),
            rs.getInt(3),
            rs.getInt(4),
            rs.getString(5));
        cardsMap.put(cardNumber, card);
      }
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
    return cardsMap;
  }

  public static Card findOneByCardNumber(String cardNumber) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;

    try {
      String sql = "SELECT card_number, name_on_card, expiration_month, expiration_year, cvc FROM " + TABLE_NAME
          + " WHERE card_number = '" + cardNumber + "';";

      pstmt = connection.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        // Read card data and put the card object into the map
        Card card = new Card(
            rs.getString(1),
            rs.getString(2),
            rs.getInt(3),
            rs.getInt(4),
            rs.getString(5));
        return card;
      }
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
    return null;
  }

  public static void update(Card card) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    try {
      String sql = "UPDATE " + TABLE_NAME
          + " SET name_on_card = ?, expiration_month = ?, expiration_year = ?, cvc = ?";

      sql += " WHERE card_number = '" + card.getCardNumber() + "';";

      pstmt = connection.prepareStatement(sql);

      pstmt.setString(1, card.getNameOnCard());
      pstmt.setInt(2, card.getExpirationMonth());
      pstmt.setInt(3, card.getExpirationYear());
      pstmt.setString(4, card.getCvc());

      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }

  // Delete the card record from the database
  public static void deleteByCardNumber(String cardNumber) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    try {
      String sql = "DELETE FROM " + TABLE_NAME + " WHERE card_number = ?;";

      pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pstmt.setString(1, cardNumber);
      pstmt.executeUpdate();

      pstmt.close();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }

  // Delete the card record from the database
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
