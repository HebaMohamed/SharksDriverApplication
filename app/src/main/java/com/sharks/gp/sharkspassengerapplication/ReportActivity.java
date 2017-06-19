package com.sharks.gp.sharkspassengerapplication;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sharks.gp.sharkspassengerapplication.myclasses.Driver;
import com.sharks.gp.sharkspassengerapplication.myclasses.MyURL;
import com.sharks.gp.sharkspassengerapplication.myclasses.Passenger;
import com.sharks.gp.sharkspassengerapplication.myclasses.Trip;
import com.squareup.picasso.Picasso;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReportActivity extends AppCompatActivity {


    ProgressDialog progress;
    TextView avgtxt;

    int currentdriverid;

    int p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12;

    BarChart mBarChart;

    ImageView avgimg, updownimg;
    CircleImageView userimg;
    TextView ignoredcount, acceptedcount;

    Driver d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        setTitle("Driver Report");


        mBarChart = (BarChart) findViewById(R.id.barchart);

        userimg = (CircleImageView) findViewById(R.id.userimg);
        avgimg = (ImageView) findViewById(R.id.avgimg);
        updownimg = (ImageView) findViewById(R.id.updownimg);

        ignoredcount = (TextView) findViewById(R.id.ignoredcount);
        acceptedcount = (TextView) findViewById(R.id.acceptedcount);


        d = MyApplication.getLoggedDriver();


//        mBarChart.addBar(new BarModel("SUN", 2.3f, 0xFF123456));
//        mBarChart.addBar(new BarModel("MON", 2.f,  0xFF343456));
//        mBarChart.addBar(new BarModel("TUE", 3.3f, 0xFF563456));
//        mBarChart.addBar(new BarModel("WED", 1.1f, 0xFF873F56));
//        mBarChart.addBar(new BarModel("THU", 2.7f, 0xFF56B7F1));
//        mBarChart.addBar(new BarModel("FRI", 2.f,  0xFF343456));
//        mBarChart.addBar(new BarModel("SAT", 0.4f, 0xFF1FF4AC));
////        mBarChart.addBar(new BarModel(4.f,  0xFF1BA4E6));

//        mBarChart.startAnimation();

//        PieChart mPieChart = (PieChart) findViewById(R.id.piechart);
//
//        mPieChart.addPieSlice(new PieModel("Freetime", 15, Color.parseColor("#FE6DA8")));
//        mPieChart.addPieSlice(new PieModel("Sleep", 25, Color.parseColor("#56B7F1")));
//        mPieChart.addPieSlice(new PieModel("Work", 35, Color.parseColor("#CDA67F")));
//        mPieChart.addPieSlice(new PieModel("Eating", 9, Color.parseColor("#FED70E")));
//
//        mPieChart.setEmptyDataText("opkpop");
//        mPieChart.startAnimation();

        avgtxt = (TextView)findViewById(R.id.avgtxt);

        setuploading();

        currentdriverid=MyApplication.getLoggedDriverID();
        progress.show();
        getAvg();




    }

    @Override
    protected void onResume() {
        super.onResume();
//        Picasso.with(this).load("data:image/png;base64,"+d.image).into(userimg);

        if(d.image != "") {
            byte[] decodedString = Base64.decode(d.image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            userimg.setImageBitmap(decodedByte);
        }else{
            userimg.setImageDrawable(getResources().getDrawable(R.drawable.usericn2));
        }
    }

    void setuploading(){
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Loading. Please wait...");
        progress.setIndeterminate(true);
        progress.setCanceledOnTouchOutside(false);
    }



    void getAvg() {
        progress.show();
        StringRequest sr = new StringRequest(Request.Method.GET, "http://sharksrest.herokuapp.com/rest/websiteservice/getdriveravg/"+currentdriverid, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String msg = obj.getString("msg");
                    int success = Integer.parseInt(obj.getString("success"));

                    if (success == 1) {

                        int acceptedcountf = obj.getInt("acceptedcount");
                        int ignoredcountf = obj.getInt("ignoredcount");

                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DAY_OF_YEAR, -7);
                        long daysAgo_7 = cal.getTimeInMillis();


                        JSONArray pattrens=obj.getJSONArray("pattrens");
                        for (int i=0;i<pattrens.length();i++)
                        {
                            JSONObject pattren = pattrens.getJSONObject(i);
                            int pattrenid = pattren.getInt("pattrenid");
                            long timestamp = pattren.getLong("timestamp");
                            if(timestamp>=daysAgo_7) {
                                if (pattrenid == 1)
                                    p1++;
                                else if (pattrenid == 2)
                                    p2++;
                                else if (pattrenid == 3)
                                    p3++;
                                else if (pattrenid == 4)
                                    p4++;
                                else if (pattrenid == 5)
                                    p5++;
                                else if (pattrenid == 6)
                                    p6++;
                                else if (pattrenid == 7)
                                    p7++;
                                else if (pattrenid == 8)
                                    p8++;
                                else if (pattrenid == 9)
                                    p9++;
                                else if (pattrenid == 10)
                                    p10++;
                                else if (pattrenid == 11)
                                    p11++;
                                else if (pattrenid == 12)
                                    p12++;
                            }
                        }

                        mBarChart.addBar(new BarModel(obj.getString("p1_name"), (float)p1 , 0xFF123456));
                        mBarChart.addBar(new BarModel(obj.getString("p2_name"),  (float)p2 ,  0xFF343456));
                        mBarChart.addBar(new BarModel(obj.getString("p3_name"),  (float)p3 , 0xFF563456));
                        mBarChart.addBar(new BarModel(obj.getString("p4_name"),  (float)p4 , 0xFF873F56));
                        mBarChart.addBar(new BarModel(obj.getString("p5_name"),  (float)p5 , 0xFF56B7F1));
                        mBarChart.addBar(new BarModel(obj.getString("p6_name"),  (float)p6 ,  0xFF343456));
                        mBarChart.addBar(new BarModel(obj.getString("p7_name"),  (float)p7 ,  0xFF1BA4E6));
                        mBarChart.addBar(new BarModel(obj.getString("p8_name"),  (float)p8 ,  0xFF343456));
                        mBarChart.addBar(new BarModel(obj.getString("p9_name"),  (float)p9 , 0xFF563456));
                        mBarChart.addBar(new BarModel(obj.getString("p10_name"), (float)p10 , 0xFF873F56));
                        mBarChart.addBar(new BarModel(obj.getString("p11_name"), (float)p11 , 0xFF56B7F1));
                        mBarChart.addBar(new BarModel(obj.getString("p12_name"), (float)p12 ,  0xFF343456));

                        mBarChart.setShowValues(true);

                        String avgtxts = obj.getString("avgtxt");
                        avgtxt.setText(obj.getString("avgtxt"));

                        int avg = obj.getInt("avg");
                        int lastavg = obj.getInt("lastavg");

                        if(avg>lastavg)
                            updownimg.setImageDrawable(getResources().getDrawable(R.drawable.arrowup));
                        else
                            updownimg.setImageDrawable(getResources().getDrawable(R.drawable.arrowdown));


                        acceptedcount.setText(String.valueOf(acceptedcountf));
                        ignoredcount.setText(String.valueOf(ignoredcountf));

                        if(avgtxts.equals("Excellent"))
                            avgimg.setImageDrawable(getResources().getDrawable(R.drawable.happy1));
                        else if(avgtxts.equals("Very Good"))
                            avgimg.setImageDrawable(getResources().getDrawable(R.drawable.happy2));
                        else if(avgtxts.equals("Good"))
                            avgimg.setImageDrawable(getResources().getDrawable(R.drawable.smile3));
                        else if(avgtxts.equals("Bad"))
                            avgimg.setImageDrawable(getResources().getDrawable(R.drawable.confused4));

                    } else {
                        Toast.makeText(ReportActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    progress.hide();
                    mBarChart.startAnimation();


                } catch (Exception e) {
                    e.printStackTrace();
                    progress.hide();
                    Toast.makeText(ReportActivity.this, "Something went wrong! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progress.hide();
                Toast.makeText(ReportActivity.this, "Something went wrong!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/x-www-form-urlencoded; charset=utf-8");
            }



            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
//                params.put("Content-Type","application/json");
                return params;
            }
        };

        sr.setRetryPolicy(new DefaultRetryPolicy(100000, 10, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the queue
        Volley.newRequestQueue(this).add(sr);

    }


}
