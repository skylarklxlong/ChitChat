package online.himakeit.chitchat.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
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

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.ContactNotificationMessage;
import online.himakeit.chitchat.R;
import online.himakeit.chitchat.ui.broadcast.HomeWatcherReceiver;
import online.himakeit.chitchat.ui.adapter.ConversationListAdapterEx;
import online.himakeit.chitchat.ui.broadcast.BroadcastManager;
import online.himakeit.chitchat.ui.fragment.ContactFragment;
import online.himakeit.chitchat.ui.fragment.DiscoverFragment;
import online.himakeit.chitchat.ui.fragment.MineFragment;
import online.himakeit.chitchat.ui.widget.DragPointView;
import online.himakeit.chitchat.ui.widget.LoadDialog;
import online.himakeit.chitchat.ui.widget.MorePopWindow;
import online.himakeit.chitchat.utils.Toasts;

/**
 * @author：LiXueLong
 * @date:2018/1/8-15:39
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des：MainActivity
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener, DragPointView.OnDragListencer, ViewPager.OnPageChangeListener, IUnReadMessageObserver {

    ImageView mIvChats, mIvContact, mIvDiscover, mIvMe, mIvContactDot, mIvDiscoverDot,
            mIvMeDot, mIvSearch, mIvAdd;
    TextView mTvChats, mTvContact, mTvDiscover, mTvMe;
    public static ViewPager mViewPager;
    DragPointView mDpvUnReadNum;
    List<Fragment> mFragmentList = new ArrayList<>();
    ConversationListFragment mConversationListFragment = null;
    private boolean isDebug;
    private Context mContext;
    private Conversation.ConversationType[] mConversationsTypes = null;

    long firstClick = 0;
    long secondClick = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        isDebug = getSharedPreferences("config", MODE_PRIVATE).getBoolean("isDebug", false);
        initViews();
        changeSelectedTabState(0);
        initMainViewPager();
        registerHomeKeyReceiver(this);
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
        initData();
    }

    private void initData() {
        final Conversation.ConversationType[] conversationTypes = {
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE
        };

        RongIM.getInstance().addUnReadMessageCountChangedObserver(this, conversationTypes);
        getConversationPush();// 获取 push 的 id 和 target
        getPushMessage();
    }

    /**
     * 得到不落地 push 消息
     */
    private void getPushMessage() {
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {
            String path = intent.getData().getPath();
            if (path.contains("push_message")) {
                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                String cacheToken = sharedPreferences.getString("loginToken", "");
                if (TextUtils.isEmpty(cacheToken)) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    if (!RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
                        LoadDialog.show(mContext);
                        RongIM.connect(cacheToken, new RongIMClient.ConnectCallback() {
                            @Override
                            public void onTokenIncorrect() {
                                LoadDialog.dismiss(mContext);
                            }

                            @Override
                            public void onSuccess(String s) {
                                LoadDialog.dismiss(mContext);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode e) {
                                LoadDialog.dismiss(mContext);
                            }
                        });
                    }
                }
            }
        }
    }

    private void getConversationPush() {
        if (getIntent() != null && getIntent().hasExtra("PUSH_CONVERSATIONTYPE") && getIntent().hasExtra("PUSH_TARGETID")) {

            final String conversationType = getIntent().getStringExtra("PUSH_CONVERSATIONTYPE");
            final String targetId = getIntent().getStringExtra("PUSH_TARGETID");

            RongIM.getInstance().getConversation(Conversation.ConversationType.valueOf(conversationType), targetId, new RongIMClient.ResultCallback<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {

                    if (conversation != null) {

                        /**
                         * 好友消息的push
                         */
                        if (conversation.getLatestMessage() instanceof ContactNotificationMessage) {
                            startActivity(new Intent(MainActivity.this, NewFriendListActivity.class));
                        } else {
                            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation")
                                    .appendPath(conversationType).appendQueryParameter("targetId", targetId).build();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {

                }
            });
        }
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
                mConversationsTypes = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
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
                mConversationsTypes = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
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
        BroadcastManager.getInstance(mContext).addAction(MineFragment.SHOW_RED, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mIvMeDot.setVisibility(View.VISIBLE);
            }
        });
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
                        mConversationListFragment.focusUnreadItem();
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
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                break;
//            case R.id.dpv_bottom_chats:
//                break;
            default:
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("systemconversation", false)) {
            mViewPager.setCurrentItem(0, false);
        }
    }

    @Override
    public void onDragOut() {
        mDpvUnReadNum.setVisibility(View.GONE);
        Toasts.showShort("清楚成功");
        RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if (conversations != null && conversations.size() > 0) {
                    for (Conversation c : conversations) {
                        RongIM.getInstance().clearMessagesUnreadStatus(c.getConversationType(), c.getTargetId(), null);
                    }
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {

            }
        }, mConversationsTypes);
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
    public void onCountChanged(int count) {
        if (count == 0) {
            mDpvUnReadNum.setVisibility(View.GONE);
        } else if (count > 0 && count < 100) {
            mDpvUnReadNum.setVisibility(View.VISIBLE);
            mDpvUnReadNum.setText(String.valueOf(count));
        } else {
            mDpvUnReadNum.setVisibility(View.VISIBLE);
            mDpvUnReadNum.setText("...");
        }
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
        RongIM.getInstance().removeUnReadMessageCountChangedObserver(this);
        super.onDestroy();
    }

    private HomeWatcherReceiver mHomeKeyReceiver = null;

    private void registerHomeKeyReceiver(Context context) {
        if (mHomeKeyReceiver == null) {
            mHomeKeyReceiver = new HomeWatcherReceiver();
            final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            try {
                context.registerReceiver(mHomeKeyReceiver, homeFilter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
