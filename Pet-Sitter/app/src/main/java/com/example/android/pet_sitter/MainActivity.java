package com.example.android.pet_sitter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    String addr = "0.0.0.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void updateIP (View v) {
        EditText input = (EditText) findViewById(R.id.ipInput);
        addr = input.getText().toString();

        Toast msg = Toast.makeText(getBaseContext(), addr, Toast.LENGTH_LONG);
        msg.show();
    }

    /*public void alternateSend (View v) {
        EditText textGrab = (EditText) findViewById(R.id.data);
        String str = textGrab.getText().toString();

        DataPackage data = new DataPackage(addr, str);
        new SendMessage().execute(data);
        textGrab.getText().clear();
    }*/

    public void openFeed(View view) {
        Intent iFeed = new Intent(this, feedActivity.class);
        startActivity(iFeed);
    }

    public void openConnect(View view) {
        //Intent iConnect = new Intent(this, connectActivity.class);
        //startActivity(iConnect);
        String url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

        Intent iConnect = new Intent(getApplicationContext(), connectActivity.class);
        iConnect.putExtra("videoUrl", url);
        startActivity(iConnect);
    }


}
