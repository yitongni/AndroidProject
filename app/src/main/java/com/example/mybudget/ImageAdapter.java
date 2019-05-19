package com.example.mybudget;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private static final String TAG = "ImageAdapter";
    private Context mContext;

    private ArrayList<ImageInformation> receipts=new ArrayList<>();
    public ImageAdapter(Context c, ArrayList<ImageInformation> receipt) {
        mContext = c;
        receipts=receipt;
    }

    @Override
    public int getCount() {
        return receipts.size();
    }

    @Override
    public Object getItem(int position) {
        return receipts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // create a new ImageView for each item referenced by the Adapter
        Log.d(TAG, receipts.get(position).getUri());
        ImageView imageView;
        if(convertView==null){
            imageView=new ImageView(mContext);
        }
        else{
            imageView=(ImageView)convertView;
        }

        Picasso.with(mContext).load(receipts.get(position).getUri()).into(imageView);

        imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setAdjustViewBounds(true);

        imageView.setFocusable(false);
        imageView.setClickable(false);
        return imageView;
    }
}