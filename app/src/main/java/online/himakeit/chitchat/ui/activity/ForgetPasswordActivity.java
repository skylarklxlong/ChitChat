package online.himakeit.chitchat.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import online.himakeit.chitchat.R;
import online.himakeit.chitchat.ui.widget.ClearWriteEditText;

/**
 * @author：LiXueLong
 * @date:2018/1/10-15:26
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des：ForgetPasswordActivity
 */
public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mIvBackground;
    private ClearWriteEditText mCwedPhone, mCwedCode, mCwedPassword, mCwedPwdConfirm;
    private Button mBtnGetCode, mBtnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        setHeadVisibility(false);
        initview();
    }

    private void initview() {
        mIvBackground = findViewById(R.id.iv_forget_background);
        mCwedPhone = findViewById(R.id.cwed_forget_phone);
        mCwedCode = findViewById(R.id.cwed_forget_code);
        mCwedPassword = findViewById(R.id.cwed_forget_password);
        mCwedPwdConfirm = findViewById(R.id.cwed_forget_password_confirm);
        mBtnGetCode = findViewById(R.id.btn_forget_getcode);
        mBtnConfirm = findViewById(R.id.btn_forget_confirm);

        mBtnGetCode.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.translate_anim);
                mIvBackground.startAnimation(animation);
            }
        }, 200);
    }

    @Override
    public void onClick(View v) {

    }
}
