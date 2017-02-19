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

import com.sharks.gp.sharkspassengerapplication.myclasses.MgrInstruction;

import java.util.ArrayList;

public class ManagerInstructionActivity extends AppCompatActivity {

    ListView instlv;
    ArrayList<MgrInstruction> instructions;
    private ArrayAdapter adapter;
    TextView headtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_instruction);
        setTitle("Manager Instructions");
        instlv=(ListView)findViewById(R.id.instlv);
        headtxt=(TextView)findViewById(R.id.headtxt);
        instructions=MyApplication.getMgrsInstructions();
        setadapter(instructions,instlv);
        headtxt.setText("Last Updated : "+instructions.get(instructions.size()-1).getInstructionDateTimeString());
        MyApplication.removeNotifications(3);


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
