package com.sharks.gp.sharkspassengerapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharks.gp.sharkspassengerapplication.myclasses.AppConstants;
import com.sharks.gp.sharkspassengerapplication.myclasses.Passenger;
import com.sharks.gp.sharkspassengerapplication.myclasses.Trip;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class LastTripDetailsActivity extends AppCompatActivity {

    public static int selectedTripID;
    Trip t1;

    ImageView mapimg;
    TextView passengername, pickuploctxt, dropoffloctxt, starttxt, endtxt, distancetxt, durationtxt, costtxt, tripratetxt;

    String test;
//    ArrayList<Double> pathwayLats = new ArrayList<>();
//    ArrayList<Double> pathwayLngs = new ArrayList<>();//lmlk

    ArrayList<Location> pathwayLocs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_trip_details);
        setTitle("Last trip");
        mapimg=(ImageView)findViewById(R.id.mapimg);
        passengername=(TextView) findViewById(R.id.passengername);
        pickuploctxt=(TextView) findViewById(R.id.pickuploctxt);
        dropoffloctxt=(TextView) findViewById(R.id.dropoffloctxt);
        starttxt=(TextView) findViewById(R.id.starttxt);
        endtxt=(TextView) findViewById(R.id.endtxt);
        distancetxt=(TextView) findViewById(R.id.distancetxt);
        durationtxt=(TextView) findViewById(R.id.durationtxt);
        costtxt=(TextView) findViewById(R.id.costtxt);
        tripratetxt=(TextView) findViewById(R.id.tripratetxt);

        //testttt get last trip by id
        t1 = new Trip(selectedTripID);
        t1.destination.setLatitude(30.123177);
        t1.destination.setLongitude(31.009540);
        t1.pickup.setLatitude(30.223177);
        t1.pickup.setLongitude(31.209540);
        t1.start_Date=new Date();
        t1.end_Date=new Date();
        t1.p=new Passenger(1);
        t1.rating=5;
        t1.price=112;
        t1.p.fullName="bla name";

        test = "uihiu";
//        pathwayLats.add(30.123177);
//        pathwayLngs.add(31.009540);
//        pathwayLats.add(30.133177);
//        pathwayLngs.add(31.019540);///////////////3dly l method
//        pathwayLats.add(30.143177);
//        pathwayLngs.add(31.029540);
        Location l1 = new Location("");
        l1.setLatitude(30.13244589999999);
        l1.setLongitude(31.0177915);
        Location l2 = new Location("");
        l2.setLatitude(30.1328793);
        l2.setLongitude(31.2034381);

        pathwayLocs.add(l1);
        pathwayLocs.add(l2);

        //end testtts

        try {
            passengername.setText(t1.p.fullName);
            pickuploctxt.setText(MyApplication.getLocationAddress(t1.pickup));
            dropoffloctxt.setText(MyApplication.getLocationAddress(t1.destination));
            starttxt.setText(t1.getStartdate());
            endtxt.setText(t1.getEnddate());
            tripratetxt.setText(t1.rating+" Stars");
            costtxt.setText(t1.price+" $");
            distancetxt.setText(getDistance()+"Km");
            durationtxt.setText(getDuration());

            String locurl = getpathwaymap();
            URL url = new URL(locurl);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            mapimg.setImageBitmap(bmp);

        } catch (IOException e) {
            e.printStackTrace();
        };



    }



    String getpathwaymap(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String staticmapurl = "https://maps.googleapis.com/maps/api/staticmap?";
        try {

            int centersize = pathwayLocs.size()/2;
            String latcenter = String.valueOf(pathwayLocs.get(centersize).getLatitude());
            String lngcenter = String.valueOf(pathwayLocs.get(centersize).getLongitude());

            staticmapurl += "center="+latcenter+","+lngcenter+"&size=290x110&path=color:0x0000ff|weight:5";//&zoom=14

            for (int i = 0; i < pathwayLocs.size(); i++) {
                String lat = String.valueOf(pathwayLocs.get(i).getLatitude());
                String lng = String.valueOf(pathwayLocs.get(i).getLongitude());
                staticmapurl+="|"+lat+","+lng;
            }
            staticmapurl+="&key="+ AppConstants.MAP_API_KEY;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return staticmapurl;
    }

    double getDistance(){
        double d = 0;
        for (int i = 0; i < pathwayLocs.size()-1; i++) {//-1 u know
            double dist = pathwayLocs.get(i).distanceTo(pathwayLocs.get(i+1));
            d+=dist;
        }
        return d/1000;//km
    }

    String getDuration(){
        long diff =  t1.start_Date.getTime() - t1.end_Date.getTime();
        int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
        int hours = (int) (diff / (1000 * 60 * 60));
        int minutes = (int) (diff / (1000 * 60));
        int seconds = (int) (diff / (1000));
        String duration = hours+":"+minutes+":"+seconds;
        return duration;
    }
}
