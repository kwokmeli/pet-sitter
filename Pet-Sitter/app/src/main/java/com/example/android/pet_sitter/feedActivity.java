package com.example.android.pet_sitter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by mel on 5/26/2017.
 */

public class feedActivity extends AppCompatActivity {

    int foodAmount = 10;
    String addr = "0.0.0.0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        addr = getIntent().getExtras().getString("ip");
    }

   /* public void incrementFood (View v) {
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
    }*/

    public void alternateSend(View v) {
        EditText textGrab = (EditText) findViewById(R.id.foodData);
        String str = textGrab.getText().toString();
        int weight = Integer.parseInt(str);

        if ((weight >= 10) && (weight <= 250) && (weight%10 == 0)) {
            DataPackage data = new DataPackage(addr, str);
            new SendMessage().execute(data);
            textGrab.getText().clear();
        } else {
            Toast msg = Toast.makeText(getBaseContext(), "Please enter a valid amount.", Toast.LENGTH_LONG);
            msg.show();
        }
    }
}