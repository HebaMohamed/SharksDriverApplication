package com.sharks.gp.sharkspassengerapplication;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.sharks.gp.sharkspassengerapplication.myclasses.AppConstants;
import com.sharks.gp.sharkspassengerapplication.myclasses.Passenger;
import com.sharks.gp.sharkspassengerapplication.myclasses.TalkMessage;
import com.sharks.gp.sharkspassengerapplication.myclasses.Trip;

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

import at.markushi.ui.CircleButton;

public class TalkActivity extends AppCompatActivity {

    CircleButton micbtn, thumbupbtn, thumbdownbtn;
    final int REQ_CODE_SPEECH_INPUT = 100;

    ListView talklv;
    Spinner languagesspinner;
    ArrayAdapter adapter;
    ArrayList<TalkMessage> msgs = new ArrayList<>();

    ArrayList<String> languages = new ArrayList<>();
    ArrayList<String> languagesCodes = new ArrayList<>();
    int selectedIndex;

    Passenger passenger;

    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_activity);
        setTitle("Passenger Conversation");

        micbtn=(CircleButton)findViewById(R.id.micbtn);
        thumbupbtn=(CircleButton)findViewById(R.id.thumbupbtn);
        thumbdownbtn=(CircleButton)findViewById(R.id.thumbdownbtn);
        talklv=(ListView)findViewById(R.id.talklv);
        languagesspinner = (Spinner)findViewById(R.id.languagesspinner);
        MyApplication.removeNotifications(3);

        try {
            passenger=MyApplication.getTripPassenger();
            trip = MyApplication.getTripRequest();

        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();//3shn l err
        StrictMode.setThreadPolicy(policy);

        //get languages
        for (Locale locale : Locale.getAvailableLocales()) {
            languages.add(locale.getDisplayName());
            languagesCodes.add(locale.getLanguage());
            if(locale.getDisplayName().equals("English")){//take as default
                selectedIndex=languages.size()-1;
            }
        }

        micbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,  "Say Something");
                try {
                    startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(), "Speech is not supported", Toast.LENGTH_SHORT).show();
                }
            }
        });

        thumbupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage("Yes");
            }
        });

        thumbdownbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage("No");
            }
        });
//        msgs.add(new TalkMessage("p","drop me here"));//testtttt
//        msgs.add(new TalkMessage("d","yes"));
//        setadapter(msgs,talklv);

        //listen & get initial value
        MyApplication.myFirebaseRef.child(AppConstants.FIRE_TRIPS).child(String.valueOf(trip.trip_ID)).child("talk").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                msgs.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    try {
                        String msg = postSnapshot.child("msg").getValue(String.class);
                        String f = postSnapshot.child("f").getValue(String.class);

                        if(f!=null) {

                            //translate
                            try {
                                String str;
                                if (f.equals("p"))
                                    str = callUrlAndParseResult("auto", languagesCodes.get(selectedIndex), msg);
                                else
                                    str = msg;
                                msgs.add(new TalkMessage(f, str));
                            } catch (Exception e) {
                                e.printStackTrace();
                                msgs.add(new TalkMessage(f, msg));
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        //Toast.makeText(MainMapActivity.this, "There is new request but the problem : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                setadapter(msgs,talklv);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });



//        /////recive passenger messages
//        IntentFilter filter = new IntentFilter(AppConstants.BROADCAST_MSG_ACTION);
//        BroadcastReceiver receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String msg =  intent.getExtras().getString("msg");
//                String msgflag =  intent.getExtras().getString("msgflag");
//
//                //translate
//                try {
//                    String str = callUrlAndParseResult("auto",languagesCodes.get(selectedIndex),msg);
//                    msgs.add(new TalkMessage(msgflag,str));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    msgs.add(new TalkMessage(msgflag,msg));
//                }
//
//                setadapter(msgs,talklv);
//            }
//        };
//        registerReceiver(receiver, filter);




        //spinning part
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        languagesspinner.setAdapter(adapter);

        languagesspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position!=0)
                    selectedIndex=position;//3shn l default
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        languagesspinner.setSelection(selectedIndex);//as default



    }

    //Receiving speech input
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String txt = String.valueOf(result.get(0));
//                    Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
                    sendMessage(txt);
                }
                break;
            }

        }
    }
    //fill lv
    void setadapter(final ArrayList<TalkMessage> a, ListView lv){
        lv.setAdapter(null);
        adapter =new ArrayAdapter(TalkActivity.this, R.layout.passengertalklayout, android.R.id.text1, a)
        {
            public View getView(int position, View convertView, ViewGroup parent) {
//                View view = super.getView(position, convertView, parent);
                LayoutInflater inflater = (LayoutInflater) MyApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view;
                if(a.get(position).msgFlag.equals("p"))
                    view = inflater.inflate(R.layout.passengertalklayout, parent, false);
                else //d
                    view = inflater.inflate(R.layout.drivertalklayout, parent, false);

                TextView msgtxt = (TextView) view.findViewById(R.id.msgtxt);
                msgtxt.setText(a.get(position).msg);
                return view;
            }
        };
        lv.setAdapter(adapter);
        lv.setSelection(adapter.getCount() - 1);
    }

    //send pub msg notificaiton
    void sendMessage(String msg){

        long t = System.currentTimeMillis();
        MyApplication.myFirebaseRef.child("trips").child(String.valueOf(trip.trip_ID)).child("talk").child(String.valueOf(t)).child("msg").setValue(msg);
        MyApplication.myFirebaseRef.child("trips").child(String.valueOf(trip.trip_ID)).child("talk").child(String.valueOf(t)).child("f").setValue("d");


//        JSONObject jso = new JSONObject();
//        try {
//            jso.put("type", "passengertalk");
//            jso.put("msg", msg);
//            jso.put("msgflag", "d");
//            MyApplication.sendNotificationToChannel(jso,"passenger"+passenger.id);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        //show msg
//        //translate
//        try {
//            String str = callUrlAndParseResult("auto",languagesCodes.get(selectedIndex),msg);
//            msgs.add(new TalkMessage("d",str));
//        } catch (Exception e) {
//            e.printStackTrace();
//            msgs.add(new TalkMessage("d",msg));
//        }
//
//        setadapter(msgs,talklv);
    }


    private String callUrlAndParseResult(String langFrom, String langTo, String word) throws Exception
    {

        String url = "https://translate.googleapis.com/translate_a/single?"+
                "client=gtx&"+
                "sl=" + langFrom +
                "&tl=" + langTo +
                "&dt=t&q=" + URLEncoder.encode(word, "UTF-8");

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return parseResult(response.toString());
    }

    private String parseResult(String inputJson) throws Exception
    {
  /*
   * inputJson for word 'hello' translated to language Hindi from English-
   * [[["नमस्ते","hello",,,1]],,"en"]
   * We have to get 'नमस्ते ' from this json.
   */

        JSONArray jsonArray = new JSONArray(inputJson);
        JSONArray jsonArray2 = (JSONArray) jsonArray.get(0);
        JSONArray jsonArray3 = (JSONArray) jsonArray2.get(0);

        return jsonArray3.get(0).toString();
    }



}
