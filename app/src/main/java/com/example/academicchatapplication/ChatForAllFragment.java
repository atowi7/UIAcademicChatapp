package com.example.academicchatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatForAllFragment extends Fragment {
    ImageView imageView;
    TextView textView;
    TextView lastmsgtxt;

    public String lastmsg="";
    public ChatForAllFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chatsforall, container, false);

        imageView = view.findViewById(R.id.alluseritem_image);
        textView = view.findViewById(R.id.alluseritem_name);
        lastmsgtxt = view.findViewById(R.id.alluseritem_msgtxt);

        imageView.setImageResource(R.drawable.user_group_icon_foreground);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Message_p.class);
                intent.putExtra("chat","all");
                getContext().startActivity(intent);
            }
        });

        setLastMsg(lastmsgtxt);
        return view;
    }

    public void setLastMsg(TextView lastmsgtxt){
        lastmsg="default";
        DatabaseReference df = FirebaseDatabase.getInstance().getReference("Chat");

        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if((chat.getSender().equals(Login.loginid)||chat.getReceiver().equals(Login.loginid))&& chat.getStatus().equals("all")){
                        if(chat.getMessagetxt().equals("no_nas")){
                            lastmsg="IMAGE";
                        }else {
                            if(chat.getMessagetxt().length()>10){
                                lastmsg=chat.getMessagetxt().substring(0,10)+"...";
                            }else{
                                lastmsg=chat.getMessagetxt();
                            }

                        }
                    }
                }

                if(lastmsg.equals("default")){
                    lastmsgtxt.setText("no message");
                }else{
                    lastmsgtxt.setText(lastmsg);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}