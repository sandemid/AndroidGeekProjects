package com.example.androidgeekproject;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.androidgeekproject.database.DBTables;
import com.example.androidgeekproject.rest.OpenWeatherRepo;
import com.example.androidgeekproject.rest.entites.WeatherRequestRestModel;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class WeatherDataParser {

    private static final String OPEN_WEATHER_API_KEY = "f3f2763fe63803beef4851d6365c83bc";
    private static WeatherDataParser weatherDataParser = null;
    private String placeName;
    private String currentTemp;
    private String icon;
    private String updatedText;
    private String details;
    private String detailsString;
    private WeatherRequestRestModel model;
    private MainActivity activity;

    private WeatherDataParser() {
        model = new WeatherRequestRestModel();
    }

    static WeatherDataParser getInstance() {
        if(weatherDataParser == null) {
            weatherDataParser = new WeatherDataParser();
        }
        return weatherDataParser;
    }

    void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    void loadWeather(String city) {

        OpenWeatherRepo.getSingleton().getAPI().loadWeather(city,
                OPEN_WEATHER_API_KEY, "metric")
                .enqueue(new Callback<WeatherRequestRestModel>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherRequestRestModel> call,
                                           @NonNull Response<WeatherRequestRestModel> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            model = response.body();
                            renderWeather();
                            activity.updateWeatherViews();
                            recordWeatherInDB();
                        } else {
                            Toast.makeText(activity.getApplicationContext(), "Для указанного города не нашлось информации: "
                                    + city, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherRequestRestModel> call, Throwable t) {
                        Toast.makeText(activity.getApplicationContext(), "Для указанного города не нашлось информации: "
                                + city, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void recordWeatherInDB() {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        DBTables.addWeather(placeName, currentTemp, dateFormat.format(new Date(System.currentTimeMillis())), detailsString, activity.getDatabase());
    }

    private void renderWeather() {
        setPlaceName();
        setDetails();
        setCurrentTemp();
        setUpdatedText();
        setWeatherIcon(model.weather[0].id,model.sys.sunrise* 1000,model.sys.sunset * 1000);
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";

        if(actualId == 800) {
            long currentTime = new Date().getTime();
            if(currentTime >= sunrise && currentTime < sunset) {
                icon = activity.getApplicationContext().getResources().getString(R.string.weather_sunny);
            } else {
                icon = activity.getApplicationContext().getResources().getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2: {
                    icon = activity.getApplicationContext().getResources().getString(R.string.weather_thunder);
                    break;
                }
                case 3: {
                    icon = activity.getApplicationContext().getResources().getString(R.string.weather_drizzle);
                    break;
                }
                case 5: {
                    icon = activity.getApplicationContext().getResources().getString(R.string.weather_rainy);
                    break;
                }
                case 6: {
                    icon = activity.getApplicationContext().getResources().getString(R.string.weather_snowy);
                    break;
                }
                case 7: {
                    icon = activity.getApplicationContext().getResources().getString(R.string.weather_foggy);
                    break;
                }
                case 8: {
                     icon = activity.getApplicationContext().getResources().getString(R.string.weather_cloudy);
                    break;
                }
                default: {
                    icon = activity.getApplicationContext().getResources().getString(R.string.weather_cloudy);
                    break;
                }
            }
        }
        this.icon = icon;
    }

    private void setUpdatedText(){
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String updateOn = dateFormat.format(new Date(model.dt * 1000));
        this.updatedText = "Last update: " + updateOn;
    }

    private void setCurrentTemp() {
        currentTemp = String.format(Locale.getDefault(), "%.2f", model.main.temp) + "\u2103";
    }

    private void setDetails() {
        this.detailsString = model.weather[0].description.toUpperCase()
                + ", Humidity: " + model.main.humidity + "%"
                + ", Pressure: " + model.main.pressure + "hPa";
        this.details = model.weather[0].description.toUpperCase() + "\n"
                + "Humidity: " + model.main.humidity + "%" + "\n"
                + "Pressure: " + model.main.pressure + "hPa";
    }

    private void setPlaceName() {
        placeName = model.name.toUpperCase() + ", " + model.sys.country;
    }

    String getPlaceName() {
        return placeName;
    }

    String getCurrentTemp() {
        return currentTemp;
    }

    String getIcon() {
        return icon;
    }

    String getUpdatedText() {
        return updatedText;
    }

    String getDetails() {
        return details;
    }
}
