package online.himakeit.chitchat.ui.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.HashMap;
import java.util.Map;

import online.himakeit.chitchat.utils.JsonManager;

/**
 * @author：LiXueLong
 * @date：2018/1/10
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des: 集中管理广播
 * [A brief description]
 * //在任何地方发送广播
 * BroadcastManager.getInstance(mContext).sendBroadcast(FindOrderActivity.ACTION_RECEIVE_MESSAGE);
 * //页面在oncreate中初始化广播
 * BroadcastManager.getInstance(mContext).addAction(ACTION_RECEIVE_MESSAGE, new BroadcastReceiver(){
 * @Override public void onReceive(Context arg0, Intent intent) {
 * String command = intent.getAction();
 * if(!TextUtils.isEmpty(command)){
 * if((ACTION_RECEIVE_MESSAGE).equals(command)){
 * //获取json结果
 * String json = intent.getStringExtra("result");
 * //做你该做的事情
 * }
 * }
 * }
 * });
 * //页面在ondestory销毁广播
 * BroadcastManager.getInstance(mContext).destroy(ACTION_RECEIVE_MESSAGE);
 */
public class BroadcastManager {
    private Context mContext;
    private static BroadcastManager mInstance;
    private Map<String, BroadcastReceiver> receiverMap;

    /**
     * 构造方法
     *
     * @param mContext
     */
    private BroadcastManager(Context mContext) {
        this.mContext = mContext;
        receiverMap = new HashMap<String, BroadcastReceiver>();
    }

    /**
     * 获取BroadcastManager的实列，单例模式
     *
     * @param context
     * @return
     */
    public static BroadcastManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BroadcastManager.class) {
                if (mInstance == null) {
                    mInstance = new BroadcastManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 添加注册广播接收器
     *
     * @param action
     * @param receiver
     */
    public void addAction(String action, BroadcastReceiver receiver) {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(action);
            mContext.registerReceiver(receiver, filter);
            receiverMap.put(action, receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送广播
     *
     * @param action 唯一标识
     */
    public void sendBroadcast(String action) {
        sendBroadcast(action, "");
    }

    /**
     * 发送广播
     *
     * @param action 唯一标识
     * @param obj    参数
     */
    public void sendBroadcast(String action, Object obj) {
        try {
            Intent intent = new Intent();
            intent.setAction(action);
            intent.putExtra("result", JsonManager.beanToJson(obj));
            mContext.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送广播
     *
     * @param action
     * @param s
     */
    public void sendBroadcast(String action, String s) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("string", s);
        mContext.sendBroadcast(intent);
    }

    /**
     * 销毁广播
     *
     * @param action
     */
    public void destroy(String action) {
        if (receiverMap != null) {
            BroadcastReceiver receiver = receiverMap.get(action);
            if (receiver != null) {
                mContext.unregisterReceiver(receiver);
            }
        }
    }

}
