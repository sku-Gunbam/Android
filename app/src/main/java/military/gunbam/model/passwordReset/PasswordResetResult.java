package military.gunbam.model.passwordReset;

import com.google.firebase.auth.FirebaseUser;

public class PasswordResetResult {
    private boolean success;
    private String error;

    public PasswordResetResult(boolean success) {
        this.success = success;
    }

    public PasswordResetResult(String error) {
        this.error = error;
    }

    public boolean getSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }
}
