package com.example.teledoctor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity
{
    private Button UpdateAccountSettings;
    private EditText userName,userStatus;
    private CircleImageView userProfileImage;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private Toolbar SettingsToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //mAuth = FirebaseAuth.getInstance();


        //currentUserId = mAuth.getCurrentUser().getUid();
        mAuth=FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged( FirebaseAuth firebaseAuth) {


                if (firebaseAuth.getCurrentUser() == null) {

                    SendUserToLoginActivity();

                }
                else

                     currentUserId = mAuth.getCurrentUser().getUid();


            }
        };


        RootRef = FirebaseDatabase.getInstance().getReference();

        InitializeFields();

        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateSettings();
            }
        });

        RetrieveUserInfo();
    }





    private void InitializeFields()
    {
        UpdateAccountSettings =(Button) findViewById(R.id.update_setting);
        userName =(EditText) findViewById(R.id.user_name);
        userStatus =(EditText) findViewById(R.id.profile_status);
        userProfileImage =(CircleImageView) findViewById(R.id.profile_image);
        SettingsToolBar = (Toolbar) findViewById(R.id.menu_settings);
        //setSupportActionBar(SettingsToolBar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowCustomEnabled(true);
        //getSupportActionBar().setTitle("Account Settings");
    }

   private void UpdateSettings() {
       String setUsername = userName.getText().toString();
       String setUserStatus = userStatus.getText().toString();


       if (TextUtils.isEmpty(setUsername)) {
           Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
       }
       if (TextUtils.isEmpty(setUserStatus)) {
           Toast.makeText(this, "Please enter your status", Toast.LENGTH_SHORT).show();
       } else {
          //String currentUserId = mAuth.getCurrentUser().getUid();
           Toast.makeText(this, "Current user"+currentUserId, Toast.LENGTH_SHORT).show();
           /*mAuth=FirebaseAuth.getInstance();
           FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
               @Override
               public void onAuthStateChanged( FirebaseAuth firebaseAuth) {


                   if (firebaseAuth.getCurrentUser() == null) {

                       SendUserToLoginActivity();

                   }
                   else
                       currentUserId = mAuth.getCurrentUser().getUid();


               }
           };*/

           HashMap<String, String> profileMap = new HashMap<>();
           profileMap.put("uid", currentUserId);
           profileMap.put("name", setUsername);
           profileMap.put("status", setUserStatus);
           RootRef.child("Users").child( mAuth.getCurrentUser().getUid()).setValue(profileMap)
                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete( Task<Void> task) {
                           if (task.isSuccessful()) {
                               SendUserToMainActivity();
                               Toast.makeText(SettingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();


                           } else {
                               String message = task.getException().toString();
                               Toast.makeText(SettingsActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                           }

                       }
                   });
           SendUserToMainActivity();

       }
   }




    private void RetrieveUserInfo() {

        //String currentUserId = mAuth.getCurrentUser().getUid();
        mAuth=FirebaseAuth.getInstance();
        /*FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged( FirebaseAuth firebaseAuth) {


                if (firebaseAuth.getCurrentUser() == null) {

                    SendUserToLoginActivity();

                }
                else
                    currentUserId = mAuth.getCurrentUser().getUid();


            }
        };*/



        RootRef.child("Users").child( mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image")))) {
                            String retriveUserName = dataSnapshot.child("name").getValue(String.class);
                            String retriveStatus = dataSnapshot.child("status").getValue(String.class);
                            String retriveProfileImage = dataSnapshot.child("image").getValue().toString();

                            userName.setText(retriveUserName);
                            userStatus.setText(retriveStatus);
                        } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")&& (dataSnapshot.hasChild("status")))) {

                            String retriveUserName = dataSnapshot.child("name").getValue(String.class);
                            String retriveStatus = dataSnapshot.child("status").getValue(String.class);


                            userName.setText(retriveUserName);
                            userStatus.setText(retriveStatus);
                        } else {
                            userName.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingsActivity.this, "Please set and update your profile information", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this,MainActivity.class);//going from login activity to main activity
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//it is for no exit on pressing backbutton
        //it means that if uder is logged in he cannot go back to log in he have to sign out first
        startActivity (mainIntent);
        finish();

    }
    private void SendUserToLoginActivity() {

    Intent logIntent = new Intent(SettingsActivity.this,LoginActivity.class);
    logIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//it is for no exit on pressing backbutton
    startActivity (logIntent);
    finish();
}


}
