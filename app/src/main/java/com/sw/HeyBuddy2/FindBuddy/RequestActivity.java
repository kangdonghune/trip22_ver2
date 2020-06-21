package com.sw.HeyBuddy2.FindBuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nordan.dialog.Animation;
import com.nordan.dialog.DialogType;
import com.nordan.dialog.NordanAlertDialog;
import com.squareup.picasso.Picasso;
import com.sw.HeyBuddy2.Main.MainActivity;
import com.sw.HeyBuddy2.Main.QMainActivity;
import com.sw.HeyBuddy2.R;
import com.sw.HeyBuddy2.Setting.SettingQuestionActivity;
import com.sw.HeyBuddy2.utils.Contacts;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    Query query;
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
        query=db.collection("Users").document(currentUserId).collection("Matching").whereEqualTo("ismatched", false).whereEqualTo("received", true);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()){
                    NordanAlertDialog.Builder alert=new NordanAlertDialog.Builder(RequestActivity.this);
                    alert.setAnimation(Animation.SLIDE);
                    alert.setDialogType(DialogType.INFORMATION);
                    alert.setTitle("No Request");
                    alert.setMessage("Sorry. No request came.");
                    alert.isCancellable(true);
                    alert.build().show();
                }
            }
        });
        FirestoreRecyclerOptions<Contacts> fsOptions =
                new FirestoreRecyclerOptions.Builder<Contacts>().setQuery(query, Contacts.class).build();

        fsAdapter = new FirestoreRecyclerAdapter<Contacts, RequestsViewHolder>(fsOptions){

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout, viewGroup, false);
                RequestsViewHolder holder = new RequestsViewHolder(view);
                return holder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull final Contacts model) {
                holder.itemView.findViewById(R.id.requests_accept_btn).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.requests_cancel_btn).setVisibility(View.VISIBLE);

                // listuserid는 나의 매칭 항목에 있는 상대방의 uid 목록
                final String listUserId = getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();
                DocumentReference docRef = getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference();
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
                                        getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference().set(ismatched).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                                                        sendFCM(listUserId, currentUserId, true);
                                                                        //매칭이 수락되면 위시리스트에서 제거
                                                                        db.collection("Users").document(listUserId).collection("Wishlist").document(currentUserId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
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
                                                        sendFCM(listUserId, currentUserId, false);
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

    private void sendFCM(final String receiverId, String senderId, final boolean result){
        final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
        final String SERVER_KEY = "AAAAst3LJCQ:APA91bFXxaAjnupdToP6oUYp8qXK8akknY5EKOo-8_ZXURJ64zraxbV27OnKrMhIaQm9hKx4JcPqtQRvl1_O6xbob-xv66WEvXFrV7wzLAXHsJA_tt1RTXXLP7v-9fXq6BXQsliEPFT4";
        db.collection("Users").document(senderId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                final String senderName = task.getResult().get("name").toString();
                db.collection("Users").document(receiverId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                        final String receiverFCMId = task.getResult().get("FCMToken").toString();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //JSON 메시지 생성
                                    JSONObject pushRoot = new JSONObject();
                                    JSONObject pushMsg = new JSONObject();
                                    JSONObject pushData = new JSONObject();
                                    if(result){
                                        pushMsg.put("body", senderName+"님과 매칭되었습니다.");
                                    } else{
                                        pushMsg.put("body", senderName+"님께 보낸 매칭 요청이 거절되었습니다.");
                                    }
                                    pushMsg.put("title", "매칭 결과");
                                    pushMsg.put("click_action", ".questioner_main");
                                    pushData.put("pushType", "isaccept");
                                    pushRoot.put("notification", pushMsg);
                                    pushRoot.put("to", receiverFCMId);
                                    pushRoot.put("data", pushData);
                                    //POST 방식으로 FCM 서버에 전송
                                    URL Url = new URL(FCM_MESSAGE_URL);
                                    HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                                    conn.setRequestMethod("POST");
                                    conn.setDoOutput(true);
                                    conn.setDoInput(true);
                                    conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                                    conn.setRequestProperty("Accept", "application/json");
                                    conn.setRequestProperty("Content-type", "application/json");
                                    OutputStream os = conn.getOutputStream();
                                    os.write(pushRoot.toString().getBytes("utf-8"));
                                    os.flush();
                                    conn.getResponseCode();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
            }
        });

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