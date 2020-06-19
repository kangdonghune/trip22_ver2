package com.sw.HeyBuddy2.LoginRegiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sw.HeyBuddy2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import xyz.hasnat.sweettoast.SweetToast;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountButton;
    private EditText userName,userEmail, userPassword,userPasswordConfirm;
    private TextView alreadyHaveAccount;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        createAccountButton = (Button)findViewById(R.id.signup_button);
        userName=(EditText)findViewById(R.id.signup_name);
        userEmail = (EditText)findViewById(R.id.signup_email);
        userPassword = (EditText)findViewById(R.id.signup_password);
        userPasswordConfirm=(EditText)findViewById(R.id.signup_confirm);
        alreadyHaveAccount = (TextView)findViewById(R.id.already_have_account);

        progressDialog = new ProgressDialog(RegisterActivity.this);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=userName.getText().toString();
                String email = userEmail.getText().toString();
                String password = userPassword.getText().toString();
                String confirmPassword=userPasswordConfirm.getText().toString();
                CreateNewAccount(name, email, password, confirmPassword);
            }
        });

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

    }

    private void registerSuccessPopUp() {
        // Custom Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        View view = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.register_success_popup, null);
        builder.setCancelable(false);

        builder.setView(view);
        builder.show();
    }
    private void CreateNewAccount(String name, String email, String password, String confirmPassword) {

        if(TextUtils.isEmpty(name)){
            SweetToast.error(RegisterActivity.this,"Your name is required");
        }
        else if (TextUtils.isEmpty(email)){
            SweetToast.error(RegisterActivity.this, "Your email is required.");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            SweetToast.error(RegisterActivity.this, "Your email is not valid.");
        }
        else if (TextUtils.isEmpty(password)){
            SweetToast.error(RegisterActivity.this, "Please fill this password field");
        } else if (password.length() < 6){
            SweetToast.error(RegisterActivity.this, "Create a password at least 6 characters long.");
        }else if (TextUtils.isEmpty(confirmPassword)){
            SweetToast.warning(RegisterActivity.this, "Please retype in password field");
        } else if (!password.equals(confirmPassword)){
            SweetToast.error(RegisterActivity.this, "Your password don't match with your confirm password");

        } else {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                Map<String, Object> Userinfo = new HashMap<>();
                                String name=userName.getText().toString();
                                String deviceToken = String.valueOf(FirebaseInstanceId.getInstance().getInstanceId());
                                String currentUserID = mAuth.getCurrentUser().getUid();


                                Userinfo.put("name",name);
                                Userinfo.put("verified","false");
                                Userinfo.put("deviceToken", deviceToken);

                                db.collection("Users").document(currentUserID).set(Userinfo, SetOptions.merge())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    // SENDING VERIFICATION EMAIL TO THE REGISTERED USER'S EMAIL
                                                    currentUser = mAuth.getCurrentUser();
                                                    if (currentUser != null){
                                                        currentUser.sendEmailVerification()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){

                                                                            registerSuccessPopUp();

                                                                            // LAUNCH activity after certain time period
                                                                            new Timer().schedule(new TimerTask(){
                                                                                public void run() {
                                                                                    RegisterActivity.this.runOnUiThread(new Runnable() {
                                                                                        public void run() {
                                                                                            mAuth.signOut();

                                                                                            Intent mainIntent =  new Intent(RegisterActivity.this, LoginActivity.class);
                                                                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                            startActivity(mainIntent);
                                                                                            finish();

                                                                                            SweetToast.info(RegisterActivity.this, "Please check your email & verify.");

                                                                                        }
                                                                                    });
                                                                                }
                                                                            }, 4000);


                                                                        } else {
                                                                            mAuth.signOut();
                                                                        }
                                                                    }
                                                                });
                                                    }

                                                }

                                            }
                                        });
                            }
                            else {
                                String message = task.getException().getMessage();
                                SweetToast.error(RegisterActivity.this, "Error occurred : " + message);
                            }
                            progressDialog.dismiss();
                        }
                    });
            //config progressbar
            progressDialog.setTitle(R.string.accounting);
            progressDialog.setMessage(getText(R.string.wait));
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

        }
    }
}