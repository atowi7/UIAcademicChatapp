package com.example.academicchatapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class CodeVerification extends AppCompatActivity {
    EditText edusercode;
    DatabaseReference df;
    public boolean c=false;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.code_verification);

        edusercode = (EditText) findViewById(R.id.CODEEDIT);
    }

    public void go(View view) {

        String usercode = edusercode.getText().toString();

        c=true;

        if (usercode.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Filled is empty", Toast.LENGTH_SHORT).show();
        } else {
            // get user code from database
            df = FirebaseDatabase.getInstance().getReference("user").child(Login.loginid).child("usercode");
            df.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String code = snapshot.getValue(String.class);
                    if(c) {
                        c = false;
                        if (usercode.equals(code)) {
                            openHome();
                            //setOn();
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid Code", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }

    public void openHome() {
        // open home page
        Intent intent = new Intent(this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();

    }



    /*public void loginCheck(String userid){
        if (Login.usertype.equals("advisor")) {
            df = FirebaseDatabase.getInstance().getReference(Login.usertype).child(userid);
            df.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);

                    openHome();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (Login.usertype.equals("student")) {
            df = FirebaseDatabase.getInstance().getReference(Login.usertype).child(userid);
            df.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);

                    openHome();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }*/


}