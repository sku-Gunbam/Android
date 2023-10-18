package military.gunbam;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import military.gunbam.view.activity.GalleryActivity;

// TODO -> "java.lang.AssertionError는 당신의 테스트 코드에서 assertTrue 또는 assertFalse 어설션 중 하나에서 실패" 해결해야함
@RunWith(AndroidJUnit4.class)
public class GalleryActivityTest {

    @Rule
    public ActivityScenarioRule<GalleryActivity> activityScenarioRule = new ActivityScenarioRule<>(GalleryActivity.class);

    @Test
    public void testGetImagesPath() {
        // GalleryActivity가 자동으로 시작하고, getImagesPath 메서드 호출
        activityScenarioRule.getScenario().onActivity(activity -> {
            // getImagesPath 메서드를 호출하고 반환된 이미지 경로 목록을 확인
            ArrayList<String> imagesPath = activity.getImagesPath(activity);

            // 검증: imagesPath는 null이 아니어야 하며, 비어서는 안 됨
            assertNotNull(imagesPath);
            assertFalse(imagesPath.isEmpty());
        });
    }
}
