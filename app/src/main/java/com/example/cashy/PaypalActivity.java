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

import androidx.appcompat.app.AppCompatActivity;

public class PaypalActivity extends AppCompatActivity {

    EditText amount,paypalEmail,paypalName;

    Button redeem;
    int mCoins,mGems;
    float mCash;

    Dialog myDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        amount = findViewById(R.id.editText);
        paypalEmail = findViewById(R.id.editText1);
        paypalName = findViewById(R.id.textName);
        redeem = findViewById(R.id.button);

        myDialog = new Dialog(this);
        Intent intent = getIntent();
        String coins = intent.getStringExtra("coins");
        String cash = intent.getStringExtra("cash");
        String gems = intent.getStringExtra("gems");

        if (coins != null && cash != null && gems != null) {
            mCoins = Integer.parseInt(coins);
            mCash = Float.parseFloat(cash);
            mGems = Integer.parseInt(gems);
        }

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
                    showNegativePopup("You need atleast $30 to redeem your amount...");
                }
                else{
                    //display positive popup
                    showPositivePopup("You will receive payment after 5 buisness days...");
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

}
