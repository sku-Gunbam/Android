package military.gunbam.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

import military.gunbam.model.passwordReset.PasswordResetModel;
import military.gunbam.model.passwordReset.PasswordResetResult;

public class PasswordResetViewModel extends ViewModel {

    private PasswordResetModel passwordResetModel = new PasswordResetModel();
    private MutableLiveData<PasswordResetResult> passwordResetLiveData = new MutableLiveData<>();

    public void sendPasswordResetEmail(String email) {
        passwordResetModel.passwordReset(email, new PasswordResetModel.PasswordResetCallback() {
            @Override
            public void onPasswordResetSuccess(boolean success) {
                passwordResetLiveData.setValue(new PasswordResetResult(success));
            }

            @Override
            public void onPasswordResetFailure(String errorMessage) {
                passwordResetLiveData.setValue(new PasswordResetResult(errorMessage));
            }
        });
    }

    public MutableLiveData<PasswordResetResult> getPasswordResetLiveData() {
        return passwordResetLiveData;
    }
}