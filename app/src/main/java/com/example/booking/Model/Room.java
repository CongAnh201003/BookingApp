package com.example.booking.Model;

import java.io.Serializable;

public class Room implements Serializable {
    private String id;
    private String name;
    private String area;
    private int bedCount;
    private String description;
    private double price;
    private float starRating;
    private String imageUrl;
    private String category;
    private int totalRooms; // Trường này quan trọng để checkAvailability
    private int capacityAdults;
    private int capacityChildren;

    // Constructor trống cho Firebase
    public Room() {
    }

    public Room(String id, String name, String area, int bedCount, String description, double price, float starRating, String imageUrl, String category, int totalRooms, int capacityAdults, int capacityChildren) {
        this.id = id;
        this.name = name;
        this.area = area;
        this.bedCount = bedCount;
        this.description = description;
        this.price = price;
        this.starRating = starRating;
        this.imageUrl = imageUrl;
        this.category = category;
        this.totalRooms = totalRooms;
        this.capacityAdults = capacityAdults;
        this.capacityChildren = capacityChildren;
    }

    // Các Getter và Setter
    public String getId() { return id; }
    public String getName() { return name; }
    public String getArea() { return area; }
    public int getBedCount() { return bedCount; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public float getStarRating() { return starRating; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public int getTotalRooms() { return totalRooms; } // Phương thức bị thiếu gây lỗi build
    public int getCapacityAdults() { return capacityAdults; }
    public int getCapacityChildren() { return capacityChildren; }

    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }
}