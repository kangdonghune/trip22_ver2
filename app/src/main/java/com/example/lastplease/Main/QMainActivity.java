package com.example.lastplease.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.lastplease.LoginRegiser.LoginActivity;
import com.example.lastplease.Profile.question_profile;
import com.example.lastplease.R;
import com.example.lastplease.Setting.SettingQuestionActivity;
import com.example.lastplease.Setting.SettingResponActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class QMainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FragmentPagerAdapter fragmentPagerAdapter;

    FloatingActionButton fab, fab1, fab2, fab3, fab4,fab5;
    LinearLayout fabLayout1, fabLayout2, fabLayout3, fabLayout4,fabLayout5;
    View fabBGLayout;
    boolean isFABOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_main);
        Intent intent=getIntent();
        String Newl=intent.getStringExtra("NLocation");
        String NewLang = intent.getStringExtra("NewL");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ViewPager viewPager = findViewById(R.id.view_pager);
        fragmentPagerAdapter = new q_ViewPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(fragmentPagerAdapter);

        //플롯 버튼 제어
        fabLayout1 = (LinearLayout) findViewById(R.id.fabLayout1);
        fabLayout2 = (LinearLayout) findViewById(R.id.fabLayout2);
        fabLayout3 = (LinearLayout) findViewById(R.id.fabLayout3);
        fabLayout4 = (LinearLayout) findViewById(R.id.fabLayout4);
        fabLayout5 = (LinearLayout) findViewById(R.id.fabLayout5);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        fab5 = (FloatingActionButton) findViewById(R.id.fab5);

        fabBGLayout = findViewById(R.id.fabBGLayout);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile=new Intent(QMainActivity.this, question_profile.class);
                startActivity(profile);
            }
        });

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(QMainActivity.this);
                View view = LayoutInflater.from(QMainActivity.this).inflate(R.layout.logout_dailog, null);

                ImageButton imageButton = view.findViewById(R.id.logoutImg);
                imageButton.setImageResource(R.drawable.logout);
                builder.setCancelable(true);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("YES, Log out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        logOutUser();
                    }
                });
                builder.setView(view);
                builder.show();
            }
        });

        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });


    }
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            Intent loginIntent = new Intent(QMainActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        }
        else{
            VerifyUserExistance();

        }
    }
    private void VerifyUserExistance() {
        String currentUserID = mAuth.getCurrentUser().getUid();

        db.collection("Users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    if(document.exists()){
                        Map<String, Object> map = document.getData();
                        if(!map.containsKey("name")){
                            Intent settingsIntent = new Intent(QMainActivity.this, SettingQuestionActivity.class);
                            startActivity(settingsIntent);
                        }
                        if(!map.containsKey("status")){
                            Intent settingsIntent = new Intent(QMainActivity.this, SettingQuestionActivity.class);
                            startActivity(settingsIntent);
                        }
                    }
                }
            }
        });

    }

    private void showFABMenu() {
        isFABOpen = true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fabLayout3.setVisibility(View.VISIBLE);
        fabLayout4.setVisibility(View.VISIBLE);
        fabLayout5.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);
        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        fabLayout3.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
        fabLayout4.animate().translationY(-getResources().getDimension(R.dimen.standard_190));
        fabLayout5.animate().translationY(-getResources().getDimension(R.dimen.standard_235));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotation(0);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0);
        fabLayout3.animate().translationY(0);
        fabLayout4.animate().translationY(0);
        fabLayout5.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) { }
            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                    fabLayout3.setVisibility(View.GONE);
                    fabLayout4.setVisibility(View.GONE);
                    fabLayout5.setVisibility(View.GONE);
                }
            }
            @Override
            public void onAnimationCancel(Animator animator) { }
            @Override
            public void onAnimationRepeat(Animator animator) { }
        });
    }

    @Override
    public void onBackPressed() {
        if (isFABOpen) {
            closeFABMenu();
        } else {
            super.onBackPressed();
        }
    }
    private void logOutUser() {
        Intent loginIntent =  new Intent(QMainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}