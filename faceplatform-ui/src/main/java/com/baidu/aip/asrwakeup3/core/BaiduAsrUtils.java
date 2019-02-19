package com.baidu.aip.asrwakeup3.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.baidu.aip.asrwakeup3.core.recog.MyRecognizer;
import com.baidu.aip.asrwakeup3.core.recog.listener.IRecogListener;
import com.baidu.aip.asrwakeup3.core.recog.listener.MessageStatusRecogListener;

import java.util.Map;

/**
 * Created by camming on 2018\11\7 0007.
 *
 * 百度语音识别 单例
 */

public class BaiduAsrUtils {



    /**
     * 识别控制器，使用MyRecognizer控制识别的流程
     */
    protected MyRecognizer myRecognizer;
    //protected Handler handler;
    private Context mContent;
    private static BaiduAsrUtils install= null;


    public static BaiduAsrUtils getInstall(){
        if(install==null)
            install = new BaiduAsrUtils();
        return install;
    }

    public  void initAsr(Context context, Handler handler){
        mContent = context;

        // 1.1 新建一个回调类，识别引擎会回调这个类告知重要状态和识别结果
        IRecogListener listener = new MessageStatusRecogListener(handler);
        //  1.2 初始化：new一个IRecogListener示例 & new 一个 MyRecognizer 示例
        if(myRecognizer!=null)
            myRecognizer.release();
        myRecognizer = new MyRecognizer(mContent, listener);

    }

    /**
     * 开始语音识别
     * */
    public void startAsr() {
        final Map<String, Object> params = fetchParams();
        params.put("vad.endpoint-timeout",0);//设置为长语音识别
        if(myRecognizer!=null)
            myRecognizer.start(params);
    }
    protected Map<String, Object> fetchParams() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContent);
        //  上面的获取是为了生成下面的Map， 自己集成时可以忽略
        Map<String, Object> params = new OnlineRecogParams().fetch(sp);
        //  集成时不需要上面的代码，只需要params参数。
        return params;
    }
    /**
     * 停止语音识别
     * 停止后的语音不再识别 停止前的语音识别
     * */
    public void stop() {
        if(myRecognizer!=null)
            myRecognizer.stop();
    }

    /**
     * 开始录音后，手动点击“取消”按钮。
     * SDK会取消本次识别，回到原始状态。
     */
    public void cancel() {
        if(myRecognizer!=null)
            myRecognizer.cancel();
    }

    public void release(){
        if(myRecognizer!=null)
            myRecognizer.release();
    }
}
