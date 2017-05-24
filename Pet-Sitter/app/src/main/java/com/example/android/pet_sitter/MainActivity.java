package com.example.android.pet_sitter;

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

    public void alternateSend (View v) {
        EditText textGrab = (EditText) findViewById(R.id.data);
        String str = textGrab.getText().toString();

        DataPackage data = new DataPackage(addr, str);
        new SendMessage().execute(data);
        textGrab.getText().clear();
    }


}
