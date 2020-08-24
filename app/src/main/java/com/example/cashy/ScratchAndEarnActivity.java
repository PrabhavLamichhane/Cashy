package com.example.cashy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anupkumarpanwar.scratchview.ScratchView;
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
   int [] coinsNo = new int[]{150,200,350,500,50,100,250,30,650};
   int [] gemsNo = new int[]{2,3,1,8,6,10,4,12,5};
   float [] cashNo = new float[]{1, (float) 0.8, (float) 1.5, (float) 0.3,3,2, (float) 1.7, (float) 2.4,5};
   DatabaseReference databaseReference;
   FirebaseUser user;

    int mCoins,mGems;
    float mCash;

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

        //pick items randomly
        final Random rand = new Random();
        final String item = items[rand.nextInt(items.length)];

        // generate coin amount,gem amount and cash amount randomly

//        int randomNum = 10 * rand.nextInt(6);
//        coinVal.setText(Integer.toString(randomNum));

        // after scratch code reveal

        Log.d("won", "onCreate: "+item);

        if(item.equals("coin")){
            // update coin in db
            String amount = String.valueOf(coinsNo[rand.nextInt(coinsNo.length)]);
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
            String amount = String.valueOf(cashNo[rand.nextInt(cashNo.length)]);
            reward.setText("$ "+amount);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.cash, getApplicationContext().getTheme()));
            } else {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.cash));
            }
            performAllUpdate(amount,"cash");
        }else if(item.equals("gem")){
            //udpate gem in db
            String amount = String.valueOf(gemsNo[rand.nextInt(gemsNo.length)]);
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



        }else if(item.equals("scratch")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.scratch, getApplicationContext().getTheme()));
            } else {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.scratch));
            }


        }else if(item.equals("spin")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.roulette, getApplicationContext().getTheme()));
            } else {
                rewardIcon.setImageDrawable(getResources().getDrawable(R.drawable.roulette));
            }

        }else{
            // to watch ads activity
        }



        scratchView.setRevealListener(new ScratchView.IRevealListener() {
            @Override
            public void onRevealed(ScratchView scratchView) {
                scratchView.setVisibility(View.GONE);
                Toast.makeText(ScratchAndEarnActivity.this, "Revealed", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {
                if (percent>=0.5) {
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
        finish();
    }

    public void exit(View view) {
        Intent intent = new Intent(ScratchAndEarnActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
