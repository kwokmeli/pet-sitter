package com.example.android.pet_sitter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by mel on 5/26/2017.
 */

public class feedActivity extends AppCompatActivity {

    int foodAmount = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
    }

    public void incrementFood (View v) {
        if (foodAmount < 250) {
            foodAmount = foodAmount + 10;
        } else {
            return;
        }
        displayFoodQuantity(foodAmount);
    }

    public void decrementFood (View v) {
        if (foodAmount > 10) {
            foodAmount = foodAmount - 10;
        } else {
            return;
        }
        displayFoodQuantity(foodAmount);
    }

    private void displayFoodQuantity (int amount) {
        TextView foodQuantTextView = (TextView) findViewById(R.id.foodAmount);
        foodQuantTextView.setText("" + amount);
    }
}
