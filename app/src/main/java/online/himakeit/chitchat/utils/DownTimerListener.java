package online.himakeit.chitchat.utils;

/**
 * @author：LiXueLong
 * @date：2018/1/12
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des:倒计时监听类
 */
public interface DownTimerListener {

    /**
     * [倒计时每秒方法]<BR>
     * @param millisUntilFinished
     */
    void onTick(long millisUntilFinished);

    /**
     * [倒计时完成方法]<BR>
     */
    void onFinish();
}