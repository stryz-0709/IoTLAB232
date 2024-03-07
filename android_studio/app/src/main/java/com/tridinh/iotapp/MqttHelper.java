package com.tridinh.iotapp;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MqttHelper {
    MainActivity mainActivity;
    public MqttAndroidClient mqttAndroidClient;

    public final String[] arrayTopics = {"stryz_0709/feeds/sensor1", "stryz_0709/feeds/sensor2", "stryz_0709/feeds/sensor3"};

    final String clientId = "12345678";
    final String username = "stryz_0709";
    final String password = "aio_JcLI78Fa5S9sHnsJfxCArbsLZgJW";

    final String serverUri = "tcp://io.adafruit.com:1883";

    public MqttHelper(Context context){

        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("mqtt", s);
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (topic.contains("sensor1")){
                    Log.d("SENSOR1", message.toString());
//                    sensorData.temp = message.toString();
                }
                else if (topic.contains("sensor2")){
//                    sensorData.humid = message.toString();
                    Log.d("SENSOR2", message.toString());
                }
                else if (topic.contains("sensor3")){
//                    sensorData.light = message.toString();
                    Log.d("SENSOR3", message.toString());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        connect();
    }


    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void connect(){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        try {

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
//                    mqttAndroidClient.(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Failed to connect to: " + serverUri + exception.toString());
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }

    private void subscribeToTopic() {
        for(int i = 0; i < arrayTopics.length; i++) {
            try {
                mqttAndroidClient.subscribe(arrayTopics[i], 0, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d("TEST", "Subscribed!");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d("TEST", "Subscribed fail!");
                    }
                });

            } catch (MqttException ex) {
                System.err.println("Exception subscribing");
                ex.printStackTrace();
            }
        }
    }

}