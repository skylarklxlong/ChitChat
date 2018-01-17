package online.himakeit.baseframe.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import online.himakeit.baseframe.R;
import online.himakeit.baseframe.ui.fragment.ContactFragment;
import online.himakeit.baseframe.ui.fragment.ConversationFragment;
import online.himakeit.baseframe.ui.fragment.DiscoverFragment;
import online.himakeit.baseframe.ui.fragment.MineFragment;
import online.himakeit.baseframe.ui.widget.DragPointView;
import online.himakeit.baseframe.ui.widget.MorePopWindow;

/**
 * @author：LiXueLong
 * @date:2018/1/8-15:39
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des：MainActivity
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener, DragPointView.OnDragListencer, ViewPager.OnPageChangeListener {

    ImageView mIvChats, mIvContact, mIvDiscover, mIvMe, mIvContactDot, mIvDiscoverDot,
            mIvMeDot, mIvSearch, mIvAdd;
    TextView mTvChats, mTvContact, mTvDiscover, mTvMe;
    public static ViewPager mViewPager;
    DragPointView mDpvUnReadNum;
    List<Fragment> mFragmentList = new ArrayList<>();

    long firstClick = 0;
    long secondClick = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        changeSelectedTabState(0);
        initMainViewPager();
    }

    private void changeSelectedTabState(int position) {
        mIvChats.setBackgroundDrawable(getResources().getDrawable(position == 0 ? R.drawable.ic_tab_chat_hover : R.drawable.ic_tab_chat));
        mTvChats.setTextColor(position == 0 ? getResources().getColor(R.color.colorPrimary) : getResources().getColor(R.color.color_text_03));

        mIvContact.setBackgroundDrawable(getResources().getDrawable(position == 1 ? R.drawable.ic_tab_contacts_hover : R.drawable.ic_tab_contacts));
        mTvContact.setTextColor(position == 1 ? getResources().getColor(R.color.colorPrimary) : getResources().getColor(R.color.color_text_03));

        mIvDiscover.setBackgroundDrawable(getResources().getDrawable(position == 2 ? R.drawable.ic_tab_discover_hover : R.drawable.ic_tab_discover));
        mTvDiscover.setTextColor(position == 2 ? getResources().getColor(R.color.colorPrimary) : getResources().getColor(R.color.color_text_03));

        mIvMe.setBackgroundDrawable(getResources().getDrawable(position == 3 ? R.drawable.ic_tab_me_hover : R.drawable.ic_tab_me));
        mTvMe.setTextColor(position == 3 ? getResources().getColor(R.color.colorPrimary) : getResources().getColor(R.color.color_text_03));
    }

    private void initMainViewPager() {
        mViewPager = findViewById(R.id.vp_main);
        mDpvUnReadNum = findViewById(R.id.dpv_bottom_chats);
        mDpvUnReadNum.setOnClickListener(this);
        mDpvUnReadNum.setDragListencer(this);

        mFragmentList.add(new ConversationFragment());
        mFragmentList.add(new ContactFragment());
        mFragmentList.add(new DiscoverFragment());
        mFragmentList.add(new MineFragment());

        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }
        };
        mViewPager.setAdapter(fragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setOnPageChangeListener(this);
        initData();
    }

    private void initData() {
    }

    private void initViews() {

        RelativeLayout mRlChats = findViewById(R.id.rl_bottom_chats);
        RelativeLayout mRlContact = findViewById(R.id.rl_bottom_contact);
        RelativeLayout mRlDiscover = findViewById(R.id.rl_bottom_discover);
        RelativeLayout mRlMe = findViewById(R.id.rl_bottom_me);

        mIvChats = findViewById(R.id.iv_bottom_chats);
        mIvContact = findViewById(R.id.iv_bottom_contact);
        mIvDiscover = findViewById(R.id.iv_bottom_discover);
        mIvMe = findViewById(R.id.iv_bottom_me);

        mTvChats = findViewById(R.id.tv_bottom_chats);
        mTvContact = findViewById(R.id.tv_bottom_contact);
        mTvDiscover = findViewById(R.id.tv_bottom_discover);
        mTvMe = findViewById(R.id.tv_bottom_me);

        mIvContactDot = findViewById(R.id.iv_bottom_contact_dot);
        mIvDiscoverDot = findViewById(R.id.iv_bottom_discover_dot);
        mIvMeDot = findViewById(R.id.iv_bottom_me_dot);

        mIvSearch = findViewById(R.id.iv_top_search);
        mIvAdd = findViewById(R.id.iv_top_add);

        mRlChats.setOnClickListener(this);
        mRlContact.setOnClickListener(this);
        mRlDiscover.setOnClickListener(this);
        mRlMe.setOnClickListener(this);
        mIvAdd.setOnClickListener(this);
        mIvSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_bottom_chats:
                if (mViewPager.getCurrentItem() == 0) {
                    if (firstClick == 0) {
                        firstClick = System.currentTimeMillis();
                    } else {
                        secondClick = System.currentTimeMillis();
                    }
                    KLog.e("MainActivity time = " + (secondClick - firstClick));
                    if (secondClick - firstClick > 0 && secondClick - firstClick <= 800) {
                        firstClick = 0;
                        secondClick = 0;
                    } else if (firstClick != 0 && secondClick != 0) {
                        firstClick = 0;
                        secondClick = 0;
                    }
                }
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.rl_bottom_contact:
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.rl_bottom_discover:
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.rl_bottom_me:
                mViewPager.setCurrentItem(3, false);
                mIvMeDot.setVisibility(View.GONE);
                break;
            case R.id.iv_top_add:
                MorePopWindow morePopWindow = new MorePopWindow(MainActivity.this);
                morePopWindow.showPopupWindow(mIvAdd);
                break;
            case R.id.iv_top_search:
                break;
            default:
        }
    }

    @Override
    public void onDragOut() {
        mDpvUnReadNum.setVisibility(View.GONE);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        changeSelectedTabState(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
