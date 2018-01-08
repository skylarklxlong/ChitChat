package online.himakeit.chitchat.server.network.async;

import online.himakeit.chitchat.server.network.http.HttpException;

/**
 * @author：LiXueLong
 * @date：2018/1/8
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des:
 */
public interface OnDataListener {
    /**
     * 异步耗时方法
     * @String parameter 请求传参,可不填
     * @param requestCode 请求码
     * @return
     * @throws HttpException
     */
    Object doInBackground(int requestCode, String parameter) throws HttpException;
    /**
     * 成功方法（可直接更新UI）
     * @param requestCode 请求码
     * @param result 返回结果
     */
    void onSuccess(int requestCode, Object result);

    /**
     * 失败方法（可直接更新UI）
     * @param requestCode 请求码
     * @param state 返回状态
     * @param result 返回结果
     */
    void onFailure(int requestCode, int state, Object result);
}
