package com.example.academicchatapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.squareup.picasso.Picasso;



public class DisplayImage_DialogFragment extends DialogFragment {
    String imgurl;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_displayimage,container,false);
        ImageView imageView = v.findViewById(R.id.showingImage);

        // Library for displaying the image from the url
        Picasso.get().load(getImgurl()).resize(1000,1000).into(imageView);

        return v;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) { this.imgurl = imgurl; }
}
