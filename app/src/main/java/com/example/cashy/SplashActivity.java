package com.example.cashy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 4000;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.d("sp-", "onCreate: splash");
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        FirebaseDatabase.getInstance().getReference().keepSynced(true);

        Log.d("user123", "onCreate: "+user);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user !=null){
                    //sign in
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();// currently signed in user
                }else{
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }

                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        },SPLASH_TIME_OUT);


    }


}
