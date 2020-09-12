package com.example.cashy;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cashy.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {


    EditText name,rdEmail,rdPassword;

    FirebaseAuth mAuth;
    DatabaseReference reference;

    ProgressDialog progressDialog;
    Dialog myDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // avoid multiple users registering from same device later on...

        name = findViewById(R.id.name);
        rdEmail = findViewById(R.id.editText);
        rdPassword = findViewById(R.id.editTextPassword);

        //        Progress Dialog
        myDialog = new Dialog(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    public void register(View v){
        final String name1 = name.getText().toString();
        final String email = rdEmail.getText().toString();
        final String password = rdPassword.getText().toString();


        //validate email
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //set error and focus to email
            rdEmail.setError("Invalid email");
            rdEmail.setFocusable(true);
            return;
        }

        else if(TextUtils.isEmpty(email)){
            //Show error message
            rdEmail.setError("Email field should not be empty");
            rdEmail.setFocusable(true);
            return;
        }

        else if(TextUtils.isEmpty(name1)){
            //Show error message
            name.setError("This field should not be empty");
            name.setFocusable(true);
            return;
        }

        else if(!name1.matches("[a-zA-Z ]+")){
            name.setError("This field accepts only alphabets");
            name.setFocusable(true);
            return;
        }

        else if(TextUtils.isEmpty(password)){
            //Show error message
            rdPassword.setError("This field should not be empty");
            rdPassword.setFocusable(true);
            return;
        }

        else if(password.length() < 6){
            //Show error message
            rdPassword.setError("Password length should be at least 6 characters");
            rdPassword.setFocusable(true);
            return;
        }



        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                    //put refer code
                    String refer = email.substring(0,email.lastIndexOf("@"));
                    String referCode = refer.replace(".","");
                    User user = new User(name1,email,firebaseUser.getUid(),"",referCode,1000, 20,(float) 1.0);

                    // Store in db
                    reference.child(firebaseUser.getUid()).setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        finish();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        finish();
                                        Toast.makeText(RegisterActivity.this, "Registration Succeed...", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(RegisterActivity.this, "User could not be registered...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    progressDialog.dismiss();

                    Toast.makeText(RegisterActivity.this, "Something" +
                            "went wrong", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                //Set error
                showPopup(e.getMessage());
//                Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void goToLogin(View v){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    public void showPopup(String msg){
        myDialog.setContentView(R.layout.result_popup_error);
        Button cancel = myDialog.findViewById(R.id.cancel);
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