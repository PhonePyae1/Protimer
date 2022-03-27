package com.example.protimer.ui.ui;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.protimer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class History2 extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<history> historyArrayList;
    myAdapter myAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    FirebaseUser fu;
    String userid;
    ImageView arrow;
    ImageButton delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerview);
        arrow = findViewById(R.id.arrow4);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        delete = findViewById(R.id.deleteHistory1);
        db = FirebaseFirestore.getInstance();
        historyArrayList = new ArrayList<history>();
        myAdapter = new myAdapter(History2.this,historyArrayList);
        recyclerView.setAdapter(myAdapter);
        EventChangeListener();

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent switchActivityIntent = new Intent(History2.this, Profile.class);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(History2.this).toBundle();
                startActivity(switchActivityIntent,b);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("history").document(userid).collection("history").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            db.collection("history").document(userid).collection("history").document(snapshot.getId()).delete();
                        }
                    }
                });
            }
        });
    }

    private void EventChangeListener() {
        fu = FirebaseAuth.getInstance().getCurrentUser();

        assert fu != null;
        userid = fu.getUid();
        db.collection("history").document(userid).collection("history").orderBy("date").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firebase Error",error.getMessage());
                    return;
                }

                for(DocumentChange dc: value.getDocumentChanges()){
                    if (dc.getType() == DocumentChange.Type.ADDED){
                        historyArrayList.add(dc.getDocument().toObject(history.class));
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}