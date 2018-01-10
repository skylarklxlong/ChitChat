package online.himakeit.chitchat.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import io.rong.imkit.RongIM;
import online.himakeit.chitchat.R;
import online.himakeit.chitchat.SealAppContext;

/**
 * @author：LiXueLong
 * @date:2018/1/6-20:14
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des：SplashActivity
 */
public class SplashActivity extends AppCompatActivity {

    private Context context;
    private android.os.Handler handler = new android.os.Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this;

        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String loginToken = sharedPreferences.getString("loginToken", "");
        if (!TextUtils.isEmpty(loginToken)) {
            RongIM.connect(loginToken, SealAppContext.getInstance().getConnectCallback());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToMain();
                }
            }, 800);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToLogin();
                }
            }, 800);
        }
    }

    private void goToMain() {
        startActivity(new Intent(context, MainActivity.class));
        finish();
    }

    private void goToLogin() {
        startActivity(new Intent(context, LoginActivity.class));
        finish();
    }
}
