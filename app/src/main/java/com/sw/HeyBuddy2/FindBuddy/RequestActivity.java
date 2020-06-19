package com.sw.HeyBuddy2.FindBuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;
import com.sw.HeyBuddy2.R;
import com.sw.HeyBuddy2.utils.Contacts;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestActivity extends AppCompatActivity {

    private RecyclerView mRequestsList;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter fsAdapter;
    private String reqname,reqstatus, user_uri;

    public RequestActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        mRequestsList = (RecyclerView)findViewById(R.id.chat_requests_list);
        mRequestsList.setLayoutManager(new LinearLayoutManager(getApplication()));
    }

    public void onStart() {
        super.onStart();
        FirestoreRecyclerOptions<Contacts> fsOptions =
                new FirestoreRecyclerOptions.Builder<Contacts>().setQuery(db.collection("Users").document(currentUserId).collection("Matching").whereEqualTo("ismatched", false).whereEqualTo("received", true), Contacts.class).build();

        fsAdapter = new FirestoreRecyclerAdapter<Contacts, RequestsViewHolder>(fsOptions){

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout, viewGroup, false);
                RequestsViewHolder holder = new RequestsViewHolder(view);
                return holder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, final int position, @NonNull final Contacts model) {
                holder.itemView.findViewById(R.id.requests_accept_btn).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.requests_cancel_btn).setVisibility(View.VISIBLE);

                // listuserid는 나의 매칭 항목에 있는 상대방의 uid 목록
                final String listUserId = getSnapshots().getSnapshot(position).getId();
                DocumentReference docRef = getSnapshots().getSnapshot(position).getReference();
                docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot.exists()){
                            Map<String, Object> reqMap = documentSnapshot.getData();
                            //친구 요청을 받은 경우
                            if(reqMap.containsKey("received")){
                                db.collection("Users").document(listUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            reqname = task.getResult().get("name").toString();
                                            reqstatus=task.getResult().get("status").toString();
                                            if(task.getResult().contains("user_image")){
                                                user_uri=task.getResult().get("user_image").toString();
                                                Picasso.get().load(user_uri)
                                                        .placeholder(R.drawable.default_profile_image)
                                                        .error(R.drawable.default_profile_image)
                                                        .resize(0,90)
                                                        .into(holder.profileImage);
                                            }
                                            holder.userName.setText(reqname);
                                            holder.userStatus.setText(reqstatus);
                                        }
                                    }
                                });
                                //수락한 경우
                                holder.itemView.findViewById(R.id.requests_accept_btn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final Map<String, Object> ismatched = new HashMap<>();
                                        ismatched.put("ismatched", true);
                                        //수락한 경우에 일단 내 매칭리스트에서 ismatched 값을 넣음
                                        getSnapshots().getSnapshot(position).getReference().set(ismatched).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //내 매칭리스트에 ismatched가 들어간 다음에는 상대의 매칭리스트에도 ismatched 값을 넣어줌
                                                db.collection("Users").document(listUserId).collection("Matching").document(currentUserId).set(ismatched).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        //상대 매칭리스트에 ismatched값이 들어가면 상대 매칭리스트에서 sent 값을 없애줌
                                                        Map<String,Object> removesent = new HashMap<>();
                                                        removesent.put("sent", FieldValue.delete());
                                                        db.collection("Users").document(listUserId).collection("Matching").document(currentUserId).update(removesent).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                //상대 매칭 리스트에서 sent 값이 없어지면, 내 매칭 리스트에서 recieved 값도 없애줌
                                                                Map<String,Object> removereceived = new HashMap<>();
                                                                removereceived.put("received", FieldValue.delete());
                                                                db.collection("Users").document(currentUserId).collection("Matching").document(listUserId).update(removereceived).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                                        sendFCM(listUserId, currentUserId, true);
                                                                        Toast.makeText(getApplication(), "매칭이 수락되었습니다",Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                                //거절한 경우
                                holder.itemView.findViewById(R.id.requests_cancel_btn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //상대 매칭리스트에서 sent 값을 없애줌
                                        Map<String,Object> removesent = new HashMap<>();
                                        removesent.put("sent", FieldValue.delete());
                                        db.collection("Users").document(listUserId).collection("Matching").document(currentUserId).update(removesent).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //상대 매칭 리스트에서 sent 값이 없어지면, 내 매칭 리스트에서 recieved 값도 없애줌
                                                Map<String,Object> removereceived = new HashMap<>();
                                                removereceived.put("received", FieldValue.delete());
                                                db.collection("Users").document(currentUserId).collection("Matching").document(listUserId).update(removereceived).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                        sendFCM(listUserId, currentUserId, false);
                                                        Toast.makeText(getApplication(), "매칭이 거절되었습니다",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                            //친구 요청을 보낸 경우
                            if(reqMap.containsKey("sent")){

                            }
                        }
                    }
                });


            }
        };
        mRequestsList.setAdapter(fsAdapter);
        fsAdapter.startListening();
    }
    public static class RequestsViewHolder extends  RecyclerView.ViewHolder{
        TextView userName, userStatus;
        CircleImageView profileImage;
        Button acceptButton, cancelButton;
        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            acceptButton = (Button)itemView.findViewById(R.id.requests_accept_btn);
            cancelButton = (Button)itemView.findViewById(R.id.requests_cancel_btn);

        }
    }
}