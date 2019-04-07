package com.example.androidgeekproject;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidgeekproject.fragments.Fragments;
import com.example.androidgeekproject.fragments.NavAboutFragment;
import com.example.androidgeekproject.fragments.NavContactFragment;
import com.example.androidgeekproject.fragments.NavMyProfileFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragments currentFragment = new Fragments();
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
        initActionBar();
        initNavView();

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
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem search = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                TextView s = findViewById(R.id.main_text_view);
                s.setText(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (id){
            case R.id.menu_change:
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
            default:
                return super.onOptionsItemSelected(item);
        }
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
        } else if (id == R.id.nav_share) {
            transaction.remove(currentFragment);
        } else if (id == R.id.nav_send) {

        }
        transaction.commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }}
