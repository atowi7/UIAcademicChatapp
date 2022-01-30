package com.example.academicchatapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {
    public static boolean r = false;

    @Override
    protected void onStart() {
        super.onStart();
        Message_p.active=false;
        startService(new Intent(this,Service.class));

        /*String notify = getIntent().getStringExtra("notify");
        if(notify!=null){
            //remove notification user id
            Toast.makeText(getApplicationContext(), "notify is deleted", Toast.LENGTH_SHORT).show();
            removeNotificationUserid();
        }else{
            Toast.makeText(getApplicationContext(), "notify null", Toast.LENGTH_SHORT).show();
        }*/

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        setOn();

             //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.vpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewpagerAdapter viewpagerAdapter = new ViewpagerAdapter(getSupportFragmentManager());

        // access database to get the user type to show the specific information for the specific type
        DatabaseReference df_usertype = FirebaseDatabase.getInstance().getReference("user").child(Login.loginid).child("usertype");

        df_usertype.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.getValue(String.class);
                if (type.equals("advisor")) {
                    viewpagerAdapter.addFragments(new UsersFragment(), "Student");
                    //viewpagerAdapter.addFragments(new ChatsFragment(), "Chat");
                    viewpagerAdapter.addFragments(new ChatForAllFragment(), "ForAll");
                    viewpagerAdapter.addFragments(new ProfileFragment(), "Profile");
                } else if (type.equals("student")) {
                    viewpagerAdapter.addFragments(new UsersFragment(), "Myadvisor");
                   // viewpagerAdapter.addFragments(new ChatsFragment(), "Chat");
                    viewpagerAdapter.addFragments(new ProfileFragment(), "Profile");

                }

                viewPager.setAdapter(viewpagerAdapter);

                tabLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // access database to get the user name
        DatabaseReference df_username = FirebaseDatabase.getInstance().getReference("user").child(Login.loginid).child("username");

        df_username.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                Toast.makeText(getApplicationContext(), "Welcome " + name, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void openViewPager(){

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.support:
                Intent intent = new Intent(this, TechnicalSupport.class);
                startActivity(intent);
                break;
            case R.id.logout :// logout from the home activity if the user clicks the logout button

                        Intent mainIntent = new Intent(this, Login.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);

                        stopService(new Intent(this, Service.class));

                       setOff();

                       removeUserLogin();

                       cancelNotification();

                       finish();

                return true;

        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    class ViewpagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragments;
        ArrayList<String> titles;

        public ViewpagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragments(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void setOn(){
        //set the user status to the online status
        DatabaseReference df=FirebaseDatabase.getInstance().getReference("user").child(Login.loginid).child("userstatus");
        df.setValue("true");
    }
    public void setOff(){
        //set the user status to the offline status
        DatabaseReference df=FirebaseDatabase.getInstance().getReference("user").child(Login.loginid).child("userstatus");
        df.setValue("false");
        //set the user code to the default value if the user logout
        DatabaseReference df2=FirebaseDatabase.getInstance().getReference("user").child(Login.loginid).child("usercode");
        df2.setValue("#");
    }

    public void removeUserLogin(){
        //remove the saved login if the user logout
        SharedPreferences sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("loginid");
        editor.apply();
    }

    public void removeNotificationUserid(){
        DatabaseReference  df = FirebaseDatabase.getInstance().getReference("Notification").child(Login.loginid);
        df.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              snapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void cancelNotification(){
        NotificationManagerCompat.from(this).cancelAll();
    }

}