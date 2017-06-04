package com.example.android.pet_sitter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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

    String addr = "192.168.0.66";
    int hourValue = 0;
    int minuteValue = 0;

    //new
    private ServerSocket serverSocket;
    Handler updateConversationHandler;
    Thread serverThread = null;
    private TextView text;
    public static final int SERVERPORT = 9999;
    //new

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //new
        text = (TextView) findViewById(R.id.text2);
        updateConversationHandler = new Handler();
        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();
        //new
    }

    //new
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

            try {

                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {

            while (!Thread.currentThread().isInterrupted()) {

                try {

                    String read = input.readLine();
                    if (read != null) {
                        if (read.equals("hello")) {
                            updateConversationHandler.post(new updateUIThread("i saw hello"));
                        } else {
                            updateConversationHandler.post(new updateUIThread(read));
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    class updateUIThread implements Runnable {
        private String msg;

        public updateUIThread(String str) {
            this.msg = str;
        }

        @Override
        public void run() {
            TextView currentDisplay = (TextView) findViewById(R.id.updateWater);
            currentDisplay.setText("Current percent of water: " + msg);
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

    public void openConnect(View view) {
        String url = "rtsp://";
        url = url.concat(addr).concat(":8554/x");

        Intent iConnect = new Intent(getApplicationContext(), connectActivity.class);
        iConnect.putExtra("videoUrl", url);
        startActivity(iConnect);
    }

    /**************FOOD*************/
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

            if ((weight >= 10) && (weight <= 250) && (weight%10 == 0)) {
                msg = Toast.makeText(getBaseContext(), "Amount valid.", Toast.LENGTH_LONG);
                msg.show();
                DataPackage data = new DataPackage(addr, strWeight);
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
        Toast msgTimes;
        EditText textGrab = (EditText) findViewById(R.id.foodTimes);
        String strTimes = textGrab.getText().toString();

        // Check if input is numerical AND an integer to prevent crashes
        if ( Pattern.matches("[0-9]+", strTimes) ) {
            times = Integer.parseInt(strTimes);

            if ((times >= 1) && (times <= 24)) {
                msgTimes = Toast.makeText(getBaseContext(), "Number of times valid.", Toast.LENGTH_LONG);
                msgTimes.show();
                DataPackage dataTimes = new DataPackage(addr, hourValue + ":" + minuteValue + "times" + strTimes);
                new SendMessage().execute(dataTimes);
                textGrab.getText().clear();
            } else {
                msgTimes = Toast.makeText(getBaseContext(), "Please enter a number between 1 and 24.", Toast.LENGTH_LONG);
                msgTimes.show();
            }
        } else {
            msgTimes = Toast.makeText(getBaseContext(), "Please enter a valid numerical value.", Toast.LENGTH_LONG);
            msgTimes.show();
        }
    }
    /*********************************/


}
