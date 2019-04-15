package com.example.androidgeekproject;

public enum MainActivityKeys {

    KEY1("DEFAULT_WEATHER_CITY"),
    KEY_CITY_TEXT_VIEW("cityTextView"),
    KEY_UPDATED_TEXT_VIEW("updatedTextView"),
    KEY_TEMPERATURE_TEXT_VIEW("currentTemperatureTextView"),
    KEY_WEATHER_ICON_TEXT_VIEW("weatherIconTextView"),
    KEY_SERVICE_RUN("serviceRun"),
    KEY_SHOW_DEF_CIT_DIALOG("showSaveDefaultCityDialog"),
    KEY_DETAILS_TEXT("detailsText");

    private String description;

    public String getDescription() {
        return description;
    }

    MainActivityKeys(String description) {
        this.description = description;
    }
}
