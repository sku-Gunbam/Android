package military.gunbam.model.passwordReset;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class PasswordResetModel {

    private FirebaseAuth mAuth;

    public PasswordResetModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public interface PasswordResetCallback {
        void onPasswordResetSuccess(boolean success);
        void onPasswordResetFailure(String errorMessage);
    }

    public void passwordReset(String email, final PasswordResetModel.PasswordResetCallback callback) {
        if (TextUtils.isEmpty(email)) {
            String errorMessage = "이메일을 입력해주세요.";
            callback.onPasswordResetFailure(errorMessage);
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            boolean success = true;
                            callback.onPasswordResetSuccess(success);
                        } else {
                            String errorMessage = "비밀번호 재설정 도중 오류가 발생했습니다. 다시 시도해주세요.";

                            if (task.getException() != null) {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    errorMessage = "가입되지 않은 이메일 주소입니다. 회원가입해주세요.";
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    errorMessage = "이메일 주소가 잘못되었습니다.";
                                } catch (FirebaseNetworkException e) {
                                    errorMessage = "인터넷 연결 상태를 확인해주세요.";
                                } catch (Exception e) {
                                    Log.e("PasswordResetModel", "비밀번호 재설정 실패", e);
                                }
                            }
                            callback.onPasswordResetFailure(errorMessage);
                        }
                    }
                });
    }
}