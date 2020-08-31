package com.example.cashy;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PaytmActivity extends AppCompatActivity {

    int mCoins,mGems;
    float mCash;

    EditText amount,paytmName,paytmNo;
    Button redeem;
    Dialog myDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytm);


        amount = findViewById(R.id.editText);
        paytmName = findViewById(R.id.editText1);
        paytmNo = findViewById(R.id.textno);
        redeem = findViewById(R.id.button);

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
                 }else if(mCash<30){
                     //display negative popup
                     showNegativePopup("You need atleast $30 to redeem your amount...");
                 }else{
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
