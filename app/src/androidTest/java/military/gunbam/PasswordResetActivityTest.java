package military.gunbam;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import military.gunbam.view.activity.PasswordResetActivity;

@RunWith(AndroidJUnit4.class)
public class PasswordResetActivityTest {

    @Rule
    public ActivityScenarioRule<PasswordResetActivity> scenarioRule = new ActivityScenarioRule<>(PasswordResetActivity.class);

    @Test
    public void testPasswordReset() {
        // 이메일 입력 필드를 찾아 입력 작업 수행
        Espresso.onView(ViewMatchers.withId(R.id.emailEditText))
                .perform(ViewActions.typeText("test@example.com")); // 테스트용 이메일 입력

        // 비밀번호 재설정 버튼을 찾아 클릭
        Espresso.onView(ViewMatchers.withId(R.id.passwordSendButton))
                .perform(ViewActions.click());
    }

    @Test
    public void testNavigateToLogin() {
        // '로그인 화면으로 이동' 버튼을 찾아 클릭
        Espresso.onView(ViewMatchers.withId(R.id.gotoLoginButton))
                .perform(ViewActions.click());
    }


}
