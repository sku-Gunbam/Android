package military.gunbam.model.signUp;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpModel {

    private FirebaseAuth mAuth;

    public SignUpModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public interface SignUpCallback {
        void onSignUpSuccess(FirebaseUser user);
        void onSignUpFailure(String errorMessage);
    }

    public void signUp(String email, String password, String passwordCheck, final SignUpModel.SignUpCallback callback) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordCheck)) {
            String errorMessage = "이메일과 비밀번호를 입력해주세요.";
            callback.onSignUpFailure(errorMessage);
            return;
        } else if (!passwordsMatch(password, passwordCheck)) {
            String errorMessage = "비밀번호가 일치하지 않습니다.";
            callback.onSignUpFailure(errorMessage);
            return;
            }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            callback.onSignUpSuccess(user);
                        } else {
                            String errorMessage = "회원가입 도중 오류가 발생했습니다. 다시 시도해주세요.";
                            if (task.getException() != null) {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    errorMessage = "비밀번호는 6자 이상이어야 합니다.";
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    errorMessage = "유효한 이메일 주소를 입력해주세요.";
                                } catch (FirebaseAuthUserCollisionException e) {
                                    errorMessage = "이미 등록된 이메일 주소입니다. 다른 이메일을 사용해주세요.";
                                } catch (FirebaseNetworkException e) {
                                    errorMessage = "인터넷 연결 상태를 확인해주세요.";
                                } catch (Exception e) {
                                    Log.e("SignUpModel", "회원가입 실패", e);
                                }
                            }
                            callback.onSignUpFailure(errorMessage);
                        }
                    }
                });
    }

    private boolean passwordsMatch(String password, String passwordCheck) {
        return password.equals(passwordCheck);
    }
}