package military.gunbam.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.core.OutputHandler;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector;
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import military.gunbam.R;
import military.gunbam.viewmodel.DeepLearningViewModel;
import military.gunbam.viewmodel.DeepLearningViewModelFactory;

public class TestDeepLearningActivity extends BasicActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private File file;
    private String fileName = null;
    private ImageView imgViewResult;
    private ObjectDetector objectDetector;
    private ObjectDetector.ObjectDetectorOptions options;
    private ObjectDetectorResult detectionResult;
    private DeepLearningViewModel deepLearningViewModel;
    private Button selectImageButton;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testdeeplearning);

        Context context = getApplication();
        String modelPath = "model2.tflite";
        deepLearningViewModel = new ViewModelProvider(this, new DeepLearningViewModelFactory(context, modelPath)).get(DeepLearningViewModel.class);
        deepLearningViewModel = new DeepLearningViewModel(this, modelPath);
        imgViewResult = findViewById(R.id.img_view_result);

        options = ObjectDetector.ObjectDetectorOptions.builder()
                .setBaseOptions(BaseOptions.builder().setModelAssetPath(modelPath).build())
                .setRunningMode(RunningMode.IMAGE)
                .setMaxResults(5)
                .build();
        objectDetector = ObjectDetector.createFromOptions(getApplicationContext(), options);

        deepLearningViewModel.getResultBitmap().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable Bitmap bitmap) {
                if (bitmap != null) {
                    imgViewResult.setImageBitmap(bitmap);
                }
            }
        });

        selectImageButton = findViewById(R.id.deep_button);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 갤러리에서 이미지 선택
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                deepLearningViewModel.run(selectedImage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri photoUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(photoUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String getTime = sdf.format(date);

                fileName = getNickname + " posted:" + getTime + ".jpg";
                file = new File(getCacheDir(), fileName);
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();

                Uri uri = Uri.fromFile(file);
                imgViewResult.setVisibility(View.VISIBLE);
                imgViewResult.setImageURI(uri);

                Bitmap originalBitmap = bitmap; // Your original bitmap


                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int newWidth = width / 2;
                int newHeight = height / 2;

                Bitmap topLeft = Bitmap.createBitmap(originalBitmap, 0, 0, newWidth, newHeight);
                Bitmap topRight = Bitmap.createBitmap(originalBitmap, newWidth, 0, newWidth, newHeight);
                Bitmap bottomLeft = Bitmap.createBitmap(originalBitmap, 0, newHeight, newWidth, newHeight);
                Bitmap bottomRight = Bitmap.createBitmap(originalBitmap, newWidth, newHeight, newWidth, newHeight);



                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()  {
                    public void run() {
                        //deepLearningViewModel.run(bitmap);
                        //Bitmap leftUp = deepLearningViewModel.run(topLeft);
                        //Bitmap rightUp = deepLearningViewModel.run(topRight);
                        //Bitmap leftDown = deepLearningViewModel.run(bottomLeft);
                        //Bitmap rightDown = deepLearningViewModel.run(bottomRight);

                        Bitmap mergeBitmap = mergeBitmapImage(leftUp, rightUp, leftDown, rightDown);
                        imgViewResult.setImageBitmap(mergeBitmap);
                    }
                }, 1); // 0.001초후
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/
}
