package military.gunbam.model;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.widget.ImageView;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.components.containers.Category;
import com.google.mediapipe.tasks.components.containers.Detection;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.core.OutputHandler;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector;
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class DeepLearingModel {
    private String[] labels = {"CAR_NUM", "RANK", "MARK", "KOREA", "NAME"};
    private static final int WIDTH = 320;
    private static final int HEIGHT = 320;
    private static final int CHANNELS = 3;

    private ObjectDetector objectDetector;
    private ObjectDetector.ObjectDetectorOptions options;
    private ObjectDetectorResult detectionResult;
    private ImageView deepLearningImageView;
    private Bitmap processedBitmap, originalBitmap;

    public DeepLearingModel(Context context, String modelPath) {
        options = ObjectDetector.ObjectDetectorOptions.builder()
                .setBaseOptions(BaseOptions.builder().setModelAssetPath(modelPath).build())
                .setRunningMode(RunningMode.IMAGE)
                .setMaxResults(5)
                .build();
        objectDetector = ObjectDetector.createFromOptions(context, options);
    }


    public Bitmap deepLearing(Bitmap bitmap) {
        originalBitmap = bitmap;
        Log.d("이미지뷰",originalBitmap.getWidth() + " " + originalBitmap.getHeight());

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, WIDTH, HEIGHT, true);

        int[] intValues = new int[WIDTH * HEIGHT];
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());

        float[] floatValues = new float[WIDTH * HEIGHT * CHANNELS];
        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            floatValues[i * 3] = ((val >> 16) & 0xFF) ;
            floatValues[i * 3 + 1] = ((val >> 8) & 0xFF) ;
            floatValues[i * 3 + 2] = (val & 0xFF);
        }

        byte[][][][] inputArray = new byte[1][HEIGHT][WIDTH][CHANNELS];
        for (int i = 0; i < HEIGHT; ++i) {
            for (int j = 0; j < WIDTH; ++j) {
                for (int c = 0; c < CHANNELS; ++c) {
                    inputArray[0][i][j][c] = (byte)floatValues[(i * WIDTH + j) * CHANNELS + c];
                }
            }
        }

        MPImage mpImage = new BitmapImageBuilder(originalBitmap).build();
        ObjectDetectorResult detectionResult = objectDetector.detect(mpImage);

        processedBitmap = originalBitmap;

        OutputHandler.PureResultListener<ObjectDetectorResult> listener = new OutputHandler.PureResultListener<ObjectDetectorResult>() {
            @Override
            public void run(ObjectDetectorResult result) {

                long timestampMs = result.timestampMs();
                System.out.println("결과값 출력");
                System.out.println("timestampMs: " + Long.toString(result.timestampMs()));
                List<Detection> detections = result.detections();
                for(Detection detection : detections){
                    RectF bbox = detection.boundingBox();

                    //System.out.println("bbox.bottom: " +bbox.bottom);
                    //System.out.println("bbox.left: " +bbox.left);
                    //System.out.println("bbox.right: " +bbox.right);
                    //System.out.println("bbox.top: " +bbox.top);

                    List<Category> category = detection.categories();

                    for(Category cat : detection.categories() ){
                        String catName = cat.categoryName();
                        String displayName = cat.displayName();
                        float catScore = cat.score();
                        int index = cat.index();
                        //System.out.println("catName: " + catName);
                        //System.out.println("displayName: " + displayName);
                        //System.out.println("catScore: " + catScore);
                        //System.out.println("index: " + Integer.toString(index));

                        if (catScore >= 0.6) {
                            // 이미지에 바운딩 박스 및 정보를 그리고 수정된 비트맵을 얻습니다.
                            processedBitmap = mosaicBoundingBoxOnBitmap(originalBitmap, bbox, catName, catScore);
                            System.out.println("catName: " + catName);
                            System.out.println("catScore: " + catScore);
                        }
                    }
                }
            }
        };

        listener.run(detectionResult);
        Log.d("이미지뷰",processedBitmap.getWidth() + "*" + processedBitmap.getHeight());
        return processedBitmap ;
    }

    public Bitmap mosaicBoundingBoxOnBitmap(Bitmap bitmap, RectF bbox, String catName, float catScore) {
        // Create a mutable copy of the input bitmap to work on
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        // Create a Canvas object to draw on the mutable bitmap
        Canvas canvas = new Canvas(mutableBitmap);

        // Define the paint for drawing the bounding box
        Paint paint = new Paint();
        paint.setColor(Color.RED); // You can choose any color you like
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3); // Adjust the width of the bounding box lines

        // Draw the bounding box on the Canvas
        // canvas.drawRect(bbox, paint);

        // Get the bounding box coordinates
        int left = Math.max(0, (int) bbox.left);
        int top = Math.max(0, (int) bbox.top);
        int right = Math.min(bitmap.getWidth(), (int) bbox.right);
        int bottom = Math.min(bitmap.getHeight(), (int) bbox.bottom);

        // Define the size of the mosaic pixels (adjust as needed)
        int mosaicSize = 100;

        // Loop through the bounding box region and apply mosaic effect
        for (int y = top; y < bottom; y += mosaicSize) {
            for (int x = left; x < right; x += mosaicSize) {
                int color = mutableBitmap.getPixel(x, y);
                for (int mosaicY = y; mosaicY < y + mosaicSize && mosaicY < bottom; mosaicY++) {
                    for (int mosaicX = x; mosaicX < x + mosaicSize && mosaicX < right; mosaicX++) {
                        mutableBitmap.setPixel(mosaicX, mosaicY, color);
                    }
                }
            }
        }

        // Define the paint for drawing text (e.g., category name and score)
        Paint textPaint = new Paint();
        textPaint.setColor(Color.RED); // You can choose a text color
        textPaint.setTextSize(100); // Adjust the text size

        // Calculate the position to draw the text
        float textX = bbox.left;
        float textY = bbox.bottom + 20; // Adjust the vertical position

        // Draw the category name and score
        // canvas.drawText("Category: " + catName, textX, textY, textPaint);
        // canvas.drawText("Score: " + catScore, textX, textY + 100, textPaint); // Adjust vertical spacing

        return mutableBitmap; // Return the bitmap with bounding box and mosaic effect
    }

    public MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
    public Bitmap mergeBitmapImage(Bitmap originalBitmap, Bitmap leftUp, Bitmap rightUp, Bitmap leftDown, Bitmap rightDown) {
        // 가로로 합쳐진 이미지의 폭과 높이 계산
        int resultWidth = originalBitmap.getWidth();
        int resultHeight = originalBitmap.getHeight();

        // 새로운 비트맵 생성
        Bitmap mergeResult = Bitmap.createBitmap(resultWidth, resultHeight, Bitmap.Config.ARGB_8888);

        // 비트맵을 캔버스에 그림
        Canvas canvas = new Canvas(mergeResult);
        canvas.drawBitmap(leftUp, 0, 0, null);
        canvas.drawBitmap(rightUp, leftUp.getWidth(), 0, null);
        canvas.drawBitmap(leftDown, 0, leftUp.getHeight(), null);
        canvas.drawBitmap(rightDown, leftUp.getWidth(), leftUp.getHeight(), null);

        //Bitmap resizedBitmap = Bitmap.createScaledBitmap(mergeResult, WIDTH*2, HEIGHT*2, true);

        Log.d("이미지뷰",mergeResult.getWidth() + "*" + mergeResult.getHeight());

        return mergeResult;
    }
}
