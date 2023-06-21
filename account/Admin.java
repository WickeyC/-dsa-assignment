package account;

public class Admin extends Account {
  // ******************************
  // --- --- Data members
  // ******************************
  private String position;

  // ******************************
  // --- --- Constructors
  // ******************************
  public Admin() {
    super();
  }

  public Admin(
      String email,
      String username,
      String password,
      String firstName,
      String lastName,
      String position) {
    super(email, username, password, firstName, lastName);
    this.position = position;
  }

  // ******************************
  // --- --- Methods
  // ******************************
  public String getPosition() {
    return this.position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  @Override
  public String toString() {
    return String.format("| %-25s | %-29s | %-17s | %-17s | %-12s |", this.getName().getFullName(), this.getEmail(),
        this.getUsername(), this.position, this.getDateCreated().toString());
  }
}