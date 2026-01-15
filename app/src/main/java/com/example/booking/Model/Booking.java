package com.example.booking.Model;

import java.io.Serializable;

public class Booking implements Serializable {
    private String bookingId;
    private String userId;
    private String roomId;
    private String roomName;
    private double price;
    private long timestamp;
    private String status;

    public Booking() {
    }

    public Booking(String bookingId, String userId, String roomId, String roomName, double price, long timestamp, String status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.roomId = roomId;
        this.roomName = roomName;
        this.price = price;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
