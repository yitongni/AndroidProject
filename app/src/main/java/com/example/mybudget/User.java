package com.example.mybudget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;

public class User implements Serializable {

    private Double budget;
    private HashMap<String, ArrayList<Category>> userExpenses=new HashMap<>();
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

    public void addCategory(String categoryName, String description, Double cost, String id, String date){
        Category category=new Category(description, cost, id, categoryName, date);
        ArrayList<Category> categories=new ArrayList<>();
        categories.add(category);
        if(this.userExpenses.get(categoryName)==null){
            this.userExpenses.put(categoryName, categories);
        }
        else{
            this.userExpenses.get(categoryName).add(category);
        }
    }

    public HashMap<String, ArrayList<Category>> getUserCategory() {
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

//    public Double getTotalSpent(){
//        Double totalSpent=0.00;
//        for(Category value: userExpenses.values()){
//            totalSpent+=value.getTotalCost();
//        }
//        return totalSpent;
//    }
}
