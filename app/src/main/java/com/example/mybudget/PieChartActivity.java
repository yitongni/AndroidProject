package com.example.mybudget;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.api.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class PieChartActivity extends AppCompatActivity {

    private User user=new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piechart);

        if(getIntent().hasExtra("MyClass")){
            user=(User)getIntent().getSerializableExtra("MyClass");
        }

        PieChartView pieChartView = findViewById(R.id.chart);

        List<SliceValue> pieData = new ArrayList<>();
//        PieChart chart = (PieChart) findViewById(R.id.chart);
//        chart.setEntryLabelColor(R.color.pink);
//        List<PieEntry> entries = new ArrayList<>();
//
        Double spent=user.getTotalSpent();
        float totalSpent = spent.floatValue();
        for(int i=0; i<user.getUserCategory().size(); i++)
        {
            Double cost=user.getUserCategory().get(i).getTotalCost();
            float totalCost = cost.floatValue();
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            pieData.add(new SliceValue(totalCost, color).setLabel(user.getUserCategory().get(i).getCategory()+": "+ String.valueOf(totalCost)));
        }


        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(20);
        pieChartView.setPieChartData(pieChartData);
//        PieDataSet set = new PieDataSet(entries, "My Budget");
//        set.setValueTextSize(16f);
//        set.setColors(new int[] { R.color.red, R.color.blue, R.color.green, R.color.yellow, R.color.pink});
//        PieData data = new PieData(set);
//        chart.setData(data);
//        chart.invalidate(); // refresh
    }
}
