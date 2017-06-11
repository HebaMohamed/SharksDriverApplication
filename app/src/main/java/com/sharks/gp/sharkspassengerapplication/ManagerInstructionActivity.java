package com.sharks.gp.sharkspassengerapplication;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.sharks.gp.sharkspassengerapplication.myclasses.AppConstants;
import com.sharks.gp.sharkspassengerapplication.myclasses.MgrInstruction;
import com.sharks.gp.sharkspassengerapplication.myclasses.TalkMessage;

import java.util.ArrayList;

public class ManagerInstructionActivity extends AppCompatActivity {

    ListView instlv;
    private ArrayAdapter adapter;
    TextView headtxt;

    ArrayList<MgrInstruction> instructions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_instruction);
        setTitle("Manager Instructions");
        instlv=(ListView)findViewById(R.id.instlv);
//        headtxt=(TextView)findViewById(R.id.headtxt);
        //instructions=MyApplication.getMgrsInstructions();


        int did = MyApplication.getLoggedDriverID();




        //setadapter(instructions,instlv);
//        if(instructions.size()!=0)
//            headtxt.setText("Last Updated : "+instructions.get(instructions.size()-1).getInstructionDateTimeString());
//        MyApplication.removeNotifications(3);



        //listen & get initial value
        MyApplication.myFirebaseRef.child(AppConstants.FIRE_DRIVER).child(String.valueOf(did)).child("mgrinstruction").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                instructions.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Long time = Long.parseLong(postSnapshot.getKey());
                    String msg = postSnapshot.getValue(String.class);
                    MgrInstruction i = new MgrInstruction(msg,time);

                    instructions.add(i);
                }
                setadapter(instructions,instlv);

                if(instructions.size()==0)
                    Toast.makeText(ManagerInstructionActivity.this, "There is no instructions for now", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });


    }

    //fill lv
    void setadapter(final ArrayList<MgrInstruction> a, ListView lv){
        lv.setAdapter(null);
        adapter =new ArrayAdapter(ManagerInstructionActivity.this, R.layout.mgrtalklayout, android.R.id.text1, a)
        {
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) MyApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.mgrtalklayout, parent, false);

                TextView msgtxt = (TextView) view.findViewById(R.id.msgtxt);
                TextView insttimetxt = (TextView) view.findViewById(R.id.insttimetxt);
                msgtxt.setText(a.get(position).msg);
                insttimetxt.setText(a.get(position).getInstructionDateTimeString());
                return view;
            }
        };
        lv.setAdapter(adapter);
    }

}
