package com.baidu.aip.asrwakeup3.core;

/**
 * Created by camming on 2018\11\27 0027.
 *
 */

public class IAsrState {

    public static final int STATUS_NONE = 2;

    int STATUS_READY = 3;
    int STATUS_SPEAKING = 4;
    int STATUS_RECOGNITION = 5;

    public static final int STATUS_FINISHED = 6;
    public static final int WAKE_INIT_FAILED = -1;
    public static final int WAKE_INIT_SUCCESS = 1;
    int STATUS_LONG_SPEECH_FINISHED = 7;
    public static final int STATUS_VOLUME= 8;

    int STATUS_STOPPED = 10;


    /**合成开始 与STATUS_TTS_COMPELE_ASR 是一起的*/
    public static final int STATUS_TTS_START_ASR = 19;
    /**合成结束 STATUS_TTS_START 是一起的*/
    public static final int STATUS_TTS_COMPELE_ASR = 20;
    /**单独实现 合成结束*/
    public static final int STATUS_TTS_START = 21;
    public static final int STATUS_TTS_COMPELE = 22;


    public static final int STATUS_FACE_RECOOG_COMPELE = 23;

    int STATUS_WAITING_READY = 8001;
    int WHAT_MESSAGE_STATUS = 9001;

    int STATUS_WAKEUP_SUCCESS = 7001;
    int STATUS_WAKEUP_EXIT = 7003;
}
