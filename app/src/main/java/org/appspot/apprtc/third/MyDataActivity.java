package org.appspot.apprtc.third;

import org.appspot.apprtc.CallActivity;
import org.appspot.apprtc.ConnectActivity;

/**
 * Created by jgautier on 24/09/2017.
 */

public class MyDataActivity {



    private static ConnectActivity connectActivity;
    private static CallActivity callActivity;
    private static MainActivity mainActivity;
    private static MyWebSocketServer myWebSocketServer;
    private static MyWebSocketClientManager myWebSocketClientManager;

    public static MyWebSocketClientManager getMyWebSocketClientManager() {
        return myWebSocketClientManager;
    }

    public static void setMyWebSocketClientManager(MyWebSocketClientManager myWebSocketClientManager) {
        MyDataActivity.myWebSocketClientManager = myWebSocketClientManager;
    }

    public static MyWebSocketServer getMyWebSocketServer() {
        return myWebSocketServer;
    }

    public static void setMyWebSocketServer(MyWebSocketServer myWebSocketServer) {
        MyDataActivity.myWebSocketServer = myWebSocketServer;
    }

    public static ConnectActivity getConnectActivity() {
        return connectActivity;
    }

    public static void setConnectActivity(ConnectActivity connectActivity) {
        MyDataActivity.connectActivity = connectActivity;
    }

    public static CallActivity getCallActivity() {
        return callActivity;
    }

    public static void setCallActivity(CallActivity callActivity) {
        MyDataActivity.callActivity = callActivity;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public static void setMainActivity(MainActivity mainActivity) {
        MyDataActivity.mainActivity = mainActivity;
    }

}
