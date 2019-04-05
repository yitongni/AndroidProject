package com.example.mybudget;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class User {

    private Double budget;
    private ArrayList<Category> userCategory;
    private String email;
    private String userID;

    public User(){
        budget = 0.00;
        userCategory=new ArrayList<>();
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

    public void addCategory(Category name){
        userCategory.add(name);
    }

    public ArrayList<Category> getUserCategory() {
        return userCategory;
    }

    public boolean containsCategory(String categoryName) {
        for(int i=0; i<userCategory.size(); i++) {
            if (userCategory.get(i).getCategory().equals(categoryName)) {
                return true;
            }
        }
        return false;
    }

    public int getCategoryPosition(String categoryName) {
        for(int i=0; i<userCategory.size(); i++) {
            if (userCategory.get(i).getCategory().equals(categoryName)) {
                return i;
            }
        }
        return -1;
    }
}
