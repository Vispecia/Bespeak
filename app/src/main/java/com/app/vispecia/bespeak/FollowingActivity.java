package com.app.vispecia.bespeak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowingActivity extends AppCompatActivity implements FollowingAdapter.UserViewClickListener{

    RecyclerView mView;
    FollowingAdapter mAdapter;
    List<Users> mData;


    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mUserDataRef;


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null)
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);


        mData = new ArrayList<>();
        mView = findViewById(R.id.following_recyclerView);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        mUserDataRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("following");
        retrieveData();
    }

    private void retrieveData() {

        mUserDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mData.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Users u = data.getValue(Users.class);
                        mData.add(u);

                }

                mAdapter = new FollowingAdapter(FollowingActivity.this, mData, FollowingActivity.this);
                mView.setLayoutManager(new LinearLayoutManager(FollowingActivity.this));
                mView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void userOnClickListener(Users index) {

        String clickedUserId = index.getId();
        Intent intent = new Intent(this,OtherUserProfileActivity.class);
        intent.putExtra("uid",clickedUserId);
        startActivity(intent);

    }
}
