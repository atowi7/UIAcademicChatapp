package com.example.academicchatapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Service extends android.app.Service {

    DatabaseReference df;
    ValueEventListener valueEventListener;
    SharedPreferences sharedPreferences;
    boolean c=false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getLoginid();
        createNotif();

        return super.onStartCommand(intent, flags, startId);
        }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void getLoginid(){
        //Login to the system if the user is not logged out
        sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("loginid","default");

        if(!id.equals("default")){
            Login.loginid=id;
        }
    }
    public void createNotif(){
                  // create notification table on the database
                    df = FirebaseDatabase.getInstance().getReference("Notification").child(Login.loginid);
                    df.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                if(!Message_p.active) {
                                    sentNotification(snap.getKey());
                                }
                            }

                            snapshot.getRef().removeValue();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
    }

    public void sentNotification(String id){
        // build the notification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel("chat", "chatn", NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(notificationChannel);

            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "chat");

            DatabaseReference dfuser = FirebaseDatabase.getInstance().getReference("user").child("username");
            dfuser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = snapshot.getValue(String.class);
                    builder.setContentTitle(username);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            builder.setContentText("NEW MESSAGE");
            builder.setSmallIcon(R.drawable.splash_icon1);
            builder.setAutoCancel(true);

            Intent intent = new Intent(this, Home.class);
            //intent.putExtra("notify","clicked");
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            builder.setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(0, builder.build());
    }
}
