package online.himakeit.chitchat.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.socks.library.KLog;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import online.himakeit.chitchat.Constant;
import online.himakeit.chitchat.R;
import online.himakeit.chitchat.server.network.http.HttpException;
import online.himakeit.chitchat.server.response.GetTokenResponse;
import online.himakeit.chitchat.server.response.GetUserInfoByIdResponse;
import online.himakeit.chitchat.server.response.LoginResponse;
import online.himakeit.chitchat.ui.widget.ClearWriteEditText;
import online.himakeit.chitchat.ui.widget.LoadDialog;
import online.himakeit.chitchat.utils.KeyboardUtils;
import online.himakeit.chitchat.utils.TextStrUtils;
import online.himakeit.chitchat.utils.Toasts;

/**
 * @author：LiXueLong
 * @date:2018/1/8-15:39
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des：LoginActivity
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private static final int LOGIN = 5;
    private static final int GET_TOKEN = 6;
    private static final int SYNC_USER_INFO = 9;

    private ImageView mIvBackground;
    private ClearWriteEditText mCwedUserName, mCwedPassWord;
    private String phoneString;
    private String passwordString;
    private String connectResultId;
    private SharedPreferences mSp;
    private SharedPreferences.Editor mSpEditor;
    private String loginToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setHeadVisibility(false);

        mSp = getSharedPreferences("config", MODE_PRIVATE);
        mSpEditor = mSp.edit();
        initview();
    }

    private void initview() {
        mIvBackground = findViewById(R.id.iv_login_background);
        mCwedUserName = findViewById(R.id.cwed_login_phone);
        mCwedPassWord = findViewById(R.id.cwed_login_pwd);
        Button mBtnSign = findViewById(R.id.btn_login_sign);
        TextView mTvForgot = findViewById(R.id.tv_login_forgot);
        TextView mTvRegister = findViewById(R.id.tv_login_register);

        mBtnSign.setOnClickListener(this);
        mTvForgot.setOnClickListener(this);
        mTvRegister.setOnClickListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.translate_anim);
                mIvBackground.startAnimation(animation);
            }
        }, 200);

        mCwedUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 11) {
                    KeyboardUtils.hideSoftInput(mContext, mCwedUserName);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /**
         * 是否登录过，取出已缓存的帐号密码
         */
        String mStrOldPhone = mSp.getString(Constant.SEALTALK_LOGING_PHONE, "");
        String mStrOldPassword = mSp.getString(Constant.SEALTALK_LOGING_PASSWORD, "");
        if (!TextUtils.isEmpty(mStrOldPhone) && !TextUtils.isEmpty(mStrOldPassword)) {
            mCwedUserName.setText(mStrOldPhone);
            mCwedPassWord.setText(mStrOldPassword);
        }

        if (getIntent().getBooleanExtra("kickedByOtherClient", false)) {
            final AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
            alertDialog.show();
            Window window = alertDialog.getWindow();
            window.setContentView(R.layout.dialog_login);
            TextView mTvOk = window.findViewById(R.id.tv_ok);
            mTvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                }
            });

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login_forgot:
                startActivityForResult(new Intent(LoginActivity.this, ForgetPasswordActivity.class), 1);
                break;
            case R.id.tv_login_register:
                startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class), 1);
                break;
            case R.id.btn_login_sign:
                phoneString = mCwedUserName.getText().toString().trim();
                passwordString = mCwedPassWord.getText().toString().trim();
                if (TextUtils.isEmpty(phoneString)) {
                    Toasts.showShort("手机号不能为空");
                    mCwedUserName.shakeAnimation();
                    return;
                }
                if (TextStrUtils.isMobileNum(phoneString)) {
                    Toasts.showShort("请输入正确的手机号码");
                    mCwedUserName.shakeAnimation();
                    return;
                }
                if (TextUtils.isEmpty(passwordString)) {
                    Toasts.showShort("密码不能为空");
                    mCwedPassWord.shakeAnimation();
                    return;
                }
                if (" ".equals(passwordString)) {
                    Toasts.showShort("密码不能为包含空格");
                    mCwedPassWord.shakeAnimation();
                    return;
                }
                LoadDialog.show(mContext);
                mSpEditor.putBoolean("exit", false);
                mSpEditor.apply();
                request(LOGIN, true);
                break;
            default:
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && data != null) {
            String phone = data.getStringExtra("phone");
            String password = data.getStringExtra("password");
            mCwedUserName.setText(phone);
            mCwedPassWord.setText(password);
        } else if (requestCode == 1 && data != null) {
            String phone = data.getStringExtra("phone");
            String password = data.getStringExtra("password");
            String id = data.getStringExtra("id");
            String nickname = data.getStringExtra("nickname");
            if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(id) && !TextUtils.isEmpty(nickname)) {
                mCwedUserName.setText(phone);
                mCwedPassWord.setText(password);
                mSpEditor.putString(Constant.SEALTALK_LOGING_PHONE, phone);
                mSpEditor.putString(Constant.SEALTALK_LOGING_PASSWORD, password);
                mSpEditor.putString(Constant.SEALTALK_LOGIN_ID, id);
                mSpEditor.putString(Constant.SEALTALK_LOGIN_NAME, nickname);
                mSpEditor.apply();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        switch (requestCode) {
            case LOGIN:
                return mAction.login("86", phoneString, passwordString);
            case GET_TOKEN:
                return mAction.getToken();
            case SYNC_USER_INFO:
                return mAction.getUserInfoById(connectResultId);
            default:
        }
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case LOGIN:
                    LoginResponse loginResponse = (LoginResponse) result;
                    if (loginResponse.getCode() == 200) {
                        loginToken = loginResponse.getResult().getToken();
                        if (!TextUtils.isEmpty(loginToken)) {
                            RongIM.connect(loginToken, new RongIMClient.ConnectCallback() {
                                @Override
                                public void onTokenIncorrect() {
                                    KLog.e(TAG, " LOGIN onTokenIncorrect");
                                    reGetToken();
                                }

                                @Override
                                public void onSuccess(String s) {
                                    connectResultId = s;
                                    KLog.e(TAG, "LOGIN onSuccess connectResultId is : " + s);
                                    mSpEditor.putString(Constant.SEALTALK_LOGIN_ID, s);
                                    mSpEditor.apply();
                                    // TODO: 2018/1/9 修改数据库存储路径为根据userId
                                    request(SYNC_USER_INFO, true);

                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    KLog.e(TAG, "LOGIN onError " + errorCode.getValue() + "----" + errorCode.getMessage());
                                }
                            });
                        }
                    } else if (loginResponse.getCode() == 100) {
                        LoadDialog.dismiss(mContext);
                        Toasts.showShort("手机号码或密码错误");
                    } else if (loginResponse.getCode() == 1000) {
                        LoadDialog.dismiss(mContext);
                        Toasts.showShort("手机号码或密码错误");
                    }
                    break;
                case GET_TOKEN:
                    GetTokenResponse getTokenResponse = (GetTokenResponse) result;
                    if (getTokenResponse.getCode() == 200) {
                        String token = getTokenResponse.getResult().getToken();
                        if (!TextUtils.isEmpty(token)) {
                            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                                @Override
                                public void onTokenIncorrect() {
                                    KLog.e(TAG, "GET_TOKEN onTokenIncorrect");
                                }

                                @Override
                                public void onSuccess(String s) {
                                    connectResultId = s;
                                    KLog.e(TAG, "GET_TOKEN onSuccess connectResultId is : " + s);
                                    mSpEditor.putString(Constant.SEALTALK_LOGIN_ID, s);
                                    mSpEditor.apply();
                                    // TODO: 2018/1/9 修改数据库存储路径为根据userId
                                    request(SYNC_USER_INFO, true);
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    KLog.e(TAG, "GET_TOKEN onError " + errorCode.getValue()
                                            + "----" + errorCode.getMessage());
                                }
                            });
                        }
                    }
                    break;
                case SYNC_USER_INFO:
                    GetUserInfoByIdResponse getUserInfoByIdResponse = (GetUserInfoByIdResponse) result;
                    if (getUserInfoByIdResponse.getCode() == 200) {
                        if (TextUtils.isEmpty(getUserInfoByIdResponse.getResult().getPortraitUri())) {
                            // TODO: 2018/1/9 要做处理
                        }
                        String nickname = getUserInfoByIdResponse.getResult().getNickname();
                        String portraitUri = getUserInfoByIdResponse.getResult().getPortraitUri();
                        mSpEditor.putString(Constant.SEALTALK_LOGIN_NAME, nickname);
                        mSpEditor.putString(Constant.SEALTALK_LOGING_PORTRAIT, portraitUri);
                        mSpEditor.apply();
                        RongIM.getInstance().refreshUserInfoCache(
                                new UserInfo(connectResultId, nickname, Uri.parse(portraitUri)));
                    }
                    // TODO: 2018/1/9 同步好友,群组,群组成员,黑名单数据
                    goToMain();
                    break;
                default:
            }
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        // TODO: 2018/1/9 判断是否有网
        switch (requestCode) {
            case LOGIN:
                LoadDialog.dismiss(mContext);
                Toasts.showShort("登录接口请求失败");
                break;
            case GET_TOKEN:
                LoadDialog.dismiss(mContext);
                Toasts.showShort("获取token接口请求失败");
                break;
            case SYNC_USER_INFO:
                LoadDialog.dismiss(mContext);
                Toasts.showShort("同步用户信息接口请求失败");
                break;
            default:
        }
    }

    private void reGetToken() {
        request(GET_TOKEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void goToMain() {
        mSpEditor.putString("loginToken", loginToken);
        mSpEditor.putString(Constant.SEALTALK_LOGING_PHONE, phoneString);
        mSpEditor.putString(Constant.SEALTALK_LOGING_PASSWORD, passwordString);
        mSpEditor.apply();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
