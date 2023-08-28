package military.gunbam.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import military.gunbam.R;

import static military.gunbam.utils.Util.showToast;

public class PasswordResetActivity extends BasicActivity {
    private static final String TAG = "PasswordResetActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.passwordSendButton).setOnClickListener(onClickListener);
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
                case R.id.passwordSendButton:
                    send();
                    break;
                case R.id.gotoLoginButton:
                    startNewActivityAndClearStack(LoginActivity.class);
                    break;
            }
        }
    };

    private void send() {
        // 이메일 주소의 유효성을 확인하는 정규표현식
        // String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        // 비밀번호의 유효성을 확인하는 정규표현식
        String email = ((EditText)findViewById(R.id.emailEditText)).getText().toString();


        // 공백 체크
        if (email.length() > 0){
            // 비밀번호 재설정
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showToast(PasswordResetActivity.this, "이메일로 비밀번호 재설정 링크를 보냈습니다.");
                            } else {
                                showToast(PasswordResetActivity.this, "존재하지 않거나 유효하지 않은 이메일입니다.");
                            }
                        }
                    });
        } else {
            showToast(PasswordResetActivity.this, "이메일을 입력해주세요.");
        }
    }
}