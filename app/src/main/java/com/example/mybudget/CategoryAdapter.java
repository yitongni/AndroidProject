package com.example.mybudget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> {
    public CategoryAdapter(Context context, ArrayList<Category> crimes) {
        super(context, 0, crimes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_layout, parent, false);
        }

        TextView description = convertView.findViewById(R.id.category);

        Category category=getItem(position);

        String name= category.getCategory();

        description.setText(name);

        return convertView;
    }
}
