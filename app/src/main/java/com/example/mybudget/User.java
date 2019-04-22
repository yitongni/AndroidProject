package com.example.mybudget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;

public class User implements Serializable {

    private Double budget;
    private HashMap<String, Category> userExpenses=new HashMap<>();
    private String email;
    private String userID;

    public User(){
        budget = 0.00;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public String getEmail() {
        return email;
    }

    public String getUserID() {
        return userID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void addCategory(String categoryName, Double cost){
        Category category=new Category(categoryName);
        category.addCost(cost);

        if(this.userExpenses.get(categoryName)==null){
            this.userExpenses.put(categoryName, category);
        }
        else{
            this.userExpenses.get(categoryName).addCost(cost);
        }
    }

    public HashMap<String, Category> getUserCategory() {
        return this.userExpenses;
    }

//    public boolean containsCategory(String categoryName) {
//        for(int i=0; i<userCategory.size(); i++) {
//            if (userCategory.get(i).getCategory().equals(categoryName)) {
//                return true;
//            }
//        }
//        return false;
//    }

//    public int getCategoryPosition(String categoryName) {
//        for(int i=0; i<userCategory.size(); i++) {
//            if (userCategory.get(i).getCategory().equals(categoryName)) {
//                return i;
//            }
//        }
//        return -1;
//    }

    public Double getTotalSpent(){
        Double totalSpent=0.00;
        for(Category value: userExpenses.values()){
            totalSpent+=value.getTotalCost();
        }
        return totalSpent;
    }
}
