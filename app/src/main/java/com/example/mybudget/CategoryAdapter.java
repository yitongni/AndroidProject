package com.example.mybudget;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Double> {
    public CategoryAdapter(Context context, ArrayList<Double> categories) {
        super(context, 0, categories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_layout, parent, false);
        }

        TextView cost = convertView.findViewById(R.id.cost);

        Double categoryCost=getItem(position);

        cost.setText("Total Spent: " + String.format("%.2f", categoryCost));

        return convertView;
    }
}
