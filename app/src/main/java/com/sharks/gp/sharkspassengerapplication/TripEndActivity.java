package com.sharks.gp.sharkspassengerapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TripEndActivity extends AppCompatActivity {

    TextView costtxt, stxt, dtxt, distancetxt, distancecosttxt, durationtxt, durationcosttxt;
    Button donebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_end);
        setTitle("End Trip");
        costtxt=(TextView) findViewById(R.id.costtxt);
        stxt=(TextView) findViewById(R.id.stxt);
        dtxt=(TextView) findViewById(R.id.dtxt);
        distancetxt=(TextView) findViewById(R.id.distancetxt);
        distancecosttxt=(TextView) findViewById(R.id.distancecosttxt);
        durationtxt=(TextView) findViewById(R.id.durationtxt);
        durationcosttxt=(TextView) findViewById(R.id.durationcosttxt);
        donebtn=(Button)findViewById(R.id.donebtn);

        MyApplication.endRequest();

        /// /donetrip and get data into here //fill data
        donebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.beReady();
                startActivity(new Intent(TripEndActivity.this, MainMapActivity.class));
                finish();
            }
        });
    }
}
