package com.example.diningapp.util;

public enum DiningHall {
    DIETRICK     ("Dietrick"),
    TURNER_PLACE ("Turner Place"),
    SQUIRES      ("Squires"),
    OWENS        ("Owens"),
    JOHNSON      ("Johnson");

    public final String description;

    DiningHall(String description) {
        this.description = description;
    }
}
