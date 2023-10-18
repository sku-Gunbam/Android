package military.gunbam;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import military.gunbam.view.activity.TestDeepLearningActivity;

@RunWith(AndroidJUnit4.class)
public class DeepLearningActivityTest {

    @Rule
    public ActivityScenarioRule<TestDeepLearningActivity> activityRule =
            new ActivityScenarioRule<>(TestDeepLearningActivity.class);

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void testDeepLearningWithSelectedImage() {
        // 스캐나리오 시작
        ActivityScenario<TestDeepLearningActivity> scenario = ActivityScenario.launch(TestDeepLearningActivity.class);

        // 기다리는 동안 Sleep, UI 작업이 완료될 때까지 기다릴 수 있도록 한다.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 갤러리에서 이미지 선택 시뮬레이션
        Bitmap testBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.test_image); // 테스트 이미지 리소스
        Uri testImageUri = createTestImageUri(testBitmap); // 테스트 이미지를 저장하고 Uri 생성

        // 액티비티 결과 확인
        scenario.onActivity(activity -> {
            Intent data = new Intent();
            data.setData(testImageUri);
            activity.onActivityResult(activity.PICK_IMAGE_REQUEST, activity.RESULT_OK, data);
        });

        // 기다리는 동안 Sleep, Deep Learning 작업이 완료될 때까지 기다릴 수 있도록 한다.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 테스트 결과 확인
        Espresso.onView(ViewMatchers.withId(R.id.img_view_result))
                .check((view, noViewFoundException) -> {
                    ImageView imageView = (ImageView) view;
                    // 여기에서 imageView의 상태를 확인하여 테스트를 수행하거나 통과 여부를 결정할 수 있습니다.
                });
    }

    private Uri createTestImageUri(Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName + ".jpg");

        try {
            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(imageFile);
    }
}
