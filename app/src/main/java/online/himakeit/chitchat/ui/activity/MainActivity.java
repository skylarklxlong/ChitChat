package online.himakeit.chitchat.ui.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongContext;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;
import online.himakeit.chitchat.R;
import online.himakeit.chitchat.ui.adapter.ConversationListAdapterEx;
import online.himakeit.chitchat.ui.fragment.ContactFragment;
import online.himakeit.chitchat.ui.fragment.DiscoverFragment;
import online.himakeit.chitchat.ui.fragment.MineFragment;
import online.himakeit.chitchat.ui.widget.DragPointView;

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
    ViewPager mViewPager;
    DragPointView mDpvUnReadNum;
    List<Fragment> mFragmentList = new ArrayList<>();
    ConversationListFragment mConversationListFragment = null;
    private boolean isDebug;
    private Context mContext;
    private Conversation.ConversationType[] mConversationsTypes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        isDebug = getSharedPreferences("config", MODE_PRIVATE).getBoolean("isDebug", false);
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

        mFragmentList.add(initConversationList());
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
    }

    private Fragment initConversationList() {
        if (mConversationListFragment == null) {
            ConversationListFragment listFragment = new ConversationListFragment();
            listFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
            Uri uri;
            if (isDebug) {
                uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                        .appendPath("conversationlist")
                        .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "true") //设置私聊会话是否聚合显示
                        .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//群组
                        .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                        .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                        .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//系统
                        .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "true")
                        .build();
                mConversationsTypes = new Conversation.ConversationType[] {Conversation.ConversationType.PRIVATE,
                        Conversation.ConversationType.GROUP,
                        Conversation.ConversationType.PUBLIC_SERVICE,
                        Conversation.ConversationType.APP_PUBLIC_SERVICE,
                        Conversation.ConversationType.SYSTEM,
                        Conversation.ConversationType.DISCUSSION
                };

            } else {
                uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                        .appendPath("conversationlist")
                        .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                        .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
                        .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                        .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                        .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//系统
                        .build();
                mConversationsTypes = new Conversation.ConversationType[] {Conversation.ConversationType.PRIVATE,
                        Conversation.ConversationType.GROUP,
                        Conversation.ConversationType.PUBLIC_SERVICE,
                        Conversation.ConversationType.APP_PUBLIC_SERVICE,
                        Conversation.ConversationType.SYSTEM
                };
            }
            listFragment.setUri(uri);
            mConversationListFragment = listFragment;
            return listFragment;
        } else {
            return mConversationListFragment;
        }
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
                break;
            case R.id.iv_top_add:
                break;
            case R.id.iv_top_search:
                break;
            case R.id.dpv_bottom_chats:
                break;
            default:
        }
    }

    @Override
    public void onDragOut() {

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
}
