package com.example.mybudget;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {

    private String description;
    private Double cost;
    private String id;
    private String category;
    private String date;

    public Category(){}

    public Category(String name, Double cost1, String id1, String category1, String date1){
        this.description=name;
        this.cost=cost1;
        this.id=id1;
        this.category=category1;
        this.date=date1;
    }

    public String getDescription() {
        return this.description;
    }

    public Double getCost() {
        return this.cost;
    }

    public String getId(){
        return this.id;
    }

    public String getCategory(){
        return this.category;
    }

    public String getDate() {
        return date;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }
}
