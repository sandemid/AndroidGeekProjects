package com.example.androidgeekproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidgeekproject.database.DatabaseHelper;
import com.example.androidgeekproject.fragments.Fragments;
import com.example.androidgeekproject.fragments.NavAboutFragment;
import com.example.androidgeekproject.fragments.NavContactFragment;
import com.example.androidgeekproject.fragments.NavMyProfileFragment;
import com.example.androidgeekproject.fragments.NavURLFragment;
import com.example.androidgeekproject.fragments.NavWeatherHistoryFragment;
import com.example.androidgeekproject.transform.CircularTransformation;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean showSaveDefaultCityDialog = true;
    private final String DEFAULT_CITY = "Moscow";
    private boolean serviceRun = false;
    private static int currentOrientation = 0;
    private static Fragments currentFragment = new Fragments();
    private SQLiteDatabase database;
    private WeatherDataParser weatherDataParser;
    private TextView cityTextView;
    private TextView updatedTextView;
    private TextView detailsTextView;
    private TextView currentTemperatureTextView;
    private ImageView imageViewWeather;
    private String detailsText;
    private String weatherImageURI;
    private SharedPreferences sharedPreferences;
    private BroadcastReceiver broadcastReceiverSerivice, broadcastReceiverTime;
    private SensorManager sensorManager;
    private Sensor sensorTemperature;
    private Sensor sensorHumidity;
    private SensorEventListener listenerTemp = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            NavMyProfileFragment.setCurrentTemperature(Float.toString(event.values[0]));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener listenerHum = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            NavMyProfileFragment.setCurrentHumidity(Float.toString(event.values[0]));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        initDB();
        initActionBar();
        initViews();
        initNavView();
        initSensors();
        currentOrientation = getResources().getConfiguration().orientation;
        if (savedInstanceState == null) {
            loadDefaultCityWeather();
        }
        startBroadcastReceiverService();
    }

    private void initDB() {
        database = new DatabaseHelper(getApplicationContext()).getWritableDatabase();
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState){
        saveInstanceState.putString(MainActivityKeys.KEY_CITY_TEXT_VIEW.getDescription(), cityTextView.getText().toString());
        saveInstanceState.putString(MainActivityKeys.KEY_TEMPERATURE_TEXT_VIEW.getDescription(), currentTemperatureTextView.getText().toString());
        saveInstanceState.putString(MainActivityKeys.KEY_UPDATED_TEXT_VIEW.getDescription(), updatedTextView.getText().toString());
        saveInstanceState.putBoolean(MainActivityKeys.KEY_SHOW_DEF_CIT_DIALOG.getDescription(), showSaveDefaultCityDialog);
        saveInstanceState.putBoolean(MainActivityKeys.KEY_SERVICE_RUN.getDescription(),serviceRun);
        saveInstanceState.putString(MainActivityKeys.KEY_DETAILS_TEXT.getDescription(), detailsText);
        saveInstanceState.putString(MainActivityKeys.KEY_WEATHER_ICON_TEXT_VIEW.getDescription(), weatherImageURI);
        super.onSaveInstanceState(saveInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle saveInstanceState) {
        super.onRestoreInstanceState(saveInstanceState);
        cityTextView.setText(saveInstanceState.getString(MainActivityKeys.KEY_CITY_TEXT_VIEW.getDescription()));
        currentTemperatureTextView.setText(saveInstanceState.getString(MainActivityKeys.KEY_TEMPERATURE_TEXT_VIEW.getDescription()));
        updatedTextView.setText(saveInstanceState.getString(MainActivityKeys.KEY_UPDATED_TEXT_VIEW.getDescription()));
        showSaveDefaultCityDialog = saveInstanceState.getBoolean(MainActivityKeys.KEY_SHOW_DEF_CIT_DIALOG.getDescription());
        serviceRun = saveInstanceState.getBoolean(MainActivityKeys.KEY_SERVICE_RUN.getDescription());
        detailsText = saveInstanceState.getString(MainActivityKeys.KEY_DETAILS_TEXT.getDescription());
        weatherImageURI = saveInstanceState.getString(MainActivityKeys.KEY_WEATHER_ICON_TEXT_VIEW.getDescription());
        loadImage(weatherImageURI);
        if (currentOrientation != Configuration.ORIENTATION_LANDSCAPE) {
            detailsTextView.setText(detailsText);
        }
    }

    private void loadDefaultCityWeather() {
        String city = sharedPreferences.getString(MainActivityKeys.KEY1.getDescription(), null);
        if (city == null) {
            city = DEFAULT_CITY;
        }
        weatherDataParser = WeatherDataParser.getInstance();
        weatherDataParser.setActivity(this);
        weatherDataParser.loadWeather(city);
    }

    private void startBroadcastReceiverService() {
        broadcastReceiverSerivice = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!serviceRun) {
                    serviceRun = true;
                    String text = intent.getStringExtra(NavAboutFragment.KEY_FROM_FRAGMENT);
                    Toast.makeText(getApplicationContext(), text,
                            Toast.LENGTH_SHORT).show();
                    Intent intentService = new Intent(MainActivity.this, BackgroundService.class);
                    startService(intentService);
                } else {
                    Toast.makeText(getApplicationContext(), "Service already run!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        IntentFilter filter = new IntentFilter(NavAboutFragment.BROADCAST_ACTION);
        this.registerReceiver(broadcastReceiverSerivice, filter);
    }

    private void initViews() {
        cityTextView = findViewById(R.id.city_field);
        updatedTextView = findViewById(R.id.updated_field);
        detailsTextView = findViewById(R.id.details_field);
        currentTemperatureTextView = findViewById(R.id.current_temperature_field);
        imageViewWeather = findViewById(R.id.imageViewWeather);
    }

    private void initSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        sensorManager.registerListener(listenerTemp, sensorTemperature,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listenerHum, sensorHumidity,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listenerTemp, sensorTemperature);
        sensorManager.unregisterListener(listenerHum, sensorHumidity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listenerTemp, sensorTemperature,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listenerHum, sensorHumidity,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.broadcastReceiverSerivice);
    }

    private void initNavView() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_change_item:
                changeItem();
                return true;
            case R.id.menu_sort_name_inc:
                sortItemNameInc();
                return true;
            case R.id.menu_sort_name_dec:
                sortItemNameDec();
                return true;
            case R.id.menu_sort_data_inc:
                sortItemDataInc();
                return true;
            case R.id.menu_sort_data_dec:
                sortItemDataDec();
                return true;
            case R.id.menu_sort_sum_inc:
                sortItemSumInc();
                return true;
            case R.id.menu_sort_sum_dec:
                sortItemSumDec();
                return true;
            case R.id.menu_settings:
                Toast.makeText(MainActivity.this, R.string.toast_settings_description,
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_change_city:
                showInputDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.change_city);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            weatherDataParser = WeatherDataParser.getInstance();
            weatherDataParser.setActivity(this);
            weatherDataParser.loadWeather(input.getText().toString());
            if (showSaveDefaultCityDialog && !sharedPreferences.getString(MainActivityKeys.KEY1.getDescription(), " ").equals(input.getText().toString())) {
                showSaveDefaultCityDialog = false;
                showSaveDefaultCityDialog(input.getText().toString());
            }
        });
        builder.show();
    }

    private void showSaveDefaultCityDialog(String city) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(city);
        builder.setTitle(R.string.change_city_default);
        builder.setPositiveButton("YES", (dialog, which) -> {
            saveToPreference(sharedPreferences, city);
        });

        builder.setNegativeButton("NO", (dialog, which) -> {
        });

        builder.show();
    }

    private void saveToPreference(SharedPreferences sharedPreferences, String city) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MainActivityKeys.KEY1.getDescription(), city);
        editor.apply();
    }

    public void updateWeatherViews() {
        cityTextView.setText(weatherDataParser.getPlaceName());
        updatedTextView.setText(weatherDataParser.getUpdatedText());
        detailsText = weatherDataParser.getDetails();
        if (currentOrientation != Configuration.ORIENTATION_LANDSCAPE) {
            detailsTextView.setText(detailsText);
        }
        currentTemperatureTextView.setText(weatherDataParser.getCurrentTemp());
        weatherImageURI = weatherDataParser.getIcon();
        loadImage(weatherImageURI);
    }

    private void loadImage(String imageURI) {
        Picasso.get()
                .load(imageURI)
                .transform(new CircularTransformation(600))
                .into(imageViewWeather);
    }

    private void sortItemSumDec() {
        Toast.makeText(MainActivity.this, "Сортировка по убыванию суммы",
                Toast.LENGTH_SHORT).show();
    }

    private void sortItemSumInc() {
        Toast.makeText(MainActivity.this, "Сортировка по возрастанию суммы",
                Toast.LENGTH_SHORT).show();
    }

    private void sortItemDataDec() {
        Toast.makeText(MainActivity.this, "Сортировка по убыванию даты",
                Toast.LENGTH_SHORT).show();
    }

    private void sortItemDataInc() {
        Toast.makeText(MainActivity.this, "Сортировка по возрастанию даты",
                Toast.LENGTH_SHORT).show();
    }

    private void sortItemNameDec() {
        Toast.makeText(MainActivity.this, "Сортировка по убыванию имени",
                Toast.LENGTH_SHORT).show();
    }

    private void sortItemNameInc() {
        Toast.makeText(MainActivity.this, "Сортировка по возрастанию имени",
                Toast.LENGTH_SHORT).show();
    }

    private void changeItem() {
        Toast.makeText(MainActivity.this, "Изменить элемент",
                Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (id == R.id.nav_profile) {
            NavMyProfileFragment myProfileFragment = new NavMyProfileFragment();
            transaction.replace(R.id.fragmentContainer, myProfileFragment);
            currentFragment = myProfileFragment;
        } else if (id == R.id.nav_about) {
            NavAboutFragment navAboutFragment = new NavAboutFragment();
            transaction.replace(R.id.fragmentContainer, navAboutFragment);
            currentFragment = navAboutFragment;
        } else if (id == R.id.nav_contacts) {
            NavContactFragment navContactFragment = new NavContactFragment();
            transaction.replace(R.id.fragmentContainer, navContactFragment);
            currentFragment = navContactFragment;
        } else if (id == R.id.nav_site){
            NavURLFragment navURLFragment = new NavURLFragment();
            transaction.replace(R.id.fragmentContainer, navURLFragment);
            currentFragment = navURLFragment;
        } else if (id == R.id.nav_weather_history){
            NavWeatherHistoryFragment navWeatherHistoryFragment = new NavWeatherHistoryFragment();
            transaction.replace(R.id.fragmentContainer, navWeatherHistoryFragment);
            currentFragment = navWeatherHistoryFragment;
        } else if (id == R.id.nav_main) {
            transaction.remove(currentFragment);
        } else if (id == R.id.nav_share) {
            transaction.remove(currentFragment);
        } else if (id == R.id.nav_send) {
            transaction.remove(currentFragment);
        }
        transaction.commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }
}
