package com.example.booking.Model;

import java.io.Serializable;

public class Booking implements Serializable {
    private String bookingId;
    private String userId;
    private String roomId;
    private String roomName;
    private String guestName;
    private String guestPhone;
    private long checkInDate;
    private long checkOutDate;
    private double totalPrice;
    private long timestamp;
    private String status;
    private String staffId; // New field to track who handled the booking

    public Booking() {
    }

    public Booking(String bookingId, String userId, String roomId, String roomName, String guestName, String guestPhone, long checkInDate, long checkOutDate, double totalPrice, long timestamp, String status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.roomId = roomId;
        this.roomName = roomName;
        this.guestName = guestName;
        this.guestPhone = guestPhone;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
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

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }

    public long getCheckInDate() { return checkInDate; }
    public void setCheckInDate(long checkInDate) { this.checkInDate = checkInDate; }

    public long getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(long checkOutDate) { this.checkOutDate = checkOutDate; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    // Fallback for old code using getPrice()
    public double getPrice() { return totalPrice; }
}
