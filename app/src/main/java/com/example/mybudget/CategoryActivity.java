package com.example.mybudget;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataLongClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

public class CategoryActivity extends AppCompatActivity {

    private static final String TAG = "CategoryActivity";
    private TextView mTextView, totalSpentTextView;
    private FirebaseUser myuser;
    private String category;
    private ArrayList<Category> categories=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        mTextView=(TextView)findViewById(R.id.textViewCategory);
        totalSpentTextView=(TextView)findViewById(R.id.textViewTotalSpent);
        myuser= FirebaseAuth.getInstance().getCurrentUser();
        getCategories();
    }

    //Getting Category
    private void getCategories(){
        Log.d(TAG, "Initiating");
        Intent intent = getIntent();
        if (intent.hasExtra("Category")) {
            category = intent.getStringExtra("Category");
        }
        retrieveInformation();
    }

    //Delete a specific item on the table
    private void deleteCost(final int position){
        Log.d(TAG, "Deleting");
        //cost.remove(position);
        final String id=categories.get(position).getId();
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("/").child("users").child(myuser.getUid()).child("Category").child(category).child(id);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                databaseReference.removeValue();
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

    }

    //Retrieve specific category from the database
    private void retrieveInformation(){
        Query query= FirebaseDatabase.getInstance().getReference("/").child("users").child(myuser.getUid()).child("Category").child(category);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Retrieving from database");
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Category category=new Category(postSnapshot.child("description").getValue(String.class), postSnapshot.child("cost").getValue(Double.class), postSnapshot.child("id").getValue(String.class), postSnapshot.child("category").getValue(String.class), postSnapshot.child("date").getValue(String.class));
                    categories.add(category);
                }
                for(int i=0; i<categories.size(); i++){
                    Log.d(TAG, categories.get(i).getCategory());
                    Log.d(TAG, categories.get(i).getCost().toString());
                    Log.d(TAG, categories.get(i).getDescription());
                    Log.d(TAG, categories.get(i).getId());
                }
                populateView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //Calculate total spent for this category
    private void calculateTotalSpent(){
        Double totalspent=0.00;
        for(int i=0; i<categories.size(); i++){
            totalspent+=categories.get(i).getCost(); //Calculate user total price
        }
        totalSpentTextView.setText(getString(R.string.totalCost,String.format("%.2f", totalspent)));
    }

    private void populateView(){
        Log.d(TAG, "Populating View");

        mTextView.setText(category);

        calculateTotalSpent();

        final String[] TABLE_HEADERS = { "Description", "Cost", "Date"};

        SortableTableView<Category> sortableTableView = (SortableTableView<Category>) findViewById(R.id.tableView);
        final CategoryTableDataAdapter categoryTableDataAdapter=new CategoryTableDataAdapter(this, categories);
        sortableTableView.setDataAdapter(categoryTableDataAdapter);

        //Set the header
        sortableTableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, TABLE_HEADERS));

        //Set row colors
        int colorEvenRows = getResources().getColor(R.color.grey);
        int colorOddRows = getResources().getColor(R.color.white);
        sortableTableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

        //Set comparators so we can sort the rows
        sortableTableView.setColumnComparator(0, new CategoryDescriptionComparator());
        sortableTableView.setColumnComparator(1, new CategoryCostComparator());
        sortableTableView.setColumnComparator(2, new CategoryDateComparator());

        //Click and hold on a row to delete it
        sortableTableView.addDataLongClickListener(new TableDataLongClickListener<Category>() {
            @Override
            public boolean onDataLongClicked(int rowIndex, Category clickedData) {
                deleteCost(rowIndex);
                categories.remove(rowIndex);
                categoryTableDataAdapter.notifyDataSetChanged();
                calculateTotalSpent();
                return true;
            }
        });
    }

    //Comparator for table to sort by cost
    private static class CategoryCostComparator implements Comparator<Category> {
        @Override
        public int compare(Category category1, Category category2) {
            return category1.getCost().compareTo(category2.getCost());
        }
    }

    //Comparator for table to sort by description
    private static class CategoryDescriptionComparator implements Comparator<Category> {
        @Override
        public int compare(Category category1, Category category2) {
            return category1.getDescription().compareTo(category2.getDescription());
        }
    }

    //Comparator for table to sort by date
    private static class CategoryDateComparator implements Comparator<Category> {
        @Override
        public int compare(Category category1, Category category2) {
            DateFormat dateFormat=new SimpleDateFormat("dd/mm/yyyy");
            try {
                return dateFormat.parse(category1.getDate()).compareTo(dateFormat.parse(category2.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}
