package com.example.cashy;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
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

import static java.lang.Integer.parseInt;

public class PaypalActivity extends AppCompatActivity {

    EditText amount,paypalEmail,paypalName;

    Button redeem;
    int mCoins,mGems;
    float mCash;

    FirebaseUser user;
    DatabaseReference databaseReference;

    Dialog myDialog;
    ImageView backBtn;

    AdView adView1;

    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        amount = findViewById(R.id.editText);
        paypalEmail = findViewById(R.id.editText1);
        paypalName = findViewById(R.id.textName);
        redeem = findViewById(R.id.button);
        backBtn = findViewById(R.id.back);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        myDialog = new Dialog(this);
        Intent intent = getIntent();
        String coins = intent.getStringExtra("coins");
        String cash = intent.getStringExtra("cash");
        String gems = intent.getStringExtra("gems");

        if (coins != null && cash != null && gems != null) {
            mCoins = parseInt(coins);
            mCash = Float.parseFloat(cash);
            mGems = parseInt(gems);
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PaypalActivity.this,PaymentActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amountCash = amount.getText().toString();
                String name = paypalEmail.getText().toString();
                String no = paypalName.getText().toString();

                if(TextUtils.isEmpty(amountCash)){
                    //Show error message
                    amount.setError("This can't be empty...");
                    amount.setFocusable(true);
                    return;
                }else if(TextUtils.isEmpty(name)){
                    paypalEmail.setError("This can't be empty...");
                    paypalEmail.setFocusable(true);
                    return;
                }else if(TextUtils.isEmpty(no)){
                    paypalName.setError("This can't be empty...");
                    paypalName.setFocusable(true);
                    return;
                }else if(!Patterns.EMAIL_ADDRESS.matcher(name).matches()){
                    //set error and focus to email
                    paypalEmail.setError("Invalid email");
                    paypalEmail.setFocusable(true);
                    return;
                }else if(mCash<30){
                    //display negative popup

                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }else {
                        showNegativePopup("You need atleast $30 to redeem your amount...");

                    }

                    mInterstitialAd.setAdListener(new AdListener(){
                        @Override
                        public void onAdClosed() {
                            // Code to be executed when the interstitial ad is closed.
                            showNegativePopup("You need atleast $30 to redeem your amount...");
                        }
                    });

                }
                else{
                    //display positive popup
                    Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
                    HashMap<String, Object> results = new HashMap<>();

                    if(mCoins>Integer.parseInt(amountCash) * 1000) {
                        mCoins = mCoins - Integer.parseInt(amountCash) * 1000;
                    }else{
                        mCoins = 0;
                    }
                    mCash = (float) (mCash - Integer.parseInt(amountCash));
                    results.put("coins", mCoins );
                    results.put("cash", mCash);
                    results.put("redeemed",true);
                    results.put("redeedmedCash",amountCash);
                    results.put("paypalEmail",name);
                    results.put("paypalName",no);


                    databaseReference.child(user.getUid()).updateChildren(results)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
//                        sendToResultActivity("correct");
                                    // show result_popup_positive
                                    if (mInterstitialAd.isLoaded()) {
                                        mInterstitialAd.show();
                                    }else {
                                        showPositivePopup("You will receive payment after 5 buisness days...");
                                    }

                                    mInterstitialAd.setAdListener(new AdListener(){
                                        @Override
                                        public void onAdClosed() {
                                            // Code to be executed when the interstitial ad is closed.
                                            showPositivePopup("You will receive payment after 5 buisness days...");
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });


                }

            }
        });

    }

    public void showNegativePopup(String msg){
        myDialog.setContentView(R.layout.result_popup_error);
        ImageView cancel = myDialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });

        Button ok = myDialog.findViewById(R.id.OK);
        TextView errorMsg = myDialog.findViewById(R.id.errorMsg);
        errorMsg.setText(msg);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.show();

    }


    public void showPositivePopup(String msg){
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
        ok.setText("OK");
        no.setVisibility(View.GONE);
        TextView errorMsg = myDialog.findViewById(R.id.errorMsg);
        errorMsg.setText(msg);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PaypalActivity.this,MainActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PaypalActivity.this,MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

}
