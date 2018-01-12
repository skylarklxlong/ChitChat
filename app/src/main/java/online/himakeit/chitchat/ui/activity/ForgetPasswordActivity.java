package online.himakeit.chitchat.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import online.himakeit.chitchat.R;
import online.himakeit.chitchat.server.network.http.HttpException;
import online.himakeit.chitchat.server.response.CheckPhoneResponse;
import online.himakeit.chitchat.server.response.RestPasswordResponse;
import online.himakeit.chitchat.server.response.SendCodeResponse;
import online.himakeit.chitchat.server.response.VerifyCodeResponse;
import online.himakeit.chitchat.ui.widget.ClearWriteEditText;
import online.himakeit.chitchat.ui.widget.LoadDialog;
import online.himakeit.chitchat.utils.DownTimer;
import online.himakeit.chitchat.utils.DownTimerListener;
import online.himakeit.chitchat.utils.KeyboardUtils;
import online.himakeit.chitchat.utils.TextStrUtils;
import online.himakeit.chitchat.utils.Toasts;

/**
 * @author：LiXueLong
 * @date:2018/1/10-15:26
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des：ForgetPasswordActivity
 */
public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener, DownTimerListener {

    private static final int CHECK_PHONE = 31;
    private static final int SEND_CODE = 32;
    private static final int CHANGE_PASSWORD = 33;
    private static final int VERIFY_CODE = 34;
    private static final int CHANGE_PASSWORD_BACK = 1002;
    private String phone, mCodeToken;
    private boolean available;
    private ImageView mIvBackground;
    private ClearWriteEditText mCwedPhone, mCwedCode, mCwedPassword, mCwedPwdConfirm;
    private Button mBtnGetCode, mBtnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        setHeadTitle("忘记密码");
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
        mCwedPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 11) {
                    if (!TextStrUtils.isMobileNum(s.toString().trim())) {
                        phone = mCwedPhone.getText().toString().trim();
                        request(CHECK_PHONE, true);
                        KeyboardUtils.hideSoftInput(mContext, mCwedPhone);
                    } else {
                        Toasts.showShort("非法手机号");
                    }
                } else {
                    mBtnGetCode.setClickable(false);
                    mBtnGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_btn_gary));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mCwedCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    KeyboardUtils.hideSoftInput(mContext, mCwedCode);
                    if (available) {
                        mBtnConfirm.setClickable(true);
                        mBtnConfirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_btn_pink));
                    }
                } else {
                    mBtnConfirm.setClickable(false);
                    mBtnConfirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_btn_gary));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
            case R.id.btn_forget_getcode:
                if (TextUtils.isEmpty(mCwedPhone.getText().toString().trim())) {
                    Toasts.showShort("手机号不能为空");
                } else {
                    DownTimer downTimer = new DownTimer();
                    downTimer.setListener(this);
                    downTimer.startDown(60 * 1000);
                    request(SEND_CODE);
                }
                break;
            case R.id.btn_forget_confirm:
                if (TextUtils.isEmpty(mCwedPhone.getText().toString())) {
                    Toasts.showShort("手机号不能为空");
                    mCwedPhone.setShakeAnimation();
                    return;
                }

                if (TextUtils.isEmpty(mCwedCode.getText().toString())) {
                    Toasts.showShort("验证码为空");
                    mCwedCode.setShakeAnimation();
                    return;
                }

                if (TextUtils.isEmpty(mCwedPassword.getText().toString())) {
                    Toasts.showShort("密码不能为空");
                    mCwedPassword.setShakeAnimation();
                    return;
                }

                if (mCwedPassword.length() < 6 || mCwedPassword.length() > 16) {
                    Toasts.showShort("密码必须为6-16位字符，区分大小写");
                    return;
                }

                if (TextUtils.isEmpty(mCwedPwdConfirm.getText().toString())) {
                    Toasts.showShort("确认密码不能为空");
                    mCwedPwdConfirm.setShakeAnimation();
                    return;
                }

                if (!mCwedPwdConfirm.getText().toString().equals(mCwedPassword.getText().toString())) {
                    Toasts.showShort("填写的确认密码与新密码不一致");
                    return;
                }

                LoadDialog.show(mContext);
                request(VERIFY_CODE);
                break;
            default:
        }

    }

    @Override
    public void onTick(long millisUntilFinished) {
        mBtnGetCode.setText("seconds:" + String.valueOf(millisUntilFinished / 1000));
        mBtnGetCode.setClickable(false);
        mBtnGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_btn_gary));
    }

    @Override
    public void onFinish() {
        mBtnGetCode.setText("获取验证码");
        mBtnGetCode.setClickable(true);
        mBtnGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_btn_pink));
    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        switch (requestCode) {
            case CHECK_PHONE:
                return mAction.checkPhoneAvailable("86", phone);
            case SEND_CODE:
                return mAction.sendCode("86", phone);
            case CHANGE_PASSWORD:
                return mAction.restPassword(mCwedPassword.getText().toString(), mCodeToken);
            case VERIFY_CODE:
                return mAction.verifyCode("86", phone, mCwedCode.getText().toString());
            default:
        }
        return super.doInBackground(requestCode, parameter);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case CHECK_PHONE:
                    CheckPhoneResponse response = (CheckPhoneResponse) result;
                    if (response.getCode() == 200) {
                        if (response.isResult()) {
                            Toasts.showShort("此号码未被注册");
                            mBtnGetCode.setClickable(false);
                            mBtnGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_btn_gary));
                        } else {
                            available = true;
                            mBtnGetCode.setClickable(true);
                            mBtnGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_btn_pink));
                        }
                    }
                    break;
                case SEND_CODE:
                    SendCodeResponse scrres = (SendCodeResponse) result;
                    if (scrres.getCode() == 200) {
                        Toasts.showShort("短信已发出,请注意查收");
                    } else if (scrres.getCode() == 5000) {
                        Toasts.showShort("短信发送频率超限");
                    }
                    break;
                case VERIFY_CODE:
                    VerifyCodeResponse vcres = (VerifyCodeResponse) result;
                    switch (vcres.getCode()) {
                        case 200:
                            mCodeToken = vcres.getResult().getVerification_token();
                            if (!TextUtils.isEmpty(mCodeToken)) {
                                request(CHANGE_PASSWORD);
                            } else {
                                Toasts.showShort("code token is null");
                                LoadDialog.dismiss(mContext);
                            }
                            break;
                        case 1000:
                            //验证码错误
                            Toasts.showShort("验证码错误");
                            LoadDialog.dismiss(mContext);
                            break;
                        case 2000:
                            //验证码过期
                            Toasts.showShort("验证码过期,请重新请求");
                            LoadDialog.dismiss(mContext);
                            break;
                        default:
                    }
                    break;

                case CHANGE_PASSWORD:
                    RestPasswordResponse response1 = (RestPasswordResponse) result;
                    if (response1.getCode() == 200) {
                        LoadDialog.dismiss(mContext);
                        Toasts.showShort("更改成功");
                        Intent data = new Intent();
                        data.putExtra("phone", phone);
                        data.putExtra("password", mCwedPassword.getText().toString());
                        setResult(CHANGE_PASSWORD_BACK, data);
                        this.finish();
                    }
                    break;
                default:
            }
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case CHECK_PHONE:
                Toasts.showShort("手机号可用请求失败");
                break;
            case SEND_CODE:
                Toasts.showShort("获取验证码请求失败");
                break;
            default:
        }
    }
}
