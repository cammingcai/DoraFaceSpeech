package com.baidu.aip.asrwakeup3.core;

import android.content.Context;

import com.baidu.aip.asrwakeup3.core.wakeup.WakeupEventAdapter;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;

import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by camming on 2018\11\28 0028.
 *
 * 语音唤醒  唤醒词：多啦多啦
 */

public class BaiduWakeUpUtils {

    private static BaiduWakeUpUtils install;
    private EventManager wakeup;
    private static Context mContext;
  //  private IWakeupListener mIWakeupListener;
    private static WakeupEventAdapter mWakeupEventAdapter;
    public static synchronized BaiduWakeUpUtils getInstall(Context context){
        mContext  = context;

        if(install==null){
            install = new BaiduWakeUpUtils();
        }
        return install;
    }


    public void initWakeUp(WakeupEventAdapter wakeupEventAdapter){
        mWakeupEventAdapter = wakeupEventAdapter;
        wakeup = EventManagerFactory.create(mContext, "wp");
        wakeup.registerListener(mWakeupEventAdapter); //  EventListener 中 onEvent方法
    }


    public void releaseWake() {
        stopWakeup();
        wakeup.unregisterListener(mWakeupEventAdapter);
        wakeup = null;
    }
    public void stopWakeup() {
        wakeup.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
    }
    public void startWake() {
        Map<String, Object> params = new TreeMap<String, Object>();
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        // "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下
        String json = new JSONObject(params).toString();
        wakeup.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);
    }
   // WakeupEventAdapter wakeupEventAdapter =new WakeupEventAdapter(mIWakeupListener);


//    new IWakeupListener() {
//        @Override
//        public void onSuccess(String word, WakeUpResult result) {
//            Log.i(TAG,"唤醒onSuccess");
//            Log.e(TAG,"唤醒onSuccess");
//        }
//
//        @Override
//        public void onStop() {
//            Log.i(TAG,"唤醒onStop");
//        }
//
//        @Override
//        public void onError(int errorCode, String errorMessge, WakeUpResult result) {
//            Log.i(TAG,"唤醒onError="+errorMessge);
//        }
//
//        @Override
//        public void onASrAudio(byte[] data, int offset, int length) {
//            Log.i(TAG,"唤醒onASrAudio");
//        }
//    }

}
