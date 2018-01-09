package online.himakeit.chitchat;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

/**
 * @author：LiXueLong
 * @date：2018/1/9
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des:融云相关监听,事件集合类
 */
public class SealAppContext implements RongIM.ConversationListBehaviorListener,
        RongIM.ConversationBehaviorListener,
        RongIM.UserInfoProvider,
        RongIM.GroupInfoProvider,
        RongIM.GroupUserInfoProvider,
        RongIM.LocationProvider,
        RongIMClient.OnReceiveMessageListener,
        RongIMClient.ConnectionStatusListener {


    private static final int CLICK_CONVERSATION_USER_PORTRAIT = 1;


    private final static String TAG = "SealAppContext";
    public static final String UPDATE_FRIEND = "update_friend";
    public static final String UPDATE_RED_DOT = "update_red_dot";
    public static final String UPDATE_GROUP_NAME = "update_group_name";
    public static final String UPDATE_GROUP_MEMBER = "update_group_member";
    public static final String GROUP_DISMISS = "group_dismiss";

    private Context mContext;
    private static SealAppContext mInstance;
    private RongIM.LocationProvider.LocationCallback mLocationCallback;
    private static ArrayList<Activity> mActivities;

    public SealAppContext(Context mContext) {
        this.mContext = mContext;
        initListener();
        mActivities = new ArrayList<>();
        // TODO: 2018/1/9 初始化SealUserInfoManager

    }

    /**
     * 初始化 RongCloud.
     *
     * @param context
     */
    public static void init(Context context) {
        if (mInstance == null) {
            synchronized (SealAppContext.class) {
                if (mInstance == null) {
                    mInstance = new SealAppContext(context);
                }
            }
        }
    }

    /**
     * 获取RongCloud 实例
     *
     * @return
     */
    public static SealAppContext getInstance() {
        return mInstance;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * init 后就能设置的监听
     */
    private void initListener() {

    }

    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        return false;
    }

    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        return false;
    }

    @Override
    public boolean onMessageClick(Context context, View view, Message message) {
        return false;
    }

    @Override
    public boolean onMessageLinkClick(Context context, String s) {
        return false;
    }

    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }

    @Override
    public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    @Override
    public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    @Override
    public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
        return false;
    }

    @Override
    public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
        return false;
    }

    @Override
    public Group getGroupInfo(String s) {
        return null;
    }

    @Override
    public GroupUserInfo getGroupUserInfo(String s, String s1) {
        return null;
    }

    @Override
    public void onStartLocation(Context context, LocationCallback locationCallback) {

    }

    @Override
    public UserInfo getUserInfo(String s) {
        return null;
    }

    @Override
    public void onChanged(ConnectionStatus connectionStatus) {

    }

    @Override
    public boolean onReceived(Message message, int i) {
        return false;
    }
}
