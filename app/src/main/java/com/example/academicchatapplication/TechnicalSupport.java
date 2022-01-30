package com.example.academicchatapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class TechnicalSupport extends AppCompatActivity {
    EditText editText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.technical_support);

        editText = findViewById(R.id.edittxt_ts);

    }


    public void send(View view){
// method for sending verification code to the user email
        String email = "academicchatapp@gmail.com"; // our email
        String pass = "123j,hwg"; // our password

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
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("academicchatapp@gmail.com"));
            mimeMessage.setSubject("From "+Login.loginid);
            mimeMessage.setText("For support : "+"\n"+editText.getText().toString());

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

            Toast.makeText(getApplicationContext(), "Thanks for your cooperation", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "We have received your message and will work to solve it soon", Toast.LENGTH_SHORT).show();


            } else {
                Toast.makeText(getApplicationContext(), "Sending is failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
