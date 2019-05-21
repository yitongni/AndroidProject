package com.example.mybudget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.TableDataAdapter;

public class CategoryTableDataAdapter extends TableDataAdapter<Category> {

    public CategoryTableDataAdapter(Context context, ArrayList<Category> data) {
        super(context, data);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        Category category = getRowData(rowIndex);

        View renderedView=null;
        switch (columnIndex) {
            case 0:
                //Set the description
                TextView textView=new TextView(getContext());
                textView.setText(category.getDescription());
                textView.setTextSize(25);
                renderedView=textView;
                break;
            case 1:
                //Set the cost
                TextView textViewCost=new TextView(getContext());
                textViewCost.setText(category.getCost().toString());
                textViewCost.setTextSize(25);
                renderedView=textViewCost;
                break;
            case 2:
                //Set the category
                TextView textViewCategory=new TextView(getContext());
                textViewCategory.setText(category.getDate());
                textViewCategory.setTextSize(25);
                renderedView=textViewCategory;
                break;
        }
        return renderedView;
    }
}