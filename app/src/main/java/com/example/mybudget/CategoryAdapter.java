package com.example.mybudget;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> {
    public CategoryAdapter(Context context, ArrayList<Category> categories) {
        super(context, 0, categories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_layout, parent, false);
        }

        Category category=getItem(position);

        TextView cost = convertView.findViewById(R.id.cost);
        Double categoryCost=category.getCost();
        cost.setText(String.format(getContext().getString(R.string.totalCost), categoryCost.toString()));

        TextView description =convertView.findViewById(R.id.description);
        String descript=category.getDescription();
        description.setText(descript);

        TextView date=convertView.findViewById(R.id.date);
        String date1=category.getDate();
        date.setText(date1);

        return convertView;
    }
}
