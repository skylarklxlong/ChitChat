package online.himakeit.chitchat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author：LiXueLong
 * @date：2018/1/8
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des:
 */
public class SharedPreferencesContext {
    private static SharedPreferencesContext mSharedPreferencesContext;
    private static Context mContext;
    private static SharedPreferences mSharedPreferences;

    public static void init(Context context) {
        mSharedPreferencesContext = new SharedPreferencesContext(context);
    }

    public static SharedPreferencesContext getInstance() {
        if (mSharedPreferencesContext == null) {
            mSharedPreferencesContext = new SharedPreferencesContext();
        }
        return mSharedPreferencesContext;
    }

    private SharedPreferencesContext() {
    }

    private SharedPreferencesContext(Context context) {
        mContext = context;
        mSharedPreferencesContext = this;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferences getmSharedPreferences() {
        return mSharedPreferences;
    }
}
