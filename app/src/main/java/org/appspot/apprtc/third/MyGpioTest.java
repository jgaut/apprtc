package org.appspot.apprtc.third;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jgautier on 16/09/2017.
 */

public class MyGpioTest extends Thread{

    private String TAG = this.getClass().toString();
    private Gpio gpioTemp;
    private PeripheralManagerService manager;
    private List<String> portList;
    private HashMap hashMap;

    public MyGpioTest(){
        super();
        manager = new PeripheralManagerService();
        portList = manager.getGpioList();
        if (portList.isEmpty()) {
            Log.i(TAG, "No GPIO port available on this device.");
        } else {
            Log.i(TAG, "List of available ports: " + portList);
        }
        hashMap=new HashMap();
    }

    public void run() {

        while (true) {
            //Listing des ports GPIO
            for (String gpioT : portList) {
                try {
                    gpioTemp = manager.openGpio(gpioT);
                    gpioTemp.setDirection(Gpio.DIRECTION_IN);
                    if(hashMap.containsValue(gpioT) && !hashMap.get(gpioT).equals(gpioTemp.getValue())){
                        Log.i(TAG, "GPIO " + gpioT + "is " + gpioTemp.getValue());
                    }
                    gpioTemp.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
