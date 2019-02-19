package com.baidu.aip.asrwakeup3.core.wakeup;

/**
 * Created by fujiayi on 2017/6/21.
 */

public interface IWakeupListener {
    /**
     * 唤醒启动成功
     */
    void onStartSuccess();
    void onSuccess(String word, WakeUpResult result);

    void onStop();

    void onError(int errorCode, String errorMessge, WakeUpResult result);

    void onASrAudio(byte[] data, int offset, int length);
}
