package org.appspot.apprtc.third;

import org.java_websocket.drafts.Draft_6455;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by jgautier on 10/01/2018.
 */

public class MyWebSocketClientManager extends Thread{

    private MyWebSocketClient myWebSocketClient;

    public MyWebSocketClient getMyWebSocketClient() {
        return myWebSocketClient;
    }

    public void setMyWebSocketClient(MyWebSocketClient myWebSocketClient) {
        this.myWebSocketClient = myWebSocketClient;
    }

    MyWebSocketClientManager(){}

    public void connect(int timeout) {
        try {
            if (myWebSocketClient != null && myWebSocketClient.isOpen()) {
                myWebSocketClient.close();
            }
            myWebSocketClient = new MyWebSocketClient(new URI("ws://"+MyAppProperties.getProperty("MyWebSocket.hostname")+":"+MyAppProperties.getProperty("MyWebSocket.port")), new Draft_6455());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        myWebSocketClient.connect();
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
