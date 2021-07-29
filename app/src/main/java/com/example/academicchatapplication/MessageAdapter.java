package com.example.academicchatapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

        if (imgurl.equals("default")) {
            holder.userimage.setImageResource(R.drawable.splash_icon1);
        }else{
            Picasso.get().load(imgurl).into( holder.userimage);
        }
        if(!chat.getMessagetxt().equals("empty")) {
            holder.textmsg.setText(chat.getMessagetxt());
            holder.imgmsg.setVisibility(View.GONE);
        }

        if(!chat.getMessageimg().equals("empty")) {
            Picasso.get().load(chat.getMessageimg()).into(holder.imgmsg);
            holder.textmsg.setVisibility(View.GONE);
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

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            textmsg = (TextView) itemView.findViewById(R.id.chat_uname);
            imgmsg = (ImageView) itemView.findViewById(R.id.chat_img);
            userimage = (ImageView) itemView.findViewById(R.id.chat_pimg);
        }
    }
}
