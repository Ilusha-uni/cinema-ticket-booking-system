# 🎬 Cinema Ticket Booking System

A desktop application built with **Java Swing** and **MySQL** that provides a seamless interface for users to browse movies, select showtimes, and book seats, while providing administrators with a robust panel for data management.

## 🚀 Features

### **User Side**
* **Authentication:** Login and registration with input validation.
* **Movie Browsing:** Modern, card-based UI to view movie details (Duration, Rating, Description).
* **Booking Workflow:**
    * Browse specific showtimes for selected movies.
    * Interactive seat selection.
    * Booking confirmation page.
* **Account Management:** Password change functionality.

### **Admin Side**
* **User Management:** Full CRUD (Create, Read, Update, Delete) operations for managing cinema users, including the ability to update user details (name, password, roles, etc.) and control user access.
---

## 🛠️ Tech Stack

* **Language:** Java
* **GUI Framework:** Swing
* **Database:** MySQL
* **Connectivity:** JDBC (Java Database Connectivity)

---

## 📂 Project Structure

```text
├── database_dump/                 # SQL dump 
│   └── cinema_database_dump.sql   # Cinema database dump
├── src/                           # Source folder for all project code
│   └── cinema/                    # Main package for all your project files
│       ├── auth/                  # Authentication-related files (UI + session logic)
│       │   ├── ChangePasswordForm.java  # Password reset UI
│       │   ├── LoginForm.java           # Entry point / Login UI
│       │   ├── RegistrationForm.java    # New user signup UI
│       │   └── UserSession.java         # Manages current logged-in user state
│       ├── booking/               # Movie booking UI (no SQL here)
│       │   ├── BookingConfirmationPage.java  # Final booking summary UI
│       │   ├── MovieListingPage.java         # Main dashboard with movie cards
│       │   ├── SeatSelectionPage.java        # Visual seat picker UI
│       │   └── WatchShowtimesPage.java       # List of times for a specific movie
│       ├── dao/                   # Data Access Objects (DB CRUD operations)
│       │   ├── BookingDAO.java     # Booking & tickets DB operations
│       │   ├── MovieDAO.java       # Movie database operations 
│       │   ├── SeatDAO.java        # Seat database operations 
│       │   ├── ShowtimeDAO.java    # Showtime database operations 
│       │   └── UserDAO.java        # User database operations
│       ├── database/               # Handles database connection and SQL queries
│       │   └── DatabaseConnection.java  # Handles MySQL connection pooling
│       ├── exception/             # Exception handling (logic only, not UI)
│       │   ├── InputValidator.java         # Business logic for data validation
│       │   └── InvalidInputException.java  # Custom exception for error handling
│       ├── models/                # Data models (pure data objects, no DB logic)
│       │   ├── Movie.java          # Movie data object
│       │   ├── Seat.java           # Seat data object
│       │   ├── Showtime.java       # Showtime data object
│       │   └── User.java           # User data object (authentication info)
│       ├──panels/                 # GUI Panels and UI logic for admin/users
│       │    ├── AdminPanel.java     # Main admin dashboard container UI
│       │    └── UsersPanel.java     # CRUD interface for User management UI
│       └── images/                # Static resources, not code
│           ├── default.jpg                 # Default movie poster
│           ├── lion_king.jpg               # Lion King movie poster
│           ├── rise_of_gru.jpg             # Rise of Gru movie poster
│           ├── toy_story.jpg               # Toy Story movie poster
│           └── zootopia.jpg                # Zootopia movie poster
└── README.md                       # Project documentation file

````

---

## ⚙️ Setup & Installation

### 1. Database Configuration

1. Create a MySQL database named `cinema_db`.
2. Import the SQL schema from: `database_dump/cinema_database_dump.sql`
3. Update the database credentials in: `src/cinema/database/DatabaseConnection.java`


   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/cinema_db";
   private static final String USER = "your_username";
   private static final String PASS = "your_password";
   ```

### 2. Image Assets

* Place movie posters in an `images/` folder at the root of the project.
* Ensure filenames match the `image_path` column in your `movies` database table.

### 3. Execution

Compile and run the project using your IDE or via CLI:

```bash
# Run the main entry point
java cinema.auth.LoginForm
```

---

## 🖥️ Key UI Components

### **Login**

The system starts with a modern login screen, where users can either log in with their existing credentials or register as a new user. The `LoginForm` class provides an authentication interface, using `UserSession` to track the logged-in user's ID. This ensures that tickets are linked to the correct account, allowing users to manage their bookings and profile information.

The **LoginForm** uses:

* **Input validation** to ensure proper email format and that fields are not left empty.
* **Error handling** with custom messages if the user credentials are invalid.

---

### **Registration & Password Management**

#### **User Registration (`RegistrationForm`)**

New users can create an account through a dedicated registration form. The system ensures:

* **Validated input fields** (email format, full name, password requirements)
* **Password confirmation check** to prevent mismatches
* **Minimum password length enforcement**
* Automatic insertion of user data into the database with a default role (`USER`)

After successful registration, users are redirected to the login screen.

