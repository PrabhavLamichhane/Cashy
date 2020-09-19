package com.example.cashy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anupkumarpanwar.scratchview.ScratchView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class ScratchAndEarnActivity extends AppCompatActivity {

    ScratchView scratchView;
    Button button;
    TextView reward;
    ImageView rewardIcon;
    //    TextView coinVal;
    String[] items = new String[]{"coin","cash","gem","quiz","scratch"
            ,"spin","ads"};

    String item;
    DatabaseReference databaseReference;
    FirebaseUser user;

    int mCoins,mGems;
    float mCash;

    RewardedAd rewardedAd;

    AdView adView1;
    TextView totalCoins,totalCash,totalGems;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_and_earn);

        scratchView = findViewById(R.id.scratchView);
//        coinVal = findViewById(R.id.coinVal);
        button = findViewById(R.id.getReward1);
        reward = findViewById(R.id.reward);
        rewardIcon = findViewById(R.id.rewardicon);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent1 = getIntent();
        final String coins = intent1.getStringExtra("coins");
        final String cash = intent1.getStringExtra("cash");
        final String gems = intent1.getStringExtra("gems");

        if(coins!=null && cash!=null && gems!=null) {
            mCoins = Integer.parseInt(coins);
            mGems = Integer.parseInt(gems);
            mCash = Float.parseFloat(cash);
        }

        totalCoins = findViewById(R.id.totalCoins);
        totalCash = findViewById(R.id.totalCash);
        totalGems = findViewById(R.id.totalgems);

        float amount1 = Float.parseFloat(cash);
        totalCoins.setText(coins);
        totalCash.setText(String.format("%.02f",amount1));
        totalGems.setText(gems);


        //pick items randomly
        final Random rand = new Random();
        item = items[rand.nextInt(items.length)];

        MobileAds.initialize(ScratchAndEarnActivity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        adView1 = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView1.loadAd(adRequest);

        loadAd();

        // generate coin amount,gem amount and cash amount randomly

//        int randomNum = 10 * rand.nextInt(6);
//        coinVal.setText(Integer.toString(randomNum));

        // after scratch code reveal

        Log.d("won", "onCreate: "+item);

        if(item.equals("coin")){
            // update coin in db
            String amount = String.valueOf(Math.round((Math.random()*(500-100)+100)/10)*10);
            reward.setText(amount);
            Log.d("scratch-coin", "onCreate: ");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.coin, getApplicationContext().getTheme()));
            } else {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.coin));
            }
            performAllUpdate(amount,"coin");
        }else if(item.equals("cash")){
            //update cash in db
            String amount = String.format("%.02f",((Math.random()*(1-0.25)+0.25)/10)*10);
            reward.setTextColor(Color.parseColor("#32CD32"));
            reward.setText("$ "+amount);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.cash, getApplicationContext().getTheme()));
            } else {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.cash));
            }
            performAllUpdate(amount,"cash");
        }else if(item.equals("gem")){
            //udpate gem in db
            String amount = String.valueOf(Math.round((Math.random()*(12-5)+5)/10)*10);
            reward.setTextColor(Color.parseColor("#1f872e"));
            reward.setText(amount);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.gem, getApplicationContext().getTheme()));
            } else {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.gem));
            }
            performAllUpdate(amount,"gem");
        }else if(item.equals("quiz")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.quiz1, getApplicationContext().getTheme()));
            } else {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.quiz1));
            }
            reward.setTextColor(Color.parseColor("#0000ff"));
            reward.setText("Quiz");
        // send to quiz activity

        }else if(item.equals("scratch")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.scratch, getApplicationContext().getTheme()));
            } else {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.scratch));
            }

            reward.setTextColor(Color.parseColor("#9B8706"));
            reward.setText("Scratch");


        }else if(item.equals("spin")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.roulette, getApplicationContext().getTheme()));
            } else {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.roulette));
            }

            reward.setTextColor(Color.parseColor("#dd3333"));
            reward.setText("Spin");


        }else{
            // to watch ads activity
            showAd();
        }



        // change this later on
        scratchView.setRevealListener(new ScratchView.IRevealListener() {
            @Override
            public void onRevealed(ScratchView scratchView) {
                scratchView.setVisibility(View.GONE);
//                Toast.makeText(ScratchAndEarnActivity.this, "Revealed", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {
                if (percent>=0.9) {
//                    Toast.makeText(ScratchAndEarnActivity.this, "Revealed", Toast.LENGTH_SHORT).show();
                    Log.d("Reveal Percentage", "onRevealPercentChangedListener: " + String.valueOf(percent));
                }
            }
        });

    }

    public void performAllUpdate(final String topText, final String item){


        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        HashMap<String, Object> results = new HashMap<>();


        if(item.equals("coin")) {
            int coinAmoount = Integer.parseInt(topText);
            results.put("coins", mCoins + coinAmoount);
            results.put("cash", mCash + 0.001 * coinAmoount);
        }else if(item.equals("cash")){
            float cashAmount = Float.parseFloat(topText);
            results.put("cash", mCash + cashAmount);
        }else{
            // update for gem
            float gemAmount = Float.parseFloat(topText);
            results.put("gems", mGems + gemAmount);
        }

        databaseReference.child(user.getUid()).updateChildren(results)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        Intent intent = new Intent(ScratchAndEarnActivity.this,ResultActivity.class);
//                        intent.putExtra("value",topText);
//                        intent.putExtra("item",item);
//                        intent.putExtra("status","other");
//                        startActivity(intent);
//                        finish();

                        Log.d("db-fetch-success", "onComplete: Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db-fetch-failed", "onFailure: "+e.getMessage());
            }
        });


