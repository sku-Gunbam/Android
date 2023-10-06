package military.gunbam.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import military.gunbam.R;
import military.gunbam.model.passwordReset.PasswordResetResult;
import military.gunbam.viewmodel.PasswordResetViewModel;

public class PasswordResetActivity extends BasicActivity {

    private PasswordResetViewModel passwordResetViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        passwordResetViewModel = new ViewModelProvider(this).get(PasswordResetViewModel.class);

        findViewById(R.id.passwordSendButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoLoginButton).setOnClickListener(onClickListener);

        passwordResetViewModel.getPasswordResetLiveData().observe(this, new Observer<PasswordResetResult>() {
            @Override
            public void onChanged(PasswordResetResult passwordResetResult) {
                if (passwordResetResult == null) {
                    return;
                }

                if (passwordResetResult.getError() != null) {
                    showToast(PasswordResetActivity.this, passwordResetResult.getError());
                }

                if (passwordResetResult.getSuccess() != false) {
                    showToast(PasswordResetActivity.this, "이메일로 비밀번호 재설정 링크를 보냈습니다.");
                    startNewActivityAndClearStack(LoginActivity.class);
                }
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.passwordSendButton:
                    String email = ((EditText)findViewById(R.id.emailEditText)).getText().toString();
                    passwordResetViewModel.sendPasswordResetEmail(email);
                    break;
                case R.id.gotoLoginButton:
                    startNewActivityAndClearStack(LoginActivity.class);
                    break;
            }
        }
    };

    @Override
    // 뒤로가기 로그인 방지
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}