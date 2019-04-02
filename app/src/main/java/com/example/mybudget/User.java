package com.example.mybudget;

import java.util.ArrayList;

public class User {

    private double budget;
    private ArrayList<Category> userCategory =new ArrayList<>();
    private String email;
    private String userID;

    public User(){}

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
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

    public void setUserCategory(ArrayList<Category> userCategory) {
        this.userCategory = userCategory;
    }

    public ArrayList<Category> getUserCategory() {
        return userCategory;
    }
}
