package com.example.cashy;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class PaytmActivity extends AppCompatActivity {

    int mCoins,mGems;
    float mCash;

    EditText amount,paytmName,paytmNo;
    Button redeem;
    Dialog myDialog;
    FirebaseUser user;
    DatabaseReference databaseReference;

    AdView adView1;

    ImageView backBtn;

    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytm);


        amount = findViewById(R.id.editText);
        paytmName = findViewById(R.id.editText1);
        paytmNo = findViewById(R.id.textNo);
        redeem = findViewById(R.id.button);
        backBtn = findViewById(R.id.back);

        myDialog = new Dialog(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        Intent intent = getIntent();
        String coins = intent.getStringExtra("coins");
        String cash = intent.getStringExtra("cash");
        String gems = intent.getStringExtra("gems");

        if(coins!=null && cash!=null && gems!=null) {
            mCoins = Integer.parseInt(coins);
            mCash = Float.parseFloat(cash);
            mGems = Integer.parseInt(gems);

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
                startActivity(new Intent(PaytmActivity.this,PaymentActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amountCash = amount.getText().toString();
                String name = paytmName.getText().toString();
                String no = paytmNo.getText().toString();

                 if(TextUtils.isEmpty(amountCash)){
                    //Show error message
                    amount.setError("This can't be empty...");
                    amount.setFocusable(true);
                    return;
                }else if(TextUtils.isEmpty(name)){
                     paytmName.setError("This can't be empty...");
                     paytmName.setFocusable(true);
                     return;
                 }else if(TextUtils.isEmpty(no)){
                     paytmNo.setError("This can't be empty...");
                     paytmNo.setFocusable(true);
                     return;
                 }else if(no.length()!=10){
                     paytmNo.setError("Invalid paytm number...");
                     paytmNo.setFocusable(true);
                     return;
                 }
                 else if(mCash<30){
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

                 }else{
                     //display positive popup
                     //display positive popup
                     Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
                     HashMap<String, Object> results = new HashMap<>();

                     if(mCoins>Integer.parseInt(amountCash) * 1000) {
                         mCoins = mCoins - Integer.parseInt(amountCash) * 1000;
                     }else{
                         mCoins = 0;
                     }

                     Log.d("coins-reedeemed", "onClick: "+mCoins);
                     mCash = (float) (mCash - Integer.parseInt(amountCash));
                     results.put("coins", mCoins );
                     results.put("cash", mCash);
                     results.put("redeemed",true);
                     results.put("redeedmedCash",amountCash);
                     results.put("payTmName",name);
                     results.put("payTmNumber",no);

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
        no.setVisibility(View.GONE);
        ok.setText("OK");
        TextView errorMsg = myDialog.findViewById(R.id.errorMsg);
        errorMsg.setText(msg);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PaytmActivity.this,MainActivity.class));
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
        startActivity(new Intent(PaytmActivity.this,MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
