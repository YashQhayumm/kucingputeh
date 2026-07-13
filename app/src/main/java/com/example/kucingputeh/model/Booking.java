package com.example.kucingputeh.model;

import com.google.gson.annotations.SerializedName;

public class Booking {

    @SerializedName("BookingID")
    private int bookingId;

    @SerializedName("RideID")
    private com.google.gson.JsonElement rideData;

    @SerializedName("passenger_id")
    private int passengerId;

    @SerializedName("seats_booked")
    private int seatsBooked;

    @SerializedName("booking_status")
    private String bookingStatus;

    public Booking() {}

    public int getRideId() {
        if (rideData == null || rideData.isJsonNull()) return 0;
        if (rideData.isJsonPrimitive()) {
            try { return rideData.getAsInt(); } catch (Exception e) { return 0; }
        }
        if (rideData.isJsonObject()) {
            com.google.gson.JsonObject obj = rideData.getAsJsonObject();
            if (obj.has("RideID")) return obj.get("RideID").getAsInt();
            if (obj.has("ride_id")) return obj.get("ride_id").getAsInt();
            if (obj.has("rideId")) return obj.get("rideId").getAsInt();
        }
        return 0;
    }

    public int getDriverId() {
        if (rideData != null && rideData.isJsonObject()) {
            com.google.gson.JsonObject obj = rideData.getAsJsonObject();
            if (obj.has("driver_id")) return obj.get("driver_id").getAsInt();
            if (obj.has("DriverID")) return obj.get("DriverID").getAsInt();
            if (obj.has("driverId")) return obj.get("driverId").getAsInt();
        }
        return 0;
    }

    public String getOrigin() {
        if (rideData != null && rideData.isJsonObject()) {
            com.google.gson.JsonObject obj = rideData.getAsJsonObject();
            if (obj.has("Origin")) return obj.get("Origin").getAsString();
        }
        return "Unknown";
    }

    public String getDestination() {
        if (rideData != null && rideData.isJsonObject()) {
            com.google.gson.JsonObject obj = rideData.getAsJsonObject();
            if (obj.has("Destination")) return obj.get("Destination").getAsString();
        }
        return "Unknown";
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