package com.sw.HeyBuddy2.Profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sw.HeyBuddy2.Feed.DeleteFeedActivity;
import com.sw.HeyBuddy2.Feed.FeedDetailActivity;
import com.sw.HeyBuddy2.R;
import com.sw.HeyBuddy2.Setting.SettingResponActivity;
import com.sw.HeyBuddy2.utils.Feed;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.sw.HeyBuddy2.utils.fullScreenImageViewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.hasnat.sweettoast.SweetToast;

public class respon_profile extends AppCompatActivity {

    int REQUEST_EXTERNAL_STORAGE_PERMISSION=1002;
    private StorageReference mStorageRef;

    RecyclerView profile_feed;

    private String currentUserID;
    private FirebaseAuth mAuth;

    private CircleImageView ivUser;
    private ImageView ivBack;
    String profile_language="";
    Button update;



    FirebaseFirestore db;

    //TextView 부분
    TextView name;
    TextView keyword;
    TextView location;
    TextView language;
    TextView introduce;
    TextView startday;
    TextView endday;

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respon_profile);

        name=findViewById(R.id.profile_name);
        keyword=findViewById(R.id.profile_keyword);
        location=findViewById(R.id.profile_location);
        language=findViewById(R.id.profile_language);
        introduce=findViewById(R.id.profile_introduce);
        update=findViewById(R.id.update);
        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        currentUserID=mAuth.getCurrentUser().getUid();

        if(ContextCompat.checkSelfPermission(respon_profile.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(respon_profile.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){

            }else{
                ActivityCompat.requestPermissions(respon_profile.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE_PERMISSION);
            }
        }else{

        }




        ivUser=findViewById(R.id.profile_ivUser);
        ivBack=findViewById(R.id.profile_ivUserBackground);
        db.collection("Users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
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
                        if (imgMap.containsKey("user_back_image")) {
                            final String userbackUri = imgMap.get("user_back_image").toString();
                            Picasso.get().load(userbackUri)
                                    .networkPolicy(NetworkPolicy.OFFLINE) // for offline
                                    .placeholder(R.drawable.profile_ivuserbackgroundimage)
                                    .error(R.drawable.profile_ivuserbackgroundimage)
                                    .resize(0,400)
                                    .into(ivBack, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(userbackUri)
                                                    .placeholder(R.drawable.profile_ivuserbackgroundimage)
                                                    .error(R.drawable.profile_ivuserbackgroundimage)
                                                    .resize(0,400)
                                                    .into(ivBack);
                                        }
                                    });
                        }
                    }
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(respon_profile.this, SettingResponActivity.class);
                startActivity(intent);
                finish();

            }
        });

        RetrieveUserInfo();

        profile_feed=(RecyclerView)findViewById(R.id.feed_list);
        GridLayoutManager proFeedGridManger=new GridLayoutManager(respon_profile.this,3);
        profile_feed.clearDisappearingChildren();
        profile_feed.setLayoutManager(proFeedGridManger);

    }
    private void RetrieveUserInfo(){
        db.collection("Users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                        if(profile_map.containsKey("NLocation")){
                            String profile_NLocation=profile_map.get("NLocation").toString();
                            location.setText(profile_NLocation);

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
                .setQuery(db.collection("Feeds").whereEqualTo("uid",currentUserID),Feed.class).build();

        FirestoreRecyclerAdapter<Feed, FeedViewHolder> feedAdapter=
                new FirestoreRecyclerAdapter<Feed, FeedViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FeedViewHolder holder, int position, @NonNull Feed model) {
                        if(getSnapshots().getSnapshot(holder.getAdapterPosition()).contains("feed_time")){
                            db.collection("Feeds").whereEqualTo("uid",currentUserID).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                if(task.getResult().getDocuments().get(holder.getAdapterPosition()).contains("feed_uri")){
                                                    String feed_uri=task.getResult().getDocuments().get(holder.getAdapterPosition()).get("feed_uri").toString();
                                                    Picasso.get().load(feed_uri)
                                                            .placeholder(R.drawable.load)
                                                            .error(R.drawable.load)
                                                            .resize(0,200)
                                                            .into(holder.feed);

                                                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                                        @Override
                                                        public boolean onLongClick(View v) {
                                                            Intent intent = new Intent(respon_profile.this, DeleteFeedActivity.class);
                                                            intent.putExtra("id", task.getResult().getDocuments().get(holder.getAdapterPosition()).getId());
                                                            intent.putExtra("feedUri",feed_uri);
                                                            startActivity(intent);
                                                            return true;
                                                        }
                                                    });
                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if(task.getResult().getDocuments().get(holder.getAdapterPosition()).contains("uid")) {
                                                                final Intent detail = new Intent(respon_profile.this, FeedDetailActivity.class);
                                                                detail.putExtra("userId", task.getResult().getDocuments().get(holder.getAdapterPosition()).get("uid").toString());
                                                                detail.putExtra("feedId",task.getResult().getDocuments().get(holder.getAdapterPosition()).getId());
                                                                detail.putExtra("btnExist",2);
                                                                startActivity(detail);
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });

                        }
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
}
