package com.example.mybudget;

import java.util.ArrayList;

public class Category {

    private String category;
    private ArrayList<Double> cost;

    public Category(){}

    public Category(String name){
        this.category=name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void addCost(double cost)
    {
        this.cost.add(cost);
    }
}
