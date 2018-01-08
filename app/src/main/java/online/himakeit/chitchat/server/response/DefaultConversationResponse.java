package online.himakeit.chitchat.server.response;

import java.util.List;

/**
 * @author：LiXueLong
 * @date：2018/1/8
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des:
 */
public class DefaultConversationResponse {
    /**
     * code : 200
     * result : [{"type":"group","id":"E1IoyL5Pj","name":"用户体验群 Ⅰ","portraitUri":"","memberCount":3,"maxMemberCount":3000},{"type":"group","id":"iNj2YO4ib","name":"用户体验群 Ⅱ","portraitUri":"","memberCount":3,"maxMemberCount":3000},{"type":"group","id":"qGEj03bpP","name":"用户体验群 Ⅲ","portraitUri":"","memberCount":3,"maxMemberCount":3000},{"type":"chatroom","id":"OIBbeKlkx","name":"聊天室 I"},{"type":"chatroom","id":"675NdFjkx","name":"聊天室 II"},{"type":"chatroom","id":"MfgILRowx","name":"聊天室 III"},{"type":"chatroom","id":"lFVuoM7Jx","name":"聊天室 IV"}]
     */

    private int code;
    /**
     * type : group
     * id : E1IoyL5Pj
     * name : 用户体验群 Ⅰ
     * portraitUri :
     * memberCount : 3
     * maxMemberCount : 3000
     */

    private List<ResultEntity> result;

    public void setCode(int code) {
        this.code = code;
    }

    public void setResult(List<ResultEntity> result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public List<ResultEntity> getResult() {
        return result;
    }

    public static class ResultEntity {
        private String type;
        private String id;
        private String name;
        private String portraitUri;
        private int memberCount;
        private int maxMemberCount;

        public void setType(String type) {
            this.type = type;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPortraitUri(String portraitUri) {
            this.portraitUri = portraitUri;
        }

        public void setMemberCount(int memberCount) {
            this.memberCount = memberCount;
        }

        public void setMaxMemberCount(int maxMemberCount) {
            this.maxMemberCount = maxMemberCount;
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPortraitUri() {
            return portraitUri;
        }

        public int getMemberCount() {
            return memberCount;
        }

        public int getMaxMemberCount() {
            return maxMemberCount;
        }
    }
}