package com.app.vispecia.bespeak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestActivity extends AppCompatActivity implements RequestAdapter.RequestViewClickListener{

    TextView emptyReq;

    RequestAdapter mAdapter;
    RecyclerView mView;
    List<Request> mData;

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference requestDbRef;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        mData = new ArrayList<>();

        mView = findViewById(R.id.request_recyclerView);
        emptyReq = findViewById(R.id.no_request);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        requestDbRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("requests");
        retrieveData();
    }

    private void retrieveData() {

        requestDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mData.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    Request request = data.getValue(Request.class);
                    mData.add(request);

                }

                mAdapter = new RequestAdapter(RequestActivity.this, mData, RequestActivity.this);
                mView.setLayoutManager(new LinearLayoutManager(RequestActivity.this));
                mView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

                if(mData.size()==0)
                {
                    emptyReq.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void requestOnClickListener(Request request) {
        Intent intent = new Intent(RequestActivity.this,ChatActivity.class);
        intent.putExtra("hisUid",request.getHisUID());
        startActivity(intent);
    }
}
