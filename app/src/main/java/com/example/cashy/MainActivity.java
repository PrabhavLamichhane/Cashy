package com.example.cashy;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class MainActivity extends AppCompatActivity {

    LinearLayout spinAndEarn,scratchAndEarn,playQuiz,profile,logout,watchAds,purchase,invite,withdraw;
    ImageView profilePic;
    TextView uName,uemail,uCoins,uCash,uGems;
    String myUid;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    RewardedAd rewardedAd;

    private InterstitialAd mInterstitialAd;

    int mCoins,mGems;
    float mCash;

    Dialog myDialog,myDialog1;
    ProgressDialog progressDialog;

    LinearLayout totalLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinAndEarn = findViewById(R.id.spinAndEarn);
        scratchAndEarn = findViewById(R.id.scratchAndEarn);
        playQuiz = findViewById(R.id.quiz);
        profile = findViewById(R.id.profile);
        logout = findViewById(R.id.logout);
        watchAds = findViewById(R.id.watch);
        purchase = findViewById(R.id.purchasecr);
        invite = findViewById(R.id.ln_invite_friend);
        withdraw = findViewById(R.id.redeem);

        profilePic = findViewById(R.id.profilepic);
        uName = findViewById(R.id.username);
        uemail = findViewById(R.id.email);
        uCoins = findViewById(R.id.totalCoins);
        uCash = findViewById(R.id.totalCash);
        uGems = findViewById(R.id.totalgems);

        totalLayout = findViewById(R.id.totalLayout);

        progressDialog = new ProgressDialog(this);


        myDialog = new Dialog(this);
        myDialog1 = new Dialog(this);
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        // in splash activity
//        FirebaseDatabase.getInstance().getReference().keepSynced(true);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        /* Enable disk persistence  */
        checkConnection();
        checkUserStatus();


        MobileAds.initialize(MainActivity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        loadAd();

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        progressDialog.setMessage("Loading user info...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();




        if(user != null){

            if(getIntent().getExtras()!=null){
                for (String key:getIntent().getExtras().keySet()){
                    if(key.equals("Quiz")){
                        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                        intent.putExtra("coins",String.valueOf(mCoins));
                        intent.putExtra("cash",String.valueOf(mCash));
                        intent.putExtra("gems",String.valueOf(mGems));
                        startActivity(intent);
                        finish();
                    }else if(key.equals("Spin")){
                        Intent intent = new Intent(MainActivity.this, SpinAndEarnActivity.class);
                        intent.putExtra("coins",String.valueOf(mCoins));
                        intent.putExtra("cash",String.valueOf(mCash));
                        intent.putExtra("gems",String.valueOf(mGems));
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent = new Intent(MainActivity.this, ScratchAndEarnActivity.class);
                        intent.putExtra("coins",String.valueOf(mCoins));
                        intent.putExtra("cash",String.valueOf(mCash));
                        intent.putExtra("gems",String.valueOf(mGems));
                        startActivity(intent);
                        finish();
                    }
                }
            }

            Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        // get data
                        String userName = "" + ds.child("name").getValue();
                        String userEmail = "" + ds.child("email").getValue();
                        String userImage = "" + ds.child("profileImg").getValue();
                        String coins = "" + ds.child("coins").getValue();
                        String cash = "" + ds.child("cash").getValue();
                        String gems = "" + ds.child("gems").getValue();


                        Log.d("info", "onDataChange: " + userName + ".." + userEmail + ".." + userImage);
                        Log.d("m-gem", "onDataChange: "+gems);
                        uName.setText(userName);
                        uemail.setText(userEmail);
                        uCoins.setText(coins);
                        uGems.setText(gems);
//                        uCash.setText(cash+"$");// set upto 2 digit in decimal
                        mCoins = Integer.parseInt(coins);
                        float amount = Float.parseFloat(cash);
                        mCash = Float.parseFloat(cash);
                        mGems = Integer.parseInt(gems);


                        uCash.setText(String.format("%.02f", amount)+"$");


                        try {
                            Picasso.get().load(userImage).into(profilePic);
                        } catch (Exception e) {
                            Picasso.get().load(R.drawable.profile_image).into(profilePic);
                        }
                        progressDialog.dismiss();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        spinAndEarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPopup("Do you want to spend 10 gems?","deduct","Yes","No",10,SpinAndEarnActivity.class);
            }
        });

        scratchAndEarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup("Do you want to spend 10 gems?","deduct","Yes","No",10,ScratchAndEarnActivity.class);

            }
        });

        playQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup("Do you want to spend 15 gems?","deduct","Yes","No",15,QuizActivity.class);

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }else {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }

                mInterstitialAd.setAdListener(new AdListener(){
                    @Override
                    public void onAdClosed() {
                        // Code to be executed when the interstitial ad is closed.
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });



        watchAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("", "onClick: Loading ads...");
                showAd();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup("Are you sure you want to logout?","logout","Yes","No",0,MainActivity.class);

            }
        });


        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // purchase later on

            }
        });

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }else {
                    Intent intent = new Intent(MainActivity.this, Invite.class);
                    startActivity(intent);
                    finish();
                }

                mInterstitialAd.setAdListener(new AdListener(){
                    @Override
                    public void onAdClosed() {
                        // Code to be executed when the interstitial ad is closed.
                        Intent intent = new Intent(MainActivity.this, Invite.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }else {

                    Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
                    intent.putExtra("coins", String.valueOf(mCoins));
                    intent.putExtra("cash", String.valueOf(mCash));
                    intent.putExtra("gems", String.valueOf(mGems));
                    startActivity(intent);
                    finish();
                }

                mInterstitialAd.setAdListener(new AdListener(){
                    @Override
                    public void onAdClosed() {
                        // Code to be executed when the interstitial ad is closed.
                        Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
                        intent.putExtra("coins", String.valueOf(mCoins));
                        intent.putExtra("cash", String.valueOf(mCash));
                        intent.putExtra("gems", String.valueOf(mGems));
                        startActivity(intent);
                        finish();
                    }
                });


            }
        });

    }


    public void checkConnection(){

        ConnectivityManager manager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

//        if(activeNetwork!=null){
//            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
//
//            }
//
//            else if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
//
//            }
//            else{
//                // Not connected
//                totalLayout.setVisibility(View.GONE);
//                showNetworkPopup("No Internet Connection...");
//            }
//        }


        if(activeNetwork == null || !activeNetwork.isConnected() || !activeNetwork.isAvailable()){
            totalLayout.setVisibility(View.GONE);
            progressDialog.dismiss();
            showNetworkPopup("No Internet Connection...");
        }else {
            progressDialog.show();
            totalLayout.setVisibility(View.VISIBLE);
            myDialog1.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkConnection();
        checkUserStatus();
    }



    public void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user !=null){
            //sign in
            myUid = user.getUid();// currently signed in user
        }else{
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void loadAd(){
        this.rewardedAd = new RewardedAd(this,getString(R.string.rewarded_ad_unit_id));
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback(){

            @Override
            public void onRewardedAdLoaded() {
                super.onRewardedAdLoaded();
                Log.d("ads", "onRewardedAdLoaded: Ads loaded successfully");
                watchAds.setEnabled(true);

            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {
                super.onRewardedAdFailedToLoad(loadAdError);
                Log.d("ads", "onRewardedAdLoaded: Ads Failed");

            }
        };

        this.rewardedAd.loadAd(new AdRequest.Builder().build(),adLoadCallback);
    }


    public void showAd(){
        if(this.rewardedAd.isLoaded()){
            RewardedAdCallback adCallback = new RewardedAdCallback() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Here add 3 gem
                    Log.d("reward", "onUserEarnedReward: Won gem");
                    Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
                    HashMap<String, Object> results = new HashMap<>();

                    Log.d("my-gems", "onClick: " + mGems);

                        results.put("gems", mGems + 3);
//                    results.put("cash", mCash - 0.001 * coinAmount);

                        // use reference to update
                        databaseReference.child(user.getUid()).updateChildren(results).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                }

                @Override
                public void onRewardedAdOpened() {
                    super.onRewardedAdOpened();
                }

                @Override
                public void onRewardedAdClosed() {
                    super.onRewardedAdClosed();
                    watchAds.setEnabled(false);
                    loadAd();
                }

                @Override
                public void onRewardedAdFailedToShow(int i) {
                    super.onRewardedAdFailedToShow(i);
                }

                @Override
                public void onRewardedAdFailedToShow(AdError adError) {
                    super.onRewardedAdFailedToShow(adError);
                    Toast.makeText(MainActivity.this, "Please try again...", Toast.LENGTH_SHORT).show();

                }

                @Override
                public int hashCode() {
                    return super.hashCode();
                }

                @Override
                public boolean equals(@Nullable Object obj) {
                    return super.equals(obj);
                }

                @Override
                protected Object clone() throws CloneNotSupportedException {
                    return super.clone();
                }

                @NonNull
                @Override
                public String toString() {
                    return super.toString();
                }

                @Override
                protected void finalize() throws Throwable {
                    super.finalize();
                }
            };

            this.rewardedAd.show(this,adCallback);
        }else{

        }
    }


    public void showPopup(String msg, final String status, String yes, String wrong,final int gemAmount, final Class<? extends Activity> ActivityToOpen){
        myDialog.setContentView(R.layout.result_popup_confirm);
        ImageView cancel = myDialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });

        Button ok = myDialog.findViewById(R.id.OK);
        Button no = myDialog.findViewById(R.id.NO);

        TextView errorMsg = myDialog.findViewById(R.id.errorMsg);
        errorMsg.setText(msg);
        ok.setText(yes);
        no.setText(wrong);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(status.equals("deduct")){
                    // deduct logic

                    Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
                    HashMap<String, Object> results = new HashMap<>();
                    Log.d("my-gems", "onClick: " + mGems);

                    if(mGems>=gemAmount){

                        mGems = mGems - gemAmount;
                        results.put("gems", mGems);
//                    results.put("cash", mCash - 0.001 * coinAmount);


                        // use reference to update
                        databaseReference.child(user.getUid()).updateChildren(results).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                // Put interstital ads

                                if (mInterstitialAd.isLoaded()) {
                                    mInterstitialAd.show();
                                } else {
                                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                                    Intent intent = new Intent(MainActivity.this, ActivityToOpen);
                                    intent.putExtra("coins",String.valueOf(mCoins));
                                    intent.putExtra("cash",String.valueOf(mCash));
                                    intent.putExtra("gems",String.valueOf(mGems));
                                    startActivity(intent);
                                    finish();
                                }

                                mInterstitialAd.setAdListener(new AdListener(){
                                    @Override
                                    public void onAdClosed() {
                                        // Code to be executed when the interstitial ad is closed.
                                        Intent intent = new Intent(MainActivity.this, ActivityToOpen);
                                        intent.putExtra("coins",String.valueOf(mCoins));
                                        intent.putExtra("cash",String.valueOf(mCash));
                                        intent.putExtra("gems",String.valueOf(mGems));
                                        startActivity(intent);
                                        finish();
                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }else{

                        //start

                        myDialog.setContentView(R.layout.result_popup_confirm);
                        ImageView cancel = myDialog.findViewById(R.id.cancel);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                myDialog.dismiss();
                            }
                        });

                        Button ok = myDialog.findViewById(R.id.OK);
                        Button no = myDialog.findViewById(R.id.NO);

                        TextView errorMsg = myDialog.findViewById(R.id.errorMsg);
                        errorMsg.setText("Not enough gems\nDo you want to spend 800 coins?");
                        ok.setText("Yes");
                        no.setText("No");

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                HashMap<String, Object> results = new HashMap<>();
                                Log.d("my-gems", "onClick: " + mGems);

                                Log.d("", "onClick: "+mCoins);
                                if(mCoins>=800) {

                                    mCoins = mCoins - 800;

                                    if (mCash > 0.002 * 800) {
                                        mCash = (float) (mCash - 0.002*800);
                                    }else{
                                        mCash = 0;
                                    }
                                    results.put("coins", mCoins);
                                    results.put("cash", mCash );


                                    // use reference to update
                                    databaseReference.child(user.getUid()).updateChildren(results).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Intent intent = new Intent(MainActivity.this, ActivityToOpen);
                                            intent.putExtra("coins", String.valueOf(mCoins));
                                            intent.putExtra("cash", String.valueOf(mCash));
                                            intent.putExtra("gems", String.valueOf(mGems));
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                                }else{
                                    Toast.makeText(MainActivity.this, "Not enough gem or coins...", Toast.LENGTH_SHORT).show();
                                    // Send to buy coins activity later on
                                    showPopup("You don't have enough gems or coins...\nWatch ads or purchase to get unlimited gems...","notEnough","Watch","Buy",0,MainActivity.class);


                                }
                            }
                        });

                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                myDialog.dismiss();
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

                        //end
                    }

                }else if(status.equals("notEnough")){
                    Toast.makeText(MainActivity.this, "Loading ads...", Toast.LENGTH_SHORT).show();
                    showAd();
                }else{
                    firebaseAuth.signOut();
                    checkUserStatus();
                }
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(status.equals("deduct")){
                        myDialog.dismiss();
                    }else if(status.equals("notEnough")){
                        startActivity(new Intent(MainActivity.this,PurchaseActivity.class));
                        finish();
                    }else{
                        myDialog.dismiss();
                        checkConnection();
                    }
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


    public void showNetworkPopup(String msg){
            myDialog1.setContentView(R.layout.result_popup_error);
            ImageView cancel = myDialog1.findViewById(R.id.cancel);
            Log.d("shown", "showNetworkPopup: ");
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDialog1.dismiss();
                    myDialog1.hide();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recreate();
                            checkConnection();
                        }
                    },2000);
                }
            });

            Button ok = myDialog1.findViewById(R.id.OK);
            TextView errorMsg = myDialog1.findViewById(R.id.errorMsg);
            errorMsg.setText(msg);
            ok.setText("Try Again");

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDialog1.dismiss();
                    myDialog1.hide();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recreate();
                            checkConnection();
                        }
                    },2000);

                }
            });

            myDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog1.setCanceledOnTouchOutside(false);

            myDialog1.show();

