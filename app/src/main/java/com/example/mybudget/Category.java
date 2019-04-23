package com.example.mybudget;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {

    private String category;
    private Double cost;

    public Category(){}

    public Category(String name, Double cost1){
        this.category=name;
        this.cost=cost1;
    }

    public String getCategory() {
        return this.category;
    }

    public Double getCost() {
        return this.cost;
    }

}
