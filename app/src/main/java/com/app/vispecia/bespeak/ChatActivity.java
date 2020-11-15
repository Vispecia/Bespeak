package com.app.vispecia.bespeak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.vispecia.bespeak.notifications.Data;
import com.app.vispecia.bespeak.notifications.Sender;
import com.app.vispecia.bespeak.notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {


    String uid;
    String hisImg;

    ImageView sendBtn,hisProfile;
    TextView hisUserName,hisOnlineStatus;
    RecyclerView mView;
    EditText chatText;


    ValueEventListener seenListener;
    DatabaseReference userSeenRef;
    List<Chat> mData;
    ChatAdapter mAdapter;


    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    FirebaseUser user;
    DatabaseReference hisInfoRef, myInfoRef;

    private RequestQueue requestQueue;

    private boolean notify = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendBtn = findViewById(R.id.send_message_button);
        hisUserName = findViewById(R.id.chat_user_name);
        hisProfile = findViewById(R.id.chat_user_profile_img);
        chatText = findViewById(R.id.chat_textArea);
        hisOnlineStatus = findViewById(R.id.chat_user_status);

        mView = findViewById(R.id.chat_recyclerView);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mView.setHasFixedSize(true);
        mView.setLayoutManager(linearLayoutManager);

        requestQueue = Volley.newRequestQueue(getApplicationContext());



        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }


        if(getIntent().hasExtra("hisUid"))
        {
            uid = getIntent().getStringExtra("hisUid");
            hisInfoRef = mDatabase.getReference("users").child(uid).child("userdetails");
            myInfoRef = mDatabase.getReference("users").child(user.getUid()).child("userdetails");
            retrieveData();
        }


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String message = chatText.getText().toString().trim();

                if(!TextUtils.isEmpty(message))
                {
                    sendMessage(message);
                }

                chatText.setText("");

            }
        });


        readMessages();
        seenMessages();
    }

    private void seenMessages() {
        userSeenRef = mDatabase.getReference("chats");
        seenListener = userSeenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Chat chat = ds.getValue(Chat.class);
                    if(chat.getReceiver()!=null && chat.getSender()!=null && chat.getReceiver().equals(user.getUid()) && chat.getSender().equals(uid))
                    {
                        HashMap<String,Object> seenMp = new HashMap<>();
                        seenMp.put("isSeen",true);
                        ds.getRef().updateChildren(seenMp);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    @Override
    protected void onStart() {

        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        checkOnlineStatus("Reading..");
        userSeenRef.removeEventListener(seenListener);
    }

    private void checkOnlineStatus(String status)
    {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("userdetails");
        HashMap<String,Object> mp = new HashMap<>();
        mp.put("onlineStatus",status);
        db.updateChildren(mp);

    }


    private void readMessages() {

        mData = new ArrayList<>();
        DatabaseReference db = mDatabase.getReference("chats");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mData.clear();

                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Chat chat = ds.getValue(Chat.class);
                    if((chat.getReceiver()!=null && chat.getSender()!=null) && (chat.getReceiver().equals(user.getUid()) && chat.getSender().equals(uid)) ||
                            (chat.getReceiver()!=null && chat.getSender()!=null) && (chat.getReceiver().equals(uid) && chat.getSender().equals(user.getUid())))
                    {
                        mData.add(chat);
                    }

                    mAdapter = new ChatAdapter(ChatActivity.this,mData,hisImg);
                    mAdapter.notifyDataSetChanged();
                    mView.setAdapter(mAdapter);

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(final String message) {

        DatabaseReference chatDB = mDatabase.getReference("chats");

        String time= String.valueOf(System.currentTimeMillis());

        HashMap<String,Object> mp = new HashMap<>();
        mp.put("sender",user.getUid());
        mp.put("receiver",uid);
        mp.put("message",message);
        mp.put("time",time);
        mp.put("isSeen",false);
        chatDB.push().setValue(mp);

        myInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                if(notify)
                {
                    sendNotification(uid,user.getUsername(),message);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(final String uid, final String username, final String message) {

        DatabaseReference allTokens = mDatabase.getReference("tokens");
        Query query = allTokens.orderByKey().equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    Token tk = ds.getValue(Token.class);
                    Data data = new Data(user.getUid(),username+": "+message,"New Message",uid,R.drawable.ic_avatar); // show bespeak logo

                    Sender sender = new Sender(data,tk.getToken());

                    try {
                        JSONObject senderJson = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJson, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                Log.d("JSONRESPONSE","onResponse: "+response.toString());

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSONRESPONSE","onResponse: "+error.toString());
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {

                                Map<String,String> headers = new HashMap<>();
                                headers.put("Content-Type","application/json");
                                headers.put("Authorization","key=AAAAm8arL-E:APA91bE6R2k45_3FpmgYJwMlHUrHuFXGBlyBTlJS2lMLTdpM4qKRl44NT0ExfKZs-BXTx2I6YnRplmi7XClyZ--bE4fyKJgfOMIb6pdDpMfGVwvaCKHAwIFPAunbQQ2UVp5IRHi-bFXW");
                                return headers;
                            }
                        };

                        requestQueue.add(jsonObjectRequest);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void retrieveData() {
        hisInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hisUserName.setText("");

                Users user = dataSnapshot.getValue(Users.class);
                hisUserName.setText(user.getUsername());

                hisImg = user.getProfileImage();

                if(user.getOnlineStatus()!=null || !user.getOnlineStatus().trim().equals(""))
                {
                    hisOnlineStatus.setText(user.getOnlineStatus());
                }

                try {

                    if(user.getProfileImage()!=null || !user.getProfileImage().equals("")) {
                        Picasso.get().load(user.getProfileImage())
                                .fit()
                                .into(hisProfile);

                    }
                    else
                    {
                        hisProfile.setImageResource(R.drawable.ic_avatar);
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

    }


}
