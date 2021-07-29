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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    Context c;
    List<User> userlist;

    public UserAdapter(Context c, List<User> userlist) {
        this.c = c;
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

        if (user.getUserimage().equals("default")) {
            holder.userimage.setImageResource(R.drawable.splash_icon1);
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
        public ImageView userstatus;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.useritem_name);
            userimage = (ImageView) itemView.findViewById(R.id.useritem_image);
            userstatus = (ImageView) itemView.findViewById(R.id.useritem_status);
        }
    }
}
