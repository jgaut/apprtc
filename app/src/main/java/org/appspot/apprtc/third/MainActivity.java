package org.appspot.apprtc.third;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;
import com.splunk.mint.Mint;

import org.appspot.apprtc.ConnectActivity;
import org.appspot.apprtc.R;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft_6455;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends Activity {

    //Client
    private TextView textView;
    private Button bOpenDoor;
    private Button bEcho;
    private Button bConnect;
    private Button bRing;
    private Button bCall;
    private Button bNextRing;

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

        //https => http://stacktips.com/snippet/how-to-trust-all-certificates-for-httpurlconnection-in-android
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                            return myTrustedAnchors;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception e) {
        }

        //Mint
        //Set the application environment
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
            MyDataActivity.setMyWebSocketServer(myWebSocketServer);

            AudioManager audioManager =(AudioManager)getSystemService(this.getBaseContext().AUDIO_SERVICE);
            Log.i(TAG, "Sound device :");
            for (AudioDeviceInfo deviceInfo : audioManager.getDevices(AudioManager.GET_DEVICES_ALL)) {
                Log.i(TAG, "Sound device :" + deviceInfo.toString());
            }

        } else {
            setContentView(R.layout.activity_main);
            WebSocketImpl.DEBUG = Boolean.getBoolean(MyAppProperties.getProperty("MyWebSocket.debug"));
            MyDataActivity.setMyWebSocketClientManager(new MyWebSocketClientManager());
            textView = (TextView) findViewById(R.id.textView);
            bOpenDoor = (Button) findViewById(R.id.bOpenDoor);
            bEcho = (Button) findViewById(R.id.bEcho);
            bConnect = (Button) findViewById(R.id.bConnect);
            bRing = (Button) findViewById(R.id.bRing);
            bCall = (Button) findViewById(R.id.bCall);
            bNextRing = (Button) findViewById(R.id.bNextRing);

            bOpenDoor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyDataActivity.getMyWebSocketClientManager().getMyWebSocketClient().send("open door");
                }
            });

            bEcho.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyDataActivity.getMyWebSocketClientManager().getMyWebSocketClient().send("echo");
                }
            });

            bConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyDataActivity.getMyWebSocketClientManager().connect(0);
                }
            });

            bRing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyDataActivity.getMyWebSocketClientManager().getMyWebSocketClient().send("ring");
                }
            });

            bNextRing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyDataActivity.getMyWebSocketClientManager().getMyWebSocketClient().send("next ring");
                }
            });

            bCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Random randomGenerator = new Random();
                    message = "intercom"+Math.abs(randomGenerator.nextInt(100));
                    Log.i(TAG, "https://appr.tc/r/"+message);
                    MyDataActivity.getMyWebSocketClientManager().getMyWebSocketClient().send(message);
                    MyDataActivity.getConnectActivity().launchCall(message);
                }
            });
            MyDataActivity.getMyWebSocketClientManager().connect(0);
        }

        MyDataActivity.setMainActivity(this);

    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public Button getbConnect() {
        return bConnect;
    }

    public void setbConnect(Button bConnect) {
        this.bConnect = bConnect;
    }

}
