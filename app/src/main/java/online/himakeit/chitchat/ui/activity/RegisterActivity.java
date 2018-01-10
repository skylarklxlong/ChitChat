package online.himakeit.chitchat.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import online.himakeit.chitchat.R;
import online.himakeit.chitchat.ui.widget.ClearWriteEditText;

/**
 * @author：LiXueLong
 * @date:2018/1/10-15:03
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des：RegisterActivity
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mIvBackground;
    private ClearWriteEditText mCwedPhone, mCwedCode, mCwedPassword, mCwedUsername;
    private Button mBtnGetCode, mBtnConfirm;
    private TextView mTvFindPwd, mTvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setHeadVisibility(false);
        initview();
    }

    private void initview() {
        mIvBackground = findViewById(R.id.iv_register_background);
        mCwedUsername = findViewById(R.id.cwed_reg_username);
        mCwedPhone = findViewById(R.id.cwed_reg_phone);
        mCwedCode = findViewById(R.id.cwed_reg_code);
        mCwedPassword = findViewById(R.id.cwed_reg_password);
        mBtnGetCode = findViewById(R.id.btn_reg_getcode);
        mBtnConfirm = findViewById(R.id.btn_reg_register);
        mTvFindPwd = findViewById(R.id.tv_reg_find_password);
        mTvLogin = findViewById(R.id.tv_reg_login);

        mBtnGetCode.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
        mTvFindPwd.setOnClickListener(this);
        mTvLogin.setOnClickListener(this);


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
        switch (v.getId()) {
            case R.id.tv_reg_find_password:
                break;
            case R.id.tv_reg_login:
                break;
            case R.id.btn_reg_getcode:
                break;
            case R.id.btn_reg_register:
                break;
            default:
        }
    }
}
