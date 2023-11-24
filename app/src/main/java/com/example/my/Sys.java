package com.example.my;

import com.google.gson.annotations.SerializedName;

public class Sys {
    @SerializedName("country")
    private String country;

    public String getCountry() {
        return country;
    }
}
