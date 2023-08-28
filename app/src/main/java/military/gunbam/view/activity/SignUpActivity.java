package military.gunbam.view.activity;

import static military.gunbam.utils.Util.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import military.gunbam.R;

public class SignUpActivity extends BasicActivity {
    private static final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.signUpButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoLoginButton).setOnClickListener(onClickListener);
    }

    @Override
    // 뒤로가기 로그인 방지
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.signUpButton:
                    signUp();
                    break;
                case R.id.gotoLoginButton:
                    startNewActivityAndClearStack(LoginActivity.class);
                    break;
            }
        }
    };

    private void signUp() {
        // 이메일 주소의 유효성을 확인하는 정규표현식
        // String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        // 비밀번호의 유효성을 확인하는 정규표현식
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z]).{6,}$";

        String email = ((EditText)findViewById(R.id.emailEditText)).getText().toString();
        String password = ((EditText)findViewById(R.id.passwordEditText)).getText().toString();
        String passwordCheck = ((EditText)findViewById(R.id.passwordCheckEditText)).getText().toString();

        // 공백 체크
        if (email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0){
            // 비밀번호 유효성 확인
            if (password.equals(passwordCheck)){
                if (password.matches(passwordPattern)) {
                    // 회원가입 실행
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, (task) -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    showToast(SignUpActivity.this, "회원가입을 축하드립니다.");
                                    startNewActivityAndClearStack(MainActivity.class);
                                } else {
                                    if (task.getException() != null) {
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthWeakPasswordException e) {
                                            showToast(SignUpActivity.this, "비밀번호는 6자 이상이어야 합니다.");
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            showToast(SignUpActivity.this, "유효한 이메일 주소를 입력해주세요.");
                                        } catch (FirebaseAuthUserCollisionException e) {
                                            showToast(SignUpActivity.this, "이미 등록된 이메일 주소입니다. 다른 이메일을 사용해주세요.");
                                        } catch (FirebaseAuthException e) {
                                            showToast(SignUpActivity.this, "회원가입 도중 오류가 발생했습니다. 다시 시도해주세요.");
                                            Log.e(TAG, "회원가입 실패", e);
                                        } catch (Exception e) {
                                            showToast(SignUpActivity.this, "알 수 없는 오류가 발생했습니다. 다시 시도해주세요.");
                                            Log.e(TAG, "알 수 없는 오류", e);
                                        }
                                    }
                                }
                            });
                } else {
                    showToast(SignUpActivity.this, "비밀번호는 최소 6자 이상이고, 영어와 숫자를 포함해야 합니다.");
                }
            } else {
                showToast(SignUpActivity.this, "비밀번호가 일치하지않습니다.");
            }
        } else {
            showToast(SignUpActivity.this, "이메일 또는 비밀번호를 입력해주세요.");
        }
    }
}