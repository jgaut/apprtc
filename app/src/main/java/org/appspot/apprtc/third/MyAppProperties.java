package org.appspot.apprtc.third;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by jeremy on 13/05/2017.
 */

public class MyAppProperties {

    private static Properties properties;
    private static AssetManager assetManager;
    private static InputStream inputStream;

    public static void init(Context context) {
        properties = new Properties();
        assetManager = context.getAssets();
        try {
            inputStream = assetManager.open("app.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String string) {
        return properties.getProperty(string, "");
    }

}
