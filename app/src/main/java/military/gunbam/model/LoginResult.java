package military.gunbam.model;

import com.google.firebase.auth.FirebaseUser;

public class LoginResult {

    private FirebaseUser success;
    private String error;

    public LoginResult(FirebaseUser success) {
        this.success = success;
    }

    public LoginResult(String error) {
        this.error = error;
    }

    public FirebaseUser getSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }
}