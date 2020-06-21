package com.sw.HeyBuddy2.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.Animator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.sw.HeyBuddy2.LoginRegiser.LoginActivity;
import com.sw.HeyBuddy2.Profile.question_profile;
import com.sw.HeyBuddy2.R;
import com.sw.HeyBuddy2.Setting.SettingQuestionActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sw.HeyBuddy2.app_eva.app_evaluation;
import com.sw.HeyBuddy2.tutorial.Local_Tutorial;
import com.sw.HeyBuddy2.tutorial.Tutorial;

import java.util.Map;

public class QMainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FragmentPagerAdapter fragmentPagerAdapter;

    FloatingActionButton fab, fab1, fab2, fab3, fab4;
    LinearLayout fabLayout1, fabLayout2, fabLayout3, fabLayout4 ;
    View fabBGLayout;
    boolean isFABOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ViewPager viewPager = findViewById(R.id.view_pager);
        fragmentPagerAdapter = new q_ViewPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(fragmentPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.q_tabLayout);
        tabLayout.setupWithViewPager(viewPager);


        //플롯 버튼 제어
        fabLayout1 = (LinearLayout) findViewById(R.id.fabLayout1);
        fabLayout2 = (LinearLayout) findViewById(R.id.fabLayout2);
        fabLayout3 = (LinearLayout) findViewById(R.id.fabLayout3);
        fabLayout4 = (LinearLayout) findViewById(R.id.fabLayout4);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);

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
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent evaluation=new Intent(QMainActivity.this, app_evaluation.class);
                startActivity(evaluation);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile=new Intent(QMainActivity.this, question_profile.class);
                startActivity(profile);
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
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
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tuto=new Intent(getApplication(), Local_Tutorial.class);
                startActivity(tuto);
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
        SharedPreferences pref = getSharedPreferences("checkFirst1", Activity.MODE_PRIVATE);
        boolean checkFirst = pref.getBoolean("checkFirst1", false);
        if(checkFirst==false){
            //앱 최초실행시
            Intent intent = new Intent(QMainActivity.this, Tutorial.class);
            startActivity(intent);

        }
        else{
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
                        if(!map.containsKey("NQLocation")){
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
        fabBGLayout.setVisibility(View.VISIBLE);
        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        fabLayout3.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
        fabLayout4.animate().translationY(-getResources().getDimension(R.dimen.standard_190));

    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotation(0);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0);
        fabLayout3.animate().translationY(0);
        fabLayout4.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) { }
            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                    fabLayout3.setVisibility(View.GONE);
                    fabLayout4.setVisibility(View.GONE);
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