//        catch (WindowManager.BadTokenException e) {
//            //use a log message
//            Log.d("TAG", "showPositivePopup: "+e);
//        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
        checkUserStatus();
    }

    static class MyFirebaseInstanceService extends FirebaseMessagingService {
    //
    //    public static int NOTIFICATION_ID =1;
    //    @Override
    //    public void onMessageReceived(RemoteMessage remoteMessage) {
    //        generateNotification(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle());
    //    }
    //
    //    private void generateNotification(String body, String title) {
    //
    //        Intent intent = new Intent(this,MainActivity.class);
    //        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    //
    //        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,
    //                intent,PendingIntent.FLAG_ONE_SHOT);
    //
    //        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    //
    //        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(this)
    //                .setSmallIcon(R.mipmap.ic_launcher)//change app icon later on
    //                .setContentTitle(title)
    //                .setContentText(body)
    //                .setAutoCancel(true)
    //                .setSound(soundUri)
    //                .setContentIntent(pendingIntent);
    //
    //        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    //
    //        if(NOTIFICATION_ID>1073741824){
    //            NOTIFICATION_ID = 0;
    //        }
    //
    //        notificationManager.notify(NOTIFICATION_ID++,notificationCompat.build());
    //
    //
    //
    //    }

        @Override
        public void onMessageReceived(RemoteMessage remoteMessage) {
            super.onMessageReceived(remoteMessage);
            if (remoteMessage.getData().isEmpty()) {

                showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            }

            else
            {
                showNotification(remoteMessage.getData());

            }

        }

        private void showNotification(Map<String, String> data) {
            String title = data.get("title").toString();
            String body = data.get("body").toString();


            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            String NOTIFICATION_CHANNEL_ID = "example.cashy.test";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Notification",
                        NotificationManager.IMPORTANCE_DEFAULT);

                notificationChannel.setDescription("Earn free games.");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.enableLights(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);

            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher) //change this to app icon
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentInfo("Info");

            notificationManager.notify(new Random().nextInt(),notificationBuilder.build());


        }

        private void showNotification(String title, String body) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            String NOTIFICATION_CHANNEL_ID = "example.myapplication.service.test";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Notification",
                        NotificationManager.IMPORTANCE_DEFAULT);

                notificationChannel.setDescription("Team Tarang");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.enableLights(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);

            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher) //change this to app icon
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentInfo("Info");

            notificationManager.notify(new Random().nextInt(),notificationBuilder.build());
        }

        @Override
        public void onNewToken(String s) {
            super.onNewToken(s);

            Log.d("TOKENFIREBASE",s);
        }
    }
}
