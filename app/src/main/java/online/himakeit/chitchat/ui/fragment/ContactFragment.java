package online.himakeit.chitchat.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import online.himakeit.chitchat.R;
import online.himakeit.chitchat.ui.widget.SideBar;

/**
 * @author：LiXueLong
 * @date：2018/1/11
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des:
 */
public class ContactFragment extends Fragment {

    EditText mEdSearch;
    ListView mListView;
    TextView mTvDialog, mTvTip;
    SideBar mSideBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mEdSearch = view.findViewById(R.id.et_contact_search);
        mListView = view.findViewById(R.id.ls_contact);
        mTvDialog = view.findViewById(R.id.tv_contact_dialog);
        mTvTip = view.findViewById(R.id.tv_contact_tip);
        mSideBar = view.findViewById(R.id.sidebar_contact);
    }
}
