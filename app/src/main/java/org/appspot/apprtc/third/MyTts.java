package org.appspot.apprtc.third;

import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import java.util.Locale;

/**
 * Created by jgautier on 22/08/2017.
 */

public class MyTts {

    private String TAG = this.getClass().toString();
    private TextToSpeech mTtsEngine;
    private static final String UTTERANCE_ID = "com.example.androidthings.bluetooth.audio.UTTERANCE_ID";

    public MyTts(Activity mainActivity) {
        mTtsEngine = new TextToSpeech(mainActivity,
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            mTtsEngine.setLanguage(Locale.FRANCE);
                        } else {
                            Log.w(TAG, "Could not open TTS Engine (onInit status=" + status
                                    + "). Ignoring text to speech");
                            mTtsEngine = null;
                        }
                    }
                });
        //Log.i(TAG, mTtsEngine.getVoice().toString())    ;
        //mTtsEngine.setVoice(new Voice("Custom", Locale.ENGLISH, Voice.QUALITY_VERY_HIGH, Voice.LATENCY_VERY_HIGH, true, null));
    }


    public void speak(String utterance) {
        Log.i(TAG, utterance);
        if (mTtsEngine != null) {
            mTtsEngine.speak(utterance, TextToSpeech.QUEUE_ADD, null, UTTERANCE_ID);
        }
    }
}
