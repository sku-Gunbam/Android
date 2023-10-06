package military.gunbam.model;

// 채팅 메시지에 대한 데이터를 저장하는 모델 클래스
import java.io.Serializable;

public class ChatData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String message;
    private String senderUid;
    private long timestamp;
    private String userName;
    private String userEmail;
    public ChatData() {

    }
    public ChatData(String message, String senderUid, long timestamp, String userName, String userEmail) {
        this.message = message;
        this.senderUid = senderUid;
        this.timestamp = timestamp;
        this.userName = userName;
        this.userEmail = userEmail;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getSenderUid() {
        return senderUid;
    }
    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getUserName() { return userName; }
    public String getUserEmail() {return userEmail; }
    //public String getSending_time() { return String.valueOf(timestamp); }
}