//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // alert to deduct gems
//                Intent intent = new Intent(ScratchAndEarnActivity.this,MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ScratchAndEarnActivity.this,MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    public void exit(View view) {
//        Log.d("clicked", "exit: none");
        if(item.equals("quiz")){
            Log.d("clicked", "exit: quiz");
            Intent intent = new Intent(ScratchAndEarnActivity.this,QuizActivity.class);
            intent.putExtra("coins",String.valueOf(mCoins));
            intent.putExtra("cash",String.valueOf(mCash));
            intent.putExtra("gems",String.valueOf(mGems));
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }else if(item.equals("spin")){
            Log.d("clicked", "exit: spin");
            Intent intent = new Intent(ScratchAndEarnActivity.this,SpinAndEarnActivity.class);
            intent.putExtra("coins",String.valueOf(mCoins));
            intent.putExtra("cash",String.valueOf(mCash));
            intent.putExtra("gems",String.valueOf(mGems));
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }else if(item.equals("scratch")){
            Log.d("clicked", "exit: scratch");
            Intent intent = new Intent(ScratchAndEarnActivity.this,ScratchAndEarnActivity.class);
            intent.putExtra("coins",String.valueOf(mCoins));
            intent.putExtra("cash",String.valueOf(mCash));
            intent.putExtra("gems",String.valueOf(mGems));
            startActivity(intent);
            finish();
        }else if(item.equals("ads")){
            Log.d("clicked", "exit: ads");
            showAd();
        }else {
            Log.d("clicked", "exit: others");
            Intent intent = new Intent(ScratchAndEarnActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
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
                            Intent intent = new Intent(ScratchAndEarnActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Intent intent = new Intent(ScratchAndEarnActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
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
                    loadAd();
                }

                @Override
                public void onRewardedAdFailedToShow(int i) {
                    super.onRewardedAdFailedToShow(i);
                }

                @Override
                public void onRewardedAdFailedToShow(AdError adError) {
                    super.onRewardedAdFailedToShow(adError);
                    Toast.makeText(ScratchAndEarnActivity.this, "Please try again...", Toast.LENGTH_SHORT).show();

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


    private void loadAd(){
        this.rewardedAd = new RewardedAd(this,getString(R.string.rewarded_ad_unit_id));
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback(){

            @Override
            public void onRewardedAdLoaded() {
                super.onRewardedAdLoaded();
                Log.d("ads", "onRewardedAdLoaded: Ads loaded successfully");

            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {
                super.onRewardedAdFailedToLoad(loadAdError);
                Log.d("ads", "onRewardedAdLoaded: Ads Failed");

            }
        };

        this.rewardedAd.loadAd(new AdRequest.Builder().build(),adLoadCallback);
    }
}
