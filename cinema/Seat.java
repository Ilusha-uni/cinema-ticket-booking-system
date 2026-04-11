package cinema;

public class Seat {
    private int id;
    private int hallId;
    private int rowNumber;
    private int seatNumber;
    private String seatType;
    private boolean isBooked;  // Add a field to track booking status

    public Seat(int id, int hallId, int rowNumber, int seatNumber, String seatType, boolean isBooked) {
        this.id = id;
        this.hallId = hallId;
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.isBooked = isBooked;  // Initialize the booking status
    }

    // Getters and setters
    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    // Other getters and setters...


    public int getId() {
        return id;
    }

    public int getHallId() {
        return hallId;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

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
