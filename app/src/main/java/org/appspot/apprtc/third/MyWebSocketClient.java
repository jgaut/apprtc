package org.appspot.apprtc.third;

import android.widget.Button;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by jeremy on 10/06/2017.
 * https://github.com/TooTallNate/Java-WebSocket/blob/master/src/main/example/ChatClient.java
 */

public class MyWebSocketClient extends WebSocketClient {

    private TextView textView;
    private Button bConnect;

    public MyWebSocketClient(URI serverUri , Draft draft, TextView textView, Button bConnect ) {
        super( serverUri, draft );
        this.textView = textView;
        this.bConnect = bConnect;
    }

    public MyWebSocketClient(URI serverURI ) {
        super( serverURI );
    }

    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        textView.append("\n"+ "opened connection. Timeout="+this.getConnectionLostTimeout() );
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
        bConnect.setEnabled(false);
    }

    @Override
    public void onMessage( String message ) {
        System.out.println( "received: " + message );
        if(message!=null){
            textView.append("\n"+message);
        }
    }

    @Override
    public void onFragment( Framedata fragment ) {
        textView.append("\n"+ "received fragment: " + new String( fragment.getPayloadData().array()));
    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        textView.append("\n"+"Connection closed by " + ( remote ? "remote peer" : "us" ) + ". reason=" +reason);
        bConnect.setEnabled(true);
    }

    @Override
    public void onError( Exception ex ) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }

    @Override
    public void send(String string){
        if(this.isOpen()) {
            super.send(string);
        }
    }

}
