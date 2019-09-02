package com.baidu.idl.face.platform;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;


import com.baidu.idl.face.platform.ui.FaceSDKResSettings;
import com.baidu.idl.face.platform.ui.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by camming on 2019\1\30 0030.
 * code is data  data is code
 */

public class DoraAndroidToUnityManager {


    /**
     * unity项目启动时的的上下文
     */
    private Activity unityActivity;

    private MyDialog1 dialog;
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
     * Toast显示unity发送过来的内容
     * @param content           消息的内容
     * @return                  调用是否成功
     */
    public boolean showToast(String content){

        callUnity("UnityAndroidCommunicationObj","AndroidCallUnityCB", content);
        return true;
    }


    /**
     * 初始化人脸采集
     */
    public boolean initFaceLiveness() {
       boolean initFace =  FaceSDKManager.getInstance().initialize(getActivity(), Config.licenseID, Config.licenseFileName);
       if(initFace)
           setFaceConfig();
       return initFace;

      //  callUnity("UnityAndroidCommunicationObj","AndroidCallUnityCB", "initFace");
    }

    /**
     * 设置人脸人家参数
     */

    public void setFaceConfig() {
        FaceSDKResSettings.initializeResId();
        List<LivenessTypeEnum> livenessList = new ArrayList<LivenessTypeEnum>();
        livenessList.clear();
        livenessList.add(LivenessTypeEnum.Eye);
        livenessList.add(LivenessTypeEnum.Mouth);
        livenessList.add(LivenessTypeEnum.HeadUp);
        livenessList.add(LivenessTypeEnum.HeadDown);
        livenessList.add(LivenessTypeEnum.HeadLeft);
        livenessList.add(LivenessTypeEnum.HeadRight);
        livenessList.add(LivenessTypeEnum.HeadLeftOrRight);

//        FaceConfig config = FaceSDKManager.getInstance().getFaceConfig();
//        // SDK初始化已经设置完默认参数（推荐参数），您也根据实际需求进行数值调整
//        config.setLivenessTypeList(livenessList);
//        config.setLivenessRandom(true);
//        config.setBlurnessValue(FaceEnvironment.VALUE_BLURNESS);
//        config.setBrightnessValue(FaceEnvironment.VALUE_BRIGHTNESS);
//        config.setCropFaceValue(FaceEnvironment.VALUE_CROP_FACE_SIZE);
//        config.setHeadPitchValue(FaceEnvironment.VALUE_HEAD_PITCH);
//        config.setHeadRollValue(FaceEnvironment.VALUE_HEAD_ROLL);
//        config.setHeadYawValue(FaceEnvironment.VALUE_HEAD_YAW);
//        config.setMinFaceSize(FaceEnvironment.VALUE_MIN_FACE_SIZE);
//        config.setNotFaceValue(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD);
//        config.setOcclusionValue(FaceEnvironment.VALUE_OCCLUSION);
//        config.setCheckFaceQuality(true);
//        config.setFaceDecodeNumberOfThreads(2);
//
//        FaceSDKManager.getInstance().setFaceConfig(config);
    }

    /**
     * 显示人脸显示框
     * */
    public void showFace(int x,int y,int facex,int facey){

        if(dialog==null)
            dialog = new MyDialog1(getActivity(),x,y ,facex,facey,R.layout.activity_face_liveness_v3100);
//            dialog = new MyDialog1(getActivity(), R.layout.activity_face_liveness_v3100);
        dialog.mIsCompletion = false;
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        dialog.setFaceListener(new IFaceListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void faceResult(String status,String result) {
                Log.i("DoraAndroidToUnityManager","DoraAndroidToUnityManager status="+status.toString()+",message="+result);
               // callUnity("UnityAndroidCommunicationObj","AndroidCallUnityCB", status+","+result);
                callUnity("UnityAndroidCommunicationObj","AndroidCallUnityCB", status+","+result);
            }
        });

    }

    public void showFace(){

        if(dialog==null)
            dialog = new MyDialog1(getActivity(),0,0,0,0,R.layout.activity_face_liveness_v3100);
//            dialog = new MyDialog1(getActivity(), R.layout.activity_face_liveness_v3100);
        dialog.mIsCompletion = false;
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        dialog.setFaceListener(new IFaceListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void faceResult(String status,String result) {
                Log.i("DoraAndroidToUnityManager","DoraAndroidToUnityManager status="+status.toString()+",message="+result);
                // callUnity("UnityAndroidCommunicationObj","AndroidCallUnityCB", status+","+result);
                callUnity("UnityAndroidCommunicationObj","AndroidCallUnityCB", status+","+result);
            }
        });

    }
    /**
     * 隐藏人脸显示框
     * */
    public void dismissFace(){
        if(dialog!=null){
            dialog.dismiss();
            dialog.mIsCompletion = true;
        }
    }

    /**
     * 设置是否检测完成  true为检测完成 false 为检测未完成
     * */
    public void setDetectFaceIsCompletion(boolean bool){
        if(dialog!=null)
            dialog.mIsCompletion = bool;
    }

    /**
     * 开始检测
     * */
    public void startDetectFace(){
        if(dialog!=null)
            dialog.startPreview();
    }
    /**
     * 停止检测
     * */
    public void stopDetectFace(){
        if(dialog!=null)
            dialog.stopPreview();
    }
}
