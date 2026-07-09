package com.example.kucingputeh.model;

import com.google.gson.annotations.SerializedName;

public class Ride {

    @SerializedName(value = "RideID", alternate = {"ride_id", "rideId", "id"})
    private int rideId;

    @SerializedName(value = "driver_id", alternate = {"DriverID", "driverId"})
    private int driverId;

    @SerializedName(value = "Origin", alternate = {"origin"})
    private String origin;

    @SerializedName(value = "Destination", alternate = {"destination"})
    private String destination;

    @SerializedName(value = "DepartureTime", alternate = {"departure_time", "departureTime"})
    private String departureTime;

    @SerializedName(value = "available_seats", alternate = {"AvailableSeats", "seats"})
    private int availableSeats;

    public Ride() {}

    public Ride(int driverId, String origin, String destination, String departureTime, int availableSeats) {
        this.driverId = driverId;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
    }

    public int getRideId() { return rideId; }
    public void setRideId(int rideId) { this.rideId = rideId; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public String getOrigin() { return origin == null ? "" : origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination == null ? "" : destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDepartureTime() { return departureTime == null ? "" : departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
}
