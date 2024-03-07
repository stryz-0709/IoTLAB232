package com.tridinh.iotapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    AlertDialog.Builder alertDialog;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    FragmentManager fragmentManager;
    MqttClient client;
    HomeFragment homeFragment = new HomeFragment();
    SensorData sensorData = new SensorData();

    android.widget.EditText temp;

    public final String[] topics = {"stryz_0709/feeds/sensor1", "stryz_0709/feeds/sensor2", "stryz_0709/feeds/sensor3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_layout, new HomeFragment(), "HomeFragment")
                    .commit();
        }

        progressDialog = new ProgressDialog(this);
        alertDialog = new AlertDialog.Builder(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

//        homeFragment.temp = (HomeFragment) findViewbyId(R.id.temp);

//        homeFragment.temp.setText("30");
        /////MQTT
        try {
            client = new MqttClient("tcp://io.adafruit.com:1883","123456789", new MemoryPersistence());
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setUserName("stryz_0709");
            connectOptions.setPassword("aio_fNfx39uGm4iyD8e3bYnYzhUIOIe7".toCharArray());
            connectOptions.setCleanSession(true);
            client.connect(connectOptions);
            subscribeToTopic();
        } catch (MqttException e) {
            Toast.makeText(MainActivity.this, "No signal", Toast.LENGTH_SHORT).show();
            Log.d("MQTT", "EXCEPTION FAILURE");
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (topic.contains("sensor1")){
                    Log.d("SENSOR1", message.toString());
                    sensorData.temp = message.toString();
                }
                else if (topic.contains("sensor2")){
                    sensorData.humid = message.toString();
                    Log.d("SENSOR2", message.toString());
                }
                else if (topic.contains("sensor3")){
                    sensorData.light = message.toString();
                    Log.d("SENSOR3", message.toString());
                }
                else if (topic.contains("button1")){
                    sensorData.button1 = message.toString();
                    Log.d("BUTTON1", message.toString());
                }
                else if (topic.contains("button2")){
                    sensorData.button2 = message.toString();
                    Log.d("BUTTON2", message.toString());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    bottomNavigationView.setSelectedItemId(R.id.bottom_home);
                    openFragment(new HomeFragment());
                } else if (itemId == R.id.nav_settings) {
//            openFragment(new SettingsFragment());
                } else if (itemId == R.id.nav_editprofile) {
//            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
//            intent.putExtra("userInfo", new Gson().toJson(userInfo));
//            startActivity(intent);
                } else if (itemId == R.id.nav_about) {
//            openFragment(new AboutFragment());
                } else if (itemId == R.id.nav_share) {
                    ShareApp(MainActivity.this);
                } else if (itemId == R.id.nav_logout) {
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        View headerView = navigationView.getHeaderView(0);

        TextView navName = (TextView) headerView.findViewById(R.id.nav_name);
        navName.setText("Tri Dinh");
        TextView navEmail = (TextView) headerView.findViewById(R.id.nav_email);
        navEmail.setText("userInfo@email.com");

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.bottom_home) {
                    Menu menu = navigationView.getMenu();
                    menu.findItem(R.id.nav_home).setChecked(true);
                    openFragment(new HomeFragment());
                    return true;
                } else if (itemId == R.id.bottom_graph) {
                    clearNavigationSelection(navigationView);
                    openFragment(new GraphFragment());
                    return true;
                } else if (itemId == R.id.bottom_search) {
                    clearNavigationSelection(navigationView);
                    openFragment(new SearchFragment());
                    return true;
                }
                return false;
            }
        });

        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            openFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void openFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_layout, fragment);
        transaction.commit();
    }

    private void clearNavigationSelection(NavigationView navigationView) {
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_home).setChecked(false);
        menu.findItem(R.id.nav_about).setChecked(false);
        menu.findItem(R.id.nav_settings).setChecked(false);
        menu.findItem(R.id.nav_editprofile).setChecked(false);
    }

    private void subscribeToTopic() {
        for (String topic : topics) {
            try {
                client.subscribe(topic);
            } catch (MqttException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void ShareApp(Context context){
        // code here
        final String appPackageName = context.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Download Now: https://play.google.com/store/apps/details?id=" + appPackageName );
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public SensorData getSensorData(){
        return this.sensorData;
    }

    public void buttonPressed(int btn){
        if (btn == 1){
            try {
                MqttMessage button1 = new MqttMessage(String.valueOf(sensorData.button1).getBytes());
                client.publish("button1", "0".getBytes() , 0, true);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        else if (btn == 2){
            try {
                MqttMessage button2 = new MqttMessage(String.valueOf(sensorData.button2).getBytes());
                client.publish("button2", button2);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }



    }
}