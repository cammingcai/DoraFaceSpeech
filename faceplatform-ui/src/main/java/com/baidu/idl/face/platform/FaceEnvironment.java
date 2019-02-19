/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.baidu.idl.face.platform;

import java.util.ArrayList;
import java.util.List;

/**
 * SDK全局配置信息
 */
final public class FaceEnvironment {

    // SDK基本信息
    public static final String TAG = "Baidu-IDL-FaceSDK";
    public static final String OS = "android";
    public static final String SDK_VERSION = "3.1.0.0";
    public static final int AG_ID = 3;

    /**光照范围*/
    public static final float VALUE_BRIGHTNESS = 40f;
    /**模糊度范围*/
    public static final float VALUE_BLURNESS = 0.5f;
    /**人脸遮挡范围 根据本功能的需求  推荐0.5 -1   */
    public static final float VALUE_OCCLUSION = 1.0f;
    public static final int VALUE_HEAD_PITCH = 20;
    public static final int VALUE_HEAD_YAW = 20;
    public static final int VALUE_HEAD_ROLL = 20;
    public static final int VALUE_CROP_FACE_SIZE = 400;
    /**最小检测人脸80-200 推荐120-200   越小越耗性能  */
    public static final int VALUE_MIN_FACE_SIZE = 130;
    /**没有人脸  */
    public static final float VALUE_NOT_FACE_THRESHOLD = 0.1f;
    /**是否进行质量检测*/
    public static final boolean VALUE_IS_CHECK_QUALITY = false;
    public static final int VALUE_DECODE_THREAD_NUM = 2;
    public static final int VALUE_LIVENESS_DEFAULT_RANDOM_COUNT = 6;
    public static final int VALUE_MAX_CROP_IMAGE_NUM = 1;

    // 识别策略配置参数
    public static long TIME_TIPS_REPEAT = 1000L;
    public static long TIME_MODULE = 0L;
    public static long TIME_DETECT_NO_FACE_CONTINUOUS = 1000L;

    public static long TIME_DETECT_MODULE = 60 * 1000L;
    public static long TIME_LIVENESS_MODULE = 60 * 1000L;

    // 识别策略参数
    private static boolean mIsDebug = false;
    private static int[] mSoundIds;
    private static int[] mTipsTextIds;

    public static final List<LivenessTypeEnum> livenessTypeDefaultList = new ArrayList<LivenessTypeEnum>();

    static {
        livenessTypeDefaultList.add(LivenessTypeEnum.Eye);
        livenessTypeDefaultList.add(LivenessTypeEnum.Mouth);
        livenessTypeDefaultList.add(LivenessTypeEnum.HeadUp);
        livenessTypeDefaultList.add(LivenessTypeEnum.HeadDown);
        livenessTypeDefaultList.add(LivenessTypeEnum.HeadLeft);
        livenessTypeDefaultList.add(LivenessTypeEnum.HeadRight);

        mSoundIds = new int[FaceStatusEnum.values().length];
        mTipsTextIds = new int[FaceStatusEnum.values().length];
        for (int i = 0; i < mSoundIds.length; i++) {
            mSoundIds[i] = 0;
            mTipsTextIds[i] = 0;
        }
    }

    public static boolean isDebugable() {
        return mIsDebug;
    }

    public static int getSoundId(FaceStatusEnum status) {
        int soundId = mSoundIds[status.ordinal()];
        return soundId;
    }

    public static int getTipsId(FaceStatusEnum status) {
        int tipsId = mTipsTextIds[status.ordinal()];
        return tipsId;
    }


    public static void setSoundId(FaceStatusEnum status, int soundId) {
        if (mSoundIds != null) {
            try {
                mSoundIds[status.ordinal()] = soundId;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void setTipsId(FaceStatusEnum status, int tipsId) {
        if (mTipsTextIds != null) {
            try {
                mTipsTextIds[status.ordinal()] = tipsId;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
