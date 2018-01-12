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
import android.widget.TextView;

import online.himakeit.chitchat.R;
import online.himakeit.chitchat.server.network.http.HttpException;
import online.himakeit.chitchat.server.response.CheckPhoneResponse;
import online.himakeit.chitchat.server.response.RegisterResponse;
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
 * @date:2018/1/10-15:03
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des：RegisterActivity
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener, DownTimerListener {

    private static final int CHECK_PHONE = 1;
    private static final int SEND_CODE = 2;
    private static final int VERIFY_CODE = 3;
    private static final int REGISTER = 4;
    private static final int REGISTER_BACK = 1001;
    private ImageView mIvBackground;
    private ClearWriteEditText mCwedPhone, mCwedCode, mCwedPassword, mCwedUsername;
    private Button mBtnGetCode, mBtnConfirm;
    private TextView mTvFindPwd, mTvLogin;
    private String mPhone, mCode, mNickName, mPassword, mCodeToken;
    private boolean isRequestCode = false;
    boolean isBright = true;

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
        mBtnGetCode.setClickable(false);
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
        addEditTextListener();
    }

    private void addEditTextListener() {
        mCwedPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 11 && isBright) {
                    if (!TextStrUtils.isMobileNum(s.toString().trim())) {
                        mPhone = s.toString().trim();
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
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mCwedPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 5) {
                    mBtnConfirm.setClickable(true);
                    mBtnConfirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_btn_pink));
                } else {
                    mBtnConfirm.setClickable(false);
                    mBtnConfirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_btn_gary));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_reg_find_password:
                startActivity(new Intent(RegisterActivity.this, ForgetPasswordActivity.class));
                break;
            case R.id.tv_reg_login:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                break;
            case R.id.btn_reg_getcode:
                if (TextUtils.isEmpty(mCwedPhone.getText().toString().trim())) {
                    Toasts.showShort("手机号不能为空");
                } else {
                    isRequestCode = true;
                    DownTimer downTimer = new DownTimer();
                    downTimer.setListener(this);
                    downTimer.startDown(60 * 1000);
                    request(SEND_CODE);
                }
                break;
            case R.id.btn_reg_register:
                mPhone = mCwedPhone.getText().toString().trim();
                mCode = mCwedCode.getText().toString().trim();
                mNickName = mCwedUsername.getText().toString().trim();
                mPassword = mCwedPassword.getText().toString().trim();


                if (TextUtils.isEmpty(mNickName)) {
                    Toasts.showShort("昵称不能为空");
                    mCwedUsername.setShakeAnimation();
                    return;
                }
                if (mNickName.contains(" ")) {
                    Toasts.showShort("昵称不能包含空格");
                    mCwedUsername.setShakeAnimation();
                    return;
                }

                if (TextUtils.isEmpty(mPhone)) {
                    Toasts.showShort("手机号不能为空");
                    mCwedPhone.setShakeAnimation();
                    return;
                }
                if (TextUtils.isEmpty(mCode)) {
                    Toasts.showShort("验证码不能为空");
                    mCwedCode.setShakeAnimation();
                    return;
                }
                if (TextUtils.isEmpty(mPassword)) {
                    Toasts.showShort("密码不能为空");
                    mCwedPassword.setShakeAnimation();
                    return;
                }
                if (mPassword.contains(" ")) {
                    Toasts.showShort("密码不能包含空格");
                    mCwedPassword.setShakeAnimation();
                    return;
                }

                if (!isRequestCode) {
                    Toasts.showShort("未向服务器获取验证码");
                    return;
                }

                LoadDialog.show(mContext);
                request(VERIFY_CODE, true);
                break;
            default:
        }
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mBtnGetCode.setText(String.valueOf(millisUntilFinished / 1000) + "s");
        mBtnGetCode.setClickable(false);
        mBtnGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_btn_gary));
        isBright = false;
    }

    @Override
    public void onFinish() {
        mBtnGetCode.setText("获取验证码");
        mBtnGetCode.setClickable(true);
        mBtnGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_btn_pink));
        isBright = true;
    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        switch (requestCode) {
            case CHECK_PHONE:
                return mAction.checkPhoneAvailable("86", mPhone);
            case SEND_CODE:
                return mAction.sendCode("86", mPhone);
            case VERIFY_CODE:
                return mAction.verifyCode("86", mPhone, mCode);
            case REGISTER:
                return mAction.register(mNickName, mPassword, mCodeToken);
            default:
        }
        return super.doInBackground(requestCode, parameter);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case CHECK_PHONE:
                    CheckPhoneResponse cprres = (CheckPhoneResponse) result;
                    if (cprres.getCode() == 200) {
                        if (cprres.isResult()) {
                            mBtnGetCode.setClickable(true);
                            mBtnGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_btn_pink));
                            Toasts.showShort("手机号可用");
                        } else {
                            mBtnGetCode.setClickable(false);
                            mBtnGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_btn_gary));
                            Toasts.showShort("手机号已被注册");
                        }
                    }
                    break;
                case SEND_CODE:
                    SendCodeResponse scrres = (SendCodeResponse) result;
                    if (scrres.getCode() == 200) {
                        Toasts.showShort("短信已发出，请注意查收");
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
                                request(REGISTER);
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
                            Toasts.showShort("验证码过期，请重新请求");
                            LoadDialog.dismiss(mContext);
                            break;
                        default:
                    }
                    break;

                case REGISTER:
                    RegisterResponse rres = (RegisterResponse) result;
                    switch (rres.getCode()) {
                        case 200:
                            LoadDialog.dismiss(mContext);
                            Toasts.showShort("注册成功");
                            Intent data = new Intent();
                            data.putExtra("phone", mPhone);
                            data.putExtra("password", mPassword);
                            data.putExtra("nickname", mNickName);
                            data.putExtra("id", rres.getResult().getId());
                            setResult(REGISTER_BACK, data);
                            this.finish();
                            break;
                        case 400:
                            // 错误的请求
                            break;
                        case 404:
                            //token 不存在
                            break;
                        case 500:
                            //应用服务端内部错误
                            break;
                        default:
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
            case VERIFY_CODE:
                LoadDialog.dismiss(mContext);
                Toasts.showShort("验证码是否可用请求失败");
                break;
            case REGISTER:
                LoadDialog.dismiss(mContext);
                Toasts.showShort("注册请求失败");
                break;
            default:
        }
    }
}
