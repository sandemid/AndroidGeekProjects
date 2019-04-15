package com.example.androidgeekproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidgeekproject.fragments.Fragments;
import com.example.androidgeekproject.fragments.NavAboutFragment;
import com.example.androidgeekproject.fragments.NavContactFragment;
import com.example.androidgeekproject.fragments.NavMyProfileFragment;
import com.example.androidgeekproject.fragments.NavURLFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String KEY1 = "DEFAULT_WEATHER_CITY";
    private boolean showSaveDefaultCityDialog = true;
    private boolean serviceRun = false;
    private Fragments currentFragment = new Fragments();
    private WeatherDataParser weatherDataParser;
    private TextView cityTextView;
    private TextView updatedTextView;
    private TextView detailsTextView;
    private TextView currentTemperatureTextView;
    private TextView weatherIconTextView;
    private SharedPreferences sharedPreferences;
    private BroadcastReceiver broadcastReceiver;
    private SensorManager sensorManager;
    private Sensor sensorTemperature;
    private Sensor sensorHumidity;
    private Handler handler;
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
        handler = new Handler();
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        initActionBar();
        initViews();
        initNavView();
        initSensors();
        //загружаем погоду для города из настроек по умолчанию
        loadDefaultCityWeather();
        //мониторит создание интента на запуск сервиса BackgroundService из фрагмента NavAboutFragment
        startBroadcastReceiver();
    }

    private void loadDefaultCityWeather() {
        String city = sharedPreferences.getString(KEY1, null);
        if (city != null) {
            new Thread(() -> {
                weatherDataParser = new WeatherDataParser(getApplicationContext(), WeatherDataLoader.getJSONData(city));
                if (weatherDataParser.updateWeatherData()){
                    handler.post(this::updateWeatherViews);
                } else {
                    handler.post(() -> Toast.makeText(MainActivity.this, "Для указанного города не нашлось информации: "
                            + city, Toast.LENGTH_SHORT).show());
                }
            }).start();
        }
    }

    private void startBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
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
        this.registerReceiver(broadcastReceiver, filter);
    }

    private void initViews() {
        cityTextView = findViewById(R.id.city_field);
        updatedTextView = findViewById(R.id.updated_field);
        detailsTextView = findViewById(R.id.details_field);
        currentTemperatureTextView = findViewById(R.id.current_temperature_field);
        weatherIconTextView = findViewById(R.id.weather_icon);
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
        this.unregisterReceiver(this.broadcastReceiver);
    }

    private void initNavView() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initFabWithPopup();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initFabWithPopup() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, view);
            getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
            popup.setOnMenuItemClickListener((item) -> {
                if(item.getItemId() == R.id.menu_popup_add_1) {
                    Toast.makeText(getApplicationContext(), "Добавить элемент 1 типа",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Добавить элемент 2 типа",
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            });
            popup.show();
        });
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
        builder.setPositiveButton("OK", (dialog, which) -> new Thread(() -> {
            weatherDataParser = new WeatherDataParser(getApplicationContext(), WeatherDataLoader.getJSONData(input.getText().toString()));
            if (weatherDataParser.updateWeatherData()){
                handler.post(() -> {
                    updateWeatherViews();
                    //диалог сохранения значения по умолчанию показывается только один раз
                    //в течение жизненного цикла Активити при вводе корректного города отличного от дефолтного,
                    //и по которому срабатывает GET-запрос
                if (showSaveDefaultCityDialog && !sharedPreferences.getString(KEY1, " ").equals(input.getText().toString())) {
                        showSaveDefaultCityDialog = false;
                        showSaveDefaultCityDialog(input.getText().toString());
                    }
                });
            } else {
                handler.post(() -> Toast.makeText(MainActivity.this, "Для указанного города не нашлось информации: "
                                + input.getText().toString(), Toast.LENGTH_SHORT).show());
            }
        }).start());
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
        editor.putString(KEY1, city);
        editor.apply();
    }

    private void updateWeatherViews() {
        cityTextView.setText(weatherDataParser.getPlaceName());
        updatedTextView.setText(weatherDataParser.getUpdatedText());
        detailsTextView.setText(weatherDataParser.getDetails());
        currentTemperatureTextView.setText(weatherDataParser.getCurrentTemp());
        weatherIconTextView.setText(weatherDataParser.getIcon());
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
    }}
