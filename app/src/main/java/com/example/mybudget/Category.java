package com.example.mybudget;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {

    private String category;
    private ArrayList<Double> cost=new ArrayList<>();

    public Category(){
        //cost=new ArrayList<>();
    }

    public Category(String name){
        this.category=name;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void deleteCost(int position){
        cost.remove(position);
    }

    public ArrayList<Double> getCost() {
        return this.cost;
    }

    public void setCost(ArrayList<Double> cost) {
        this.cost = cost;
    }

    public void addCost(Double mycost) {
        this.cost.add(mycost);
    }

    public Double getTotalCost() {
        Double totalCost=0.00;
        for(int i=0; i<this.cost.size(); i++){
            totalCost+=this.cost.get(i);
        }
        return totalCost;
    }

}
