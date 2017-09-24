package org.appspot.apprtc.third;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * Created by jeremy on 15/01/2017.
 */

public class IftttHttpRequest extends AsyncTask<String, Void, String> {

    private String TAG = this.getClass().toString();
    protected String doInBackground(String... events){

        for (String event : events) {
            String url = "https://maker.ifttt.com/trigger/"+event+"/with/key/";
            String charset = java.nio.charset.StandardCharsets.UTF_8.name();  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
            URLConnection connection = null;
            try {
                connection = new URL(url + MyAppProperties.getProperty("iftttKey")).openConnection();
                Log.i(TAG, connection.getURL().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.setRequestProperty("Accept-Charset", charset);
            try {
                InputStream response = connection.getInputStream();
                Scanner s = new Scanner(response).useDelimiter("\\A");
                String result = s.hasNext() ? s.next() : "";
                Log.i(TAG, result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
