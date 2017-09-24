package org.appspot.apprtc.third;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.splunk.mint.Mint;

import org.appspot.apprtc.ConnectActivity;
import org.appspot.apprtc.R;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft_6455;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Random;

public class MainActivity extends Activity {

    //Client
    private MyWebSocketClient myWebSocketClient;
    private TextView textView;
    private Button bOpenDoor;
    private Button bEcho;
    private Button bConnect;
    private Button bRing;
    private Button bCall;
    private EditText editTextRoomId;

    //Server
    public static MyWebSocketServer myWebSocketServer;
    private MyGpio myGpio;
    private String TAG = this.getClass().toString();
    private MyTts myTts;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain all app properties
        MyAppProperties.init(this.getApplicationContext());

        //Mint
        // Set the application environment
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.enableDebugLog();
        Mint.setUserOptOut(false);
        Mint.initAndStartSessionHEC(this.getApplication(), MyAppProperties.getProperty("Splunk.hec.url"), MyAppProperties.getProperty("Splunk.hec.token"));
        //Set some form of userIdentifier for this session
        Mint.setUserIdentifier(MyAppProperties.getProperty("Splunk.id"));

        Log.i(TAG, "Model=" + Build.MODEL);
        if (Build.MODEL.equals("iot_rpi3")) {

            // Text-to-Speech
            myTts = new MyTts(this);

            // GPIO
            myGpio = new MyGpio(myTts);

            //WebSocketServer
            WebSocketImpl.DEBUG = Boolean.getBoolean(MyAppProperties.getProperty("MyWebSocket.debug"));
            try {
                InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(MyAppProperties.getProperty("MyWebSocket.hostname")), Integer.parseInt(MyAppProperties.getProperty("MyWebSocket.port")));
                myWebSocketServer = new MyWebSocketServer(Integer.parseInt(MyAppProperties.getProperty("MyWebSocket.port")), myGpio, myTts);
                myWebSocketServer.start();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

        } else {
            setContentView(R.layout.activity_main);
            WebSocketImpl.DEBUG = Boolean.getBoolean(MyAppProperties.getProperty("MyWebSocket.debug"));

            textView = (TextView) findViewById(R.id.textView);
            bOpenDoor = (Button) findViewById(R.id.bOpenDoor);
            bEcho = (Button) findViewById(R.id.bEcho);
            bConnect = (Button) findViewById(R.id.bConnect);
            bRing = (Button) findViewById(R.id.bRing);
            bCall = (Button) findViewById(R.id.bCall);
            editTextRoomId = (EditText) findViewById(R.id.editTextRoomId);

            bOpenDoor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myWebSocketClient.send("open door");
                }
            });

            bEcho.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myWebSocketClient.send("echo");
                }
            });

            bConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myWebserverConnect();
                }
            });

            bRing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myWebSocketClient.send("ring");
                }
            });

            bCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Random randomGenerator = new Random();
                    message = "intercom"+Math.abs(randomGenerator.nextInt(100));
                    Log.i(TAG, "https://appr.tc/r/"+message);
                    myWebSocketClient.send(message);
                    MyDataActivity.getConnectActivity().launchCall(message);
                }
            });

        }
        myWebserverConnect();
        MyDataActivity.setMainActivity(this);
    }

    private void myWebserverConnect() {
        try {
            if (myWebSocketClient != null) {
                myWebSocketClient.close();
            }
            myWebSocketClient = new MyWebSocketClient(new URI("ws://"+MyAppProperties.getProperty("MyWebSocket.hostname")+":"+MyAppProperties.getProperty("MyWebSocket.port")), new Draft_6455(), textView, bConnect);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        myWebSocketClient.connect();
    }

}