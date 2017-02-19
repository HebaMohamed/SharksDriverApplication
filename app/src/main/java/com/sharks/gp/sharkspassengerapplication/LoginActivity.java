package com.sharks.gp.sharkspassengerapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sharks.gp.sharkspassengerapplication.myclasses.Driver;

public class LoginActivity extends AppCompatActivity {

    EditText driveridtxt, driverpasstxt;
    Button loginbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();


        driveridtxt=(EditText)findViewById(R.id.driveridtxt);
        driverpasstxt=(EditText)findViewById(R.id.driverpasstxt);
        loginbtn=(Button)findViewById(R.id.loginbtn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check()) {
                    //example login
                    try {
                        Driver d = new Driver(1, "harry", "h@h.h", "", "");//test
                        MyApplication.storeLogin(d);
                        startActivity(new Intent(LoginActivity.this, MainMapActivity.class));
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    boolean check() {
        String id = driveridtxt.getText().toString();
        String pass = driverpasstxt.getText().toString();
        if (id.equals("") || pass.equals(""))
            return false;
        else
            return true;
    }
}
