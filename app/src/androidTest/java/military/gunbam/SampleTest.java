package military.gunbam;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import military.gunbam.view.activity.LoginActivity;

@RunWith(AndroidJUnit4.class)
public class SampleTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> scenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLogin() {
        // 미리 정의된 사용자 이름과 암호를 사용하여 로그인 시나리오를 시뮬레이트합니다.
        Espresso.onView(ViewMatchers.withId(R.id.emailEditText))
                .perform(ViewActions.typeText("your_test_email@example.com")); // 여기에 테스트용 이메일 입력

        Espresso.onView(ViewMatchers.withId(R.id.passwordEditText))
                .perform(ViewActions.typeText("your_test_password")); // 여기에 테스트용 비밀번호 입력

        Espresso.onView(ViewMatchers.withId(R.id.loginButton))
                .perform(ViewActions.click());
    }

    @Test
    public void testNavigateToSignUp() {
        // 회원 가입 버튼을 클릭하여 회원 가입 화면으로 이동
        Espresso.onView(ViewMatchers.withId(R.id.gotoSignButton))
                .perform(ViewActions.click());
    }

    @Test
    public void testNavigateToPasswordReset() {
        // 비밀번호 재설정 버튼을 클릭하여 비밀번호 재설정 화면으로 이동
        Espresso.onView(ViewMatchers.withId(R.id.gotoPasswordResetButton))
                .perform(ViewActions.click());
    }
}
