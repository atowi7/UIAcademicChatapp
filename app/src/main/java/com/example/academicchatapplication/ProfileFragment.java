package com.example.academicchatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import org.w3c.dom.Text;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    ImageView userimage;
    TextView username;

    DatabaseReference df;

    StorageReference storageRef;

    public final int PROFILE_PICK=101;

    public ProfileFragment() {
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        userimage = view.findViewById(R.id.profile_userimage);
        username = view.findViewById(R.id.profile_username);

        storageRef = FirebaseStorage.getInstance().getReference();
        // get user image from the database
        df = FirebaseDatabase.getInstance().getReference("user").child(Login.loginid);

        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                username.setText(user.getUsername());

                if (user.getUserimage().equals("default")) {
                    userimage.setImageResource(R.drawable.splash_icon1);
                }else{
                    Picasso.get().load(user.getUserimage()).into(userimage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //choose the image
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PROFILE_PICK);
            }
        });

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PROFILE_PICK&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            Uri uri = data.getData();
            uploadTodatabase(uri);
        }else{
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }
    public void uploadTodatabase(Uri uri){
        // upload the image to the firebase storage
     StorageReference imgref = storageRef.child("images/profile_images/p"+(int) (Math.random() * 10));

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Changing your profile image");
        progressDialog.show();
     imgref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
         @Override
         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
             progressDialog.dismiss();
             imgref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                 @Override
                 public void onSuccess(Uri uri) {
                     //set the user image as url
                     DatabaseReference df =FirebaseDatabase.getInstance().getReference("user").child(Login.loginid);
                     df.child("userimage").setValue(uri.toString());
                 }
             });
             Toast.makeText(getContext(), "setting profile image is done", Toast.LENGTH_SHORT).show();
         }
     }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
             progressDialog.dismiss();
             Toast.makeText(getContext(), "setting profile image error", Toast.LENGTH_SHORT).show();
         }
     }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
         @Override
         public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
             progressDialog.setMessage("Processing...");
         }
     });
    }
}