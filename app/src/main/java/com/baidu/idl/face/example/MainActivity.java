package com.baidu.idl.face.example;

import com.baidu.aip.asrwakeup3.core.BaiduAsrUtils;
import com.baidu.aip.asrwakeup3.core.IAsrState;
//import com.baidu.idl.face.platform.*;
//import com.baidu.idl.face.platform.DoraAndroidToUnityManager;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.LivenessTypeEnum;
import com.baidu.idl.face.platform.MyDialog1;
import com.gz.aidea.R;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private RecyclerCustomAdapter mCustomAdapter;
    private Dialog mDefaultDialog;

    private int[] mDataset = new int[]{
            R.string.main_item_face_live,
            R.string.main_item_face_detect
    };
    public MyAsrHandler myAsrHandler = new MyAsrHandler();

    MyDialog1 dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
     //   getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 根据需求添加活体动作
        ExampleApplication.livenessList.clear();
        ExampleApplication.livenessList.add(LivenessTypeEnum.Eye);
        ExampleApplication.livenessList.add(LivenessTypeEnum.Mouth);
        ExampleApplication.livenessList.add(LivenessTypeEnum.HeadUp);
        ExampleApplication.livenessList.add(LivenessTypeEnum.HeadDown);
        ExampleApplication.livenessList.add(LivenessTypeEnum.HeadLeft);
        ExampleApplication.livenessList.add(LivenessTypeEnum.HeadRight);
        ExampleApplication.livenessList.add(LivenessTypeEnum.HeadLeftOrRight);

        mLayoutManager = new LinearLayoutManager(this);
        mCustomAdapter = new RecyclerCustomAdapter(mDataset);
        this.findViewById(R.id.main_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startItemActivity(SettingsActivity.class);
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);

        int scrollPosition = 0;
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
        mRecyclerView.setAdapter(mCustomAdapter);

        requestPermissions(99, Manifest.permission.CAMERA);




//        dialog = new MyDialog1(this,R.layout.activity_face_liveness_v3100);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setCancelable(false);
//        dialog.show();
//
//        dialog.setFaceListener(new IFaceListener() {
//            @SuppressLint("LongLogTag")
//            @Override
//            public void faceResult(String status,String result) {
//                Log.i("DoraAndroidToUnityManager","DoraAndroidToUnityManager status="+status.toString()+",message="+result);
//
//            }
//        });

        DoraAndroidToUnityManager  manager =
                new DoraAndroidToUnityManager();
        manager.initFaceLiveness(this);

        manager.showFace(this);


