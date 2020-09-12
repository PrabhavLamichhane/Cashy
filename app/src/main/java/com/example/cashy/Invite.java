package com.example.cashy;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cashy.model.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Invite extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private String oppositeUid;

    DatabaseReference reference;

//    Toolbar toolbar;
    TextView referCodeTv;
    Button shareBtn,redeemBtn;

    ImageView backBtn;

    AdView adView1;

    Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        referCodeTv = findViewById(R.id.referCodeTv);
        shareBtn = findViewById(R.id.invite);
        redeemBtn = findViewById(R.id.redeemCode);
        backBtn = findViewById(R.id.back);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        myDialog = new Dialog(this);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adView1 = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView1.loadAd(adRequest);

        //load data
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String referCode = dataSnapshot.child("referCode")
                                .getValue(String.class);
                        referCodeTv.setText(referCode);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Invite.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });


        redeemAvailability();
        
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String referCode = referCodeTv.getText().toString();

                String shareBody = "Hey I am using the best earning app. Join using" +
                        " my invite code to get instantly 20 gems. My invite code is " +
                        referCode+"\n"+"Download from Play Store\n"+"https://play.google.com/store/apps/details?id="
                        +getPackageName();
                // instead of empty use link

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(intent);

            }
        });

        redeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(Invite.this);
                editText.setHint("abc123");

                LinearLayout.LayoutParams layoutParams
                        = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                                ,LinearLayout.LayoutParams.MATCH_PARENT);

                editText.setLayoutParams(layoutParams);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Invite.this);

                alertDialog.setTitle("Redeem code.");

                alertDialog.setView(editText);

                alertDialog.setPositiveButton("Redeem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String inputText = editText.getText().toString();

                        if(TextUtils.isEmpty(inputText)){
                            Toast.makeText(Invite.this, "Please input valid code...", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(inputText.equals(referCodeTv.getText().toString())){
                            Toast.makeText(Invite.this, "You cannot use your own code...", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        redeemQuery(inputText,dialogInterface);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();
            }
        });
    }

    private void redeemAvailability() {

        reference.child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists() && dataSnapshot.hasChild("redeemed")) {


                            boolean isAvailable = dataSnapshot.child("redeemed")
                                    .getValue(Boolean.class);

                            if (isAvailable) {
                                redeemBtn.setVisibility(View.GONE);
                                redeemBtn.setEnabled(false);
                            } else {
                                redeemBtn.setEnabled(true);
                                redeemBtn.setVisibility(View.VISIBLE);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



    }

    private void redeemQuery(String inputText,final DialogInterface dialog) {

        Query query = reference.orderByChild("referCode").equalTo(inputText);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    oppositeUid = ds.getKey();

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.child(oppositeUid).getValue(User.class);
                            int gem = user.getGems();
                            int updatedGem = gem + 20;

                            Log.d("fUser", "onDataChange: "+firebaseUser);
                            User myUser = dataSnapshot.child(firebaseUser.getUid()).getValue(User.class);
                            Log.d("myUser", "onDataChange: "+myUser);
                            int myGem = myUser.getGems();
                            int myUpdatedGem = myGem + 20;

                            HashMap<String,Object> map = new HashMap<>();
                            map.put("gems",updatedGem);

                            HashMap<String,Object> myMap = new HashMap<>();
                            myMap.put("gems",myUpdatedGem);
                            myMap.put("redeemed",true);

                            reference.child(oppositeUid).updateChildren(map);
                            reference.child(firebaseUser.getUid()).updateChildren(myMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            dialog.dismiss();
                                            //show dialog
                                            showPositivePopup(String.valueOf(20));
//                                            Toast.makeText(Invite.this, "Congrats...", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });




                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Invite.this, "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showPositivePopup(String reward1){
        myDialog.setContentView(R.layout.result_popup_positive);
        //ImageView sth = myDialog.findViewById(sth);
        TextView reward = myDialog.findViewById(R.id.rewardAmount);
        ImageView rewardIcon = myDialog.findViewById(R.id.rewardIcon);
        Button getReward = myDialog.findViewById(R.id.getReward);


            reward.setText(reward1);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.gem, getApplicationContext().getTheme()));
            } else {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.gem));
            }
            reward.setTextColor(Color.parseColor("#1f872e"));
            getReward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent quizIntent = new Intent(Invite.this,MainActivity.class);
                    startActivity(quizIntent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }
            });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.setCanceledOnTouchOutside(false);
        try {
            myDialog.show();
        }
        catch (WindowManager.BadTokenException e) {
            //use a log message
            Log.d("TAG", "showPositivePopup: "+e);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Invite.this,MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
