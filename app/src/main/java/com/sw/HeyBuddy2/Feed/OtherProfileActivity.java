package com.sw.HeyBuddy2.Feed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;
import com.sw.HeyBuddy2.R;
import com.sw.HeyBuddy2.utils.Feed;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherProfileActivity extends AppCompatActivity {
    private static final String TAG = "OtherProfileActivity";
    String feed_uri;
    private String receiverUserId, senderUserId;

    RecyclerView profile_feed;
    private CircleImageView ivUser;
    private ImageView ivBack;

    FirebaseFirestore db;

    //TextView 부분
    TextView name;
    TextView keyword;
    TextView location;
    TextView language;
    TextView introduce;
    String profile_language="";
    private FirebaseAuth mAuth;

    private Button sendMessageRequestButton, addToWishlistButton;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        name=findViewById(R.id.profile_name);
        keyword=findViewById(R.id.profile_keyword);
        location=findViewById(R.id.profile_location);
        language=findViewById(R.id.profile_language);
        introduce=findViewById(R.id.profile_introduce);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        intent=getIntent();

        senderUserId = mAuth.getCurrentUser().getUid();
        receiverUserId=intent.getExtras().get("userId").toString();

        ivUser=findViewById(R.id.profile_ivUser);
        ivBack=findViewById(R.id.profile_ivUserBackground);

        sendMessageRequestButton = (Button) findViewById(R.id.send_message_request_button);
        addToWishlistButton = findViewById(R.id.add_to_wishlist_button);

        sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendChatRequest();
            }
        });

        db.collection("Users").document(receiverUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    if(document.exists()){
                        Map<String, Object> imgMap = document.getData();
                        if (imgMap.containsKey("user_image")) {
                            final String userUri = imgMap.get("user_image").toString();
                            Picasso.get().load(userUri)
                                    .networkPolicy(NetworkPolicy.OFFLINE) // for offline
                                    .placeholder(R.drawable.default_profile_image)
                                    .error(R.drawable.default_profile_image)
                                    .resize(0,100)
                                    .into(ivUser, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(userUri)
                                                    .placeholder(R.drawable.default_profile_image)
                                                    .error(R.drawable.default_profile_image)
                                                    .resize(0,100)
                                                    .into(ivUser);
                                        }
                                    });
                        }
                        if(imgMap.containsKey("user_back_image")){
                            final String backUri = imgMap.get("user_back_image").toString();
                            Picasso.get().load(backUri)
                                    .networkPolicy(NetworkPolicy.OFFLINE) // for offline
                                    .placeholder(R.drawable.default_profile_image)
                                    .error(R.drawable.default_profile_image)
                                    .resize(0,400)
                                    .into(ivBack, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(backUri)
                                                    .placeholder(R.drawable.default_profile_image)
                                                    .error(R.drawable.default_profile_image)
                                                    .resize(0,400)
                                                    .into(ivBack);
                                        }
                                    });
                        }
                    }
                }
            }
        });

        RetrieveUserInfo();

        profile_feed=(RecyclerView)findViewById(R.id.feed_list);
        GridLayoutManager proFeedGridManger=new GridLayoutManager(getApplication(),3);
        profile_feed.setLayoutManager(proFeedGridManger);
    }
    private void RetrieveUserInfo(){
        db.collection("Users").document(receiverUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    if(document.exists())
                    {
                        Map<String, Object> profile_map=document.getData();// 문서 전체를 profile_map으로 받아온것
                        if(profile_map.containsKey("name")) {
                            String profile_name = profile_map.get("name").toString();
                            name.setText(profile_name);
                        }
                        if(profile_map.containsKey("location")){
                            HashMap<String,Boolean> locationpart=(HashMap)profile_map.get("location");
                            String profile_location="";
                            for(String userlocation : locationpart.keySet())
                            {
                                profile_location=profile_location+userlocation;
                            }
                            location.setText(profile_location);
                        }
                        if(profile_map.containsKey("status")){
                            String profile_status = profile_map.get("status").toString();
                            introduce.setText(profile_status);
                        }
                        if(profile_map.containsKey("newL")){
                            String L=(String)profile_map.get("newL");
                            if(L.equals("English"))
                                profile_language= "Main : "+L+ "  Sub : ";
                            if(L.equals("Korean"))
                                profile_language= "Main : "+L+ "  Sub : ";
                            if(L.equals("Chinese"))
                                profile_language= "Main : "+L + "  Sub : ";

                        }
                        if(profile_map.containsKey("language")){
                            HashMap<String,Boolean> langlist=(HashMap)profile_map.get("language");
                            String aa="";
                            for(String userlang:langlist.keySet()) {

                                aa=aa+userlang+",  ";
                            }
                            language.setText(profile_language+aa);
                        }

                        if(profile_map.containsKey("NLocation")){
                            String profile_NLocation=profile_map.get("NLocation").toString();
                            location.setText(profile_NLocation);

                        }

                        if(profile_map.containsKey("newL")){
                            String L=(String)profile_map.get("newL");
                            if(L.equals("English"))
                                profile_language= "Main : "+L+ "  Sub : ";
                            if(L.equals("Korean"))
                                profile_language= "Main : "+L+ "  Sub : ";
                            if(L.equals("Chinese"))
                                profile_language= "Main : "+L + "  Sub : ";

                            language.setText(profile_language);

                        }
                        if(profile_map.containsKey("newSubL")){
                            List<String> l=(ArrayList<String>)profile_map.get("newSubL");
                            String profile_subLanguage="";
                            for(String subL : l){
                                profile_subLanguage=profile_subLanguage+" "+subL+" ";

                            }

                            profile_language+=profile_subLanguage;

                            language.setText(profile_language);


                        }

                        if(profile_map.containsKey("newI")){
                            List<String> check_key=(ArrayList<String>)profile_map.get("newI");;
                            String profile_userkeyword="";

                            for(String userinterest:check_key){

                                profile_userkeyword=profile_userkeyword+"#"+userinterest+" ";
                            }
                            keyword.setText(profile_userkeyword);

                        }

                    }
                }
            }
        });
    }
    public void onStart(){
        super.onStart();
        FirestoreRecyclerOptions<Feed> options =new FirestoreRecyclerOptions.Builder<Feed>()
                .setQuery(db.collection("Feeds").whereEqualTo("uid",receiverUserId),Feed.class).build();

        FirestoreRecyclerAdapter<Feed, FeedViewHolder> feedAdapter=
                new FirestoreRecyclerAdapter<Feed, FeedViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FeedViewHolder holder, final int position, @NonNull Feed model) {
                        db.collection("Feeds").whereEqualTo("uid",receiverUserId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    if(task.getResult().getDocuments().get(position).contains("feed_uri")){
                                        feed_uri=task.getResult().getDocuments().get(position).get("feed_uri").toString();
                                        Picasso.get().load(feed_uri)
                                                .placeholder(R.drawable.load)
                                                .error(R.drawable.load)
                                                .resize(0,200)
                                                .into(holder.feed);

                                    }
                                }
                            }
                        });
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String userId = getSnapshots().getSnapshot(position).get("uid").toString();
                                String feedId= getSnapshots().getSnapshot(position).getId();
                                Intent profileIntent = new Intent(getApplication(), FeedDetailActivity.class);
                                profileIntent.putExtra("userId", userId);
                                profileIntent.putExtra("feedId", feedId);
                                startActivity(profileIntent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.question_profile_feed, parent, false);
                        return new FeedViewHolder(view);
                    }
                };
        profile_feed.setAdapter(feedAdapter);
        feedAdapter.startListening();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder{
        ImageView feed;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            feed=itemView.findViewById(R.id.profile_feed);
        }
    }
    private void SendChatRequest() {

        if(sendMessageRequestButton.getText().equals(getString(R.string.cancel_invite))){
            Map<String,Object> removesent = new HashMap<>();
            removesent.put("sent", FieldValue.delete());
            //removesent.put("ismatched", false);
            db.collection("Users").document(senderUserId).collection("Matching")
                    .document(receiverUserId).update(removesent).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Map<String,Object> removereceived = new HashMap<>();
                        removereceived.put("received", FieldValue.delete());
                        //removereceived.put("ismatched", false);
                        db.collection("Users").document(receiverUserId)
                                .collection("Matching").document(senderUserId).update(removereceived);
                    }
                }
            });
            sendMessageRequestButton.setText(R.string.add_friend);
        } else{
            Map<String, Object> requestInfo_send = new HashMap<>();
            requestInfo_send.put("sent", true);
            requestInfo_send.put("ismatched", false);
            db.collection("Users").document(senderUserId).collection("Matching").document(receiverUserId).set(requestInfo_send, SetOptions.merge()).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Map<String, Object> requestInfo_receive = new HashMap<>();
                                requestInfo_receive.put("received", true);
                                requestInfo_receive.put("ismatched", false);
                                db.collection("Users").document(receiverUserId)
                                        .collection("Matching").document(senderUserId)
                                        .set(requestInfo_receive, SetOptions.merge());
//                                sendFCM(receiverUserId);
                                sendMessageRequestButton.setText(R.string.cancel_invite);
                            }
                        }
                    }
            );
        }
    }
}