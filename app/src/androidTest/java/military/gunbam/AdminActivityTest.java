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

import military.gunbam.view.activity.AdminActivity;

@RunWith(AndroidJUnit4.class)
public class AdminActivityTest {

    @Rule
    public ActivityScenarioRule<AdminActivity> activityScenarioRule = new ActivityScenarioRule<>(AdminActivity.class);

    @Test
    public void testClickAdminMenu1Button() {
        // Admin Menu 1 버튼 클릭 테스트
        ActivityScenario<AdminActivity> scenario = activityScenarioRule.getScenario();
        scenario.onActivity(activity -> {
            // Admin Menu 1 버튼을 찾고 클릭 동작 수행
            Espresso.onView(ViewMatchers.withId(R.id.admin_menu_1_button))
                    .perform(ViewActions.click());
        });
    }
}
