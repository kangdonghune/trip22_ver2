package com.example.lastplease.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lastplease.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class fragment_q_viewpage1 extends Fragment {
    private View view;
    TextView checkLang;
    TextView checkLo;
    ImageView ask;
    ImageView findbuddy;
    private FirebaseFirestore db;
    private String currentUserID;
    String nlang="";
    String nl="";
    private FirebaseAuth mAuth;


    public static fragment_q_viewpage1 newinstance(){
        fragment_q_viewpage1 fragment_q_viewpage1 = new fragment_q_viewpage1();
        return fragment_q_viewpage1;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_q_viewpage1, container, false);

        checkLang=view.findViewById(R.id.checkLang);
        checkLo=view.findViewById(R.id.checkLo);
        ask=view.findViewById(R.id.Ask);
        findbuddy=view.findViewById(R.id.Find_Buddy);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        db.collection("Users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Map<String, Object> map = document.getData();

                        if(map.containsKey("NLocation")){
                            nl=map.get("NQLocation").toString();
                            checkLo.setText(nl);
                        }
                        if(map.containsKey("newL"))
                        {
                            nlang=map.get("newQL").toString();
                            checkLang.setText(nlang);
                        }

                    }
                }
            }

        });
        return view;
    }
}
