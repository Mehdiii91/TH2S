package com.example.my;

import com.google.gson.annotations.SerializedName;

public class Wheather {
    @SerializedName("description")
    private String description;

    public String getDescription() {
        return description;
    }
}
