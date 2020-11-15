package com.app.vispecia.bespeak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OtherUserProfileActivity extends AppCompatActivity implements RecyclerViewBookAdapter.BookViewClickListener{

    String uid;
    String hisProfile;
    HashMap<String,Request> requestData;

    Users addThisUserToFollowingList;

    ImageView userProfileImageView;

    RecyclerView mLayout;
    ArrayList<Book> mData;
    RecyclerViewBookAdapter mBookAdapter;

    TextView userName, bio;
    ImageView followBtn;


    CoordinatorLayout mCoordinatorLayout;
    FloatingActionButton backFab,messageFab;


    FirebaseAuth mAuth;
    DatabaseReference mBookDataRef,userInfoRef,mFollowingDBRef,userFollowerRef,requestDbRef;
    FirebaseDatabase mDatabase;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        userProfileImageView = findViewById(R.id.other_user_profile_img);

        userName = findViewById(R.id.other_profile_username_textView);
        bio = findViewById(R.id.other_profile_bio_textView);
        followBtn = findViewById(R.id.add_to_followingList);

        mLayout = findViewById(R.id.other_user_profile_recyclerView);
        mData = new ArrayList<>();
        requestData = new HashMap<>();

        mDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        requestDbRef = mDatabase.getReference("users").child(user.getUid()).child("requests");

        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }


        if(getIntent().hasExtra("uid")) {
            uid = getIntent().getStringExtra("uid");
            userInfoRef = mDatabase.getReference("users").child(uid).child("userdetails");
           // userFollowerRef = mDatabase.getReference("users").child(uid).child("followers");
            mBookDataRef = mDatabase.getReference("users").child(uid).child("books");
            mBookDataRef.keepSynced(true);
            retrieveData();
        }
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout_other_user_profile_activity);
        backFab = findViewById(R.id.back_from_other_user_profile_fab);
        messageFab = findViewById(R.id.message_other_user_profile_fab);


        backFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(OtherUserProfileActivity.this, ProfileActivity.class));
            }
        });

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFollowed = PreferenceManager.getDefaultSharedPreferences(OtherUserProfileActivity.this).getBoolean(user.getUid()+addThisUserToFollowingList.getId() +"_followed",false);

                if(!isFollowed) {
                    followBtn.setImageResource(R.drawable.ic_favorite);
                    mFollowingDBRef = mDatabase.getReference("users").child(user.getUid()).child("following");
                    mFollowingDBRef.child(addThisUserToFollowingList.getId()).setValue(addThisUserToFollowingList);


                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                            .putBoolean(user.getUid()+addThisUserToFollowingList.getId() +"_followed", true).commit();


                }
                else
                {
                    followBtn.setImageResource(R.drawable.ic_favorite_border);
                    mFollowingDBRef = mDatabase.getReference("users").child(user.getUid()).child("following");
                    mFollowingDBRef.child(addThisUserToFollowingList.getId()).removeValue();

                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                            .putBoolean(user.getUid()+addThisUserToFollowingList.getId() +"_followed", false).commit();
                }
            }
        });

        messageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(OtherUserProfileActivity.this,ChatActivity.class);
                intent.putExtra("hisUid",uid);
                startActivity(intent);

            }
        });


    }


    private void retrieveData() {

        mBookDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mData.clear();

                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    Book book = data.getValue(Book.class);
                    mData.add(book);
                }

                mBookAdapter = new RecyclerViewBookAdapter(OtherUserProfileActivity.this,mData,OtherUserProfileActivity.this);
                mLayout.setLayoutManager(new GridLayoutManager(OtherUserProfileActivity.this,3));
                mLayout.setAdapter(mBookAdapter);
                mBookAdapter.updateData(mData);
                mBookAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        userInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName.setText("");
                bio.setText("");

                Users user = dataSnapshot.getValue(Users.class);
                addThisUserToFollowingList = user;

                userName.setText(user.getUsername());
                bio.setText("Change on settings tab");
                hisProfile = user.getProfileImage();

                try {

                    if(user.getProfileImage()!=null || !user.getProfileImage().equals("")) {
                        Picasso.get().load(user.getProfileImage())
                                .fit()
                                .into(userProfileImageView);
                    }
                    else
                    {
                        userProfileImageView.setImageResource(R.drawable.ic_avatar);
                    }




                }catch (Exception e)
                {
                    e.printStackTrace();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        requestDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                requestData.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    Request request = data.getValue(Request.class);
                    requestData.put(request.getBookName(),request);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void bookOnClickListener(Book index, View view) {


        if(requestData.containsKey(index.getBookName()))
        {
            Snackbar snackbar =  Snackbar.make(mCoordinatorLayout, "You already bespeak for " + index.getBookName() + " before. Please tell them why you need this book over chat.", Snackbar.LENGTH_INDEFINITE)
                    .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark))
                    .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite))
                    .setAction("Chat", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(OtherUserProfileActivity.this,ChatActivity.class);
                            intent.putExtra("hisUid",uid);
                            startActivity(intent);

                        }
                    });

            View snack = snackbar.getView();
            TextView tv = snack.findViewById(R.id.snackbar_text);
            tv.setMaxLines(3);
            snackbar.show();

        }

        else {
            if (index.isExchangable()) {
                Request request = new Request(hisProfile, index.getImageUrl(), index.getBookName(), uid,userName.getText().toString());
                DatabaseReference db = mDatabase.getReference("users").child(user.getUid()).child("requests");
                db.push().setValue(request);

                Snackbar.make(mCoordinatorLayout, "You just bespeak for" + index.getBookName(), Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent))
                        .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark))
                        .show();

            } else {
                Snackbar.make(mCoordinatorLayout, index.getBookName() + " is not available right now. Comeback later :]", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent))
                        .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark))
                        .show();
            }
        }





    }
}
