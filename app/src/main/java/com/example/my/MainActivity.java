package com.example.my;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private MyApiService openWeather;
    private TextView display;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private RecyclerView.Adapter adapterHourly;
    private RecyclerView recyclerView;

    private TextView temps;
    private TextView temps_max;
    private TextView temps_min;
    private TextView pression;
    private TextView ressenti;
    private TextView vitesse;
    private TextView ville;
    private TextView humidite;
    private TextView description;


    private static final String API_KEY = "d72d766e94ff0802aefbb5ea59e5b18e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerview();
        description = findViewById(R.id.textView);
        temps = findViewById(R.id.textView3);
        temps_max = findViewById(R.id.textView4);
        ville = findViewById(R.id.textView5);
        pression = findViewById(R.id.textView6);
        vitesse = findViewById(R.id.textView8);
        humidite = findViewById(R.id.textView10);
        vitesse = findViewById(R.id.textView);


//     Ajout de la date du jour
        TextView dateTextView = findViewById(R.id.dateTextView);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat= new SimpleDateFormat("EEEE d MMMM",new java.util.Locale("fr"));
        String formattedDate = dateFormat.format(calendar.getTime());
        dateTextView.setText(formattedDate);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Créer une instance de l'interface OpenWeatherMapApi
        openWeather = retrofit.create(MyApiService.class);
        callWeatherApi("Paris");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Demander la permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            // Permission déjà accordée, commencez à écouter les mises à jour de localisation
            startListening();
        }
    }

    private void startListening() {
        // Vérifier à nouveau la permission pour les versions Android 6.0 et supérieures
        if (Build.VERSION.SDK_INT < 23 ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        getLocation();
    }


    private void callWeatherApi(String cityName) {

        Call<WeatherResponse> call = openWeather.getWeather(cityName,API_KEY,"metric");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                WeatherResponse weatherResponse = response.body();

                double temperature = weatherResponse.getMain().getTemp();
                double temperature_max = weatherResponse.getMain().getTemp_max();
                double temperature_min = weatherResponse.getMain().getTemp_min();
                double press = weatherResponse.getMain().getPression();
                double vite = weatherResponse.getWind().getSpeed();
                double humi =  weatherResponse.getMain().getHumidite();
                String descri = weatherResponse.getWheather().getDescription();
                String city = weatherResponse.getVille();

                int arrondiTemperature = (int)Math.round(temperature);
                int arrondiTemperature_max = (int)Math.round(temperature_max);
                int arrondiTemperature_min = (int)Math.round(temperature_min);
                int arrondiHumidite = (int)Math.round(humi);
                String strhumidite = arrondiHumidite + "%";
                System.out.println(strhumidite);
                int arrondiVitesse = (int)Math.round(vite);
                int arrondiPression = (int)Math.round(press);


                temps.setText(String.valueOf(arrondiTemperature)+"°");
                temps_max.setText(String.valueOf(arrondiTemperature_min)+"°min | "+String.valueOf(arrondiTemperature_max)+"°max" );
                humidite.setText(strhumidite);
                pression.setText(String.valueOf(arrondiPression)+" hPa");
                vitesse.setText(String.valueOf(arrondiVitesse)+" km/h");
                description.setText(descri);
                ville.setText(city);
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {

            }
        });
    }

    private void getLocation() {
        if (Build.VERSION.SDK_INT < 23 ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();

                // Appel à la méthode pour récupérer la ville
                getCityFromCoordinates(latitude, longitude);
            }
        }
    }

    private void getCityFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                String cityName = addresses.get(0).getLocality();

                // Faites quelque chose avec le nom de la ville (par exemple, affichez-le dans un Toast)
                callWeatherApi(cityName);
            } else {
                // Aucune adresse trouvée
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerview() {
        ArrayList<Hourly> items = new ArrayList<>();

        items.add(new Hourly("9 pm", 28, "cloudy"));
        items.add(new Hourly("10 pm", 29, "sunny"));
        items.add(new Hourly("11 pm", 30, "wind"));
        items.add(new Hourly("12 pm", 29, "rainy"));
        items.add(new Hourly("1 am", 27, "storm"));

        recyclerView = findViewById(R.id.view1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapterHourly = new HourlyAdapters(items);
        recyclerView.setAdapter(adapterHourly);
    }
}