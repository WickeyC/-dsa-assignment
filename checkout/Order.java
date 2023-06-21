package checkout;

import java.time.LocalDate;
import java.time.LocalDateTime;

import account.Address;
import adt.LinkedList;
import adt.ListInterface;
import ordermenu.*;

public class Order{
  // ******************************
  // --- --- Data members
  // ******************************
  private static Order currentOrder;
  private int id;
  private String voucherApplied;
  private double voucherDiscount;
  private int pointsUsed;
  private double finalPrice;
  private Address deliveryAddress;
  private String paymentMethod;
  private int pointsEarned;
  private LocalDate deliveryDate;
  private String status;
  private LocalDateTime orderTime;
  private ListInterface<CartItem> items;

  // ******************************
  // --- --- Constructors
  // ******************************
  public Order() {
    this.items = new LinkedList<CartItem>();
  }

  public Order(
      int id,
      String voucherApplied,
      double voucherDiscount,
      int pointsUsed,
      double finalPrice,
      Address deliveryAddress,
      String paymentMethod,
      int pointsEarned,
      LocalDate deliveryDate,
      String status,
      LocalDateTime orderTime) {
    this.id = id;
    this.voucherApplied = voucherApplied;
    this.voucherDiscount = voucherDiscount;
    this.pointsUsed = pointsUsed;
    this.finalPrice = finalPrice;
    this.deliveryAddress = deliveryAddress;
    this.paymentMethod = paymentMethod;
    this.pointsEarned = pointsEarned;
    this.deliveryDate = deliveryDate;
    this.status = status;
    this.orderTime = orderTime;
    this.items = new LinkedList<CartItem>();
  }

  // ******************************
  // --- --- Methods
  // ******************************
  public static Order getCurrentOrder() {
    return Order.currentOrder;
  }

  public static void setCurrentOrder(Order currentOrder) {
    Order.currentOrder = currentOrder;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getVoucherApplied() {
    return voucherApplied;
  }

  public void setVoucherApplied(String voucherApplied) {
    this.voucherApplied = voucherApplied;
  }

  public double getVoucherDiscount() {
    return voucherDiscount;
  }

  public void setVoucherDiscount(double voucherDiscount) {
    this.voucherDiscount = voucherDiscount;
  }

  public int getPointsUsed() {
    return this.pointsUsed;
  }

  public void setPointsUsed(int pointsUsed) {
    this.pointsUsed = pointsUsed;
  }

  public double getFinalPrice() {
    return this.finalPrice;
  }

  public void setFinalPrice(double finalPrice) {
    this.finalPrice = finalPrice;
  }

  public Address getDeliveryAddress() {
    return this.deliveryAddress;
  }

  public void setDeliveryAddress(Address deliveryAddress) {
    this.deliveryAddress = deliveryAddress;
  }

  public String getPaymentMethod() {
    return this.paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public int getPointsEarned() {
    return this.pointsEarned;
  }

  public void setPointsEarned(int pointsEarned) {
    this.pointsEarned = pointsEarned;
  }

  public LocalDate getDeliveryDate() {
    return this.deliveryDate;
  }

  public void setDeliveryDate(LocalDate deliveryDate) {
    this.deliveryDate = deliveryDate;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public LocalDateTime getOrderTime() {
    return this.orderTime;
  }

  public void setOrderTime(LocalDateTime orderTime) {
    this.orderTime = orderTime;
  }

  public ListInterface<CartItem> getItems() {
    return this.items;
  }
}
