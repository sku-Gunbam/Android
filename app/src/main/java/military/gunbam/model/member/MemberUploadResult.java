package military.gunbam.model.member;

import com.google.firebase.auth.FirebaseUser;

public class MemberUploadResult {

    private MemberInfo success;
    private String error;

    public MemberUploadResult(MemberInfo success) {
        this.success = success;
    }

    public MemberUploadResult(String error) {
        this.error = error;
    }

    public MemberInfo getSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }
}
