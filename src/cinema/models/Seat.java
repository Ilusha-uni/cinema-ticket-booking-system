// Package declaration
package cinema.models;

/**
 * Represents a seat within a cinema hall.
 * Contains information about its location, type, and booking status.
 */
public class Seat {

    // Fields

    /** Unique identifier for the seat */
    private int id;

    /** ID of the hall this seat belongs to */
    private int hallId;

    /** Row number in the hall */
    private int rowNumber;

    /** Seat number within the row */
    private int seatNumber;

    /** Seat type or category (e.g., "regular", "VIP") */
    private String seatType;

    /** Indicates whether the seat is currently booked */
    private boolean isBooked;

    // Constructor

    /**
     * Constructs a new Seat with the specified location, type, and booking status.
     *
     * @param id Unique seat identifier
     * @param hallId ID of the hall this seat belongs to
     * @param rowNumber Row number of the seat
     * @param seatNumber Seat number within the row
     * @param seatType Type or category of the seat
     * @param isBooked true if the seat is already booked
     */
    public Seat(int id, int hallId, int rowNumber, int seatNumber, String seatType, boolean isBooked) {
        this.id = id;
        this.hallId = hallId;
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.isBooked = isBooked;
    }

    // Getters

    /** @return the seat ID */
    public int getId() { return id; }

    /** @return the hall ID */
    public int getHallId() { return hallId; }

    /** @return the row number of the seat */
    public int getRowNumber() { return rowNumber; }

    /** @return the seat number within the row */
    public int getSeatNumber() { return seatNumber; }

    /** @return the type or category of the seat */
    public String getSeatType() { return seatType; }

    /** @return true if the seat is currently booked */
    public boolean isBooked() { return isBooked; }

    // Setters

    /**
     * Updates the booking status of the seat.
     *
     * @param booked true to mark as booked, false otherwise
     */
    public void setBooked(boolean booked) { isBooked = booked; }

    // Overrides

    /**
     * Provides a string representation of the seat, including its hall, row, and number.
     *
     * @return a string describing the seat
     */
    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ", hallId=" + hallId +
                ", rowNumber=" + rowNumber +
                ", seatNumber=" + seatNumber +
                ", seatType='" + seatType + '\'' +
                ", isBooked=" + isBooked +
                '}';
    }
}
