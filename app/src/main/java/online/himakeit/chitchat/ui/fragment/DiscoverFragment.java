package online.himakeit.chitchat.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import online.himakeit.chitchat.R;

/**
 * @author：LiXueLong
 * @date：2018/1/11
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des:
 */
public class DiscoverFragment extends Fragment implements View.OnClickListener {

    LinearLayout mLlChatRoom1, mLlChatRoom2, mLlChatRoom3, mLlChatRoom4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mLlChatRoom1 = view.findViewById(R.id.ll_discover_chatroom1);
        mLlChatRoom2 = view.findViewById(R.id.ll_discover_chatroom2);
        mLlChatRoom3 = view.findViewById(R.id.ll_discover_chatroom3);
        mLlChatRoom4 = view.findViewById(R.id.ll_discover_chatroom4);

        mLlChatRoom1.setOnClickListener(this);
        mLlChatRoom2.setOnClickListener(this);
        mLlChatRoom3.setOnClickListener(this);
        mLlChatRoom4.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

    }
}
