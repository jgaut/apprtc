package org.appspot.apprtc.third;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;


import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jeremy on 18/01/2017.
 * https://developer.android.com/things/hardware/raspberrypi-io.html
 */

public class MyGpio {

    private static boolean delay = true;
    private final String ConfGpioRing = "BCM24";
    private final String TAG = this.getClass().toString();
    private Gpio gpioTemp;
    private Gpio gpioRing;
    private Gpio gpioOpenDoor;
    private Gpio gpioTestRing;
    private final GpioCallback mGpioRingCallback;
    private GpioCallback mGpioTestCallback;
    private final MyTts myTts;
    private long timeNextRing=0;
    private long timeNextRingDelay = 60*5;

    private MyGpioTest myGpioTest;

    MyGpio(final MyTts myTts) {

        //Listing des ports GPIO
        PeripheralManagerService manager = new PeripheralManagerService();
        List<String> portList = manager.getGpioList();
        if (portList.isEmpty()) {
            Log.i(TAG, "No GPIO port available on this device.");
        } else {
            Log.i(TAG, "List of available ports: " + portList);
        }

        //Gestion du callback
        mGpioRingCallback = new GpioCallback() {
            @Override
            public boolean onGpioEdge(Gpio gpio) {
                // Read the active low pin state
                try {
                    Log.i("GPIO-Callback", "BCM24="+gpio.getValue());
                    if (gpio.getValue() && delay) {
                        delay=false;
                        Log.i(TAG, "Setup Security delay=" + delay);
                        MyLog.logEvent(gpio.getName()+ "=" + gpio.getValue());
                        if(timeNextRing>=(System.currentTimeMillis() / 1000)){
                            openDoor();
                            timeNextRing=0;
                        }
                        new Timer().schedule(new TimerTask(){
                            public void run(){
                                delay=true;
                                Log.i(TAG, "Reset Security delay=" + delay);
                                //initGpio24();
                            }
                        },5000);

                        if (MyDataActivity.getMyWebSocketServer() != null) {
                            MyDataActivity.getMyWebSocketServer().sendToAll("Ring");
                        }
                        myTts.speak("On sonne !");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Continue listening for more interrupts => true
                return true;
            }

            @Override
            public void onGpioError(Gpio gpio, int error) {
                Log.w(TAG, gpio + ": Error event " + error);
            }
        };

        //Initialisation des GPIO pour la détection de la sonnerie
        //BCM4 -> DIRECTION_IN + ACTIVE_LOW + EDGE_RISING
        try {
            gpioRing = manager.openGpio(ConfGpioRing);
            gpioRing.registerGpioCallback(mGpioRingCallback);
            gpioRing.setDirection(Gpio.DIRECTION_IN);
            gpioRing.setActiveType(Gpio.ACTIVE_HIGH);
            gpioRing.setEdgeTriggerType(Gpio.EDGE_BOTH);
            Log.i(TAG, "First time "+gpioRing.getName()+"=" + gpioRing.getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Initialisation des GPIO pour la détection de la sonnerie -> Simulation
        //BCM18 -> OUT
        try {
            gpioTestRing = manager.openGpio("BCM18");
            gpioTestRing.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            Log.i(TAG, "First time BCM18=" + gpioTestRing.getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Initialisation des GPIO pour l'ouverture de la porte
        //BCM23 -> OUT to open the door
        try {
            gpioOpenDoor = manager.openGpio("BCM23");
            // Initialize the pin as an input
            gpioOpenDoor.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            Log.i(TAG, "First time BCM23=" + gpioOpenDoor.getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.myTts=myTts;
    }

    public Gpio getGpioTestRing() {
        return gpioTestRing;
    }

    public GpioCallback mGpioRingCallback() {
        return mGpioRingCallback;
    }

    public void openDoor(){
        MyLog.logEvent("Open Door");
        try {
            gpioOpenDoor.setValue(true);
            Log.i(TAG, "BCM23:" + gpioOpenDoor.getValue());
            new Timer().schedule(new TimerTask(){
                public void run(){
                    try {
                        gpioOpenDoor.setValue(false);
                        Log.i(TAG, "BCM23:" + gpioOpenDoor.getValue());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            },1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTimeNextRing(long timeNextRing) {
        this.timeNextRing = timeNextRing*this.timeNextRingDelay;
    }
}
