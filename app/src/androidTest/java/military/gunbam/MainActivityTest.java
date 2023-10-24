package military.gunbam;

import androidx.test.core.app.ActivityScenario;
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
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> scenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testBottomNavigationViewClick() {
        // LoginActivity가 자동으로 시작
        // 여기에서 로그인 프로세스를 자동화하고 로그인 후 MainActivity로 이동
        Espresso.onView(ViewMatchers.withId(R.id.emailEditText))
                .perform(ViewActions.typeText("your_test_email@example.com"));

        Espresso.onView(ViewMatchers.withId(R.id.passwordEditText))
                .perform(ViewActions.typeText("your_test_password"));

        Espresso.onView(ViewMatchers.withId(R.id.loginButton))
                .perform(ViewActions.click());

    }
}
