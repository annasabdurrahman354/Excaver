package com.excaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivitySignIn extends AppCompatActivity {

    //private SignInButton mGoogleBtn;
    private Button btnLogin;
    private ImageView btnRegister;
    private EditText inputEmail, inputPassword;
    private TextView txtForgetPass;

    private DatabaseReference mUserAccount;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        mUserAccount = FirebaseDatabase.getInstance().getReferenceFromUrl("https://excaver-e200c.firebaseio.com").child("UserAccount");
        FirebaseUser mUser= mAuth.getCurrentUser();

        inputEmail = (EditText) findViewById(R.id.signup_email);
        inputPassword = (EditText) findViewById(R.id.signin_password);
        btnLogin = (Button) findViewById(R.id.signin_go);
        btnRegister = (ImageView) findViewById(R.id.signin_register);
        txtForgetPass = (TextView) findViewById(R.id.signin_forget);


        if (mUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            String uid = mAuth.getCurrentUser().getUid();
            intent.putExtra("user_id", uid);
            startActivity(intent);
            finish();
        }

        //mTalent = FirebaseDatabase.getInstance().getReferenceFromUrl("https://excaver-e200c.firebaseio.com").child("Talent");
        //mJob =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://excaver-e200c.firebaseio.com").child("Job");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(ActivitySignIn.this, ActivitySignUp.class);
                registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerIntent);
            }
        });

        txtForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resetintent = new Intent(ActivitySignIn.this, ActivityResetPassword.class);
                startActivity(resetintent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });
    }

    private void checkLogin(){
        final String email = inputEmail.getText().toString().trim();
        final String password = inputPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(email)&& !TextUtils.isEmpty(password)) {
            final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                    .setTitleText("Signing In");
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.show();
            pDialog.setCancelable(false);

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        checkUserExist();
                        pDialog.dismiss();
                    }
                    else if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
                        pDialog.dismiss();
                        inputEmail.requestFocus();
                        inputEmail.setError("Email tidak valid.");
                    }
                    else if (password.length() < 6) {
                        pDialog.dismiss();
                        inputPassword.requestFocus();
                        inputPassword.setError("Password should be more than 6 characters long");
                    }
                    else {
                        Toast.makeText(ActivitySignIn.this, "Login Failed", Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                    }
                }

            });
        }
        else if(TextUtils.isEmpty(email))  {
            Toast.makeText(ActivitySignIn.this, "Please Fill in Email", Toast.LENGTH_LONG).show();
            inputEmail.requestFocus();
        }

        else if(TextUtils.isEmpty(password))  {
            Toast.makeText(ActivitySignIn.this, "Please Fill in Password", Toast.LENGTH_LONG).show();
            inputPassword.requestFocus();
        }
    }

    private void checkUserExist(){
        if(mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();
            mUserAccount.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        checkIfEmailVerified(user_id);

                    } else {
                        Toast.makeText(ActivitySignIn.this, "Akun pengguna tidak ada.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void checkIfEmailVerified(final String user_id)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {
            mUserAccount.child(user_id).child("newuser").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        // User lama
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        Toast.makeText(ActivitySignIn.this, "Login berhasil.", Toast.LENGTH_SHORT).show();
                        intent.putExtra("user_id", user_id);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        // User baru
                        Intent intent = new Intent(getApplicationContext(), CreateProfile.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(ActivitySignIn.this, "Harap verifikasi terlebih dahulu akun Anda.", Toast.LENGTH_SHORT).show();
        }
    }
}
