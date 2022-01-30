package com.example.academicchatapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class Login extends AppCompatActivity {
    EditText eduserid;
    public static String loginid;
    public static String usertype;

    SharedPreferences sharedPreferences;

    RadioGroup radioGroup;
    RadioButton rbusertype;
    DatabaseReference df;
    DatabaseReference df2;
    DatabaseReference df3;

    public boolean c=false;

    @Override
    protected void onStart() {
        super.onStart();

        Message_p.active=false;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        eduserid = (EditText) findViewById(R.id.IDEdit);

        radioGroup = (RadioGroup) findViewById(R.id.user_type);

    }

    public void login(View view) {
        String userid = eduserid.getText().toString();

        if (userid.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Filled is empty", Toast.LENGTH_SHORT).show();
        }else {
            Login.loginid = userid;

            checkIfUserExist();

        }
    }

    public void checkIfUserExist(){
        df= FirebaseDatabase.getInstance().getReference("user").child(Login.loginid);
        df.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "ID not found", Toast.LENGTH_SHORT).show();
                }else{
                    checkUserType();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkUserType(){
        //select user type by the user
        int id = radioGroup.getCheckedRadioButtonId();

        if (id == -1) {
            Toast.makeText(getApplicationContext(), "Please choose the type", Toast.LENGTH_SHORT).show();
        } else {
            rbusertype = (RadioButton) findViewById(id);

            //get user type as a doctor or a a student
            Login.usertype = rbusertype.getText().toString();

            //get user type from database by his id
            df2 = FirebaseDatabase.getInstance().getReference("user").child(Login.loginid).child("usertype");
            df2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String type = snapshot.getValue(String.class);

                    if (Login.usertype.equals(type)) {
                        getUerEmail();
                    } else if(!Login.usertype.equals(type)){
                        Toast.makeText(getApplicationContext(), "Please choose different type", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void getUerEmail(){
        // get user email from database
        df3 = FirebaseDatabase.getInstance().getReference("user").child(Login.loginid).child("useremail");
        df3.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.getValue(String.class);

                sendcode(Login.loginid, email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendcode(String userid, String to) {
        // method for sending verification code to the user email
        String email = "academicchatapp@gmail.com"; // our email
        String pass = "123j,hwg"; // our password
        String code = "" + (int) (Math.random() * 10000); // generate random number for the verification code

            // set the code number to the user code attribute
            DatabaseReference df = FirebaseDatabase.getInstance().getReference("user").child(userid);
            df.child("usercode").setValue(code);

            // set the attributes for the email sending process
            Properties properties = new Properties();

            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.auth", "true");


            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(email, pass);
                }
            });

            try {
                // set the attributes for the message that our email will send it with the verification code
                Message mimeMessage = new MimeMessage(session);
                mimeMessage.setFrom(new InternetAddress(email));
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                mimeMessage.setSubject("For verification");
                mimeMessage.setText(code);

                // class using thread for sending the email
                new SendEmail().execute(mimeMessage);

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error while creating message " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
    }

    private class SendEmail extends AsyncTask<Message, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(getApplicationContext(), "We will send Code to your Email", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);

                return "OK";

            } catch (Exception e) {
                return "fail";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("OK")) {
                    Toast.makeText(getApplicationContext(), "Sending Code to your Email is succuss", Toast.LENGTH_SHORT).show();
                    saveUserLogin();
                    openCodeVer();

                } else {
                    Toast.makeText(getApplicationContext(), "Sending Code to your Email is failed", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void openCodeVer() {
        // method for open the code verification page
            Intent intent = new Intent(this, CodeVerification.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("userid", Login.loginid);
            startActivity(intent);

            finish();
    }


    public void saveUserLogin(){
        //save user login if he login for the first time
        sharedPreferences = getSharedPreferences("pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loginid",Login.loginid);
        editor.apply();
    }
}