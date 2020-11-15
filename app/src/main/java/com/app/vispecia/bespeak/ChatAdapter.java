package com.app.vispecia.bespeak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{


    private static final int SENDER = 1;
    private static final int RECEIVER = 0;


    Context mContext;
    List<Chat> mData;
    String imageUrl;

    FirebaseUser user;

    public ChatAdapter(Context mContext, List<Chat> mData, String imageUrl) {
        this.mContext = mContext;
        this.mData = mData;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == SENDER)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.sender_chat,parent,false);
            return new ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.reciever_chat,parent,false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String message = mData.get(position).getMessage();
        holder.message.setText(message);

        String time = mData.get(position).getTime();

        SimpleDateFormat dateTime =new SimpleDateFormat("HH:mm:ss");

            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.parseLong(time));
            dateTime.setCalendar(cal);
            time = dateTime.format(cal.getTime());

            holder.time.setText(time);


//
//            try {
//              Picasso.get()
//                    .load(imageUrl).into(holder.profile);
//            }catch (Exception e) {
//
//            }


        if(position == mData.size()-1)
        {
            if(mData.get(position).isSeen())
            holder.isSeen.setText("Seen");
            else {
                holder.isSeen.setText("Delivered");
            }
        }
        else {
            holder.isSeen.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public int getItemViewType(int position) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(mData.get(position).getSender().equals(user.getUid()))
        {
            return SENDER;
        }
        else
        {
            return RECEIVER;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView profile;
        TextView message,time,isSeen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

          //  profile = itemView.findViewById(R.id.receiver_sender_chat_user_profile_img);
            message = itemView.findViewById(R.id.receiver_sender_chat_user_text);
            time = itemView.findViewById(R.id.receiver_sender_time_text);
            isSeen = itemView.findViewById(R.id.receiver_sender_isSeen);
        }
    }
}
