package com.sharks.gp.sharkspassengerapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sharks.gp.sharkspassengerapplication.myclasses.Driver;
import com.sharks.gp.sharkspassengerapplication.myservices.LocationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private static String TAG = "Main Activity";

    Button loginbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        loginbtn = (Button) findViewById(R.id.loginbtn);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });



        //check if user logged in
        try {
            Driver loogedDriver = MyApplication.getLoggedDriver();
            if(loogedDriver!=null) {
//                startActivity(new Intent(MainActivity.this, MainMapActivity.class));
                String appState = MyApplication.getAppState();
                if(appState.equals("request")){
                    startActivity(new Intent(MainActivity.this, TripRequestActivity.class));
                } else if(appState.equals("ready")){
//                    sendTestTrip();//test onlyyy
//                    sendMgrInstruction();//test onlyyy
                    startActivity(new Intent(MainActivity.this, MainMapActivity.class));
                } else if(appState.equals("accept")){
                    startActivity(new Intent(MainActivity.this, ArrivingActivity.class));
                } else if(appState.equals("started")){
                    startActivity(new Intent(MainActivity.this, InTripActivity.class));
                }else if(appState.equals("ended")){
                    startActivity(new Intent(MainActivity.this, TripEndActivity.class));
                }
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }


        //when login must register
//        register();
//        sendNotification();
        //startService(new Intent(MyApplication.getAppContext(), LocationService.class));//for test - only start when start trip

    }

//    void sendTestTrip(){
//
//        JSONObject jso = new JSONObject();
//        try {
//            jso.put("type", "triprequest");
//            jso.put("lat", 30.123177);
//            jso.put("lng", 31.009540);
//            jso.put("details", "I'm over here a");
//            jso.put("tripid", 1);
//            jso.put("timestamp", System.currentTimeMillis());
//            MyApplication.sendNotification(jso);
//
//        } catch (JSONException e) { e.printStackTrace(); }
//    }
//    void sendMgrInstruction(){
//
//        JSONObject jso = new JSONObject();
//        try {
//            jso.put("type", "managerinstruction");
//            jso.put("mgrmsg", "come on now");
//            jso.put("timestamp", System.currentTimeMillis());
//            MyApplication.sendNotification(jso);
//
//        } catch (JSONException e) { e.printStackTrace(); }
//    }













}
