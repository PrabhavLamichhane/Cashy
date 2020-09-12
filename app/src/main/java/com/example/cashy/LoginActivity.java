package com.example.cashy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText edEmail,edPassword;

    ProgressDialog progressDialog;
    Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edEmail = findViewById(R.id.editText);
        edPassword = findViewById(R.id.textPassword);
        myDialog = new Dialog(this);

        mAuth = FirebaseAuth.getInstance();


        progressDialog = new ProgressDialog(this);
    }

    public void loginUser(View v){
        String email = edEmail.getText().toString();
        String password = edPassword.getText().toString();


        //validate email
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //set error and focus to email
            edEmail.setError("Invalid email");
            edEmail.setFocusable(true);
            return;
        }

        else if(TextUtils.isEmpty(email)){
            //Show error message
            edEmail.setError("Email field should not be empty");
            edEmail.setFocusable(true);
            return;
        }


        else if(TextUtils.isEmpty(password)){
            //Show error message
            edPassword.setError("This field should not be empty");
            edPassword.setFocusable(true);
            return;
        }


        else if(password.length() < 6){
            //Show error message
            edPassword.setError("Password length should be at least 6 characters");
            edPassword.setFocusable(true);
            return;
        }

        // Change this later on
        progressDialog.setMessage("Logging In...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(!email.equals("") && !password.equals("")){
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        // start another activity
                        progressDialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        progressDialog.dismiss();
                        showPopup("Something went wrong. Try Again...");
                        Toast.makeText(LoginActivity.this, "User could not be logged in...", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Error while log in
                    progressDialog.dismiss();
                    showPopup(e.getMessage());
//                    Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void goToRegister(View v){
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
        finish();
    }



    public void forgotPassword(View view) {
        //Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

//        Set linear layout
        LinearLayout linearLayout = new LinearLayout(this);

//        views to set in dialog
        final EditText emailText = new EditText(this);
        emailText.setHint("Email");
        emailText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        // set min width height
        emailText.setMinEms(16);

        linearLayout.addView(emailText);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

//        button recover
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // input email
                String email = emailText.getText().toString().trim();
                beginRecoveryEmail(email);

            }
        });

        //        button cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //dismiss dialog
                dialogInterface.dismiss();
            }
        });

//        show dialog
        builder.create().show();
    }

    private void beginRecoveryEmail(String email) {

        // show progress dialog
        progressDialog.setMessage("Sending email...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            // show sth
                            Toast.makeText(LoginActivity.this,"Email Sent",Toast.LENGTH_SHORT).show();

                        }else{
                            showPopup("Try Again...");
                            Toast.makeText(LoginActivity.this,"Failed...",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //get and show proper error message
                progressDialog.dismiss();
                showPopup(e.getMessage());
//                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser !=null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void showPopup(String msg){
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

}

