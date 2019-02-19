package com.baidu.aip.asrwakeup3.core;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.aip.asrwakeup3.core.wakeup.IWakeupListener;
import com.baidu.aip.asrwakeup3.core.wakeup.WakeUpResult;
import com.baidu.aip.asrwakeup3.core.wakeup.WakeupEventAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by camming on 2019\1\30 0030.
 * code is data  data is code
 */

public class DoraAsrWakeManager {


    /**
     * unity项目启动时的的上下文
     */
    private Activity unityActivity;
    public MyAsrHandler myAsrHandler = new MyAsrHandler();
    /**
     * 获取unity项目的上下文
     * @return
     */
    Activity getActivity(){
        if(null == unityActivity) {
            try {
                Class<?> classtype = Class.forName("com.unity3d.player.UnityPlayer");
                Activity activity = (Activity) classtype.getDeclaredField("currentActivity").get(classtype);
                unityActivity = activity;
            } catch (ClassNotFoundException e) {

            } catch (IllegalAccessException e) {

            } catch (NoSuchFieldException e) {

            }
        }
        return unityActivity;
    }

    /**
     * 调用Unity的方法
     * @param gameObjectName    调用的GameObject的名称
     * @param functionName      方法名
     * @param args              参数
     * @return                  调用是否成功
     */
    boolean callUnity(String gameObjectName, String functionName, String args){
        try {
            Class<?> classtype = Class.forName("com.unity3d.player.UnityPlayer");
            Method method =classtype.getMethod("UnitySendMessage", String.class,String.class,String.class);
            method.invoke(classtype,gameObjectName,functionName,args);
            return true;
        } catch (ClassNotFoundException e) {

        } catch (NoSuchMethodException e) {

        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        }
        return false;
    }



    /**
     * 初始化唤醒
     */
    public void initWake() {

        //初始化唤醒
        BaiduWakeUpUtils.getInstall(getActivity()).initWakeUp(new WakeupEventAdapter(new IWakeupListener() {
            @Override
            public void onSuccess(String word, WakeUpResult result) {
             //   Log.i(TAG,"唤醒onSuccess，唤醒词："+word);
              //  Toast.makeText(MainActivity.this,"唤醒成功",Toast.LENGTH_SHORT).show();
                callUnity("UnityAndroidCommunicationObj","wakeSuccess", word);
            }
            @Override
            public void onStop() {
              //  Log.i(TAG,"唤醒onStop");
            }
            @Override
            public void onError(int errorCode, String errorMessge, WakeUpResult result) {
                callUnity("UnityAndroidCommunicationObj","initWakeStatus", "false");
              //  Log.i(TAG,"唤醒onError="+errorMessge);
              //  Log.i(TAG,"唤醒onErrorerrorCode="+errorCode);
              //  Toast.makeText(MainActivity.this,"唤醒onError="+errorMessge,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onASrAudio(byte[] data, int offset, int length) {
              //  Log.i(TAG,"唤醒onASrAudio");
            }

            @Override
            public void onStartSuccess() {
                callUnity("UnityAndroidCommunicationObj","initWakeStatus", "true");
              //  Log.i(TAG,"启动唤醒成功");
              //  Toast.makeText(MainActivity.this,"启动唤醒成功",Toast.LENGTH_SHORT).show();
            }
        }));
      //  BaiduWakeUpUtils.getInstall(this).startWake();
    }
    /**
     * 启动唤醒
     */
    public void startWake() {
        BaiduWakeUpUtils.getInstall(getActivity()).startWake();
    }
    /**
     * 取消唤醒
     */
    public void cancleWake() {
        BaiduWakeUpUtils.getInstall(getActivity()).stopWakeup();
    }
    /**
     * 销毁唤醒 退出的时候调用，如果再需要唤醒需要重新初始化
     */
    public void releaseWake(){
        BaiduWakeUpUtils.getInstall(getActivity()).releaseWake();
    }

    /**
     * 初始化语音识别
     */
    public void initAsr() {
        BaiduAsrUtils.getInstall().initAsr(getActivity(),myAsrHandler);
    }
    /**
     * 启动语音识别
     */
    public void startAsr() {
        BaiduAsrUtils.getInstall().startAsr();
    }
    /**
     * 停止语音识别
     */
    public void stopAsr() {
        BaiduAsrUtils.getInstall().stop();
    }
    /**
     * 销毁语音识别
     */
    public void releaseAsr() {
        BaiduAsrUtils.getInstall().release();
    }
    /**
     *
     * 长语音识别
     * 监听结果
     * */
    public class MyAsrHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handleMsg(msg);
        }
    }

    public void handleMsg(Message msg) {
        switch (msg.what) {
            case IAsrState.STATUS_FINISHED:
                if (msg.arg2 == 1) {

                    String asrResult = msg.obj.toString().trim();//识别结果
                    callUnity("UnityAndroidCommunicationObj","asrResult", asrResult);
                    Log.i("BaiduAsrUtils", "识别结果=" + asrResult);
                   // Toast.makeText(MainActivity.this,"识别结果=" + asrResult,Toast.LENGTH_SHORT).show();
                }
                break;


            case IAsrState.WAKE_INIT_FAILED:
                String fail = (String) msg.obj;
                Log.i("BaiduAsrUtils", "WAKE_INIT_FAILED=" );
              //  Toast.makeText(MainActivity.this,"fail=" + fail,Toast.LENGTH_SHORT).show();
                callUnity("UnityAndroidCommunicationObj","initAsrStatus", "false");
                break;
            case IAsrState.WAKE_INIT_SUCCESS:
                Log.i("BaiduAsrUtils", "WAKE_INIT_SUCCESS=" );
                callUnity("UnityAndroidCommunicationObj","initAsrStatus", "true");
                break;
            case IAsrState.STATUS_VOLUME:
                String volume = (String) msg.obj;
                callUnity("UnityAndroidCommunicationObj","asrVolume", volume);
                break;
        }
    }
}
