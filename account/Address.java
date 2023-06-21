package account;

public class Address {
  // ******************************
  // --- --- Data members
  // ******************************
  private int id;
  private String addressLine;
  private String city;
  private String state;
  private String postcode;

  // ******************************
  // --- --- Constructors
  // ******************************
  public Address() {
    this.id = 0;
    this.addressLine = null;
    this.city = null;
    this.state = null;
    this.postcode = null;
  }

  public Address(int id, String addressLine, String city, String state, String postcode) {
    this.id = id;
    this.addressLine = addressLine;
    this.city = city;
    this.state = state;
    this.postcode = postcode;
  }

  // ******************************
  // --- --- Methods
  // ******************************
  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getAddressLine() {
    return this.addressLine;
  }

  public void setAddressLine(String addressLine) {
    this.addressLine = addressLine;
  }

  public String getCity() {
    return this.city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return this.state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getPostcode() {
    return this.postcode;
  }

  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }
}