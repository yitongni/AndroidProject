package com.example.mybudget;

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

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        Category category = getRowData(rowIndex);
        View renderedView = LayoutInflater.from(getContext()).inflate(R.layout.category_layout, parentView, false);
        switch (columnIndex) {
            case 0:

                TextView textView=(TextView)renderedView.findViewById(R.id.description);
                if(textView.getParent() != null) {
                    ((ViewGroup)textView.getParent()).removeView(textView); // <- fix
                }
                textView.setText(category.getDescription());
                renderedView = textView;
                break;
            case 1:
                TextView textViewCost=(TextView)renderedView.findViewById(R.id.cost);
                if(textViewCost.getParent() != null) {
                    ((ViewGroup)textViewCost.getParent()).removeView(textViewCost); // <- fix
                }
                textViewCost.setText(category.getCost().toString());
                renderedView = textViewCost;
                break;
            case 2:
                TextView textViewDate=(TextView)renderedView.findViewById(R.id.date);
                if(textViewDate.getParent() != null) {
                    ((ViewGroup)textViewDate.getParent()).removeView(textViewDate); // <- fix
                }
                textViewDate.setText(category.getDate());
                renderedView = textViewDate;
                break;
//            case 3:
//                renderedView = renderPrice(car);
//                break;
        }

        return renderedView;
    }
}