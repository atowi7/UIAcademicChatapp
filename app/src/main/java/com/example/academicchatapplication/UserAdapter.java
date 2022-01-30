package com.example.academicchatapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    Context c;
    //String pagetype;
    List<User> userlist;
    String lastmsg;
    int noofmsg;


    public UserAdapter(Context c, List<User> userlist) {
        this.c = c;
        //this.pagetype = pagetype;
        this.userlist = userlist;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(c).inflate(R.layout.useritem, parent, false);
        return new UserAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userlist.get(position);

        holder.username.setText(user.getUsername());

        if (user.getUserimage().equals("default") || user.getUserimage().equals("")) {
            holder.userimage.setImageResource(R.drawable.person_icon_foreground);
        }else{
        Picasso.get().load(user.getUserimage()).into(holder.userimage);
        }

        if(user.getUserstatus().equals("true")){
            holder.userstatus.setImageResource(R.drawable.online_circle_icon_foreground);
        }else{
            holder.userstatus.setImageResource(R.drawable.offline_circle_icon_foreground);
        }

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, Message_p.class);
                intent.putExtra("userid", user.getUserid());
                intent.putExtra("chat", "one");
                c.startActivity(intent);

                //set Message to readable status
                setIsssenToTrue(user.getUserid(),holder.noofmsg);
            }
        });

        //set user last message
        setLastMsg(user.getUserid(),holder.lastmsg);

        //set number of messages
        setnoofmsg(user.getUserid(),holder.noofmsg);
    }

    public void setLastMsg(String userid, TextView lastmsgtxt){
        lastmsg ="default";

        DatabaseReference df = FirebaseDatabase.getInstance().getReference("Chat");

        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getSender().equals(Login.loginid)&&chat.getReceiver().equals(userid)||chat.getSender().equals(userid)&&chat.getReceiver().equals(Login.loginid)){
                        if(chat.getMessagetxt().equals("no_nas")){
                            lastmsg="IMAGE";
                        }else {
                            if(chat.getMessagetxt().length()>10){
                                lastmsg=chat.getMessagetxt().substring(0,10)+"...";
                            }else{
                                lastmsg=chat.getMessagetxt();
                            }

                        }

                        if(lastmsg.equals("default")){
                            lastmsgtxt.setText("no message");
                        }else{
                            lastmsgtxt.setText(lastmsg);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setnoofmsg(String userid, TextView noofmsg_txt){
        DatabaseReference df = FirebaseDatabase.getInstance().getReference("Chat");

        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noofmsg=0;
                noofmsg_txt.setText(""+0);
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getSender().equals(userid)&&chat.getReceiver().equals(Login.loginid)&&chat.getIsseen().equals("false")){
                            noofmsg++;
                        noofmsg_txt.setText(""+noofmsg);
                    }else{
                       // noofmsg_txt.setText(""+0);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setIsssenToTrue(String userid,TextView  noofmsg_txt){
        DatabaseReference df = FirebaseDatabase.getInstance().getReference("Chat");

        df.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getSender().equals(userid)&&chat.getReceiver().equals(Login.loginid)){
                        dataSnapshot.getRef().child("isseen").setValue("true");
                        noofmsg_txt.setText(""+0);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView userimage;
        public TextView lastmsg;
        public TextView noofmsg;
        public ImageView userstatus;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.useritem_name);
            userimage = (ImageView) itemView.findViewById(R.id.useritem_image);
            lastmsg = (TextView) itemView.findViewById(R.id.last_msg);
            noofmsg = (TextView) itemView.findViewById(R.id.noofmsg);
            userstatus = (ImageView) itemView.findViewById(R.id.useritem_status);
        }
    }
}
