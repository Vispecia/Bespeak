package com.app.vispecia.bespeak;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewBookAdapter extends RecyclerView.Adapter<RecyclerViewBookAdapter.ViewHolder> {


    Context mContext;
    List<Book> mData;
    BookViewClickListener mListener;

    public RecyclerViewBookAdapter(Context mContext, List<Book> mData,BookViewClickListener mListener)
    {
        this.mContext = mContext;
        this.mData = mData;
        this.mListener = mListener;
    }

    public void updateData(List<Book> mData)
    {
        this.mData = mData;
        notifyDataSetChanged();
    }

    public List<Book> getBooks(){
        return mData;
    }

    interface BookViewClickListener{
        void bookOnClickListener(Book index, View view);
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        v = layoutInflater.inflate(R.layout.single_book_list_view,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bookName.setText(mData.get(position).getBookName());
        holder.authorName.setText(mData.get(position).getAuthorName());
        Picasso.get().load(mData.get(position).getImageUrl()).fit().centerCrop().into(holder.img);
        holder.exchange.setBackgroundColor(mData.get(position).isExchangable() ? Color.parseColor("#008000") : Color.parseColor("#FF0000"));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        TextView bookName, authorName;
        ImageView img;
        View exchange;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookName = itemView.findViewById(R.id.bookName);
            authorName = itemView.findViewById(R.id.bookAuthorName);
            img = itemView.findViewById(R.id.bookImageHolder);
            exchange = itemView.findViewById(R.id.exchange);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            int index = getAdapterPosition();
            mListener.bookOnClickListener(mData.get(index),exchange);
            return true;
        }
    }

}