//        BaiduAsrUtils.getInstall().initAsr(this,myAsrHandler);
//        BaiduAsrUtils.getInstall().startAsr();

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
           // Toast.makeText(MainActivity.this,msg.what+"",Toast.LENGTH_SHORT).show();
            handleMsg(msg);
        }
    }

    public void handleMsg(Message msg) {
        switch (msg.what) {
            case IAsrState.STATUS_FINISHED:
                if (msg.arg2 == 1) {

                    String asrResult = msg.obj.toString().trim();//识别结果
                    Log.i("BaiduAsrUtils", "识别结果=" + asrResult);
                    Toast.makeText(this,asrResult,Toast.LENGTH_SHORT).show();
                    // Toast.makeText(MainActivity.this,"识别结果=" + asrResult,Toast.LENGTH_SHORT).show();
                }
                break;


            case IAsrState.WAKE_INIT_FAILED:
                String fail = (String) msg.obj;
                Log.i("BaiduAsrUtils", "WAKE_INIT_FAILED=" );
                Toast.makeText(this,"识别初始化失败",Toast.LENGTH_SHORT).show();
                //  Toast.makeText(MainActivity.this,"fail=" + fail,Toast.LENGTH_SHORT).show();
                break;
            case IAsrState.WAKE_INIT_SUCCESS:
                Toast.makeText(this,"识别初始化成功",Toast.LENGTH_SHORT).show();
                Log.i("BaiduAsrUtils", "WAKE_INIT_SUCCESS=" );
                //BaiduAsrUtils.getInstall().startAsr();
                break;
            case IAsrState.STATUS_VOLUME:
                String volume = (String) msg.obj;

                break;
        }
    }

    /**
     * 初始化人脸采集
     */
    public boolean initFaceLiveness() {
        boolean initFace =  FaceSDKManager.getInstance().initialize(this, com.baidu.idl.face.platform.Config.licenseID, com.baidu.idl.face.platform.Config.licenseFileName);
//        if(initFace)
//            setFaceConfig();
        return initFace;

        //  callUnity("UnityAndroidCommunicationObj","AndroidCallUnityCB", "initFace");
    }

    /**
     * 设置人脸人家参数
     */

    public void setFaceConfig() {

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

//    private void initLib() {
//        // 为了android和ios 区分授权，appId=appname_face_android ,其中appname为申请sdk时的应用名
//        // 应用上下文
//        // 申请License取得的APPID
//        // assets目录下License文件名
//        FaceSDKManager.getInstance().initialize(this, Config.licenseID, Config.licenseFileName);
//         setFaceConfig();
//    }
//
//    private void setFaceConfig() {
//        FaceConfig config = FaceSDKManager.getInstance().getFaceConfig();
//        // SDK初始化已经设置完默认参数（推荐参数），您也根据实际需求进行数值调整
//        config.setLivenessTypeList(ExampleApplication.livenessList);
//        config.setLivenessRandom(ExampleApplication.isLivenessRandom);
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
//    }

    private void startItemActivity(Class itemClass) {
        setFaceConfig();
        startActivity(new Intent(this, itemClass));
    }

    class RecyclerCustomAdapter extends RecyclerView.Adapter<RecyclerCustomAdapter.ViewHolder> {
        final int[] itemDataSet;

        class ViewHolder extends RecyclerView.ViewHolder {

            public final View rv;
            public final TextView tv;

            public ViewHolder(View v) {
                super(v);
                rv = v;
                tv = (TextView) v.findViewById(R.id.item_main_text);
            }
        }

        public RecyclerCustomAdapter(int[] dataSet) {
            itemDataSet = dataSet;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_main, viewGroup, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            viewHolder.tv.setText(itemDataSet[position]);
            viewHolder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (itemDataSet[position]) {
                        case R.string.main_item_face_live:
                            Toast.makeText(MainActivity.this,"活体检测",Toast.LENGTH_SHORT).show();
                           // startItemActivity(FaceLivenessExpActivity.class);
                            break;
                        case R.string.main_item_face_detect:
                         //   startItemActivity(FaceDetectExpActivity.class);
                            break;
                        default:
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return itemDataSet.length;
        }
    }

    public void requestPermissions(int requestCode, String permission) {
        if (permission != null && permission.length() > 0) {
            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    // 检查是否有权限
                    int hasPer = checkSelfPermission(permission);
                    if (hasPer != PackageManager.PERMISSION_GRANTED) {
                        // 是否应该显示权限请求
                        boolean isShould = shouldShowRequestPermissionRationale(permission);
                        requestPermissions(new String[]{permission}, requestCode);
                    }
                } else {

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        boolean flag = false;
        for (int i = 0; i < permissions.length; i++) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[i]) {
                flag = true;
            }
        }
        if (!flag) {
            requestPermissions(99, Manifest.permission.CAMERA);
        }
    }

    protected void showMessageDialog(String title, String message) {
        if (mDefaultDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title).
                    setMessage(message).
                    setNegativeButton("ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mDefaultDialog.dismiss();
                                    MainActivity.this.finish();
                                }
                            });
            mDefaultDialog = builder.create();
            mDefaultDialog.setCancelable(true);
        }
        mDefaultDialog.dismiss();
        mDefaultDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dialog.dismiss();
    }
}
