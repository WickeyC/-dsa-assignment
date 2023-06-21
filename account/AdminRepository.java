package account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import adt.ArrayList;
import utility.Database;
import utility.PasswordUtils;

// AdminRepository is a class that is focused on interacting with the database table
public class AdminRepository {
  private static final String TABLE_NAME = "admin_account";

  public static boolean create(Admin admin) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;

    try {
      String fields = "email, username, password, first_name, last_name, date_created, ";
      String values = "VALUES (?, ?, ?, ?, ?, ?::date, ";

      fields += "position";
      values += "?";

      String sql = "INSERT INTO " + TABLE_NAME + " (" + fields + ") " + values + ");";

      // Prepare the data to insert to DB
      pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pstmt.setString(1, admin.getEmail());
      pstmt.setString(2, admin.getUsername());
      String password = admin.getPassword();
      String securePassword = PasswordUtils.generateSecurePassword(password);
      admin.setPassword(securePassword);
      pstmt.setString(3, securePassword);
      pstmt.setString(4, admin.getName().getFirstName());
      pstmt.setString(5, admin.getName().getLastName());
      pstmt.setString(6, admin.getDateCreated().toString());
      pstmt.setString(7, admin.getPosition());

      pstmt.executeUpdate();
      pstmt.close();

      return true;
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
    return false;
  }

  // Return an arraylist of all Customer objects in the database
  public static ArrayList<Admin> findAll() {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    ArrayList<Admin> allAdmins = new ArrayList<Admin>();
    try {
      String sql = "SELECT * FROM " + TABLE_NAME + ";";
      pstmt = connection.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        Admin admin = new Admin(
            rs.getString(1),
            rs.getString(2),
            rs.getString(3),
            rs.getString(4),
            rs.getString(5),
            rs.getString(7));
        admin.setDateCreated(LocalDate.parse(rs.getString(6)));
        allAdmins.add(admin);
      }
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
    return allAdmins;
  }

  public static Admin findBy(String column, String value) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    Admin admin = null;

    try {
      String sql = "SELECT * FROM " + TABLE_NAME
          + " WHERE " + column + " = '" + value + "';";

      pstmt = connection.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        admin = new Admin(
            rs.getString(1),
            rs.getString(2),
            rs.getString(3),
            rs.getString(4),
            rs.getString(5),
            rs.getString(7));
        admin.setDateCreated(LocalDate.parse(rs.getString(6)));
        return admin;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return admin;
  }

  public static Admin findByEmailOrUsername(Admin admin) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    Admin foundAdmin = null;

    try {
      String sql = "SELECT * FROM " + TABLE_NAME
          + " WHERE email = '" + admin.getEmail() + "' OR "
          + " username = '" + admin.getUsername() + "';";

      pstmt = connection.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        foundAdmin = new Admin(
            rs.getString(1),
            rs.getString(2),
            rs.getString(3),
            rs.getString(4),
            rs.getString(5),
            rs.getString(7));
        foundAdmin.setDateCreated(LocalDate.parse(rs.getString(6)));
        return foundAdmin;
      }
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
    return foundAdmin;
  }

  // Update the admin data in database to the admin data passed in
  public static void update(Admin admin) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    try {
      String[] fieldsToUpdate = { "password", "first_name", "last_name", "position" };

      String sql = "UPDATE " + TABLE_NAME + " SET ";

      for (int i = 0; i < fieldsToUpdate.length; i++) {
        if (i != fieldsToUpdate.length - 1) {
          sql += fieldsToUpdate[i] + " = ?, ";
        } else {
          sql += fieldsToUpdate[i] + " = ? ";
        }
      }

      sql += " WHERE email = '" + admin.getEmail() + "';";

      pstmt = connection.prepareStatement(sql);

      String password = admin.getPassword();
      String securePassword = PasswordUtils.generateSecurePassword(password);

      pstmt.setString(1, securePassword);
      pstmt.setString(2, admin.getName().getFirstName());
      pstmt.setString(3, admin.getName().getLastName());
      pstmt.setString(4, admin.getPosition());

      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }

  public static void updatePassword(Admin admin) {
    Connection connection = Database.getConnection();
    PreparedStatement pstmt = null;
    try {
      String sql = "UPDATE " + TABLE_NAME + " SET password = ?"
          + " WHERE email = '" + admin.getEmail() + "';";

      pstmt = connection.prepareStatement(sql);

      String password = admin.getPassword();
      String securePassword = PasswordUtils.generateSecurePassword(password);

      pstmt.setString(1, securePassword);

      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }
}
