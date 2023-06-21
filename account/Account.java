package account;

import java.time.LocalDate;

public abstract class Account implements Comparable<Account> {
  // ******************************
  // --- --- Data members
  // ******************************
  private String email;
  private String username;
  private String password;
  private Name name;
  private LocalDate dateCreated;

  // ******************************
  // --- --- Constructor
  // ******************************
  protected Account() {
    this.dateCreated = LocalDate.now();
  }

  protected Account(
      String email,
      String username,
      String password,
      String firstName,
      String lastName) {
    this.email = email;
    this.username = username;
    this.password = password;
    this.name = new Name(firstName, lastName);
    this.dateCreated = LocalDate.now();
  }

  // ******************************
  // --- --- Methods
  // ******************************
  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Name getName() {
    return this.name;
  }

  public void setName(String firstName, String lastName) {
    this.name = new Name(firstName, lastName);
  }

  public LocalDate getDateCreated() {
    return this.dateCreated;
  }

  public void setDateCreated(LocalDate dateCreated) {
    this.dateCreated = dateCreated;
  }

  // Compare by full name
  @Override
  public int compareTo(Account account) {
    return this.name.getFullName().compareTo(account.name.getFullName());
  }

  public abstract String toString();
}
