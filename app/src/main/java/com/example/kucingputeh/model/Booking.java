package com.example.kucingputeh.model;

import com.google.gson.annotations.SerializedName;

public class Booking {

    @SerializedName("BookingID")
    private int bookingId;

    @SerializedName("RideID")
    private Ride ride;

    @SerializedName("passenger_id")
    private int passengerId;

    @SerializedName("seats_booked")
    private int seatsBooked;

    @SerializedName("booking_status")
    private String bookingStatus;

    public Booking() {}

    public static class Ride {
        @SerializedName("RideID")
        private int rideId;

        @SerializedName(value = "driver_id", alternate = {"DriverID", "driverId"})
        private int driverId;

        @SerializedName("Origin")
        private String origin;

        @SerializedName("Destination")
        private String destination;

        public int getRideId() { return rideId; }
        public int getDriverId() { return driverId; }
        public String getOrigin() { return origin; }
        public String getDestination() { return destination; }
    }

    public int getDriverId() {
        return ride != null ? ride.getDriverId() : 0;
    }

    public int getRideId() {
        return ride != null ? ride.getRideId() : 0;
    }

    public String getOrigin() {
        return ride != null ? ride.getOrigin() : "Unknown";
    }

    public String getDestination() {
        return ride != null ? ride.getDestination() : "Unknown";
    }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public int getPassengerId() { return passengerId; }
    public void setPassengerId(int passengerId) { this.passengerId = passengerId; }
    public int getSeatsBooked() { return seatsBooked; }
    public void setSeatsBooked(int seatsBooked) { this.seatsBooked = seatsBooked; }
    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
}