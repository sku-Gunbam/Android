package military.gunbam.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import military.gunbam.R;
import military.gunbam.model.login.LoginResult;
import military.gunbam.viewmodel.LoginViewModel;

public class LoginActivity extends BasicActivity {

    private LoginViewModel loginViewModel;

    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        findViewById(R.id.loginButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoSignButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoPasswordResetButton).setOnClickListener(onClickListener);

        loginViewModel.getLoginResultLiveData().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }

                if (loginResult.getError() != null) {
                    showToast(LoginActivity.this, loginResult.getError());
                }

                if (loginResult.getSuccess() != null) {
                    startNewActivityAndClearStack(MainActivity.class);
                }
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.loginButton:
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    loginViewModel.login(email, password);
                    break;

                case R.id.gotoSignButton:
                    startActivity(SignUpActivity.class);
                    break;

                case R.id.gotoPasswordResetButton:
                    startActivity(PasswordResetActivity.class);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}