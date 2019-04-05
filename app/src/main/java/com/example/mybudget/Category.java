package com.example.mybudget;

import java.util.ArrayList;

public class Category {

    private String category;
    private ArrayList<Double> cost=new ArrayList<>();

    public Category(){
        //cost=new ArrayList<>();
    }

    public Category(String name){
        this.category=name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ArrayList<Double> getCost() {
        return cost;
    }

    public void setCost(ArrayList<Double> cost) {
        this.cost = cost;
    }

    public void addCost(Double mycost) {
        cost.add(mycost);
    }

    public Double getTotalCost() {
        Double totalCost=0.00;
        for(int i=0; i<cost.size(); i++){
            totalCost+=cost.get(i);
        }
        return totalCost;
    }

}
