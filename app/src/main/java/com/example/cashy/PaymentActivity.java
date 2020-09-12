package com.example.cashy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class PaymentActivity extends AppCompatActivity {

    CardView paypal,paytm;

    int mCoins,mGems;
    float mCash;

    AdView adView1;

    ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        paypal = findViewById(R.id.paypal);
        paytm = findViewById(R.id.paytm);
        backBtn = findViewById(R.id.back);

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

        adView1 = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView1.loadAd(adRequest);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentActivity.this,PaypalActivity.class);
                intent.putExtra("coins",String.valueOf(mCoins));
                intent.putExtra("cash",String.valueOf(mCash));
                intent.putExtra("gems",String.valueOf(mGems));
                startActivity(intent);
                finish();
            }
        });

        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentActivity.this,PaytmActivity.class);
                intent.putExtra("coins",String.valueOf(mCoins));
                intent.putExtra("cash",String.valueOf(mCash));
                intent.putExtra("gems",String.valueOf(mGems));
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PaymentActivity.this,MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
