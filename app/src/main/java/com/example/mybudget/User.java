package com.example.mybudget;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

public class User implements Serializable {

    //TAG
    private static final String TAG = "User";

    //Private Variables
    private HashMap<String, ArrayList<Category>> userExpenses=new HashMap<>();
    private String email;
    private String userID;

    //Getter Functions
    public String getEmail() {
        return email;
    }

    public String getUserID() {
        return userID;
    }

    //Setter Functions
    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    //Adds a category
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

    //Returns user expenses
    public HashMap<String, ArrayList<Category>> getUserCategory() {
        return this.userExpenses;
    }

    //Calculates the total the user has spent
    public Double getTotalSpent(){
        Double totalSpent=0.0;
        for (Map.Entry<String, ArrayList<Category>> entry : userExpenses.entrySet()) {

            String key = entry.getKey();
            Log.d(TAG, "KEY: " + key);

            ArrayList<Category> categories=entry.getValue();
            for(int i=0; i<categories.size(); i++){
                Log.d(TAG, "Cost: " + categories.get(i).getCost());
                totalSpent+=categories.get(i).getCost();
            }
        }
        return totalSpent;
    }

    //Calculates how much a user spent on a single category
    public Double getTotalSpentForSingleCategory(String category){
        Double totalSpent=0.0;
        for (int i=0; i<userExpenses.get(category).size(); i++) {

            Log.d(TAG, "Cost: " + userExpenses.get(category).get(i).getCost());
            totalSpent+=userExpenses.get(category).get(i).getCost();
        }
        return totalSpent;
    }
}
