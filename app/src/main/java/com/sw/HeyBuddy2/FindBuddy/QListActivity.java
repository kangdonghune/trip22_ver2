package com.sw.HeyBuddy2.FindBuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.sw.HeyBuddy2.Chat.ChatActivity;
import com.sw.HeyBuddy2.Main.QMainActivity;
import com.sw.HeyBuddy2.R;
import com.sw.HeyBuddy2.utils.Contacts;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.hasnat.sweettoast.SweetToast;

public class QListActivity extends AppCompatActivity {
    private RecyclerView chatsList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserId;
    String userstatus,user_uri;
    FirestoreRecyclerOptions<Contacts> options;
    FirestoreRecyclerAdapter<Contacts, ChatsViewHolder> fsAdapter;
    Query query;

    public QListActivity(){}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        setContentView(R.layout.activity_list);
        chatsList = (RecyclerView) findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getApplication()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        query=db.collection("Users").document(currentUserId).collection("Matching").whereEqualTo("ismatched", true);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()){
                    AlertDialog.Builder alert=new AlertDialog.Builder(QListActivity.this);
                    alert.setIcon(R.drawable.ic_baseline_error_24);
                    alert.setTitle("No Buddy");
                    alert.setMessage("Sorry. There are no registered Buddys.");

                    alert.setPositiveButton("Find Buddy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent goFindBuddy=new Intent(QListActivity.this, QSecondActivity.class);
                            startActivity(goFindBuddy);
                            dialog.dismiss();
                        }
                    });

                    alert.setNegativeButton("Go Home", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent home=new Intent(QListActivity.this, QMainActivity.class);
                            startActivity(home);
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
            }
        });
        FirestoreRecyclerOptions<Contacts> options = new FirestoreRecyclerOptions.Builder<Contacts>()
                .setQuery(query, Contacts.class).build();
        FirestoreRecyclerAdapter<Contacts, ChatsViewHolder> fsAdapter =
                new FirestoreRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model) {
                        final String user_uid = getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();
                        DocumentReference docRef = getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference();
                        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                db.collection("Users").document(user_uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            final String username = task.getResult().get("name").toString();
                                            userstatus = task.getResult().get("status").toString();
                                            if(task.getResult().contains("user_image")){
                                                user_uri=task.getResult().get("user_image").toString();
                                                Picasso.get().load(user_uri)
                                                        .networkPolicy(NetworkPolicy.OFFLINE) // for Offline
                                                        .placeholder(R.drawable.default_profile_image)
                                                        .error(R.drawable.default_profile_image)
                                                        .resize(0,90)
                                                        .into(holder.profileImage, new Callback() {
                                                            @Override
                                                            public void onSuccess() {

                                                            }

                                                            @Override
                                                            public void onError(Exception e) {
                                                                Picasso.get().load(user_uri)
                                                                        .placeholder(R.drawable.default_profile_image)
                                                                        .error(R.drawable.default_profile_image)
                                                                        .resize(0,90)
                                                                        .into(holder.profileImage);

                                                            }
                                                        });
                                            }
                                            holder.userName.setText(username);
                                            holder.userStatus.setText(userstatus);

                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent chatIntent = new Intent(getApplication(), ChatActivity.class);
                                                    chatIntent.putExtra("visitUserId", user_uid);
                                                    chatIntent.putExtra("visitUserName", username);
                                                    //chatIntent.putExtra("visitUserImage", localFile.getAbsolutePath());
                                                    startActivity(chatIntent);
                                                }
                                            });
                                            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                                @Override
                                                public boolean onLongClick(View v) {
                                                    setvUid(user_uid);
                                                    Log.d("롱클릭", "onLongClick: ");
                                                    return false;
                                                }
                                            });
                                        }
                                    }
                                });


                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout, viewGroup, false);
                        registerForContextMenu(view);
                        return new ChatsViewHolder(view);
                    }
                };
        chatsList.setAdapter(fsAdapter);
        fsAdapter.startListening();
    }
    private String vUid;
    public String getvUid(){
        return vUid;
    }
    public void setvUid(String vUid){
        this.vUid = vUid;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.list_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.listmenu_report:
                SweetToast.info(getApplicationContext(), "신고하기\n"+getvUid());
                return true;
            case R.id.listmenu_endmatch:
                SweetToast.info(getApplicationContext(), "매칭종료 및 평가");
                Intent evalIntent = new Intent(QListActivity.this, UserEvaluationActivity.class);
                evalIntent.putExtra("rateeUid", getvUid());
                evalIntent.putExtra("raterUid", currentUserId);
                startActivity(evalIntent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImage;
        TextView userName,userStatus;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);

        }
    }
}
