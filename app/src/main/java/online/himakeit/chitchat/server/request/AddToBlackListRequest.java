package online.himakeit.chitchat.server.request;

/**
 * @author：LiXueLong
 * @date：2018/1/8
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des:
 */
public class AddToBlackListRequest {
    private String friendId;

    public AddToBlackListRequest(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }
}
