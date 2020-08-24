package com.example.cashy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SpinWheelResultActivity extends AppCompatActivity {

    TextView coinNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin_wheel_result);

        coinNumber = findViewById(R.id.coinValue);
        Intent intent = getIntent();
        String coins = intent.getStringExtra("value");
        coinNumber.setText(coins);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SpinWheelResultActivity.this,MainActivity.class));
        finish();
    }
}
