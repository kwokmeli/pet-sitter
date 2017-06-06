package com.example.android.pet_sitter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    //addr = ip address of rpi
    String addr = "172.20.10.9";
    int hourValue = 0;
    int minuteValue = 0;

    //new
    private ServerSocket serverSocket;
    Handler updateConversationHandler;
    Thread serverThread = null;
    //private TextView text;
    public static final int SERVERPORT = 9999;
    //new

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //new
        //text = (TextView) findViewById(R.id.text2);
        updateConversationHandler = new Handler();
        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();
        //new
    }

    //new

    /* try
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            serverSocket.close();
            Log.d("test", "activity destroyed and socket closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        updateConversationHandler = new Handler();
        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();
        Log.d("test", "activity resumed");
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ServerThread implements Runnable {

        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {

                try {
                    socket = serverSocket.accept();

                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class CommunicationThread implements Runnable {

        private Socket clientSocket;
        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;
            Log.d("test", "made new socket");
            try {

                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            Log.d("test", "running");
            while (!Thread.currentThread().isInterrupted()) {

                try {
                    String read = input.readLine();
                    Log.d("test", "read in: " + read);
                    if (read != null) {
                        updateConversationHandler.post(new updateUIThread(read));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("test", "", e);
                }
            }
            Log.d("test", "i was interrupted!");
        }

    }

    class updateUIThread implements Runnable {
        private String msg;

        public updateUIThread(String str) {
            this.msg = str;
        }

        @Override
        public void run() {
            int foodStorage;
            int waterStorage;
            String delims = "[,]";
            TextView foodDisplay = (TextView) findViewById(R.id.updateFood);
            TextView waterDisplay = (TextView) findViewById(R.id.updateWater);
            TextView tempDisplay = (TextView) findViewById(R.id.updateTemp);
            TextView foodWarning = (TextView) findViewById(R.id.foodWarning);
            TextView waterWarning = (TextView) findViewById(R.id.waterWarning);

            String[] tokens = msg.split(delims);

            if (tokens[0].equals("FOOD")) {
                foodDisplay.setText("Current weight of food: " + tokens[1] + " g");
            } else if (tokens[0].equals("WATER")) {
                waterDisplay.setText("Current percent of water: " + tokens[1] + "%");
            } else if (tokens[0].equals("TEMP")){
                tempDisplay.setText("Current system temperature: " + tokens[1] + "°C");
            } else if (tokens[0].equals("STORAGE-FOOD")) {
                foodStorage = Integer.parseInt(tokens[1]);
                if (foodStorage < 400) {
                    foodWarning.setText("Warning: Low food. Only " + tokens[1] + " grams left.");
                } else {
                    foodWarning.setText("");
                }
            } else if (tokens[0].equals("STORAGE-WATER")) {
                waterStorage = Integer.parseInt(tokens[1]);
                if (waterStorage < 25) {
                    waterWarning.setText("Warning: Low water. Only " + tokens[1] + "% left.");
                } else {
                    waterWarning.setText("");
                }
            } else {

            }

            //waterDisplay.setText("Current percent of water: " + msg);
            //text.setText(text.getText().toString()+"Client Says: "+ msg + "\n");
        }
    }
    //new


    /*public void updateIP (View v) {
        EditText input = (EditText) findViewById(R.id.ipInput);
        addr = input.getText().toString();

        Toast msg = Toast.makeText(getBaseContext(), addr, Toast.LENGTH_LONG);
        msg.show();
    }*/

    /*public void alternateSend (View v) {
        EditText textGrab = (EditText) findViewById(R.id.data);
        String str = textGrab.getText().toString();

        DataPackage data = new DataPackage(addr, str);
        new SendMessage().execute(data);
        textGrab.getText().clear();
    }

    public void openFeed(View view) {
        Intent iFeed = new Intent(getApplicationContext(), feedActivity.class);
        iFeed.putExtra("ip", addr);
        startActivity(iFeed);
    }

    public void openWater(View view) {
        Intent iWater = new Intent(getApplicationContext(), waterActivity.class);
        iWater.putExtra("ip", addr);
        startActivity(iWater);
    }*/

    public void ready(View v) {
        DataPackage data = new DataPackage(addr, "ready");
        new SendMessage().execute(data);
        Toast msg = Toast.makeText(getBaseContext(), "System ready to receive commands.", Toast.LENGTH_LONG);
        msg.show();
    }

    public void openConnect(View view) {
        String url = "rtsp://";
        url = url.concat(addr).concat(":8554/x");

        Intent iConnect = new Intent(getApplicationContext(), connectActivity.class);
        iConnect.putExtra("videoUrl", url);
        startActivity(iConnect);
    }

    /********************************
     *                              *
     *                              *
     *                              *
     *                              *
     *             FOOD             *
     *                              *
     *                              *
     *                              *
     *                              *
     ********************************/
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

    public void foodSend(View v) {
        int weight;
        Toast msg;
        EditText textGrab = (EditText) findViewById(R.id.foodData);
        String strWeight = textGrab.getText().toString();

        // Check if input is numerical to prevent crashes
        if ( Pattern.matches("[0-9]+", strWeight) ) {
            weight = Integer.parseInt(strWeight);

            if ((weight >= 10) && (weight <= 200) && (weight%10 == 0)) {
                msg = Toast.makeText(getBaseContext(), "Amount valid.", Toast.LENGTH_LONG);
                msg.show();
                DataPackage data = new DataPackage(addr, "FOOD," + strWeight);
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

    public void setSchedule(View v) {
        int times;
        int weight;
        Toast msgTimes;
        EditText textGrab1 = (EditText) findViewById(R.id.foodTimes);
        EditText textGrab2 = (EditText) findViewById(R.id.foodSchedule);
        String strTimes = textGrab1.getText().toString();
        String strWeight = textGrab2.getText().toString();

        // Check if input is numerical AND an integer to prevent crashes
        if ( Pattern.matches("[0-9]+", strTimes) && (Pattern.matches("[0-9]+", strWeight)) ) {
            times = Integer.parseInt(strTimes);
            weight = Integer.parseInt(strWeight);

            if ((times >= 1) && (times <= 24) && (weight >= 10) && (weight <= 200) && (weight % 10 == 0)) {
                msgTimes = Toast.makeText(getBaseContext(), "Schedule valid.", Toast.LENGTH_LONG);
                msgTimes.show();

                if (minuteValue < 10) {
                    DataPackage dataTimes = new DataPackage(addr, "START," + hourValue + ":0" + minuteValue + ",FREQ," + strTimes + ",FOOD," + strWeight);
                    new SendMessage().execute(dataTimes);
                } else {
                    DataPackage dataTimes = new DataPackage(addr, "START," + hourValue + ":" + minuteValue + ",FREQ," + strTimes + ",FOOD," + strWeight);
                    new SendMessage().execute(dataTimes);
                }

                textGrab1.getText().clear();
                textGrab2.getText().clear();
            } else if ( (times < 1) || (times > 24) ){
                msgTimes = Toast.makeText(getBaseContext(), "Please enter a number between 1 and 24.", Toast.LENGTH_LONG);
                msgTimes.show();
            } else if ( (weight < 10) || (weight > 200) || (weight % 10 != 0)) {
                msgTimes = Toast.makeText(getBaseContext(), "Please enter a number between 10 and 200.", Toast.LENGTH_LONG);
                msgTimes.show();
            }
        } else {
            msgTimes = Toast.makeText(getBaseContext(), "Please enter valid numerical values.", Toast.LENGTH_LONG);
            msgTimes.show();
        }
    }
    /********************************/

    /********************************
     *                              *
     *                              *
     *                              *
     *                              *
     *            WATER             *
     *                              *
     *                              *
     *                              *
     *                              *
     ********************************/
    public void waterSend(View v) {
        int delay;
        Toast msgDelay;
        EditText textGrab = (EditText) findViewById(R.id.waterData);
        String strDelay = textGrab.getText().toString();

        // Check if input is numerical to prevent crashes
        if ( Pattern.matches("[0-9]+", strDelay) ) {
            delay = Integer.parseInt(strDelay);

            if ((delay >= 0) && (delay <= 10)) {
                msgDelay = Toast.makeText(getBaseContext(), "Amount valid.", Toast.LENGTH_LONG);
                msgDelay.show();
                DataPackage data = new DataPackage(addr, "WATER," + strDelay);
                new SendMessage().execute(data);
                textGrab.getText().clear();
            } else {
                msgDelay = Toast.makeText(getBaseContext(), "Please enter a valid amount.", Toast.LENGTH_LONG);
                msgDelay.show();
            }

        } else {
            msgDelay = Toast.makeText(getBaseContext(), "Please enter a valid number of minutes.", Toast.LENGTH_LONG);
            msgDelay.show();
        }
    }
    /*********************************/

    /********************************
     *                              *
     *                              *
     *                              *
     *                              *
     *          CALCULATOR          *
     *                              *
     *                              *
     *                              *
     *                              *
     ********************************/
    public void calculate(View v) {
        double weight;
        double calories;
        int amount;
        Toast msg;
        EditText textGrab = (EditText) findViewById(R.id.petWeight);
        String strWeight = textGrab.getText().toString();
        EditText textGrab2 = (EditText) findViewById(R.id.calories);
        String strCalories = textGrab2.getText().toString();

        if ( (Pattern.matches("[0-9]+", strWeight)) && (Pattern.matches("[0-9]+", strCalories)) ) {
            weight = Double.parseDouble(strWeight);
            calories = Double.parseDouble(strCalories);
            if (calories == 0.0) {
                msg = Toast.makeText(getBaseContext(), "Please enter a valid caloric density.", Toast.LENGTH_LONG);
                msg.show();
            } else {
                amount = (int) weight * 30 / (int) calories;

                TextView amountDisplay = (TextView) findViewById(R.id.recommendedDose);
                amountDisplay.setText(" " + amount + " g");
            }

        } else if ( !(Pattern.matches("[0-9]+", strWeight)) ){
            msg = Toast.makeText(getBaseContext(), "Please enter a valid weight.", Toast.LENGTH_LONG);
            msg.show();
        } else {
            msg = Toast.makeText(getBaseContext(), "Please enter a valid caloric density.", Toast.LENGTH_LONG);
            msg.show();
        }
    }




}
