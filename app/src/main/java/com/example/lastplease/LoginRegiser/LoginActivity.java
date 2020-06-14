package com.example.lastplease.LoginRegiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lastplease.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import xyz.hasnat.sweettoast.SweetToast;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText userEmail, userPassword;
    private Button loginButton;
    private TextView linkSingUp;

    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseReference userRef;
    FirebaseDatabase database;

    private String saved_id;
    private String saved_pwd;
    private boolean saved_LoginData;
    private CheckBox checkBox;
    private SharedPreferences appData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();

        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        db = FirebaseFirestore.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        userEmail = findViewById(R.id.inputEmail);
        userPassword = findViewById(R.id.inputPassword);
        checkBox = (CheckBox)findViewById(R.id.keep_login_info);
        loginButton = findViewById(R.id.loginButton);
        linkSingUp = findViewById(R.id.linkSingUp);
        progressDialog = new ProgressDialog(this);

        if(saved_LoginData){
            userEmail.setText(saved_id);
            userPassword.setText(saved_pwd);
            checkBox.setChecked(saved_LoginData);
        }

        linkSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stEmail=userEmail.getText().toString();
                String stPassword =userPassword.getText().toString();
                loginUserAccount(stEmail, stPassword);
            }
        });
    }
    private void load(){
        //기본값, 저장된 정보 없을경우
        saved_LoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        saved_id = appData.getString("ID", "");
        saved_pwd = appData.getString("PWD", "");
    }
    private void save(){
        SharedPreferences.Editor editor = appData.edit();
        editor.putBoolean("SAVE_LOGIN_DATA", checkBox.isChecked());
        editor.putString("ID", userEmail.getText().toString().trim());
        editor.putString("PWD", userPassword.getText().toString().trim());
        editor.apply();
    }

    private void checkVerifiedEmail() {
        final FirebaseUser currentUser;
        currentUser = mAuth.getCurrentUser();
        Map<String, Object> docData = new HashMap<>();
        docData.put("verified",true);

        boolean isVerified = false;
        if (currentUser != null) {
            isVerified = currentUser.isEmailVerified();
        }
        if (isVerified){
            String UID = mAuth.getCurrentUser().getUid();
            db.collection("Users").document(UID).set(docData, SetOptions.merge());

            Intent intent = new Intent(LoginActivity.this, SelectActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            SweetToast.info(LoginActivity.this, "Email is not verified. Please verify first");
            mAuth.signOut();
        }
    }
    private void loginUserAccount(String email, String password){
        if(TextUtils.isEmpty(email)){
            SweetToast.error(this, "Email is required");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            SweetToast.error(this, "Your email is not valid.");
        } else if(TextUtils.isEmpty(password)){
            SweetToast.error(this, "Password is required");
        } else if (password.length() < 6){
            SweetToast.error(this, "May be your password had minimum 6 numbers of character.");
        } else {

            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            loginUser(userEmail.getText().toString(),userPassword.getText().toString());
        }

    }
    private void loginUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            String currentUserId = mAuth.getCurrentUser().getUid();
                            String deviceToken = String.valueOf(FirebaseInstanceId.getInstance().getInstanceId());

                            userRef.child(currentUserId).child("deviceToken")
                                    .setValue(deviceToken)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                checkVerifiedEmail();
                                            }

                                        }
                                    });
                            save();

                        }
                        else{
                            SweetToast.error(LoginActivity.this, "Your email and password may be incorrect. Please check & try again.");
                        }
                        progressDialog.dismiss();
                    }
                });
    }
}