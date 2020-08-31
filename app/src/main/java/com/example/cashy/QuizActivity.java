package com.example.cashy;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cashy.model.Question;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {


    Button b1,b2,b3,b4;
    TextView t1Question,timerTxt,score;
    int total = 0,correct = 0,wrong = 0,computerCount = 0;
    DatabaseReference reference , databaseReference;
    FirebaseUser user;

    int mCoins,mGems;
    float mCash;

    Collection<Integer> alreadyChosen;
    Dialog myDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        b1 = findViewById(R.id.button1);
        b2 = findViewById(R.id.button2);
        b3 = findViewById(R.id.button3);
        b4 = findViewById(R.id.button4);


        t1Question =findViewById(R.id.questionText);
        timerTxt = findViewById(R.id.timer);

        score = findViewById(R.id.score);

        myDialog = new Dialog(this);

        Intent intent = getIntent();
        String coins = intent.getStringExtra("coins");
        String cash = intent.getStringExtra("cash");
        String gems = intent.getStringExtra("gems");

        if(coins!=null && cash!=null && gems!=null) {
            mCoins = Integer.parseInt(coins);
            mCash = Float.parseFloat(cash);
            mGems = Integer.parseInt(gems);

        }
        reference = FirebaseDatabase.getInstance().getReference().child("Questions");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();

        score.setVisibility(View.VISIBLE);


        alreadyChosen = new HashSet<Integer>();
        updateQuestion();
        reverseTime(30,timerTxt);
    }


   /*

            This piece of code is for multiple question.

            If possible this will be implemented later on...
    */


    private void updateQuestion() {

        // button.isSelected

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                total++;

                long totalDbQuestion = dataSnapshot.getChildrenCount();

                // generate unique random number between 1 and totalDbQuestion
                Random rand = new Random();

                int rand_int1 = rand.nextInt((int) ((totalDbQuestion - 1) + 1)) + 1;// (max-min)+1)+min max-no of ques in db

                while (alreadyChosen.contains(rand_int1)){
                    rand_int1 = rand.nextInt((int) ((totalDbQuestion - 1) + 1)) + 1;
                }
                alreadyChosen.add(rand_int1);
                Log.d("hehe", "updateQuestion: "+rand_int1);

                reference.child(String.valueOf(rand_int1)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        final Question question = dataSnapshot.getValue(Question.class);

                        t1Question.setText(question.getQuestion());
                        Log.d("check", "onDataChange: "+question.getQuestion());

                        b1.setText(question.getOption1());
                        b2.setText(question.getOption2());
                        b3.setText(question.getOption3());
                        b4.setText(question.getOption4());


                        // check answer
                        b1.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View view) {
                                if (b1.getText().toString().equals(question.getAnswer())){

                                    b1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                    Log.d("hello", "onClick: "+ColorStateList.valueOf(Color.GREEN));
                                    correct++;
                                    Toast.makeText(getApplicationContext(),"Correct answer",Toast.LENGTH_SHORT).show();
                                    score.setText(correct+"/"+total);
                                    Handler handler = new Handler();

                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //out of total
                                            b1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            updateQuestion();
                                        }
                                    },2000);
                                }else{

                                    // wrong answer

                                    b1.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                    Toast.makeText(getApplicationContext(),"Incorrect",Toast.LENGTH_SHORT).show();
                                    wrong++;
                                    score.setText(correct+"/"+total);

                                    if (b2.getText().toString().equals(question.getAnswer())) {
                                        b2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                    }else if (b3.getText().toString().equals(question.getAnswer())){
                                        b3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                    }else if(b4.getText().toString().equals(question.getAnswer())){
                                        b4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                    }

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            b1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            b2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            b3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            b4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));

                                            updateQuestion();
                                        }
                                    },2000);

                                }
                            }
                        });

                        b2.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View view) {
                                if (b2.getText().toString().equals(question.getAnswer())){
//                                b1.setBackgroundColor(Color.GREEN);
                                    b2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                    correct++;
                                    Toast.makeText(getApplicationContext(),"Correct answer",Toast.LENGTH_SHORT).show();
                                    score.setText(correct+"/"+total);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            b2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            updateQuestion();
                                        }
                                    },2000);
                                }else{
                                    b2.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                    Toast.makeText(getApplicationContext(),"Incorrect",Toast.LENGTH_SHORT).show();
                                    wrong++;
                                    score.setText(correct+"/"+total);
                                    if (b1.getText().toString().equals(question.getAnswer())) {
                                        b1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                    }else if (b3.getText().toString().equals(question.getAnswer())){
                                        b3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                    }else if(b4.getText().toString().equals(question.getAnswer())){
                                        b4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                    }

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            b1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            b2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            b3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            b4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            updateQuestion();
                                        }
                                    },2000);

                                }
                            }
                        });

                        b3.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View view) {
                                if (b3.getText().toString().equals(question.getAnswer())){
//                                b1.setBackgroundColor(Color.GREEN);
                                    b3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                    correct++;
                                    Toast.makeText(getApplicationContext(),"Correct answer",Toast.LENGTH_SHORT).show();
                                    score.setText(correct+"/"+total);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            b3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            updateQuestion();
                                        }
                                    },2000);
                                }else{
                                    b3.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                    Toast.makeText(getApplicationContext(),"Incorrect",Toast.LENGTH_SHORT).show();
                                    wrong++;
                                    score.setText(correct+"/"+total);
                                    if (b2.getText().toString().equals(question.getAnswer())) {
                                        b2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                    }else if (b1.getText().toString().equals(question.getAnswer())){
                                        b1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                    }else if(b4.getText().toString().equals(question.getAnswer())){
                                        b4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                    }

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            // previous color//

                                            b1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            b2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            b3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            b4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            updateQuestion();
                                        }
                                    },2000);

                                }
                            }
                        });

                        b4.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View view) {
                                if (b4.getText().toString().equals(question.getAnswer())){
//                                b1.setBackgroundColor(Color.GREEN);
                                    b4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                    correct++;
                                    Toast.makeText(getApplicationContext(),"Correct answer",Toast.LENGTH_SHORT).show();
                                    score.setText(correct+"/"+total);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            b4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            // CHange here
                                            updateQuestion();
                                        }
                                    },2000);
                                }else{
                                    b4.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                    Toast.makeText(getApplicationContext(),"Incorrect",Toast.LENGTH_SHORT).show();
                                    wrong++;
                                    score.setText(correct+"/"+total);
                                    // background tint list not working
                                    if (b2.getText().toString().equals(question.getAnswer())) {
                                        b2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                    }else if (b3.getText().toString().equals(question.getAnswer())){
                                        b3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                    }else if(b1.getText().toString().equals(question.getAnswer())){
                                        b1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                    }

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            b1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            b2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            b3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            b4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E99C03")));
                                            updateQuestion();
                                        }
                                    },2000);

                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // change question



    }




     //For single question

