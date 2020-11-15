package com.app.vispecia.bespeak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewSearchAdapter extends RecyclerView.Adapter<RecyclerViewSearchAdapter.ViewHolder>{


    Context mContext;
    List<Users> mData;

    UserViewClickListener mListener;

    public RecyclerViewSearchAdapter(Context mContext, List<Users> mData, UserViewClickListener mListener) {
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

        view = layoutInflater.inflate(R.layout.search_result_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.username.setText(mData.get(position).getUsername());
        holder.place.setText(mData.get(position).getPlace());
        try {
            Picasso.get().load(mData.get(position).getProfileImage()).fit().into(holder.profile);
        }catch (Exception e){
            holder.profile.setImageResource(R.drawable.ic_avatar);
        }


    }

    public void updateData(List<Users> mData)
    {
        this.mData = mData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView username,place;
        ImageView profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.search_result_username);
            place = itemView.findViewById(R.id.search_result_user_place);
            profile = itemView.findViewById(R.id.search_result_profile_img);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int index = getAdapterPosition();
            mListener.userOnClickListener(mData.get(index));
        }
    }
}
