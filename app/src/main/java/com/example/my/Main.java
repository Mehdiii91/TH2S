package com.example.my;

import com.google.gson.annotations.SerializedName;

public class Main {
    @SerializedName("temp")
    private double temp;
    @SerializedName("temp_min")
    private double temp_min;
    @SerializedName("temp_max")
    private double temp_max;
    @SerializedName("pressure")
    private double pression;
    @SerializedName("humidity")
    private double humidite;

    public double getTemp() {
        return temp;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public double getPression() {
        return pression;
    }

    public double getHumidite() {
        return humidite;
    }
}
