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
    private int totalRooms; // Tổng số phòng loại này khách sạn có
    private int capacityAdults;
    private int capacityChildren;

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

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public int getBedCount() { return bedCount; }
    public void setBedCount(int bedCount) { this.bedCount = bedCount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public float getStarRating() { return starRating; }
    public void setStarRating(float starRating) { this.starRating = starRating; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getTotalRooms() { return totalRooms; }
    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }
    public int getCapacityAdults() { return capacityAdults; }
    public void setCapacityAdults(int capacityAdults) { this.capacityAdults = capacityAdults; }
    public int getCapacityChildren() { return capacityChildren; }
    public void setCapacityChildren(int capacityChildren) { this.capacityChildren = capacityChildren; }
}
