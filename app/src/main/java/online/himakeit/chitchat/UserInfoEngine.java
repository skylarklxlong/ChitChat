package online.himakeit.chitchat;

import android.content.Context;
import android.net.Uri;

import io.rong.imlib.model.UserInfo;
import online.himakeit.chitchat.server.ChatAction;
import online.himakeit.chitchat.server.network.async.AsyncTaskManager;
import online.himakeit.chitchat.server.network.async.OnDataListener;
import online.himakeit.chitchat.server.network.http.HttpException;
import online.himakeit.chitchat.server.response.GetUserInfoByIdResponse;

/**
 * @author：LiXueLong
 * @date：2018/1/12
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des:用户信息提供者的异步请求类
 */
public class UserInfoEngine  implements OnDataListener {


    private static UserInfoEngine instance;
    private UserInfoListener mListener;
    private Context context;

    public static UserInfoEngine getInstance(Context context) {
        if (instance == null) {
            instance = new UserInfoEngine(context);
        }
        return instance;
    }

    private UserInfoEngine(Context context) {
        this.context = context;
    }


    private String userid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    private static final int REQUSERINFO = 4234;

    public void startEngine(String userid) {
        setUserid(userid);
        AsyncTaskManager.getInstance(context).request(userid, REQUSERINFO, this);
    }

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        return new ChatAction(context).getUserInfoById(id);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            GetUserInfoByIdResponse res = (GetUserInfoByIdResponse) result;
            if (res.getCode() == 200) {
                UserInfo userInfo = new UserInfo(res.getResult().getId(), res.getResult().getNickname(), Uri.parse(res.getResult().getPortraitUri()));
                if (mListener != null) {
                    mListener.onResult(userInfo);
                }
            }
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        if (mListener != null) {
            mListener.onResult(null);
        }
    }

    public void setListener(UserInfoListener listener) {
        this.mListener = listener;
    }

    public interface UserInfoListener {
        void onResult(UserInfo info);
    }
}