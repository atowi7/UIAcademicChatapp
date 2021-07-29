package com.example.academicchatapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {
    RecyclerView recyclerView;
    DatabaseReference df;
    // FirebaseUser firebaseUser;

    List<User> userList;
    List<ChatList> userchatList;

    public ChatsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.recycleview_lastchat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userchatList = new ArrayList<>();

        //Display the users in the chat fragment
        df = FirebaseDatabase.getInstance().getReference("ChatList").child(Login.loginid);
        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userchatList.clear();

                for (DataSnapshot s : snapshot.getChildren()) {
                    ChatList chatList = s.getValue(ChatList.class);
                    userchatList.add(chatList);
                }

                userList = new ArrayList<>();

                df = FirebaseDatabase.getInstance().getReference("user");
                df.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                         //get all ids from user table
                        for (DataSnapshot s : snapshot.getChildren()) {
                            User user = s.getValue(User.class);
                           //get all ids from chatlist table
                            for (ChatList chatList : userchatList) {
                                if (user.getUserid().equals(chatList.getId())) {
                                    userList.add(user);
                                }
                            }
                        }
                        UserAdapter userAdapter = new UserAdapter(getContext(), userList);
                        recyclerView.setAdapter(userAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }


}