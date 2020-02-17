package com.example.teledoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {



    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;

    private FirebaseUser currentUser;

    private FirebaseAuth myAuth;
    private DatabaseReference RootRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myAuth = FirebaseAuth.getInstance();
        currentUser = myAuth.getCurrentUser();//now if the user is logeed in he dont nedd to log again
        RootRef = FirebaseDatabase.getInstance().getReference();


        mToolbar =(Toolbar)findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        TextView toolbar_title=mToolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getSupportActionBar().getTitle());
        getSupportActionBar().setTitle(null);

        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);

        myTabLayout = (TabLayout) findViewById(R.id.main_tab);
        myTabLayout.setupWithViewPager(myViewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //if user is not registered go back to login page
        if(currentUser==null)
        {
            SendUserToLoginActivity();//not logged in go to the logon page
    }
        else
        {

            //VerifyUserExistance();
        }

}

    private void VerifyUserExistance()
    {
        String currentUserId = myAuth.getCurrentUser().getUid();
        RootRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists()))//if the user already create an account can edit username and photo
                {
                    Toast.makeText(MainActivity.this,"Welcome",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    SendUserToSettingActivity();//username and pro pic is not set yet he is a new user
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.option_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.menu_logout);
        {
            myAuth.signOut();
            SendUserToLoginActivity();
        }

        if(item.getItemId() == R.id.menu_find_doctor);
        {

        }

        if(item.getItemId() == R.id.menu_settings);
        {
            SendUserToSettingActivity();
        }
        return true;
    }
    private void SendUserToLoginActivity() {

        Intent logIntent = new Intent(MainActivity.this,LoginActivity.class);
        logIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//it is for no exit on pressing backbutton
        startActivity (logIntent);
        finish();
    }
    private void SendUserToSettingActivity() {

        Intent settingIntent = new Intent(MainActivity.this,SettingsActivity.class);
       //it is for no exit on pressing backbutton without providing username and pro image user cannot access main activity
        startActivity (settingIntent);

    }
}
