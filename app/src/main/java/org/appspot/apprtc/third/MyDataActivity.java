package org.appspot.apprtc.third;

import org.appspot.apprtc.CallActivity;
import org.appspot.apprtc.ConnectActivity;

/**
 * Created by jgautier on 24/09/2017.
 */

public class MyDataActivity {

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

    public static ConnectActivity connectActivity;
    public static CallActivity callActivity;
    public static MainActivity mainActivity;

}
