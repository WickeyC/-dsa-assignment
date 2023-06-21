package account;

public class Name {
  // ******************************
  // --- --- Data members
  // ******************************
  private String firstName;
  private String lastName;

  // ******************************
  // --- --- Constructor
  // ******************************
  public Name(String firstName, String lastName) {
    setFirstName(firstName);
    setLastName(lastName);
  }

  // ******************************
  // --- --- Methods
  // ******************************
  public String getFirstName() {
    return this.firstName;
  }

  public void setFirstName(String firstName) {
    if (firstName.trim().length() == 0) {
      throw new IllegalArgumentException("firstName cannot be empty.");
    }
    this.firstName = firstName.trim();
  }

  public String getLastName() {
    return this.lastName;
  }

  public void setLastName(String lastName) {
    if (lastName.trim().length() == 0) {
      throw new IllegalArgumentException("lastName cannot be empty.");
    }
    this.lastName = lastName.trim();
  }

  public String getFullName() {
    return this.firstName + " " + this.lastName;
  }
}