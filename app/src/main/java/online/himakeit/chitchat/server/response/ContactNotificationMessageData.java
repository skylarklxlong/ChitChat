package online.himakeit.chitchat.server.response;

/**
 * @author：LiXueLong
 * @date：2018/1/10
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des:
 */
public class ContactNotificationMessageData {
    /**
     * sourceUserNickname : 赵哈哈
     * version : 1456282826213
     */

    private String sourceUserNickname;
    private long version;

    public void setSourceUserNickname(String sourceUserNickname) {
        this.sourceUserNickname = sourceUserNickname;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getSourceUserNickname() {
        return sourceUserNickname;
    }

    public long getVersion() {
        return version;
    }
}
