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


public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private List<User> suserList;

    public UsersFragment() {
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
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView = view.findViewById(R.id.recycleview_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        suserList = new ArrayList<>();

        DatabaseReference df = FirebaseDatabase.getInstance().getReference("user").child(Login.loginid).child("usertype");
        df.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.getValue(String.class);
                if(type.equals("advisor")){
                    advisorHome();
                }else if(type.equals("student")){
                    studentHome();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    public void advisorHome(){
        DatabaseReference dfgroup = FirebaseDatabase.getInstance().getReference("group").child(Login.loginid);
        dfgroup.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapparent : snapshot.getChildren()) {
                    for (DataSnapshot snapchild : snapparent.getChildren()) {

                        DatabaseReference dfuser = FirebaseDatabase.getInstance().getReference("user");

                        dfuser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                userList.clear();

                                for (DataSnapshot s : snapshot.getChildren()) {
                                    User user = s.getValue(User.class);

                                    assert user != null;

                                    if (user.getUserid().equals(snapchild.getKey())) {
                                        userList.add(user);
                                    }

                                }

                                for (User user : userList) {
                                    suserList.add(user);
                                    userAdapter = new UserAdapter(getContext(), suserList);
                                    recyclerView.setAdapter(userAdapter);

                                }


                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void studentHome(){
        DatabaseReference dfgroup = FirebaseDatabase.getInstance().getReference("group");

        dfgroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapparent : snapshot.getChildren()) {
                    for (DataSnapshot snapchild : snapparent.getChildren()) {
                        for (DataSnapshot snapchildchild : snapchild.getChildren()) {
                            if (Login.loginid.equals(snapchildchild.getKey())) {
                                DatabaseReference dfuser = FirebaseDatabase.getInstance().getReference("user");

                                dfuser.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        userList.clear();
                                        for (DataSnapshot s : snapshot.getChildren()) {
                                            User user = s.getValue(User.class);

                                            assert user != null;

                                            if (user.getUserid().equals(snapparent.getKey())) {
                                                userList.add(user);
                                                userAdapter = new UserAdapter(getContext(), userList);
                                                recyclerView.setAdapter(userAdapter);
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        }
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

  /*  public void getStdids_AdvisorName(){

        stdids = new ArrayList<>();
        advnames = new ArrayList<>();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference df2 = FirebaseDatabase.getInstance().getReference("student");
        df2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot s : snapshot.getChildren()) {
                    Student student = s.getValue(Student.class);
                    assert student != null;

                   // stdids.add(student.getStdid());
                    //advnames.add(student.getAdvisorname());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }*/


}