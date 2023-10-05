package military.gunbam.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

import military.gunbam.model.signUp.SignUpModel;
import military.gunbam.model.signUp.SignUpResult;

public class SignUpViewModel extends ViewModel {

    private SignUpModel signUpModel = new SignUpModel();
    private MutableLiveData<SignUpResult> signUpResultLiveData = new MutableLiveData<>();

    public void signUp(String email, String password, String passwordCheck) {
        signUpModel.signUp(email, password, passwordCheck, new SignUpModel.SignUpCallback() {
            @Override
            public void onSignUpSuccess(FirebaseUser user) {
                signUpResultLiveData.setValue(new SignUpResult(user));
            }

            @Override
            public void onSignUpFailure(String errorMessage) {
                signUpResultLiveData.setValue(new SignUpResult(errorMessage));
            }
        });
    }

    public MutableLiveData<SignUpResult> getSignUpResultLiveData() {
        return signUpResultLiveData;
    }
}
