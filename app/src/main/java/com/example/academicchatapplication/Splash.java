package com.example.academicchatapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Splash extends AppCompatActivity {
    SharedPreferences sharedPreferences;

    DatabaseReference df;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            checkLoginid();
            }
        }, 1000);
    }

    public void checkLoginid(){
        sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("loginid","default");

        if(!id.equals("default")){
            Login.loginid=id;
            checkUserStatus();
        }else{
            openLogin();
        }
    }

    public void checkUserStatus(){
        df = FirebaseDatabase.getInstance().getReference("user").child(Login.loginid).child("userstatus");
        df.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
                if (status.equals("true")) {
                    openHome();
                }else{
                   openLogin();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void openHome() {
        // method for open the home page
        Intent intent = new Intent(this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
    }

    public void openLogin() {
        // method for open the home page
        Intent mainIntent = new Intent(Splash.this, Login.class);
        startActivity(mainIntent);
        finish();
    }

}