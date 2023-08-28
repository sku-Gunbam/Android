package military.gunbam.model;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginModel {

    private FirebaseAuth mAuth;

    public LoginModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public interface LoginCallback {
        void onLoginSuccess(FirebaseUser user);
        void onLoginFailure(String errorMessage);
    }

    public void login(String email, String password, final LoginCallback callback) {
        // 실제 로그인 비즈니스 로직 수행
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            String errorMessage = "이메일과 비밀번호를 입력해주세요.";
            callback.onLoginFailure(errorMessage);
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            callback.onLoginSuccess(user);
                        } else {
                            String errorMessage = "로그인 도중 오류가 발생했습니다. 다시 시도해주세요.";
                            if (task.getException() != null) {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    errorMessage = "가입되지 않은 이메일 주소입니다. 회원가입해주세요.";
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    errorMessage = "이메일 주소 또는 비밀번호가 잘못되었습니다.";
                                } catch (FirebaseNetworkException e) {
                                    errorMessage = "인터넷 연결 상태를 확인해주세요.";
                                } catch (Exception e) {
                                    Log.e("LoginModel", "로그인 실패", e);
                                }
                            }
                            callback.onLoginFailure(errorMessage);
                        }
                    }
                });
    }
}