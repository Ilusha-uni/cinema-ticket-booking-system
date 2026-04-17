// Package declaration
package cinema.models;

/**
 * Represents a user of the cinema booking system.
 * Contains basic user information including name, email, password, and role.
 */
public class User {

    // Fields

    /** Unique identifier for the user */
    private int id;

    /** Full name of the user */
    private String name;

    /** Email address used for login and notifications */
    private String email;

    /** Password for authentication */
    private String password;

    /** Role of the user (e.g., "USER" or "ADMIN") */
    private String role;


    // Constructor

    /**
     * Constructs a new User with all required details.
     *
     * @param id Unique user identifier
     * @param name Full name of the user
     * @param email Email address
     * @param password User password
     * @param role User role ("USER" or "ADMIN")
     */
    public User(int id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters

    /** @return the unique user ID */
    public int getId() { return id; }

    /** @return the user's full name */
    public String getName() { return name; }

    /** @return the user's email address */
    public String getEmail() { return email; }

    /** @return the user's password */
    public String getPassword() { return password; }

    /** @return the user's role */
    public String getRole() { return role; }


    // Optional setters (useful later)

    public void setName(String name) { this.name = name; }

    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    public void setRole(String role) { this.role = role; }


    /**
     * Returns a string representation of the user.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}