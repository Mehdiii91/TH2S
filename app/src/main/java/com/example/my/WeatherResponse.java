package com.example.my;
import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("main")
    private Main main;
    @SerializedName("wind")
    private Wind wind;
    @SerializedName("sys")
    private Sys sys;
    @SerializedName("name")
    private String ville;
    @SerializedName("weather")
    private Wheather wheather;

    public Main getMain() {
        return main;
    }
    public Wheather getWheather(){return wheather;}
    public Wind getWind() {
        return wind;
    }

    public Sys getSys() {
        return sys;
    }

    public String getVille() {
        return ville;
    }
}