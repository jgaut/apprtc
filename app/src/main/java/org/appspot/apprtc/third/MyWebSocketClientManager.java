package org.appspot.apprtc.third;

import android.util.Log;

import org.java_websocket.drafts.Draft_6455;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by jgautier on 10/01/2018.
 */

public class MyWebSocketClientManager {
    private String TAG = this.getClass().toString();
    private MyWebSocketClient myWebSocketClient;


    MyWebSocketClientManager(){
        super();
    }

    public void connect() {
        try {
            if (myWebSocketClient != null && myWebSocketClient.isOpen()) {
                myWebSocketClient.close();
            }
            myWebSocketClient = new MyWebSocketClient(new URI("ws://"+MyAppProperties.getProperty("MyWebSocket.hostname")+":"+MyAppProperties.getProperty("MyWebSocket.port")), new Draft_6455());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        myWebSocketClient.connect();
    }

    public void disconnect() {
        if (myWebSocketClient != null) {
            myWebSocketClient.close();
        }
    }

    public void send(String str){
        this.myWebSocketClient.send(str);
    }
}
