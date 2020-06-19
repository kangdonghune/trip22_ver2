package com.sw.HeyBuddy2.FindBuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.sw.HeyBuddy2.Feed.OtherProfileActivity;
import com.sw.HeyBuddy2.R;
import com.sw.HeyBuddy2.utils.Contacts;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "SecondActivity";
    Button btn_next;
    private RecyclerView findUserRecyclerList;
    private CollectionReference usersRef;
    FirestoreRecyclerAdapter fsAdapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    private String currentUserId  = mAuth.getCurrentUser().getUid();
    private ArrayList myWishList = new ArrayList();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        btn_next=(Button)findViewById(R.id.btn_next);
        usersRef = db.collection("Users");
        findUserRecyclerList = (RecyclerView)findViewById(R.id.findUser_recycler_list);
        findUserRecyclerList.setLayoutManager(new LinearLayoutManager(this));
    }
    public void onStart() {
        super.onStart();
        db.collection("Users").document(currentUserId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().contains("NQLocation") && task.getResult().contains("newQL")){
                                String location=task.getResult().get("NQLocation").toString();
                                String language=task.getResult().get("newQL").toString();


                                Query query=usersRef.whereEqualTo("NLocation",location)
                                        .whereEqualTo("question",false).whereEqualTo("newL",language);
                                FirestoreRecyclerOptions<Contacts> fsOptions = new FirestoreRecyclerOptions.Builder<Contacts>()
                                        .setQuery(query, Contacts.class).build();

                                fsAdapter=new FirestoreRecyclerAdapter<Contacts, FindUserViewHolder>(fsOptions) {
                                    @NonNull
                                    @Override
                                    public FindUserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                                        View view=LayoutInflater.from(viewGroup.getContext())
                                                .inflate(R.layout.user_display_layout,viewGroup,false);
                                        FindUserViewHolder viewHolder= new FindUserViewHolder(view);
                                        return viewHolder;
                                    }

                                    @Override
                                    protected void onBindViewHolder(@NonNull FindUserViewHolder holder, int position, @NonNull Contacts model) {
                                        if(getSnapshots().getSnapshot(position).contains("uid")){
                                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        String userName=task.getResult().getDocuments().get(position).get("name").toString();
                                                        holder.userName.setText(userName);

                                                        String userStatus=task.getResult().getDocuments().get(position).get("status").toString();
                                                        holder.userStatus.setText(userStatus);

                                                        if(task.getResult().getDocuments().get(position).contains("user_image")){
                                                            String user_uri=task.getResult().getDocuments().get(position).get("user_image").toString();
                                                            Picasso.get().load(user_uri)
                                                                    .placeholder(R.drawable.default_profile_image)
                                                                    .error(R.drawable.default_profile_image)
                                                                    .resize(0,100)
                                                                    .into(holder.profileImage);
                                                        }

                                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                String visitUserId = getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();
                                                                Intent profileIntent = new Intent(SecondActivity.this, OtherProfileActivity.class);
                                                                profileIntent.putExtra("userId", visitUserId);
                                                                startActivity(profileIntent);
                                                            }
                                                        });
                                                        db.collection("Users").document(currentUserId).collection("Wishlist")
                                                                .document(getSnapshots().getSnapshot(holder.getAdapterPosition()).getId()).get()
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        if(task.isSuccessful()){
                                                                            if(task.getResult().exists()){
                                                                                holder.itemView.setBackgroundColor(Color.parseColor("#50FFC4C4"));
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                        db.collection("Users").document(currentUserId).collection("Matching")
                                                                .document(getSnapshots().getSnapshot(holder.getAdapterPosition()).getId()).get()
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        if(task.isSuccessful()){
                                                                            if(task.getResult().exists()){
                                                                                holder.itemView.setBackgroundColor(Color.parseColor("#50FFC4C4"));
                                                                            }
                                                                        }
                                                                    }
                                                                });

                                                    }
                                                }
                                            });
                                        }
                                    }
                                };
                                findUserRecyclerList.setAdapter(fsAdapter);
                                fsAdapter.startListening();

                            }
                        }
                    }
                });
    }
    public void onStop() {
        super.onStop();
        fsAdapter.stopListening();
    }
    public static class FindUserViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView profileImage;

        public FindUserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}