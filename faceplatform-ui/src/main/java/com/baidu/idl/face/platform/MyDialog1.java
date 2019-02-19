package com.baidu.idl.face.platform;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.baidu.idl.face.platform.ui.FaceSDKResSettings;
import com.baidu.idl.face.platform.ui.R;
import com.baidu.idl.face.platform.ui.utils.CameraUtils;
import com.baidu.idl.face.platform.ui.widget.FaceDetectRoundView;
import com.baidu.idl.face.platform.utils.APIUtils;
import com.baidu.idl.face.platform.utils.BitmapUtils;
import com.baidu.idl.face.platform.utils.CameraPreviewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by camming on 2019\1\30 0030.
 * code is data  data is code
 */
public class MyDialog1 extends Dialog implements
        SurfaceHolder.Callback,
        Camera.PreviewCallback,
        Camera.ErrorCallback,
        ILivenessStrategyCallback{

    public static final String TAG = MyDialog1.class.getSimpleName();

    //在构造方法里提前加载了样式
    private Context context;//上下文
    private int layoutResID;//布局文件id

    // 人脸信息
    public FaceConfig mFaceConfig;
    public ILivenessStrategy mILivenessStrategy;

    // View
    public FrameLayout mFrameLayout;
    public SurfaceView mSurfaceView;
    public SurfaceHolder mSurfaceHolder;
    // 显示Size
    private Rect mPreviewRect = new Rect();
    public int mDisplayWidth = 0;
    public int mDisplayHeight = 0;
    public int mSurfaceWidth = 0;
    public int mSurfaceHeight = 0;
    // 状态标识
    public volatile boolean mIsEnableSound = false;
    public HashMap<String, String> mBase64ImageMap = new HashMap<String, String>();
    public boolean mIsCreateSurface = false;
    public boolean mIsCompletion = false;
    // 相机
    public Camera mCamera;
    public Camera.Parameters mCameraParam;
    public int mCameraId;
    public int mPreviewWidth;
    public int mPreviewHight;
    public int mPreviewDegree;



    Bitmap image;


    private FaceDetectRoundView faceDetectRoundView;

    private IFaceListener faceListener;
    private RelativeLayout rootRl;

   // private int[] listenedItem;//监听的控件id
    public MyDialog1(Context context,int layoutResID){
        super(context, R.style.MyDialog);//加载dialog的样式
        this.context = context;
        this.layoutResID = layoutResID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //提前设置Dialog的一些样式
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.TOP | Gravity.RIGHT);
       // dialogWindow.setWindowAnimations();
        setContentView(layoutResID);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = ((Activity)context).getWindowManager();

        WindowManager.LayoutParams attributesParams = dialogWindow.getAttributes();
        attributesParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        attributesParams.dimAmount = 0.0f;//设置遮罩透明度

        Display display = windowManager.getDefaultDisplay();
        display.getMetrics(dm);
        mDisplayWidth = dm.widthPixels;
        mDisplayHeight = dm.heightPixels;

        //initFaceLiveness();


        rootRl = (RelativeLayout) findViewById(R.id.rl_face_check);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mDisplayWidth/3,mDisplayWidth/3);
        rootRl.setLayoutParams(params);

        mFrameLayout = (FrameLayout) findViewById(R.id.liveness_surface_layout);
        faceDetectRoundView  = (FaceDetectRoundView) findViewById(R.id.liveness_face_round);

        FaceSDKResSettings.initializeResId();
        mFaceConfig = FaceSDKManager.getInstance().getFaceConfig();

        mSurfaceView = new SurfaceView(context);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setSizeFromLayout();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        FrameLayout.LayoutParams cameraFL = new FrameLayout.LayoutParams(
                (int) (mDisplayWidth* FaceDetectRoundView.SURFACE_RATIO), (int) (mDisplayHeight * FaceDetectRoundView.SURFACE_RATIO),
                Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        Log.i(TAG,"mFrameLayout.getWidth()="+mFrameLayout.getWidth());
        Log.i(TAG,"mFrameLayout.getHeight()="+mFrameLayout.getHeight());
      //  RelativeLayout.LayoutParams paramscameraFL = new RelativeLayout.LayoutParams(mDisplayWidth/3,mDisplayWidth/3);
        mSurfaceView.setLayoutParams(cameraFL);

        mFrameLayout.addView(mSurfaceView);
        if (mBase64ImageMap != null) {
            mBase64ImageMap.clear();
        }



    }


    @Override
    public void onStop() {
        if (mILivenessStrategy != null) {
            mILivenessStrategy.reset();
        }
        stopPreview();
        super.onStop();
    }

    private Camera open() {
        Camera camera;
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            return null;
        }

        int index = 0;
        while (index < numCameras) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(index, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                break;
            }
            index++;
        }

        if (index < numCameras) {
            camera = Camera.open(index);
            mCameraId = index;
        } else {
            camera = Camera.open(0);
            mCameraId = 0;
        }
        return camera;
    }

    public void startPreview() {
        // mSwitch.setChecked(true);
        mIsCreateSurface = true;
        Log.i(TAG,"startPreview");
        if (mSurfaceView != null && mSurfaceView.getHolder() != null) {
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.addCallback(this);
        }

        if (mCamera == null) {
            try {
                mCamera = open();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mCamera == null) {
            return;
        }

        if (mCameraParam == null) {
            mCameraParam = mCamera.getParameters();
        }

        mCameraParam.setPictureFormat(PixelFormat.JPEG);
        int degree = displayOrientation(context);
        mCamera.setDisplayOrientation(degree);
        // 设置后无效，camera.setDisplayOrientation方法有效
        mCameraParam.set("rotation", degree);
        mPreviewDegree = degree;

        Point point = CameraPreviewUtils.getBestPreview(mCameraParam,
                new Point(mDisplayWidth, mDisplayHeight));
        mPreviewWidth = point.x;
        mPreviewHight = point.y;
//        mPreviewWidth = mDisplayWidth;
//        mPreviewHight = mDisplayHeight;
        // Preview 768,432
        Log.i(TAG,"mPreviewWidth="+mPreviewWidth+",,,mPreviewHight="+mPreviewHight);
        Log.i(TAG,"mDisplayWidth="+mDisplayWidth+",,,mDisplayHeight="+mDisplayHeight);
        if (mILivenessStrategy != null) {
            mILivenessStrategy.setPreviewDegree(degree);
        }

        mPreviewRect.set(0, 0, mPreviewHight, mPreviewWidth);
        // 指定preview的大小  如果这两个属性设置和真实的设备的不一样时就会报错
        mCameraParam.setPreviewSize(mPreviewWidth, mPreviewHight);
        mCamera.setParameters(mCameraParam);

        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.stopPreview();
            mCamera.setErrorCallback(this);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (RuntimeException e) {
            e.printStackTrace();
            CameraUtils.releaseCamera(mCamera);
            mCamera = null;
        } catch (Exception e) {
            e.printStackTrace();
            CameraUtils.releaseCamera(mCamera);
            mCamera = null;
        }
    }

    public void setFaceListener(IFaceListener listener){
        this.faceListener = listener;
    }

    public void stopPreview() {
        mIsCreateSurface = false;
        if (mCamera != null) {
            try {
                mCamera.setErrorCallback(null);
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CameraUtils.releaseCamera(mCamera);
                mCamera = null;
            }
        }
        if (mSurfaceHolder != null) {
            mSurfaceHolder.removeCallback(null);
        }
        if (mILivenessStrategy != null) {
            mILivenessStrategy = null;
        }
    }

    private int displayOrientation(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                degrees = 0;
                break;
        }
        int result = (0 - degrees + 360) % 360;
        if (APIUtils.hasGingerbread()) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraId, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                result = (info.orientation - degrees + 360) % 360;
            }
        }
        return result;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsCreateSurface = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format,
                               int width,
                               int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        if (holder.getSurface() == null) {
            return;
        }
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsCreateSurface = false;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
       // Log.i(TAG,"mIsCompletion="+mIsCompletion);
        if (mIsCompletion) {
            return;
        }

        if (mILivenessStrategy == null) {
            mILivenessStrategy = FaceSDKManager.getInstance().getLivenessStrategyModule();
            // mILivenessStrategy.setPreviewDegree(mPreviewDegree);
            mILivenessStrategy.setPreviewDegree(360-mPreviewDegree);
            mILivenessStrategy.setLivenessStrategySoundEnable(mIsEnableSound);

            Rect detectRect = FaceDetectRoundView.getPreviewDetectRect(
                    mDisplayWidth, mPreviewHight, mPreviewWidth);
            mILivenessStrategy.setLivenessStrategyConfig(
                    mFaceConfig.getLivenessTypeList(), mPreviewRect, detectRect, this);
        }
        mILivenessStrategy.livenessStrategy(data);
    }

    @Override
    public void onError(int error, Camera camera) {
    }

    @Override
    public void onLivenessCompletion(FaceStatusEnum status, String message,
                                     HashMap<String, String> base64ImageMap,int[] imgByte) {
        if (mIsCompletion) {//
            return;
        }
//        Log.i(TAG,"imgByte="+imgByte);
//        Log.e(TAG,"imgByte="+imgByte);

        if(imgByte!=null){
            //判断当前的屏幕方向 横屏的需要调换一下宽高
            if(context.getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE)
                image = BitmapUtils.createLivenessBitmap( imgByte, mPreviewRect,true);
            else
                image = BitmapUtils.createLivenessBitmap( imgByte, mPreviewRect,false);
//            Log.i(TAG,"image="+image);
//            Log.e(TAG,"image="+image);
            //ivText.setImageBitmap(image);
        }
        onRefreshView(status, message,image);
//        if (status == FaceStatusEnum.OK) {
//            mIsCompletion = true;
//           // saveImage(base64ImageMap);
//        }
       // Ast.getInstance().faceHit("liveness");
    }


    /**刷新view方法*/
    public void onRefreshView(FaceStatusEnum status, String message,Bitmap img) {
       // Log.e(TAG,"status="+status.toString()+",message="+message);
        if(faceListener!=null){
            faceListener.faceResult(status.toString(),message);
        }

        switch (status) {
            case OK:
            case Liveness_OK:
            case Liveness_Completion:
            case Error_DetectTimeout://检测超时
            case Error_LivenessTimeout://活体验证超时
            case Error_Timeout:
               // stopPreview();
               // startPreview();
                break;
            case Detect_DataNotReady:
            case Liveness_Eye://眨眼
            case Liveness_Mouth://张嘴
            case Liveness_HeadUp://向上抬头
            case Liveness_HeadDown:
            case Liveness_HeadLeft:
            case Liveness_HeadRight:
            case Liveness_HeadLeftRight:
                break;
            case Detect_PitchOutOfUpMaxRange:
            case Detect_PitchOutOfDownMaxRange:
            case Detect_PitchOutOfLeftMaxRange:
            case Detect_PitchOutOfRightMaxRange:
                break;
            default:
        }
    }



}