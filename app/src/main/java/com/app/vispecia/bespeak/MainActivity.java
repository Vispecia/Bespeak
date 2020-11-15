package com.app.vispecia.bespeak;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewSearchAdapter.UserViewClickListener{


    RecyclerView mView;
    RecyclerViewSearchAdapter mAdapter;
    List<Users> mData;
    BottomAppBar bottomAppBar;



    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mUserDataRef;

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomAppBar = findViewById(R.id.bottomAppBar);

        String currentUserPlace = "Delhi";

        if(getIntent().hasExtra("place"))
            currentUserPlace = getIntent().getStringExtra("place");

        mData = new ArrayList<>();
        mView = findViewById(R.id.searchResult_recyclerView);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mUserDataRef = FirebaseDatabase.getInstance().getReference().child("places").child(currentUserPlace);

        if (user == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                item.setChecked(true);

                switch (item.getItemId()) {
                    case R.id.profile_app_bar:  // need change from profile activity to home activity (later task)
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.settings_app_bar:
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;

                    case R.id.request_app_bar:
                        Intent intent1 = new Intent(MainActivity.this, RequestActivity.class);
                        startActivity(intent1);
                        break;

                    case R.id.search_app_bar:
                        if (Build.VERSION.SDK_INT >= 11) {
                            recreate();
                        } else {
                            Intent i = getIntent();
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            finish();
                            overridePendingTransition(0, 0);

                            startActivity(i);
                            overridePendingTransition(0, 0);
                        }
                        break;


                }

                return true;
            }
        });



        retrieveData();
    }

    private void retrieveData() {

        mUserDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mData.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Users u = data.getValue(Users.class);
                    if(!u.getId().equals(user.getUid()))
                    mData.add(u);

                }

                mAdapter = new RecyclerViewSearchAdapter(MainActivity.this, mData, MainActivity.this);
                mView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                mView.setAdapter(mAdapter);
                mAdapter.updateData(mData);
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
