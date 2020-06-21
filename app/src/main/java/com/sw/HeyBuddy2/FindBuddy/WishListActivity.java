package com.sw.HeyBuddy2.FindBuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.firebase.firestore.SetOptions;
import com.nordan.dialog.Animation;
import com.nordan.dialog.DialogType;
import com.nordan.dialog.NordanAlertDialog;
import com.nordan.dialog.NordanAlertDialogListener;
import com.squareup.picasso.Picasso;
import com.sw.HeyBuddy2.R;
import com.sw.HeyBuddy2.utils.Contacts;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class WishListActivity extends AppCompatActivity {

    private RecyclerView mWishList;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter fsAdapter;
    private String wishName, wishStatus, user_uri;
    private ConstraintLayout notfound;
    Query query;

    public WishListActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        notfound = findViewById(R.id.wishlist_notfound);
        mWishList =(RecyclerView)findViewById(R.id.wishlist_view);
        mWishList.setLayoutManager(new LinearLayoutManager(getApplication()));
    }
    @Override
    public void onStart() {
        super.onStart();
        query=db.collection("Users").document(currentUserId).collection("Wishlist");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()){
                    notfound.setVisibility(View.VISIBLE);
                }
            }
        });

        FirestoreRecyclerOptions<Contacts> fsOptions =
                new FirestoreRecyclerOptions.Builder<Contacts>().setQuery(query, Contacts.class).build();

        fsAdapter = new FirestoreRecyclerAdapter<Contacts, WishlistViewHolder>(fsOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final WishlistViewHolder holder, int position, @NonNull Contacts model) {
                notfound.setVisibility(View.INVISIBLE);
                final String listUserId = getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();
                DocumentReference docRef = getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference();
                docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot.exists()){
                            db.collection("Users").document(listUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        wishName = task.getResult().get("name").toString();
                                        wishStatus = task.getResult().get("status").toString();
                                        if(task.getResult().contains("user_image")){
                                            user_uri=task.getResult().get("user_image").toString();
                                            Picasso.get().load(user_uri)
                                                    .placeholder(R.drawable.default_profile_image)
                                                    .error(R.drawable.default_profile_image)
                                                    .resize(0,90)
                                                    .into(holder.profileImage);
                                        }
                                        holder.userName.setText(wishName);
                                        holder.userStatus.setText(wishStatus);
                                    }
                                }
                            });
                            //요청보내기
                            holder.itemView.findViewById(R.id.wishlist_send_request_btn).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //요청전송
                                    if(holder.sendButton.getText().equals("Send Request")){
                                        Map<String, Object> requestInfo_send = new HashMap<>();
                                        requestInfo_send.put("sent", true);
                                        requestInfo_send.put("ismatched", false);
                                        db.collection("Users").document(currentUserId).collection("Matching").document(listUserId).set(requestInfo_send, SetOptions.merge()).addOnCompleteListener(
                                                new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Map<String, Object> requestInfo_receive = new HashMap<>();
                                                            requestInfo_receive.put("received", true);
                                                            requestInfo_receive.put("ismatched", false);
                                                            db.collection("Users").document(listUserId)
                                                                    .collection("Matching").document(currentUserId)
                                                                    .set(requestInfo_receive, SetOptions.merge());
//                                                            sendFCM(listUserId);
                                                        }
                                                    }
                                                }
                                        );
                                        holder.sendButton.setText("Cancel Request");
                                    } else{ // 요청취소
                                        Map<String, Object> removeSent = new HashMap<>();
                                        removeSent.put("sent", FieldValue.delete());
                                        db.collection("Users").document(currentUserId).collection("Matching")
                                                .document(listUserId).update(removeSent).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Map<String,Object> removereceived = new HashMap<>();
                                                    removereceived.put("received", FieldValue.delete());
                                                    //removereceived.put("ismatched", false);
                                                    db.collection("Users").document(listUserId)
                                                            .collection("Matching").document(currentUserId).update(removereceived);
                                                }
                                            }
                                        });
                                        holder.sendButton.setText("Send Request");
                                    }
                                }
                            });
                            //위시리스트에서 삭제
                            holder.itemView.findViewById(R.id.wishlist_cancel_btn).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    db.collection("Users").document(currentUserId).collection("Wishlist").document(listUserId).delete();
                                }
                            });
                        }
                    }
                });
            }

            @NonNull
            @Override
            public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wishlist_user_display_layout, viewGroup, false);
                WishlistViewHolder holder = new WishlistViewHolder(view);
                return holder;
            }
        };
        mWishList.setAdapter(fsAdapter);
        fsAdapter.startListening();
    }

    private void sendFCM(final String receiverId){
        //Log.d(TAG, senderName + "님이 " + task.getResult().get("name").toString() + "님께 매칭요청 전송");
        final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
        final String SERVER_KEY = "AAAAst3LJCQ:APA91bFXxaAjnupdToP6oUYp8qXK8akknY5EKOo-8_ZXURJ64zraxbV27OnKrMhIaQm9hKx4JcPqtQRvl1_O6xbob-xv66WEvXFrV7wzLAXHsJA_tt1RTXXLP7v-9fXq6BXQsliEPFT4";
        db.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                    pushMsg.put("body", senderName+"님이 매칭을 요청하셨습니다.");
                                    pushMsg.put("title", "새 매칭 요청");
                                    pushData.put("pushType", "request");
                                    pushData.put("requestUserId", currentUserId);
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

    public static class WishlistViewHolder extends  RecyclerView.ViewHolder{
        TextView userName, userStatus;
        CircleImageView profileImage;
        Button sendButton, cancelButton;
        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.wishlist_users_profile_name);
            userStatus = itemView.findViewById(R.id.wishlist_users_status);
            profileImage = itemView.findViewById(R.id.wishlist_users_profile_image);
            sendButton = (Button)itemView.findViewById(R.id.wishlist_send_request_btn);
            cancelButton = (Button)itemView.findViewById(R.id.wishlist_cancel_btn);

        }
    }
}