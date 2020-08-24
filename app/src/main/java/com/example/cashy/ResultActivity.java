package com.example.cashy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ResultActivity extends AppCompatActivity {


    CardView correctCardView,incorrectCardView,otherCardView;
    TextView val;
    ImageView icon;
    Button again;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;

    int mCoins,mGems;
    float mCash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);



        Intent intent = getIntent();
        String ansStatus = intent.getStringExtra("status");
        String value = intent.getStringExtra("value");
        String item = intent.getStringExtra("item");
        String coins = intent.getStringExtra("coins");
        String cash = intent.getStringExtra("cash");
        String gems = intent.getStringExtra("gems");
        final String sender = intent.getStringExtra("sender");

        correctCardView = findViewById(R.id.correct);
        incorrectCardView = findViewById(R.id.incorrect);
        otherCardView = findViewById(R.id.other);
        val = findViewById(R.id.val);
        icon = findViewById(R.id.icon);
        again = findViewById(R.id.again);



        if(coins!=null && cash!=null && gems!=null) {
            mCoins = Integer.parseInt(coins);
            mCash = Float.parseFloat(cash);
            mGems = Integer.parseInt(gems);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        if(ansStatus.equals("correct")){
            correctCardView.setVisibility(View.VISIBLE);
            otherCardView.setVisibility(View.GONE);
            incorrectCardView.setVisibility(View.GONE);
        }else if(ansStatus.equals("incorrect")){
            correctCardView.setVisibility(View.GONE);
            otherCardView.setVisibility(View.GONE);
            incorrectCardView.setVisibility(View.VISIBLE);
        }else if(ansStatus.equals("other")){
            correctCardView.setVisibility(View.GONE);
            incorrectCardView.setVisibility(View.GONE);
            otherCardView.setVisibility(View.VISIBLE);
            val.setText(value);

            if(item.equals("coin")){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.coin, getApplicationContext().getTheme()));
                } else {
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.coin));
                }
            }else if(item.equals("cash")){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.cash, getApplicationContext().getTheme()));
                } else {
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.cash));
                }
            }else if(item.equals("gem")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.gem, getApplicationContext().getTheme()));
                } else {
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.gem));
                }
            }else if(item.equals("quiz")){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.quiz1, getApplicationContext().getTheme()));
                } else {
                    icon.setImageDrawable(getResources().getDrawable(R.drawable.quiz1));
                }

                    again.setText("Play Quiz");
                    again.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent quizIntent = new Intent(ResultActivity.this,QuizActivity.class);
                            startActivity(quizIntent);
                            finish();
                        }
                    });
                }
            }else if(item.equals("scratch")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                icon.setImageDrawable(getResources().getDrawable(R.drawable.scratch, getApplicationContext().getTheme()));
            } else {
                icon.setImageDrawable(getResources().getDrawable(R.drawable.scratch));
            }

            again.setText("Scratch and Earn");
            again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent quizIntent = new Intent(ResultActivity.this,ScratchAndEarnActivity.class);
                    startActivity(quizIntent);
                    finish();
                }
            });
            }else if(item.equals("watch")){
                // Send to watch ads


            }else if (item.equals("spin")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                icon.setImageDrawable(getResources().getDrawable(R.drawable.roulette, getApplicationContext().getTheme()));
            } else {
                icon.setImageDrawable(getResources().getDrawable(R.drawable.roulette));
            }

            again.setText("Spin and Earn");
            again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent quizIntent = new Intent(ResultActivity.this,SpinAndEarnActivity.class);
                    startActivity(quizIntent);
                    finish();
                }
            });

            }



        again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sender.equals("quiz")){
                    showCoinAlert("Play","Do you want to spend 15 gems?"
                            ,15,QuizActivity.class);
                }else if(sender.equals("spin")){
                    showCoinAlert("Spin","Do you want to spend 8 gems?"
                            ,8,SpinAndEarnActivity.class);
                }else{
                    showCoinAlert("Scratch","Do you want to spend 6 gems?"
                            ,6,ScratchAndEarnActivity.class);
                }
            }
        });


        }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ResultActivity.this,MainActivity.class));
        finish();
    }

    public void showCoinAlert(String msg, String msg1, final int gemAmount,  final Class<? extends Activity> ActivityToOpen){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(msg);
        builder.setMessage(msg1);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
                HashMap<String, Object> results = new HashMap<>();
                Log.d("my-gems", "onClick: " + mGems);

                if(mGems>=gemAmount){
                    results.put("gems", mGems - gemAmount);
//                    results.put("cash", mCash - 0.001 * coinAmount);

                    // use reference to update
                    databaseReference.child(user.getUid()).updateChildren(results).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(ResultActivity.this, ActivityToOpen);
                            intent.putExtra("coins",String.valueOf(mCoins));
                            intent.putExtra("cash",String.valueOf(mCash));
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }else{
                    Toast.makeText(ResultActivity.this, "Not enough gem...", Toast.LENGTH_SHORT).show();
                    // Send to buy coins activity later on
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }
}
