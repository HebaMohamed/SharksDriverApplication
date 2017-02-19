package com.sharks.gp.sharkspassengerapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sharks.gp.sharkspassengerapplication.myclasses.Trip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class LastTripsActivity extends AppCompatActivity {

    ArrayList<Trip> trips = new ArrayList<>();
    ListView tripslv;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_trips);
        setTitle("Last Trips");
        Trip t1 = new Trip(1);
        t1.destination.setLatitude(30.123177);
        t1.destination.setLongitude(31.009540);
        t1.start_Date=new Date();
        t1.end_Date=new Date();
        Trip t2 = new Trip(1);
        t2.destination.setLatitude(31.123177);
        t2.destination.setLongitude(31.009540);
        t2.start_Date=new Date();
        t2.end_Date=new Date();

        trips.add(t1);
        trips.add(t2);

        tripslv=(ListView)findViewById(R.id.tripslv);

        setadapter(trips,tripslv);

    }


    //fill lv
    void setadapter(final ArrayList<Trip> a, ListView lv){
        lv.setAdapter(null);
        adapter =new ArrayAdapter(LastTripsActivity.this, R.layout.lasttriplayout, android.R.id.text1, a)
        {
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) MyApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.lasttriplayout, parent, false);

                TextView disttxt = (TextView) view.findViewById(R.id.disttxt);
                String addr = null;
                try {
                    addr = MyApplication.getLocationAddress(a.get(position).destination);
                } catch (IOException e) {
                    e.printStackTrace();
                    addr="Unknown Address";
                }
                TextView starttxt = (TextView) view.findViewById(R.id.starttxt);
                disttxt.setText(addr);
                starttxt.setText(a.get(position).getStartdate());

                final int pos= position;

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LastTripDetailsActivity.selectedTripID = a.get(pos).trip_ID;
                        startActivity(new Intent(LastTripsActivity.this, LastTripDetailsActivity.class));
                    }
                });

                return view;
            }
        };
        lv.setAdapter(adapter);
    }



}
