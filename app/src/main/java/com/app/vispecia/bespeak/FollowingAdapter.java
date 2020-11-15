package com.app.vispecia.bespeak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ViewHolder>{

    Context mContext;
    List<Users> mData;
    UserViewClickListener mListener;

    public FollowingAdapter(Context mContext, List<Users> mData, UserViewClickListener mListener) {
        this.mContext = mContext;
        this.mData = mData;
        this.mListener = mListener;
    }

    interface UserViewClickListener{
        void userOnClickListener(Users index);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.single_following_list_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.username.setText(mData.get(position).getUsername());
        holder.place.setText(mData.get(position).getPlace());
        try {
            Picasso.get().load(mData.get(position).getProfileImage()).fit().into(holder.profileImg);
        }catch (Exception e){
            holder.profileImg.setImageResource(R.drawable.ic_avatar);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView profileImg;
        TextView username,place;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            profileImg = itemView.findViewById(R.id.following_result_profile_img);
            username = itemView.findViewById(R.id.following_result_username);
            place = itemView.findViewById(R.id.following_result_user_place);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int index = getAdapterPosition();
            mListener.userOnClickListener(mData.get(index));
        }
    }
}
