package com.example.googlemaps2;

import com.google.gson.annotations.SerializedName;

public class Post {
    // Initialise vars
    private int userId;
    private int id;
    private String title;
    @SerializedName("body") // "body" is the real name in Json file
    private String text;


    // Generate Getter method
    public int getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
