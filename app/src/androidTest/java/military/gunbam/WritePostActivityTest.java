package military.gunbam;

import android.content.Intent;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import military.gunbam.view.activity.WritePostActivity;

@RunWith(AndroidJUnit4.class)
public class WritePostActivityTest {

    @Rule
    public ActivityScenarioRule<WritePostActivity> scenarioRule = new ActivityScenarioRule<>(WritePostActivity.class);

    @Test
    public void testTitleInput() {
        // 제목 입력 필드를 찾아 입력 작업 수행
        Espresso.onView(ViewMatchers.withId(R.id.titleEditText))
                .perform(ViewActions.typeText("Test Title"));
    }

    @Test
    public void testContentsInput() {
        // 내용 입력 필드를 찾아 입력 작업 수행
        Espresso.onView(ViewMatchers.withId(R.id.contentsEditText))
                .perform(ViewActions.typeText("Test Contents"));
    }

    @Test
    public void testAnonymousCheckBox() {
        // 익명 체크 상자를 찾아 클릭
        Espresso.onView(ViewMatchers.withId(R.id.writePostAnonymousCheckBox))
                .perform(ViewActions.click());
    }

    @Test
    public void testImageUpload() {
        // 이미지 업로드 버튼을 찾아 클릭
        Espresso.onView(ViewMatchers.withId(R.id.image))
                .perform(ViewActions.click());
    }

}
