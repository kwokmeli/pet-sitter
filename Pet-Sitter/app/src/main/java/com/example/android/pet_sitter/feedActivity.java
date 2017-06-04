package com.example.android.pet_sitter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * Created by mel on 5/26/2017.
 */

public class feedActivity extends AppCompatActivity {

    String addr;
    int hourValue = 0;
    int minuteValue = 0;


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

    public void incrementHour (View v) {
        if (hourValue < 23) {
            hourValue = hourValue + 1;
        } else {
            hourValue = 0;
        }
        displayHour(hourValue);
    }

    public void decrementHour (View v) {
        if (hourValue > 0) {
            hourValue = hourValue - 1;
        } else {
            hourValue = 23;
        }
        displayHour(hourValue);
    }

    public void incrementMinutes (View v) {
        if (minuteValue < 59) {
            minuteValue = minuteValue + 1;
        } else {
            minuteValue = 0;
        }
        displayMinute(minuteValue);
    }

    public void decrementMinutes (View v) {
        if (minuteValue > 0) {
            minuteValue = minuteValue - 1;
        } else {
            minuteValue = 59;
        }
        displayMinute(minuteValue);
    }

    private void displayHour (int hour) {
        TextView hourDisplay = (TextView) findViewById(R.id.hourValue);
        if (hour < 10) {
            hourDisplay.setText("0" + hour);
        } else {
            hourDisplay.setText("" + hour);
        }
    }

    private void displayMinute (int minute) {
        TextView minuteDisplay = (TextView) findViewById(R.id.minuteValue);
        if (minute < 10) {
            minuteDisplay.setText("0" + minute);
        } else {
            minuteDisplay.setText("" + minute);
        }
    }

    public void alternateSend(View v) {
        int weight;
        Toast msg;
        EditText textGrab = (EditText) findViewById(R.id.foodData);
        String str = textGrab.getText().toString();

        // Check if input is numerical to prevent crashes
        if ( Pattern.matches("[0-9]+", str) ) {
            weight = Integer.parseInt(str);

            if ((weight >= 10) && (weight <= 250) && (weight%10 == 0)) {
                msg = Toast.makeText(getBaseContext(), "Amount valid.", Toast.LENGTH_LONG);
                msg.show();
                DataPackage data = new DataPackage(addr, str);
                new SendMessage().execute(data);
                textGrab.getText().clear();
            } else {
                msg = Toast.makeText(getBaseContext(), "Please enter a valid amount.", Toast.LENGTH_LONG);
                msg.show();
            }

        } else {
            msg = Toast.makeText(getBaseContext(), "Please enter a valid numerical value.", Toast.LENGTH_LONG);
            msg.show();
        }
    }
}