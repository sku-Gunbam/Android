package military.gunbam.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import military.gunbam.R;
import military.gunbam.model.signUp.SignUpResult;
import military.gunbam.viewmodel.SignUpViewModel;

public class SignUpActivity extends BasicActivity {

    private SignUpViewModel signUpViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        findViewById(R.id.signUpButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoLoginButton).setOnClickListener(onClickListener);

        signUpViewModel.getSignUpResultLiveData().observe(this, new Observer<SignUpResult>() {
            @Override
            public void onChanged(SignUpResult signUpResult) {
                if (signUpResult == null) {
                    return;
                }

                if (signUpResult.getError() != null) {
                    showToast(SignUpActivity.this, signUpResult.getError());
                }

                if (signUpResult.getSuccess() != null) {
                    startActivity(MainActivity.class);
                }
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.signUpButton:
                    String email = ((EditText)findViewById(R.id.emailEditText)).getText().toString();
                    String password = ((EditText)findViewById(R.id.passwordEditText)).getText().toString();
                    String passwordCheck = ((EditText)findViewById(R.id.passwordCheckEditText)).getText().toString();
                    signUpViewModel.signUp(email, password, passwordCheck);
                    break;
                case R.id.gotoLoginButton:
                    startNewActivityAndClearStack(LoginActivity.class);
                    break;
            }
        }
    };
}