package com.example.booking.Model;

import java.io.Serializable;

public class Hotel implements Serializable {
    private String id;
    private String name;
    private String address;
    private String description;
    private String imageUrl;
    private double price;
    private double rating;

    public Hotel() {
    }

    public Hotel(String id, String name, String address, String description, String imageUrl, double price, double rating) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.rating = rating;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
}
