package military.gunbam.model.signUp;

import com.google.firebase.auth.FirebaseUser;

public class SignUpResult {
    private FirebaseUser success;
    private String error;

    public SignUpResult(FirebaseUser success)  {
        this.success = success;
    }

    public SignUpResult(String error) {
        this.error = error;
    }

    public FirebaseUser getSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }
}
