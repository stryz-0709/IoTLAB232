package com.tridinh.iotapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class HomeFragment extends Fragment {

    View mView;
    MainActivity mainActivity;
    com.github.angads25.toggle.widget.LabeledSwitch button1, button2;
    public TextView currentDate, currentDay, temp, humid, light;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;
    SensorData sensorData = new SensorData();
    int firstDateOfTheWeek;
    private final Handler handler = new Handler(Looper.getMainLooper());


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mainActivity = (MainActivity) getActivity();

        progressDialog = new ProgressDialog(getContext());
        builder = new AlertDialog.Builder(getContext());

        currentDate = mView.findViewById(R.id.currentDate);
        currentDay = mView.findViewById(R.id.currentDay);
        button1 = mView.findViewById(R.id.button1);
        button2 = mView.findViewById(R.id.button2);
        temp = mView.findViewById(R.id.temp);
        humid = mView.findViewById(R.id.humid);
        light = mView.findViewById(R.id.light);

        currentDay.setText(new SimpleDateFormat("EEE", Locale.getDefault()).format(new Date()));
        currentDate.setText(new SimpleDateFormat("d MMM", Locale.getDefault()).format(new Date()));

//        temp.setText(sensorData.temp);


        button1.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                mainActivity.buttonPressed(1);
            }
        });

        button2.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                mainActivity.buttonPressed(2);
            }
        });

        startBackgroundThread();



        return mView;
    }

    private void startBackgroundThread() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(1000); // Sleep for 1000ms (1 sec)

                        // Post updates to the main thread using Handler
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                sensorData = mainActivity.getSensorData();
                                temp.setText(sensorData.temp);
                                humid.setText(sensorData.humid);
                                light.setText(sensorData.light);
                                button1.setOn(Objects.equals(sensorData.button1, "1"));
                                button1.setOn(Objects.equals(sensorData.button2, "1"));
                            }
                        });

                    } catch (InterruptedException e) {
                        // Restore interrupted status
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

}