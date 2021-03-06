package com.sw.HeyBuddy2.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sw.HeyBuddy2.Feed.FeedDetailActivity;
import com.sw.HeyBuddy2.utils.fullScreenImageViewer;
import com.sw.HeyBuddy2.R;

import com.sw.HeyBuddy2.utils.Feed;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class fragment_viewpage2 extends Fragment {

    private View view;

    RecyclerView feedList;
    private FirebaseFirestore db;
    private String currentUserID;
    private FirebaseAuth mAuth;

    private String username,user_uri,feed_uri, feed_desc,feed_uid;
    private Timestamp timestamp;

    RadioButton rbtRecent, rbtLike;
    Query query;
    public fragment_viewpage2(){}


    public static fragment_viewpage2 newinstance(){
        fragment_viewpage2 fragment_viewpage2 = new fragment_viewpage2();
        return fragment_viewpage2;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_viewpage2, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        feedList=(RecyclerView)view.findViewById(R.id.feed_list);
        LinearLayoutManager feedLayoutManager = new LinearLayoutManager(getContext());
        feedLayoutManager.setReverseLayout(true);
        feedLayoutManager.setStackFromEnd(true);
        feedList.clearDisappearingChildren();
        feedList.setLayoutManager(feedLayoutManager);

        rbtRecent=view.findViewById(R.id.rbtRecent);
        rbtLike=view.findViewById(R.id.rbtLike);

        return view;
    }


    public void onStart() {
        super.onStart();

        if(rbtRecent.isChecked()){
            query=db.collection("Feeds").orderBy("feed_time", Query.Direction.ASCENDING);
        }
        FirestoreRecyclerOptions<Feed> options = new FirestoreRecyclerOptions.Builder<Feed>()
                .setQuery(query, Feed.class).build();
        final FirestoreRecyclerAdapter<Feed, FeedViewHolder> feedAdapter=
                new FirestoreRecyclerAdapter<Feed, FeedViewHolder>(options){
                    @Override
                    protected void onBindViewHolder(@NonNull final FeedViewHolder holder, int position, @NonNull Feed model) {
                        if(getSnapshots().getSnapshot(holder.getAdapterPosition()).contains("uid")){
                            if(getSnapshots().getSnapshot(holder.getAdapterPosition()).contains("feed_time")){
                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            timestamp=task.getResult().getDocuments().get(holder.getAdapterPosition()).getTimestamp("feed_time", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE);
                                            SimpleDateFormat sdf=new SimpleDateFormat("MMM dd EEE", Locale.ENGLISH);
                                            String time=sdf.format(timestamp.toDate());
                                            holder.userTime.setText(time);

                                            if(task.getResult().getDocuments().get(holder.getAdapterPosition()).contains("feed_desc")){
                                                feed_desc=task.getResult().getDocuments().get(holder.getAdapterPosition()).get("feed_desc").toString();
                                                holder.feedDesc.setText(feed_desc);
                                            }

                                            if(task.getResult().getDocuments().get(holder.getAdapterPosition()).contains("feed_uri")) {
                                                feed_uri = task.getResult().getDocuments().get(holder.getAdapterPosition()).get("feed_uri").toString();
                                                Picasso.get().load(feed_uri)
                                                        .placeholder(R.drawable.load)
                                                        .error(R.drawable.load)
                                                        .resize(0,250)
                                                        .into(holder.feedImage);
                                                if(task.getResult().getDocuments().get(holder.getAdapterPosition()).contains("like_number")){
                                                    String like=task.getResult().getDocuments().get(holder.getAdapterPosition()).get("like_number").toString()+"";
                                                    holder.tvLikeNum.setText(like);
                                                }

                                                task.getResult().getDocuments().get(holder.getAdapterPosition()).getReference().collection("LikeMember").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.getResult().exists()){
                                                            holder.btn_like.setLiked(true);
                                                        }
                                                        else {
                                                            holder.btn_like.setLiked(false);
                                                        }
                                                    }
                                                });
                                            }

                                            if(task.getResult().getDocuments().get(holder.getAdapterPosition()).contains("feed_area")){
                                                HashMap<String, Boolean> feedarea=(HashMap)task.getResult().getDocuments().get(holder.getAdapterPosition()).getData().get("feed_area");
                                                String feed_area_result="";

                                                for (String feed_area_Elemet:feedarea.keySet()){
                                                    feed_area_result=feed_area_result+"#"+feed_area_Elemet+" ";
                                                }
                                                holder.feedArea.setText(feed_area_result);
                                            }

                                            if (task.getResult().getDocuments().get(holder.getAdapterPosition()).contains("uid")){
                                                feed_uid=task.getResult().getDocuments().get(holder.getAdapterPosition()).get("uid").toString();

                                                db.collection("Users").document(feed_uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            username = task.getResult().get("name").toString();
                                                            if(task.getResult().contains("user_image")){
                                                                user_uri=task.getResult().get("user_image").toString();
                                                                Picasso.get().load(user_uri)
                                                                        .placeholder(R.drawable.default_profile_image)
                                                                        .error(R.drawable.default_profile_image)
                                                                        .resize(0,70)
                                                                        .into(holder.profileImage);

                                                            }
                                                            holder.userName.setText(username);
                                                        }
                                                    }
                                                });
                                            }
                                            holder.feedImage.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if(task.getResult().getDocuments().get(holder.getAdapterPosition()).contains("feed_uri")) {
                                                        feed_uri = task.getResult().getDocuments().get(holder.getAdapterPosition()).get("feed_uri").toString();
                                                        Intent intent = new Intent(getContext(), fullScreenImageViewer.class);
                                                        intent.putExtra("uri",feed_uri );
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if(task.getResult().getDocuments().get(holder.getAdapterPosition()).contains("uid")){
                                                        final Intent detail = new Intent(getContext(), FeedDetailActivity.class);
                                                        detail.putExtra("userId", task.getResult().getDocuments().get(holder.getAdapterPosition()).get("uid").toString());
                                                        detail.putExtra("feedId",task.getResult().getDocuments().get(holder.getAdapterPosition()).getId());
                                                        detail.putExtra("btnExist",1);
                                                        startActivity(detail);
                                                    }
                                                }
                                            });
                                            holder.btn_like.setOnLikeListener(new OnLikeListener() {
                                                @Override
                                                public void liked(LikeButton likeButton) {
                                                    int intCurrentNum=Integer.parseInt(holder.tvLikeNum.getText().toString())+1;

                                                    if(task.getResult().getDocuments().get(holder.getAdapterPosition()).contains("like_number")){
                                                        HashMap<String, Object> map=new HashMap<>();
                                                        map.put("like_number",intCurrentNum);
                                                        task.getResult().getDocuments().get(holder.getAdapterPosition()).getReference().set(map, SetOptions.merge());
                                                        holder.tvLikeNum.setText(intCurrentNum+"");
                                                    }

                                                    HashMap<String, Object> update_user_data=new HashMap<>();
                                                    update_user_data.put("pushDate", new Timestamp(new Date()));
                                                    update_user_data.put("uid",currentUserID);
                                                    task.getResult().getDocuments().get(holder.getAdapterPosition()).getReference().collection("LikeMember").document(currentUserID).set(update_user_data);

                                                    if(task.getResult().getDocuments().get(holder.getAdapterPosition()).contains("feed_uri")){
                                                        final String selectFeed_uri=task.getResult().getDocuments().get(holder.getAdapterPosition()).get("feed_uri").toString();
                                                        final String docId=task.getResult().getDocuments().get(holder.getAdapterPosition()).getId();

                                                        db.collection("Users").document(currentUserID).collection("LikeFeed").document()
                                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if(task.isSuccessful()){
                                                                    HashMap<String, Object> update_feed=new HashMap<>();
                                                                    update_feed.put("feed_uri",selectFeed_uri);
                                                                    update_feed.put("doc_id",docId);
                                                                    task.getResult().getReference().set(update_feed, SetOptions.merge());
                                                                }
                                                            }
                                                        });
                                                    }

                                                }

                                                @Override
                                                public void unLiked(LikeButton likeButton) {
                                                    int intCurrentNum=Integer.parseInt(holder.tvLikeNum.getText().toString())-1;

                                                    if(task.getResult().getDocuments().get(holder.getAdapterPosition()).contains("like_number")){
                                                        HashMap<String, Object> map=new HashMap<>();
                                                        map.put("like_number",intCurrentNum);
                                                        task.getResult().getDocuments().get(holder.getAdapterPosition()).getReference().set(map,SetOptions.merge());
                                                        holder.tvLikeNum.setText(intCurrentNum+"");
                                                    }
                                                    task.getResult().getDocuments().get(holder.getAdapterPosition()).getReference().collection("LikeMember").document(currentUserID).delete();

                                                    if(task.getResult().getDocuments().get(holder.getAdapterPosition()).contains("feed_uri")){
                                                        final String selectFeed_uri=task.getResult().getDocuments().get(holder.getAdapterPosition()).get("feed_uri").toString();
                                                        final String docId=task.getResult().getDocuments().get(holder.getAdapterPosition()).getId();

                                                        db.collection("Users").document(currentUserID).collection("LikeFeed")
                                                                .whereEqualTo("feed_uri",selectFeed_uri).whereEqualTo("doc_id",docId).get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if(task.isSuccessful()){
                                                                            if(task.getResult().getDocuments().size()>0){
                                                                                for (int i=0; i<task.getResult().getDocuments().size();i++){
                                                                                    task.getResult().getDocuments().get(i).getReference().delete();
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    }

                    @NonNull
                    @Override
                    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_feed_layout, viewGroup, false);
                        return new FeedViewHolder(view);
                    }
                };
        feedList.setAdapter(feedAdapter);
        feedAdapter.startListening();

        rbtLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query=db.collection("Feeds").orderBy("like_number", Query.Direction.ASCENDING);
                feedList.removeAllViews();
                feedAdapter.notifyDataSetChanged();
                feedAdapter.startListening();

            }
        });
        rbtRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query=db.collection("Feeds").orderBy("feed_time", Query.Direction.ASCENDING);
                feedList.removeAllViews();
                feedAdapter.notifyDataSetChanged();
                feedAdapter.startListening();

            }
        });
    }
    public static class FeedViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImage;
        TextView userName,userTime,feedDesc, tvLikeNum, feedArea;
        ImageView feedImage;
        LikeButton btn_like;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userTime = itemView.findViewById(R.id.user_time);
            profileImage = itemView.findViewById(R.id.user_profile_image);
            feedImage=itemView.findViewById(R.id.user_feed_image);
            feedDesc = itemView.findViewById(R.id.user_feed_desc);
            btn_like=itemView.findViewById(R.id.btn_like);
            tvLikeNum=itemView.findViewById(R.id.tv_likeNum);
            feedArea=itemView.findViewById(R.id.feed_area);
        }
    }
}
