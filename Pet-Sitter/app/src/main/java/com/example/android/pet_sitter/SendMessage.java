package com.example.android.pet_sitter;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by mel on 5/15/2017.
 */

public class SendMessage extends AsyncTask<DataPackage, Void, Void> {
    private Exception exception;

    @Override
    protected Void doInBackground(DataPackage... params) {
        String addr = params[0].address;

        try {
            try {
                Socket socket = new Socket(addr, 9999);
                PrintWriter outToServer = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
                outToServer.print(params[0].data);
                outToServer.flush();
            } catch (IOException e) {
                Log.d("tag", "stuck in catch ioexception e");
                e.printStackTrace();
                Log.e("detailed tag", "ioexception", e);
            }
        } catch (Exception e) {
            Log.d("tag2", "stuck in catch exception e");
            this.exception = e;
            return null;
        }
        Log.d("tag3", "stuck in last return");
        return null;

    }
}

