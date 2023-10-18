package military.gunbam;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import military.gunbam.view.activity.SignUpActivity;

@RunWith(AndroidJUnit4.class)
public class SignUpActivityTest {

    @Rule
    public ActivityScenarioRule<SignUpActivity> scenarioRule = new ActivityScenarioRule<>(SignUpActivity.class);

    @Test
    public void testSignUp() {
        // 미리 정의된 사용자 정보로 회원가입 시나리오를 시뮬레이트
        Espresso.onView(ViewMatchers.withId(R.id.emailEditText))
                .perform(ViewActions.typeText("your_test_email@example.com")); // 테스트용 이메일 입력

        Espresso.onView(ViewMatchers.withId(R.id.passwordEditText))
                .perform(ViewActions.typeText("your_test_password")); // 테스트용 비밀번호 입력

        Espresso.onView(ViewMatchers.withId(R.id.passwordCheckEditText))
                .perform(ViewActions.typeText("your_test_password")); // 비밀번호 확인 필드에도 동일한 비밀번호 입력

        Espresso.onView(ViewMatchers.withId(R.id.signUpButton))
                .perform(ViewActions.click());
    }

    @Test
    public void testNavigateToLogin() {
        // 로그인 화면으로 이동하는 버튼을 클릭하여 로그인 화면으로 이동
        Espresso.onView(ViewMatchers.withId(R.id.gotoLoginButton))
                .perform(ViewActions.click());
    }
}
