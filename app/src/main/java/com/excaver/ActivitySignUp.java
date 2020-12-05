package com.excaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ActivitySignUp extends AppCompatActivity {

    private EditText inputName, inputEmail, inputPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserAccount;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initView();
        mAuth = FirebaseAuth.getInstance();
        mUserAccount =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://test1-1000ex.firebaseio.com").child("UserAccount");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });
    }

    private void startRegister(){

        final String name = inputName.getText().toString().trim();
        final String email = inputEmail.getText().toString().trim();
        final String password = inputPassword.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(password)){
            final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                    .setTitleText("Loading");
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.show();
            pDialog.setCancelable(false);

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener( ActivitySignUp.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        uid = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mUserAccount.child(uid);
                        current_user_db.child("name").setValue(name);
                        current_user_db.child("email").setValue(email);
                        current_user_db.child("id").setValue(uid);
                        current_user_db.child("newuser").setValue("true");
                        current_user_db.child("image").setValue("default").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pDialog.dismiss();
                                sendVerificationEmail();
                            }
                        });
                    }

                    else if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
                        pDialog.dismiss();
                        inputEmail.requestFocus();
                        inputEmail.setError("Alamat Email tidak valid!");

                    }

                    else if (password.length() < 8) {
                        pDialog.dismiss();
                        inputPassword.requestFocus();
                        inputPassword.setError("Password minimal 8 karakter!");
                    }

                    else{
                        pDialog.dismiss();
                        Toast.makeText(ActivitySignUp.this, "Akun dengan email tersebut telah terdaftar!", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
        else if (TextUtils.isEmpty(name)){
            Toast.makeText(ActivitySignUp.this, "Masukkan nama terlebih dahulu", Toast.LENGTH_SHORT).show();
            inputName.requestFocus();
        }
        else if (TextUtils.isEmpty(email)){
            Toast.makeText(ActivitySignUp.this, "Masukkan email terlebih dahulu", Toast.LENGTH_SHORT).show();
            inputEmail.requestFocus();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(ActivitySignUp.this, "Masukkan password terlebih dahulu", Toast.LENGTH_SHORT).show();
            inputPassword.requestFocus();
        }
    }

    private void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            showDialog();
                        }
                        else
                        {
                            Toast.makeText(ActivitySignUp.this, "Terjadi kesalahan. Silakan ulangi.", Toast.LENGTH_LONG).show();
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                        }
                    }
                });
    }

    private void showDialog(){
        final String email = inputEmail.getText().toString().trim();

        if(!TextUtils.isEmpty(email)){
            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Verifikasi Email Terkirim")
                    .setContentText("Silakan cek untuk melakukan verifikasi.")
                    .show();
        }
    }

    private void initView() {
        btnRegister = findViewById(R.id.signup_go);
        inputName = (EditText)findViewById(R.id.signup_name);
        inputEmail = (EditText)findViewById(R.id.signup_email);
        inputPassword = (EditText)findViewById(R.id.signup_password);
    }
}