package com.example.cashy;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

public class SpinAndEarnActivity extends AppCompatActivity {

    LuckyWheelView luckyWheelView;

    ArrayList<LuckyItem> items;

    DatabaseReference databaseReference;
    FirebaseUser user;

    int mCoins,mGems;
    float mCash;

    Dialog myDialog;

    AdView adView1;

    RewardedAd rewardedAd;

    TextView totalCoins,totalCash,totalGems;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin_and_earn);


        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        String coins = intent.getStringExtra("coins");
        String cash = intent.getStringExtra("cash");
        String gems = intent.getStringExtra("gems");

        Log.d("spin-gems", "onCreate: "+gems);

        if(coins!=null && cash!=null && gems!=null) {
            mCoins = Integer.parseInt(coins);
            mCash = Float.parseFloat(cash);
            mGems = Integer.parseInt(gems);
        }

        totalCoins = findViewById(R.id.totalCoins);
        totalCash = findViewById(R.id.totalCash);
        totalGems = findViewById(R.id.totalgems);

        float amount1 = Float.parseFloat(cash);
        totalCoins.setText(coins);
        totalCash.setText(String.format("%.02f",amount1));
        totalGems.setText(gems);

        myDialog = new Dialog(this);

        luckyWheelView = findViewById(R.id.luckywheel);
        items = new ArrayList<>();

        MobileAds.initialize(SpinAndEarnActivity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        adView1 = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView1.loadAd(adRequest);

        loadAd();
        // Adding items to spin wheel
        // user function to remove repeated code
        final LuckyItem item1 = new LuckyItem();
        item1.topText = String.valueOf(Math.round((Math.random()*(500-100)+100)/10)*10);
        item1.icon = R.drawable.coin;
        item1.color = Color.parseColor("#f3c70d");
        items.add(item1);

        final LuckyItem item2 = new LuckyItem();
        item2.topText = String.format("%.02f",((Math.random()*(2-0.25)+0.25)/10)*10)+"$";//(String.format("%.02f", amount)+"$")
        item2.icon = R.drawable.cash;
        item2.color = Color.parseColor("#85bb65");
        items.add(item2);

        final LuckyItem item3 = new LuckyItem();
        item3.topText = "Scratch";
        item3.icon = R.drawable.scratch;
        item3.color = Color.parseColor("#f51b00");
        items.add(item3);

        final LuckyItem item4 = new LuckyItem();
        item4.topText = "Watch";
        item4.icon = R.drawable.vids;
        item4.color = Color.parseColor("#871f78");
        items.add(item4);

        final LuckyItem item5 = new LuckyItem();
        item5.topText = String.valueOf(Math.round((Math.random()*(20-5)+5)/10)*10);
        item5.icon = R.drawable.gem;
        item5.color = Color.parseColor("#1f872e");
        items.add(item5);

        final LuckyItem item6 = new LuckyItem();
        item6.topText = String.valueOf(Math.round((Math.random()*(700-300)+300)/10)*10);
        item6.icon = R.drawable.coin;
        item6.color = Color.parseColor("#f3c70d");
        items.add(item6);

        final LuckyItem item7 = new LuckyItem();
        item7.topText = String.format("%.02f",((Math.random()*(1-0.25)+0.25)/10)*10)+"$";
        item7.icon = R.drawable.cash;
        item7.color = Color.parseColor("#85bb65");
        items.add(item7);

        final LuckyItem item8 = new LuckyItem();
        item8.topText = "Spin";
        item8.icon = R.drawable.roulette;
        item8.color = Color.parseColor("#871f78");
        items.add(item8);

        final LuckyItem item9 = new LuckyItem();
        item9.topText = "Quiz";
        item9.icon = R.drawable.quiz1;
        item9.color = Color.parseColor("#4fb6fe");
        items.add(item9);


        final LuckyItem item10 = new LuckyItem();
        item10.topText = String.valueOf(Math.round((Math.random()*(12-5)+5)/10)*10);
        item10.icon = R.drawable.gem;
        item10.color = Color.parseColor("#1f872e");
        items.add(item10);

        final LuckyItem item11 = new LuckyItem();
        item11.topText = String.format("%.02f",((Math.random()*(2-0.5)+0.5)/10)*10)+"$";
        item11.icon = R.drawable.cash;
        item11.color = Color.parseColor("#85bb65");
        items.add(item11);

        final LuckyItem item12 = new LuckyItem();
        item12.topText = String.valueOf(Math.round((Math.random()*(250-50)+50)/10)*10);
        item12.icon = R.drawable.coin;
        item12.color = Color.parseColor("#f3c70d");
        items.add(item12);









        luckyWheelView.setData(items);
        luckyWheelView.setRound(getRandomRound());


        luckyWheelView.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelected(int index) {
                if(index == 0){
                    performAllUpdate(item1.topText,"coin");
                }

                if(index == 1){
                    performAllUpdate(item2.topText.substring(0,item2.topText.length()-1),"cash");
                }

                if(index == 2){
                    showPositivePopup(item3.topText,"scratch");
                }

                if(index == 3){
                    // directly to watch activity
                    showPositivePopup(item4.topText,"watch");
                }

                if(index == 4){
                    performAllUpdate(item5.topText,"gem");
                }

                if(index == 5){
                    performAllUpdate(item6.topText,"coin");
                }

                if(index == 6){
                    performAllUpdate(item7.topText.substring(0,item7.topText.length()-1),"cash");
                }

                if(index == 7){
                    showPositivePopup(item8.topText,"spin");
                }

                if(index == 8){
                    showPositivePopup(item9.topText,"quiz");
                }

                if(index == 9){
                    performAllUpdate(item10.topText,"gem");
                }

                if(index == 10){
                    performAllUpdate(item11.topText.substring(0,item11.topText.length()-1),"cash");
                }

                if(index == 11){
                    performAllUpdate(item12.topText,"coin");

                }



            }
        });
    }

    private int getRandomRound() {
        Random rand = new Random();
        return rand.nextInt(5)+15;
    }

    public void playGame(View v){
        int index = getRandomIndex();
        luckyWheelView.startLuckyWheelWithRandomTarget(index);
    }

    private int getRandomIndex() {
        int[] ind = new int[]{1,2,3,4};
        int rand = new Random().nextInt(ind.length);
        return ind[rand];
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
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }else {
                            showPositivePopup(topText, item);
                        }

                        mInterstitialAd.setAdListener(new AdListener(){
                            @Override
                            public void onAdClosed() {
                                // Code to be executed when the interstitial ad is closed.
                                showPositivePopup(topText, item);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db-fetch-failed", "onFailure: "+e.getMessage());
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SpinAndEarnActivity.this,MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    public void showPositivePopup(String reward1,String item){
        myDialog.setContentView(R.layout.result_popup_positive);
        //ImageView sth = myDialog.findViewById(sth);
        TextView reward = myDialog.findViewById(R.id.rewardAmount);
        ImageView rewardIcon = myDialog.findViewById(R.id.rewardIcon);
        Button getReward = myDialog.findViewById(R.id.getReward);

        if(item.equals("cash")) {
            reward.setText("$"+reward1);

        }else{
            reward.setText(reward1);
        }

        if(item.equals("coin")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.coin, getApplicationContext().getTheme()));
            } else {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.coin));
            }
            reward.setTextColor(Color.parseColor("#f3c70d"));
            getReward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent quizIntent = new Intent(SpinAndEarnActivity.this,MainActivity.class);
                    startActivity(quizIntent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }
            });
        }else if(item.equals("cash")){
            reward.setTextColor(Color.parseColor("#32CD32"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.cash, getApplicationContext().getTheme()));
            } else {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.cash));
            }
            reward.setTextColor(Color.parseColor("#32CD32"));
            getReward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent quizIntent = new Intent(SpinAndEarnActivity.this,MainActivity.class);
                    startActivity(quizIntent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }
            });
        }else if(item.equals("gem")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.gem, getApplicationContext().getTheme()));
            } else {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.gem));
            }
            reward.setTextColor(Color.parseColor("#1f872e"));
            getReward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent quizIntent = new Intent(SpinAndEarnActivity.this,MainActivity.class);
                    startActivity(quizIntent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }
            });
        }else if(item.equals("quiz")){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.quiz1, getApplicationContext().getTheme()));
            } else {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.quiz1));
            }
            reward.setTextColor(Color.parseColor("#0000ff"));
            getReward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent quizIntent = new Intent(SpinAndEarnActivity.this,QuizActivity.class);
                    quizIntent.putExtra("coins",String.valueOf(mCoins));
                    quizIntent.putExtra("cash",String.valueOf(mCash));
                    quizIntent.putExtra("gems",String.valueOf(mGems));
                    startActivity(quizIntent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }
            });
        }else if(item.equals("scratch")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.scratch, getApplicationContext().getTheme()));
            } else {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.scratch));
            }
            reward.setTextColor(Color.parseColor("#9B8706"));
            getReward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SpinAndEarnActivity.this,ScratchAndEarnActivity.class);
                    intent.putExtra("coins",String.valueOf(mCoins));
                    intent.putExtra("cash",String.valueOf(mCash));
                    intent.putExtra("gems",String.valueOf(mGems));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }
            });
        }else if(item.equals("watch")){
            // Send to watch ads
            reward.setTextColor(Color.parseColor("#FF0000"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.vids, getApplicationContext().getTheme()));
            } else {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.vids));
            }
            showAd();
        }else if (item.equals("spin")){
            reward.setTextColor(Color.parseColor("#dd3333"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.roulette, getApplicationContext().getTheme()));
            } else {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.roulette));
            }

            getReward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SpinAndEarnActivity.this,SpinAndEarnActivity.class);
                    intent.putExtra("coins",String.valueOf(mCoins));
                    intent.putExtra("cash",String.valueOf(mCash));
                    intent.putExtra("gems",String.valueOf(mGems));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }
            });

        }
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
                            Intent intent = new Intent(SpinAndEarnActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Intent intent = new Intent(SpinAndEarnActivity.this,MainActivity.class);
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
                    Toast.makeText(SpinAndEarnActivity.this, "Please try again...", Toast.LENGTH_SHORT).show();

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
