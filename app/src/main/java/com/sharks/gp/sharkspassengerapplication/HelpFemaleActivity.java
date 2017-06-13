package com.sharks.gp.sharkspassengerapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sharks.gp.sharkspassengerapplication.myclasses.AppConstants;

public class HelpFemaleActivity extends AppCompatActivity  implements OnMapReadyCallback {

    private GoogleMap mMap;

    TextView numtxt, modeltxt, colortxt;

    long fid;

    Button dangerbtn, safebtn;

    int driverid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_female);

        getSupportActionBar().hide();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        numtxt = (TextView) findViewById(R.id.numtxt);
        modeltxt = (TextView) findViewById(R.id.modeltxt);
        colortxt = (TextView) findViewById(R.id.colortxt);

        dangerbtn = (Button) findViewById(R.id.dangerbtn);
        safebtn = (Button) findViewById(R.id.safebtn);

        driverid = MyApplication.getLoggedDriverID();

        dangerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.myFirebaseRef.child(AppConstants.FIRE_DRIVER).child(String.valueOf(driverid)).child("warninghelp").child("response").setValue("Danger");
                Toast.makeText(HelpFemaleActivity.this, "Your response is changed to DANGER", Toast.LENGTH_SHORT).show();
            }
        });

        safebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.myFirebaseRef.child(AppConstants.FIRE_DRIVER).child(String.valueOf(driverid)).child("warninghelp").child("response").setValue("Safe");
                Toast.makeText(HelpFemaleActivity.this, "Your response is changed to SAFE", Toast.LENGTH_SHORT).show();
            }
        });


        MyApplication.myFirebaseRef.child(AppConstants.FIRE_DRIVER).child(String.valueOf(driverid)).child("warninghelp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long femalesafteyid = dataSnapshot.child("femalesafteyid").getValue(Long.class);
                if(femalesafteyid==0) {
                    MyApplication.removeFemaleHelp();
                    Toast.makeText(HelpFemaleActivity.this, "Resume your work, thanks for your help", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(HelpFemaleActivity.this, MainMapActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        fid = MyApplication.getFemaleHelp();
        if (fid != 0) {
            //listen & get initial value
            MyApplication.myFirebaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    DataSnapshot dataSnapshot2 = dataSnapshot.child(AppConstants.FIRE_WARNING).child(AppConstants.FIRE_FEMALESAFTEY).child(String.valueOf(fid));

                    double lat = dataSnapshot2.child("lat").getValue(double.class);
                    double lng = dataSnapshot2.child("lng").getValue(double.class);
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title("Help Location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.smallblueshark)));

                    int vid = dataSnapshot2.child("vid").getValue(Integer.class);

                    DataSnapshot dataSnapshot3 = dataSnapshot.child(AppConstants.FIRE_VEHICLES).child(String.valueOf(vid));
                    numtxt.setText(dataSnapshot3.child("plate_number").getValue(String.class));
                    colortxt.setText(dataSnapshot3.child("color").getValue(String.class));
                    modeltxt.setText(dataSnapshot3.child("model").getValue(String.class));
//                animateMarkerToGB(drivermarker,new LatLng(lat, lng));
//                getDirections(lat,lng);


                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }
    }

}
