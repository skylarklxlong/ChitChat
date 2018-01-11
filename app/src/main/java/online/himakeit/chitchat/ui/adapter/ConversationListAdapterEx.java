package online.himakeit.chitchat.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imlib.model.Conversation;

/**
 * @author：LiXueLong
 * @date：2018/1/11
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des:
 */
public class ConversationListAdapterEx extends ConversationListAdapter {
    public ConversationListAdapterEx(Context context) {
        super(context);
    }

    @Override
    protected View newView(Context context, int position, ViewGroup group) {
        return super.newView(context, position, group);
    }

    @Override
    protected void bindView(View v, int position, UIConversation data) {
        if (data != null) {
            if (Conversation.ConversationType.DISCUSSION.equals(data.getConversationType())) {
                data.setUnreadType(UIConversation.UnreadRemindType.REMIND_ONLY);
            }
        }
        super.bindView(v, position, data);
    }
}
