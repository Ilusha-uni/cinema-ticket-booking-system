// Package declaration
package cinema.models;

// Represents a seat in a cinema hall
public class Seat {
    private int id;             // Seat ID
    private int hallId;         // Hall ID where the seat is located
    private int rowNumber;      // Row number
    private int seatNumber;     // Seat number in the row
    private String seatType;    // Type of seat (e.g., regular, VIP)
    private boolean isBooked;   // Booking status

    // Initialize a seat with all details
    public Seat(int id, int hallId, int rowNumber, int seatNumber, String seatType, boolean isBooked) {
        this.id = id;
        this.hallId = hallId;
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.isBooked = isBooked;
    }

    // Check if seat is booked
    public boolean isBooked() { return isBooked; }

    // Set seat booking status
    public void setBooked(boolean booked) { isBooked = booked; }

    // Getters
    public int getId() { return id; }
    public int getHallId() { return hallId; }
    public int getRowNumber() { return rowNumber; }
    public int getSeatNumber() { return seatNumber; }
    public String getSeatType() { return seatType; }

    // String representation of seat
    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ", hallId=" + hallId +
                ", rowNumber=" + rowNumber +
                ", seatNumber=" + seatNumber +
                ", seatType='" + seatType + '\'' +
                '}';
    }
}
