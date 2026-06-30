package com.example.kucingputeh.model;

import com.google.gson.annotations.SerializedName;

public class Booking {

    // @SerializedName must match the exact column names from your database/PHP JSON
    @SerializedName("BookingID")
    private int bookingId;

    @SerializedName("RideID")
    private int rideId;

    @SerializedName("seats_booked")
    private int seatsBooked;

    @SerializedName("booking_status")
    private String bookingStatus;

    // These fields come from the INNER JOIN with the Rides table
    @SerializedName("Origin")
    private String origin;

    @SerializedName("Destination")
    private String destination;

    @SerializedName("DepartureTime")
    private String departureTime;

    // Constructor
    public Booking(int bookingId, int rideId, int seatsBooked, String bookingStatus, String origin, String destination, String departureTime) {
        this.bookingId = bookingId;
        this.rideId = rideId;
        this.seatsBooked = seatsBooked;
        this.bookingStatus = bookingStatus;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
    }

    // Getters
    public int getBookingId() { return bookingId; }
    public int getRideId() { return rideId; }
    public int getSeatsBooked() { return seatsBooked; }
    public String getBookingStatus() { return bookingStatus; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public String getDepartureTime() { return departureTime; }
}