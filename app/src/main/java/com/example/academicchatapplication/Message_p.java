package com.example.academicchatapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Message_p extends AppCompatActivity {
    ImageView userimage, sendimg,shareimg;
    TextView username;
    EditText mEdit;
    Intent intent;
    String userid;
    public String chatstatus;
    public String msgtxt;
   public  String std="";
   public boolean c=false;
    public static boolean active=false;
    public final int CHAT_PICK=102;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<Chat> chatList;

    StorageReference storageRef;

    @Override
    protected void onStart() {
        super.onStart();
        Message_p.active=true;
        startService(new Intent(this,Service.class));
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_p);

        userimage = (ImageView) findViewById(R.id.user_m_image);
        username = (TextView) findViewById(R.id.user_m_name);
        mEdit = (EditText) findViewById(R.id.msend_editt);
        sendimg = (ImageView) findViewById(R.id.send_btn);
        shareimg = (ImageView) findViewById(R.id.share_btn);

        recyclerView = (RecyclerView) findViewById(R.id.recycleview_msg);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        chatstatus = intent.getStringExtra("chat");

        storageRef = FirebaseStorage.getInstance().getReference();

        //set profile
        if(chatstatus.equals("one")){
            DatabaseReference df = FirebaseDatabase.getInstance().getReference("user").child(userid);
            df.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);

                    username.setText(user.getUsername());
                    if (user.getUserimage().equals("default")) {
                        userimage.setImageResource(R.drawable.splash_icon1);
                    }else{
                        Picasso.get().load(user.getUserimage()).into(userimage);
                    }

                    readMsg(Login.loginid, userid, user.getUserimage(),chatstatus);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else if(chatstatus.equals("all")){
            username.setText("ALL STUDENTS");
            readMsg(Login.loginid, "", "default",chatstatus);
        }

        //share the image
        shareimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //choose the image
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,CHAT_PICK);
            }
        });

        //Send the message
        sendimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEdit.equals("")) {
                    Toast.makeText(getApplicationContext(), "Filled is empty", Toast.LENGTH_SHORT).show();
                } else {
                    msgtxt = mEdit.getText().toString();
                    if(chatstatus.equals("one")){
                        DatabaseReference df2 = FirebaseDatabase.getInstance().getReference();
                        HashMap<String, String> minfo = new HashMap<>();
                        minfo.put("messagetxt", msgtxt);
                        minfo.put("messageimg", "empty");
                        minfo.put("status", chatstatus);
                        minfo.put("chatallid", "");
                        minfo.put("sender", Login.loginid);
                        minfo.put("receiver", userid);

                        df2.child("Chat").push().setValue(minfo);

                        //Add the user id to the chatlist table to show the user in the chat page so we can click it to start chatting
                        DatabaseReference df3 = FirebaseDatabase.getInstance().getReference("ChatList").child(Login.loginid).child(userid);
                        df3.child("id").setValue(userid);
                        DatabaseReference df4 = FirebaseDatabase.getInstance().getReference("ChatList").child(userid).child(Login.loginid);
                        df4.child("id").setValue(Login.loginid);

                        //Add the user id to the notification table to receive the notification by the another user
                        DatabaseReference df5 = FirebaseDatabase.getInstance().getReference("Notification").child(userid).child(Login.loginid);
                        df5.child("id").setValue(Login.loginid);

                    }else if(chatstatus.equals("all")){
                        c=true;
                        String chatallid = UUID.randomUUID().toString(); //A unique id to use it in group messages

                        //Get the specific students according to them advisor so we can send the messages to them
                        DatabaseReference dfgroup = FirebaseDatabase.getInstance().getReference("group").child(Login.loginid);
                        dfgroup.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot snapparent : snapshot.getChildren()) {
                                    for (DataSnapshot snapchild : snapparent.getChildren()) {
                                        std = snapchild.getKey();
                                        DatabaseReference df = FirebaseDatabase.getInstance().getReference();
                                        HashMap<String, String> minfo = new HashMap<>();
                                        minfo.put("messagetxt",msgtxt);
                                        minfo.put("messageimg", "empty");
                                        minfo.put("status", chatstatus);
                                        if(c==true){
                                            minfo.put("chatallid",chatallid );
                                            c=false;
                                        }else{
                                            minfo.put("chatallid","null" );
                                        }

                                        minfo.put("sender", Login.loginid);
                                        minfo.put("receiver", std);

                                        df.child("Chat").push().setValue(minfo);

                                        //Add the user id to the chatlist table to show the user in the chat page so we can click it to start chatting
                                        DatabaseReference df2 = FirebaseDatabase.getInstance().getReference("ChatList").child(Login.loginid).child(std);
                                        df2.child("id").setValue(std);
                                        DatabaseReference df3 = FirebaseDatabase.getInstance().getReference("ChatList").child(std).child(Login.loginid);
                                        df3.child("id").setValue(Login.loginid);

                                        //Add the user id to the notification table to receive the notification by the another user
                                        DatabaseReference df4 = FirebaseDatabase.getInstance().getReference("Notification").child(std).child(Login.loginid);
                                        df4.child("id").setValue(Login.loginid);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    }


                //Set the edittext value to empty after sending the message
                mEdit.setText("");
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Get the image url after selecting it from the gallery
        if(requestCode==CHAT_PICK&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            Uri uri = data.getData(); //Image url

            uploadTodatabase(uri);
        }else{
            Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }
    public void uploadTodatabase(Uri uri){
        // upload the image to the firebase storage
        StorageReference imgref = storageRef.child("images/chat_images/c"+(int) (Math.random() * 10));

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Sending image");
        progressDialog.show();

        imgref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if(chatstatus.equals("one")){

                            DatabaseReference df2 = FirebaseDatabase.getInstance().getReference();
                            HashMap<String, String> minfo = new HashMap<>();
                            minfo.put("messagetxt", "empty");
                            minfo.put("messageimg", uri.toString());
                            minfo.put("status", chatstatus);
                            minfo.put("sender", Login.loginid);
                            minfo.put("receiver", userid);

                            df2.child("Chat").push().setValue(minfo);

                            DatabaseReference df3 = FirebaseDatabase.getInstance().getReference("ChatList").child(Login.loginid).child(userid);
                            df3.child("id").setValue(userid);

                            DatabaseReference df4 = FirebaseDatabase.getInstance().getReference("ChatList").child(userid).child(Login.loginid);
                            df4.child("id").setValue(Login.loginid);

                            DatabaseReference df5 = FirebaseDatabase.getInstance().getReference("Notification").child(userid).child(Login.loginid);
                            df5.child("id").setValue(Login.loginid);

                        }else if(chatstatus.equals("all")){
                            c=true;
                            String chatallid = UUID.randomUUID().toString();

                            DatabaseReference dfgroup = FirebaseDatabase.getInstance().getReference("group").child(Login.loginid);
                            dfgroup.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    for (DataSnapshot snapparent : snapshot.getChildren()) {
                                        for (DataSnapshot snapchild : snapparent.getChildren()) {
                                            DatabaseReference df = FirebaseDatabase.getInstance().getReference();
                                             std = snapchild.getKey();
                                            HashMap<String, String> minfo = new HashMap<>();
                                            minfo.put("messagetxt", "empty");
                                            minfo.put("messageimg", uri.toString());
                                            minfo.put("status", chatstatus);
                                            if(c==true){
                                                minfo.put("chatallid",chatallid );
                                                c=false;
                                            }else{
                                                minfo.put("chatallid","null" );
                                            }

                                            minfo.put("sender", Login.loginid);
                                            minfo.put("receiver", std);

                                            df.child("Chat").push().setValue(minfo);

                                            DatabaseReference df2 = FirebaseDatabase.getInstance().getReference("ChatList").child(Login.loginid).child(std);
                                            df2.child("id").setValue(std);

                                            DatabaseReference df3 = FirebaseDatabase.getInstance().getReference("ChatList").child(std).child(Login.loginid);
                                            df3.child("id").setValue(Login.loginid);

                                            DatabaseReference df4 = FirebaseDatabase.getInstance().getReference("Notification").child(std).child(Login.loginid);
                                            df4.child("id").setValue(Login.loginid);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                });
               progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
             progressDialog.setMessage("Processing...");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error in sending image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void readMsg(String id, String userid, String msgurl,String chatstatus) {
       // method for reading the message
        chatList = new ArrayList<>();

            DatabaseReference df = FirebaseDatabase.getInstance().getReference("Chat");

            df.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    chatList.clear();

                    for (DataSnapshot s : snapshot.getChildren()) {
                        Chat chat = s.getValue(Chat.class);
                        String chatallid =chat.getChatallid();

                            if (chatstatus.equals("one") && (chat.getSender().equals(id) && chat.getReceiver().equals(userid)) || (chat.getSender().equals(userid) && chat.getReceiver().equals(id))) {

                                chatList.add(chat);

                        }else if(chatstatus.equals("all")&&chat.getSender().equals(id) && chat.getStatus().equals("all") && !chatallid.equals("null")){
                                    chatList.add(chat);
                        }

                        messageAdapter = new MessageAdapter(Message_p.this, chatList, msgurl);

                        recyclerView.setAdapter(messageAdapter);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

}