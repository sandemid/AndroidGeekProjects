package com.example.androidgeekproject;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

public class WeatherDataParser {

    private final static String LOG_TAG = WeatherDataParser.class.getSimpleName();
    private String placeName;
    private String currentTemp;
    private String icon;
    private String updatedText;
    private String details;
    private JSONObject jsonObject;
    private Context context;

    public WeatherDataParser(Context context, JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        this.context = context;
    }

    public boolean updateWeatherData() {
        try {
            if(jsonObject == null) {
                return false;
            } else {
                renderWeather();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "One or more fields not found in the JSON data");
            return false;
        }
    }

    private void renderWeather() {
        Log.d(LOG_TAG, "json: " + jsonObject.toString());
        try {
            JSONObject details = jsonObject.getJSONArray("weather").getJSONObject(0);
            JSONObject main = jsonObject.getJSONObject("main");

            setPlaceName(jsonObject);
            setDetails(details, main);
            setCurrentTemp(main);
            setUpdatedText(jsonObject);
            setWeatherIcon(details.getInt("id"),
                    jsonObject.getJSONObject("sys").getLong("sunrise") * 1000,
                    jsonObject.getJSONObject("sys").getLong("sunset") * 1000);
        } catch (Exception exc) {
            exc.printStackTrace();
            Log.e(LOG_TAG, "One or more fields not found in the JSON data");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";

        if(actualId == 800) {
            long currentTime = new Date().getTime();
            if(currentTime >= sunrise && currentTime < sunset) {
//                icon = "\u1F31";
                icon = context.getResources().getString(R.string.weather_sunny);
            } else {
                icon = context.getResources().getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2: {
                    icon = context.getResources().getString(R.string.weather_thunder);
                    break;
                }
                case 3: {
                    icon = context.getResources().getString(R.string.weather_drizzle);
                    break;
                }
                case 5: {
                    icon = context.getResources().getString(R.string.weather_rainy);
                    break;
                }
                case 6: {
                    icon = context.getResources().getString(R.string.weather_snowy);
                    break;
                }
                case 7: {
                    icon = context.getResources().getString(R.string.weather_foggy);
                    break;
                }
                case 8: {
//                    icon = "\u2601";
                     icon = context.getResources().getString(R.string.weather_cloudy);
                    break;
                }
            }
        }
        this.icon = icon;
    }

    private void setUpdatedText(JSONObject jsonObject) throws JSONException {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String updateOn = dateFormat.format(new Date(jsonObject.getLong("dt") * 1000));
        String updatedText = "Last update: " + updateOn;
        this.updatedText = updatedText;
    }

    private void setCurrentTemp(JSONObject main) throws JSONException {
        String currentTextText = String.format("%.2f", main.getDouble("temp")) + "\u2103";
        currentTemp = currentTextText;
    }

    private void setDetails(JSONObject details, JSONObject main) throws JSONException {
        String detailsText = details.getString("description").toUpperCase() + "\n"
                + "Humidity: " + main.getString("humidity") + "%" + "\n"
                + "Pressure: " + main.getString("pressure") + "hPa";
        this.details = detailsText;
    }

    private void setPlaceName(JSONObject jsonObject) throws JSONException {
        String cityText = jsonObject.getString("name").toUpperCase() + ", "
                + jsonObject.getJSONObject("sys").getString("country");
        placeName = cityText;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getCurrentTemp() {
        return currentTemp;
    }

    public String getIcon() {
        return icon;
    }

    public String getUpdatedText() {
        return updatedText;
    }

    public String getDetails() {
        return details;
    }
}
