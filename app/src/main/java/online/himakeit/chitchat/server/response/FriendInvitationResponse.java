package online.himakeit.chitchat.server.response;

/**
 * @author：LiXueLong
 * @date：2018/1/8
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des:
 */
public class FriendInvitationResponse {
    /**
     * code : 200
     * result : {"action":"Sent"}
     * message : Request sent.
     */

    private int code;
    /**
     * action : Sent
     */

    private ResultEntity result;
    private String message;

    public void setCode(int code) {
        this.code = code;
    }

    public void setResult(ResultEntity result) {
        this.result = result;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public ResultEntity getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public static class ResultEntity {
        private String action;

        public void setAction(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }
    }
}
