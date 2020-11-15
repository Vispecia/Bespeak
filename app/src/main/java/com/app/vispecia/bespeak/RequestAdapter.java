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

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder>{

    Context mContext;
    List<Request> mData;
    RequestViewClickListener mListener;

    public RequestAdapter(Context mContext, List<Request> mData, RequestViewClickListener mListener) {
        this.mContext = mContext;
        this.mData = mData;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.single_request_list_view,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bookText.setText(mData.get(position).getHisName() + " bespeak for "+mData.get(position).getBookName());
        try {
            Picasso.get().load(mData.get(position).getBookImg()).fit().into(holder.bookImg);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            Picasso.get().load(mData.get(position).getProfileImg()).fit().into(holder.profileImg);
        }catch (Exception e){
            holder.profileImg.setImageResource(R.drawable.ic_avatar);
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    interface RequestViewClickListener{
        void requestOnClickListener(Request request);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView bookImg,profileImg;
        TextView bookText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookImg = itemView.findViewById(R.id.bookImageHolder);
            profileImg = itemView.findViewById(R.id.profile_img);
            bookText = itemView.findViewById(R.id.bookName);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int index = getAdapterPosition();
            mListener.requestOnClickListener(mData.get(index));
        }
    }
}
