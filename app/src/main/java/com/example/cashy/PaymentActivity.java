package com.example.cashy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class PaymentActivity extends AppCompatActivity {

    CardView paypal,paytm;

    int mCoins,mGems;
    float mCash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        paypal = findViewById(R.id.paypal);
        paytm = findViewById(R.id.paytm);

        Intent intent = getIntent();
        String coins = intent.getStringExtra("coins");
        String cash = intent.getStringExtra("cash");
        String gems = intent.getStringExtra("gems");

        if(coins!=null && cash!=null && gems!=null) {
            mCoins = Integer.parseInt(coins);
            mCash = Float.parseFloat(cash);
            mGems = Integer.parseInt(gems);

        }

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
}
