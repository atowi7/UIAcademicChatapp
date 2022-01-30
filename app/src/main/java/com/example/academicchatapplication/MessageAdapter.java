package com.example.academicchatapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    Context c;
    List<Chat> chatlist;
    String imgurl;

    public final static int MSG_LEFT = 0;
    public final static int MSG_RIGHT = 1;

    public MessageAdapter(Context c, List<Chat> chatlist, String imgurl) {
        this.c = c;
        this.chatlist = chatlist;
        this.imgurl = imgurl;
    }


    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_RIGHT) {
            View view = LayoutInflater.from(c).inflate(R.layout.chat_right_side, parent, false);
            return new MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(c).inflate(R.layout.chat_left_side, parent, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        Chat chat = chatlist.get(position);

        if (imgurl.equals("default") || imgurl.equals("")) {
            holder.userimage.setImageResource(R.drawable.person_icon_foreground);
        }else{
            Picasso.get().load(imgurl).into(holder.userimage);
        }

        if(chat.getIsdeleted().equals("true")){
            holder.textmsg.setText("Deleted Message!");
            holder.textmsg.setTypeface(holder.textmsg.getTypeface(), Typeface.ITALIC);
            holder.imgmsg.setVisibility(View.GONE);
            holder.deleteimage.setVisibility(View.GONE);
        }else {
            if (!chat.getMessagetxt().equals("no_nas")) {
                holder.textmsg.setText(chat.getMessagetxt());
                holder.imgmsg.setVisibility(View.GONE);
            }

            if (!chat.getMessageimg().equals("no_sora")) {
                Picasso.get().load(chat.getMessageimg()).into(holder.imgmsg);
                holder.textmsg.setVisibility(View.GONE);
            }
        }

        holder.imgmsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DisplayImage_DialogFragment displayImage_dialogFragment = new DisplayImage_DialogFragment();
                    displayImage_dialogFragment.setImgurl(chat.getMessageimg());
                    FragmentManager fragmentManager = ((AppCompatActivity)c).getSupportFragmentManager();
                    displayImage_dialogFragment.show(fragmentManager,"image_fragmet");
                }
            });

            holder.deleteimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Display Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                    builder.setTitle("ACTION");
                    builder.setMessage("Are you sure?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            DatabaseReference df = FirebaseDatabase.getInstance().getReference("Chat");
                            df.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot s : snapshot.getChildren()) {
                                        Chat chatt = s.getValue(Chat.class);
                                        if(chatt.getDid().equals(chat.getDid())){
                                            s.getRef().child("isdeleted").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    snapshot.getRef().setValue("true");
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


                             dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                }
            });


    }

    @Override
    public int getItemCount() {
        return chatlist.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (chatlist.get(position).getSender().equals(Login.loginid)) {
            return MSG_RIGHT;
        } else {
            return MSG_LEFT;
        }

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView textmsg;
        public ImageView imgmsg;
        public ImageView userimage;
        public ImageView deleteimage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            textmsg = (TextView) itemView.findViewById(R.id.chat_uname);
            imgmsg = (ImageView) itemView.findViewById(R.id.chat_img);
            userimage = (ImageView) itemView.findViewById(R.id.chat_pimg);
            deleteimage = (ImageView) itemView.findViewById(R.id.delete_img);
        }
    }
}
