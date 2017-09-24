package org.appspot.apprtc.third;

import android.app.Activity;
import android.util.Log;

import org.appspot.apprtc.ConnectActivity;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Random;

/**
 * Created by jeremy on 09/06/2017.
 */

/*
https://github.com/TooTallNate/Java-WebSocket/blob/master/src/main/example/ChatServer.java
*/

public class MyWebSocketServer extends WebSocketServer {

    private MyGpio myGpio;
    private MyTts myTts;
    private String TAG = this.getClass().toString();

    public MyWebSocketServer(int port, MyGpio myGpio, MyTts myTts) throws UnknownHostException {
        super(new InetSocketAddress(port));
        this.myGpio = myGpio;
        this.myTts = myTts;
    }

    public MyWebSocketServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public MyWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    public MyWebSocketServer(InetSocketAddress address, MyGpio myGpio) {
        super(address);
        this.myGpio = myGpio;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        MyLog.logEvent("WebSocketServer opened=" + handshake.getResourceDescriptor());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        MyLog.logEvent("WebSocketServer closed;code=" + code + ";reason=" + reason + ";remote=" + remote);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        MyLog.logEvent("WebSocketServer received message=" + message);
        if (message != null) {
            this.sendToAll("echo : "+ message);
            if (message.equals("open door")) {
                myGpio.openDoor();
            } else if (message.equals("echo")) {

            } else if (message.equals("ring")) {
                myGpio.getmGpio24Callback().onGpioEdge(myGpio.getGpio18());
                //myTts.speak("Someone ringing the bell!");

            } else if (message.startsWith("intercom")){
                if(MyDataActivity.getCallActivity()!=null){
                    MyDataActivity.getCallActivity().onCallHangUp();
                }
                MyDataActivity.getConnectActivity().launchCall(message);
                Log.i(TAG, "https://appr.tc/r/"+message);
                this.sendToAll("https://appr.tc/r/"+message);
            }else{
                Log.i(TAG, "message : \""+message+"\" non valide.");
            }
        } else {

        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        MyLog.logEvent("WebSocketServer error : " + ex.getMessage());
    }

    @Override
    public void onStart() {
        MyLog.logEvent("WebSocketServer started=" + this.getAddress().toString());
    }

    public void sendToAll(String text) {
        Collection<WebSocket> con = connections();
        synchronized (con) {
            for (WebSocket c : con) {
                c.send(text);
                MyLog.logEvent("WebSocketServer sent message=echo to " + c.getRemoteSocketAddress());
            }
        }
    }
}
