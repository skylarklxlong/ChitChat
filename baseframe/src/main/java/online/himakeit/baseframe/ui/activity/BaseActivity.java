package online.himakeit.baseframe.ui.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import online.himakeit.baseframe.R;

/**
 * @author：LiXueLong
 * @date：2018/1/8
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des:
 */
public class BaseActivity extends FragmentActivity {
    protected Context mContext;
    private ViewFlipper mContentView;
    private LinearLayout mHeadLayout;
    private Button mBtnLeft, mBtnRight;
    private TextView mTvTitle, mTvHeadRight;
    private Drawable mDwBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.layout_base);
        /**
         * 使得音量键控制媒体声音
         */
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mContext = this;

        initView();

    }

    private void initView() {
        mContentView = findViewById(R.id.vf_container);
        mHeadLayout = findViewById(R.id.layout_head);
        mBtnLeft = findViewById(R.id.btn_left);
        mBtnRight = findViewById(R.id.btn_right);
        mTvTitle = findViewById(R.id.tv_title);
        mTvHeadRight = findViewById(R.id.tv_right);
        /**
         * 设置跳动的界限
         */
        mDwBtnBack = getResources().getDrawable(R.drawable.back_icon);
        mDwBtnBack.setBounds(0, 0, mDwBtnBack.getMinimumWidth(), mDwBtnBack.getMinimumHeight());
    }

    @Override
    public void setContentView(View view) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        mContentView.addView(view, lp);
    }

    @Override
    public void setContentView(int layoutResID) {
        View inflate = LayoutInflater.from(mContext).inflate(layoutResID, null);
        setContentView(inflate);
    }

    /**
     * 设置头部是否显示
     *
     * @param visibility
     */
    public void setHeadVisibility(boolean visibility) {
        mHeadLayout.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置头部左边按钮是否可见
     *
     * @param visibility
     */
    public void setHeadLeftBtnVisibility(boolean visibility) {
        mBtnLeft.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置头部右边按钮是否可见
     *
     * @param visibility
     */
    public void setHeadRightBtnVisibility(boolean visibility) {
        mBtnRight.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置头部标题
     *
     * @param titleId
     */
    public void setHeadTitle(int titleId) {
        setHeadTitle(titleId, false);
    }

    /**
     * 设置头部标题
     *
     * @param titleId
     * @param flag
     */
    public void setHeadTitle(int titleId, boolean flag) {
        setHeadTitle(getString(titleId), false);
    }

    /**
     * 设置头部标题
     *
     * @param title
     */
    public void setHeadTitle(String title) {
        setHeadTitle(title, false);
    }

    /**
     * 设置头部标题
     *
     * @param title
     * @param flag
     */
    public void setHeadTitle(String title, boolean flag) {
        mTvTitle.setText(title);
        if (flag) {
            mBtnLeft.setCompoundDrawables(null, null, null, null);
        } else {
            mBtnLeft.setCompoundDrawables(mDwBtnBack, null, null, null);
        }
    }

    /**
     * 头部左边按钮点击事件
     *
     * @param view
     */
    public void onHeadLeftButtonClick(View view) {
        finish();
    }

    /**
     * 头部右边按钮点击事件
     *
     * @param view
     */
    public void onHeadRightButtonClick(View view) {
    }

    public Button getmBtnLeft() {
        return mBtnLeft;
    }

    public void setmBtnLeft(Button mBtnLeft) {
        this.mBtnLeft = mBtnLeft;
    }

    public Button getmBtnRight() {
        return mBtnRight;
    }

    public void setmBtnRight(Button mBtnRight) {
        this.mBtnRight = mBtnRight;
    }

    public Drawable getmDwBtnBack() {
        return mDwBtnBack;
    }

    public void setmDwBtnBack(Drawable mDwBtnBack) {
        this.mDwBtnBack = mDwBtnBack;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            return inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }
}
