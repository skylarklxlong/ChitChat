package online.himakeit.chitchat.ui.fragment;

import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.model.Message;

/**
 * @author：LiXueLong
 * @date：2018/1/11
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des: 会话 Fragment 继承自ConversationFragment
 * onResendItemClick: 重发按钮点击事件. 如果返回 false,走默认流程,如果返回 true,走自定义流程
 * onReadReceiptStateClick: 已读回执详情的点击事件.
 * 如果不需要重写 onResendItemClick 和 onReadReceiptStateClick ,可以不必定义此类,直接集成 ConversationFragment 就可以了
 */
public class ConversationFragmentEx extends ConversationFragment {
    @Override
    public boolean onResendItemClick(Message message) {
        return false;
    }

    @Override
    public void onReadReceiptStateClick(Message message) {
        super.onReadReceiptStateClick(message);
    }
}
