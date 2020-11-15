package com.app.vispecia.bespeak;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class BookAdapter /*extends ArrayAdapter<Book>*/ {

//
//    private Context mContext;
//    private int layoutResourceId;
//    private ArrayList data = new ArrayList();
//
//    public BookAdapter(Context context, int layoutResourceId, ArrayList data) {
//        super(context, layoutResourceId, data);
//        this.layoutResourceId = layoutResourceId;
//        this.mContext = context;
//        this.data = data;
//    }
//
//    static class ViewHolder {
//        TextView bookTitle;
//        TextView bookAuthornName;
//      //  ImageView image;
//    }
//
//    @Override
//    // Create a new ImageView for each item referenced by the Adapter
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        ViewHolder holder = null;
//        Book book = getItem(position);
//
//        if (convertView == null) {
//            // If it's not recycled, initialize some attributes
//            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
//            convertView = inflater.inflate(layoutResourceId, parent, false);
//            holder = new ViewHolder();
//            holder.bookTitle = (TextView) convertView.findViewById(R.id.bookName);
//            holder.bookAuthornName = (TextView) convertView.findViewById(R.id.bookAuthorName);
//          //  holder.image = (ImageView) convertView.findViewById(R.id.image);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();;
//        }
//
//        holder.bookTitle.setText(book.getBookName());
//        holder.bookAuthornName.setText(book.getAuthorName());
//       // holder.image.setImageResource(currentTea.getImageResourceId());
//        return convertView;
//    }
//


}
