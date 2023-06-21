package account;

import java.time.LocalDate;

import adt.LinkedHashMap;
import adt.MapInterface;
import checkout.Card;
import checkout.Order;

public class Customer extends Account {
  // ******************************
  // --- --- Data members
  // ******************************
  private String phoneNum;
  private int points;
  private LocalDate dateOfBirth;
  private MapInterface<Integer, Order> orders; // Composition
  private MapInterface<String, Card> cards; // Composition
  private MapInterface<Integer, Address> addresses; // Composition

  // ******************************
  // --- --- Constructors
  // ******************************
  public Customer() {
    super();
    this.points = 0;
    this.dateOfBirth = null;
    this.orders = new LinkedHashMap<Integer, Order>();
    this.cards = new LinkedHashMap<String, Card>();
    this.addresses = new LinkedHashMap<Integer, Address>();
  }

  public Customer(
      String email,
      String username,
      String password,
      String firstName,
      String lastName,
      String phoneNum,
      LocalDate dateOfBirth) {
    super(email, username, password, firstName, lastName);
    this.phoneNum = phoneNum;
    this.points = 0;
    this.dateOfBirth = dateOfBirth;
    this.orders = new LinkedHashMap<Integer, Order>();
    this.cards = new LinkedHashMap<String, Card>();
    this.addresses = new LinkedHashMap<Integer, Address>();
  }

  // ******************************
  // --- --- Methods
  // ******************************
  public String getPhoneNum() {
    return this.phoneNum;
  }

  public void setPhoneNum(String phoneNum) {
    this.phoneNum = phoneNum;
  }

  public int getPoints() {
    return this.points;
  }

  public void setPoints(int points) {
    this.points = points;
  }

  public LocalDate getDateOfBirth() {
    return this.dateOfBirth;
  }

  public void setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public MapInterface<Integer, Order> getOrders() {
    return this.orders;
  }

  public void setOrders(MapInterface<Integer, Order> orders) {
    this.orders = orders;
  }

  public MapInterface<String, Card> getCards() {
    return this.cards;
  }

  public void setCards(MapInterface<String, Card> cards) {
    this.cards = cards;
  }

  public MapInterface<Integer, Address> getAddresses() {
    return this.addresses;
  }

  public void setAddresses(MapInterface<Integer, Address> addresses) {
    this.addresses = addresses;
  }

  @Override
  public String toString() {
    return String.format("| %-23s | %-27s | %-16s | %-12s | %-6s | %-13s |", this.getName().getFullName(),
        this.getEmail(),
        this.getUsername(), this.phoneNum == null ? "Unspecified" : this.phoneNum, this.getPoints(),
        this.dateOfBirth == null ? "Unspecified" : this.dateOfBirth.toString());
  }
}