//    private void updateQuestion() {
//
//
//
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                long totalDbQuestion = dataSnapshot.getChildrenCount();
//
//                Random rand = new Random();
//                int rand_int1 = rand.nextInt((int) ((totalDbQuestion - 1) + 1)) + 1;// (max-min)+1)+min max-no of ques in db
//                Log.d("hehe", "updateQuestion: "+rand_int1);
//
//                reference.child(String.valueOf(rand_int1)).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//
//                        final Question question = dataSnapshot.getValue(Question.class);
//
//                        t1Question.setText(question.getQuestion());
//                        Log.d("check", "onDataChange: "+question.getQuestion());
//
//                        b1.setText(question.getOption1());
//                        b2.setText(question.getOption2());
//                        b3.setText(question.getOption3());
//                        b4.setText(question.getOption4());
//
//
//                        b1.setOnClickListener(new View.OnClickListener() {
//                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                            @Override
//                            public void onClick(View view) {
//                                if (b1.getText().toString().equals(question.getAnswer())){
//                                    b1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                                    Log.d("hello", "onClick: "+ColorStateList.valueOf(Color.GREEN));
//                                    correct++;
//                                    Toast.makeText(getApplicationContext(),"Correct answer",Toast.LENGTH_SHORT).show();
//                                    Handler handler = new Handler();
//
//                                    handler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            performAllUpdate();
//                                        }
//                                    },2000);
//                                }else{
//                                    b1.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
//                                    Toast.makeText(getApplicationContext(),"Incorrect",Toast.LENGTH_SHORT).show();
//                                    wrong++;
//
//                                    if (b2.getText().toString().equals(question.getAnswer())) {
//                                        b2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
////                                    sendToResultActivity("incorrect");
////                                    show result_popup_negative
//                                        showNegativePopup();
//                                    }else if (b3.getText().toString().equals(question.getAnswer())){
//                                        b3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
////                                    sendToResultActivity("incorrect");
//                                        showNegativePopup();
//                                    }else if(b4.getText().toString().equals(question.getAnswer())){
//                                        b4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
////                                    sendToResultActivity("incorrect");
//                                        showNegativePopup();
//
//                                    }
//
//                                }
//                            }
//                        });
//
//                        b2.setOnClickListener(new View.OnClickListener() {
//                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                            @Override
//                            public void onClick(View view) {
//                                if (b2.getText().toString().equals(question.getAnswer())){
////                                b1.setBackgroundColor(Color.GREEN);
//                                    b2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                                    correct++;
//                                    Toast.makeText(getApplicationContext(),"Correct answer",Toast.LENGTH_SHORT).show();
//                                    Handler handler = new Handler();
//                                    handler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            performAllUpdate();
//                                        }
//                                    },2000);
//                                }else{
//                                    b2.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
//                                    Toast.makeText(getApplicationContext(),"Incorrect",Toast.LENGTH_SHORT).show();
//                                    wrong++;
//                                    if (b1.getText().toString().equals(question.getAnswer())) {
////                                    sendToResultActivity("incorrect");
//                                        showNegativePopup();
//                                    }else if (b3.getText().toString().equals(question.getAnswer())){
////                                    sendToResultActivity("incorrect");
//                                        showNegativePopup();
//                                    }else if(b4.getText().toString().equals(question.getAnswer())){
////                                    sendToResultActivity("incorrect");
//                                        showNegativePopup();
//
//                                    }
//
//
//
//                                }
//                            }
//                        });
//
//                        b3.setOnClickListener(new View.OnClickListener() {
//                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                            @Override
//                            public void onClick(View view) {
//                                if (b3.getText().toString().equals(question.getAnswer())){
////                                b1.setBackgroundColor(Color.GREEN);
//                                    b3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                                    correct++;
//                                    Toast.makeText(getApplicationContext(),"Correct answer",Toast.LENGTH_SHORT).show();
//                                    Handler handler = new Handler();
//                                    handler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            performAllUpdate();
//                                        }
//                                    },2000);
//                                }else{
//                                    b3.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
//                                    Toast.makeText(getApplicationContext(),"Incorrect",Toast.LENGTH_SHORT).show();
//                                    wrong++;
//                                    if (b2.getText().toString().equals(question.getAnswer())) {
////                                    sendToResultActivity("incorrect");
//                                        showNegativePopup();
//                                    }else if (b1.getText().toString().equals(question.getAnswer())){
////                                    sendToResultActivity("incorrect");
//                                        showNegativePopup();
//                                    }else if(b4.getText().toString().equals(question.getAnswer())){
////                                    sendToResultActivity("incorrect");
//                                        showNegativePopup();
//
//                                    }
//
//
//                                }
//                            }
//                        });
//
//                        b4.setOnClickListener(new View.OnClickListener() {
//                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                            @Override
//                            public void onClick(View view) {
//                                if (b4.getText().toString().equals(question.getAnswer())){
////                                b1.setBackgroundColor(Color.GREEN);
//                                    b4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                                    correct++;
//                                    Toast.makeText(getApplicationContext(),"Correct answer",Toast.LENGTH_SHORT).show();
//                                    Handler handler = new Handler();
//                                    handler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            performAllUpdate();
//                                            // CHange here
//                                        }
//                                    },2000);
//                                }else{
//                                    b4.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
//                                    Toast.makeText(getApplicationContext(),"Incorrect",Toast.LENGTH_SHORT).show();
//                                    wrong++;
//                                    // background tint list not working
//                                    if (b2.getText().toString().equals(question.getAnswer())) {
//                                        b2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
////                                    sendToResultActivity("incorrect");
//                                        showNegativePopup();
//
//                                    }else if (b3.getText().toString().equals(question.getAnswer())){
//                                        b3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
////                                    sendToResultActivity("incorrect");
//                                        showNegativePopup();
//
//                                    }else if(b1.getText().toString().equals(question.getAnswer())){
//                                        b1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
////                                    sendToResultActivity("incorrect");
//                                        showNegativePopup();
//
//                                    }
//
//
//                                }
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//
//    }



    public void reverseTime(int seconds, final TextView tv){
        new CountDownTimer(seconds * 1000 + 1000, 1000) {
            @Override
            public void onTick(long l) {
                int seconds = (int) (l/1000);
                int minutes = seconds/60;
                seconds = seconds%60;
                tv.setText(String.format("%02d",minutes)
                            +":"+String.format("%02d",seconds));
            }

            @Override
            public void onFinish() {
                tv.setText("00:00");
                tv.setTextColor(Color.RED);
                // time off play again on quiz activity later on
//                startActivity(new Intent(QuizActivity.this,MainActivity.class));
//                finish();
                performAllUpdate();
//                sendToResultActivity("incorrect");
            }
        }.start();

    }

    public void sendToResultActivity(String answerStatus){
        Intent myIntent = new Intent(QuizActivity.this,ResultActivity.class);

        myIntent.putExtra("status",answerStatus);
        myIntent.putExtra("sender","quiz");

        startActivity(myIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(QuizActivity.this,MainActivity.class));
        finish();
    }

    public void performAllUpdate(){


        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        HashMap<String, Object> results = new HashMap<>();

        mCoins = mCoins + correct * 100;
        mCash = (float) (mCash + 0.001*mCoins);
        results.put("coins", mCoins );
        results.put("cash", mCash);

        databaseReference.child(user.getUid()).updateChildren(results)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        sendToResultActivity("correct");
                        // show result_popup_positive
                        showPositivePopup(correct*100);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


    public void showPositivePopup(int coins){
        myDialog.setContentView(R.layout.result_popup_positive);
        //ImageView sth = myDialog.findViewById(sth);;
        Button reward = myDialog.findViewById(R.id.getReward);
        TextView rewardAmount = myDialog.findViewById(R.id.rewardAmount);
        TextView score = myDialog.findViewById(R.id.score);
        score.setText(correct+"/"+total);
        rewardAmount.setText(coins+"");
        reward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizActivity.this,MainActivity.class));
                finish();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.show();
    }

    public void showNegativePopup(){
        myDialog.setContentView(R.layout.result_popup_negative);
        Button tryAgain = myDialog.findViewById(R.id.tryagain);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizActivity.this,MainActivity.class));
                finish();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.show();
    }

}