#### **Change Password (`ChangePasswordForm`)**

Authenticated users can securely update their password by providing:

* Their **registered email**
* **Current password** (for verification)
* A **new password** with confirmation

The system performs:

* **Credential verification** against stored data
* **Validation checks** (non-empty fields, password length, match confirmation)
* **Database update** only if the current password is correct

Upon success, the user is redirected back to the login screen.

---

### **Movies Dashboard (MovieListingPage)**

The Movie Dashboard displays all available movies in a card-based layout.
Each movie shows its poster, title, duration, rating, and release date.
Users can click on a movie to view available showtimes.

### **View Showtimes Screen (WatchShowtimesPage)**

The View Showtimes screen displays all available screening times for a selected movie.
Each showtime shows the start time, hall number, and ticket price.
Users can select a showtime to proceed to seat selection.

### **Seat Selection Screen (SeatSelectionPage)**

In the **Seat Selection** screen, users can:

* View the seating layout for a selected movie/showtime.
* **Select seats** and visually see which seats are taken and which are available.
* The seat selection updates in real-time, ensuring a smooth booking process.

### **Booking Confirmation Screen (BookingConfirmationPage)**

Once the user selects their seats, they are directed to a **Booking Confirmation** page where they can review their selection before confirming the booking.

### **Users Panel Screen**

The admin panel provides a dedicated Users management interface (UsersPanel) where administrators can:
* **View user details** and manage them (Create, Read, Update, Delete - CRUD).
* **Edit user information** like name, email, password, and role (admin, user).

---

### 🔄 System Flow

Login → Movie Dashboard → Showtimes Selection → Seat Selection → Booking Confirmation

---

## 📂 DAO (Data Access Object)

The **DAO layer** serves as the intermediary between the application logic and the database, encapsulating all CRUD operations for the entities in the system (e.g., **User**, **Movie**, **Showtime**, **Seat**, **Booking**). By using DAO, we achieve separation of concerns, making the application more maintainable and testable.

### 🧑‍💻 DAO Classes

The following DAO classes are implemented in the project:

- **UserDAO**: Manages user-related operations (e.g., registration, login, updates).
- **MovieDAO**: Handles movie-related operations (e.g., adding, fetching, updating movies).
- **SeatDAO**: Manages seat availability and booking status.
- **ShowtimeDAO**: Deals with movie showtimes (e.g., adding, updating, and fetching showtimes).
- **BookingDAO**: Handles ticket booking operations.

---
## ⚠️ Requirements

* **JDK 8** or higher.
* **MySQL Connector/J** added to your project classpath.
* **MySQL Server** active.

---

## 🔧 Running the Project

### 1. **Clone the Repository**

Clone the project repository to your local machine:

```bash
git clone https://github.com/Ilusha-uni/cinema-ticket-booking-system.git
cd cinema-ticket-booking-system
```

### 2. **Database Setup**

* Create the database in MySQL (as described above).
* Import the provided `cinema_database_dump.sql` to set up necessary tables.

### 3. **Build & Run**

* Open the project in your favorite IDE (e.g., IntelliJ IDEA, Eclipse).
* Ensure that **JDK 8** or higher is installed.
* Run the **LoginForm** class to launch the system.

---

## 💻 Interface Screenshots


### 🔐 Login Screen
![Login Screen](https://raw.githubusercontent.com/Ilusha-uni/cinema-ticket-booking-system/main/interface_screenshots/login.png)

### 🏠 Movie Dashboard
![Movie Dashboard](https://raw.githubusercontent.com/Ilusha-uni/cinema-ticket-booking-system/main/interface_screenshots/movie_dashboard.png)

### ⏰ Showtimes Screen
![Showtimes Screen](https://raw.githubusercontent.com/Ilusha-uni/cinema-ticket-booking-system/main/interface_screenshots/showtimes.png)

### 💺 Seat Selection Screen
![Seat Selection Screen](https://raw.githubusercontent.com/Ilusha-uni/cinema-ticket-booking-system/main/interface_screenshots/seat_selection.png)

### 🎟️ Booking Confirmation
![Booking Confirmation](https://raw.githubusercontent.com/Ilusha-uni/cinema-ticket-booking-system/main/interface_screenshots/booking_confirmation.png)

### 🛠️ Admin Panel (Users Section)
![Admin Panel](https://raw.githubusercontent.com/Ilusha-uni/cinema-ticket-booking-system/main/interface_screenshots/admin_panel_users.png)

---

## 📈 Future Enhancements

- **Email Notifications**: Implement email notifications to notify users of successful bookings or cancellations, potentially integrating an email service like **SendGrid** or **Amazon SES**.
- **Payment Integration**: Integrate a payment gateway (like **Stripe** or **PayPal**) to handle ticket payments, allowing users to pay for their bookings directly from the system.
- **Search & Filter**: Add search functionality so users can easily search for movies by title, genre, or release date. A filter for available showtimes and ticket prices could also be helpful.
- **Security Improvements**: Implement **password hashing** (e.g., using **bcrypt**) to store user passwords securely. Consider adding multi-factor authentication (MFA) for added security.
