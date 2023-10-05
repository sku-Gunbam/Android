package military.gunbam.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import military.gunbam.model.DeepLearingModel;

public class DeepLearningViewModel extends ViewModel {
    private DeepLearingModel deepLearingModel;
    private MutableLiveData<Bitmap> resultBitmap = new MutableLiveData<>();
    private Bitmap originalBitmap, topLeft, topRight, bottomLeft, bottomRight;
    private int width, height, newWidth, newHeight;

    public DeepLearningViewModel(Context context, String modelPath) {
        deepLearingModel = new DeepLearingModel(context, modelPath);
    }

    public LiveData<Bitmap> getResultBitmap() {
        return resultBitmap;
    }

    public void run(Bitmap bitmap) {
        // Step 1: Apply deep learning to the original image
        originalBitmap = deepLearingModel.deepLearing(bitmap);

        // Step 2: Calculate dimensions
        width = originalBitmap.getWidth();
        height = originalBitmap.getHeight();
        newWidth = width / 2;
        newHeight = height / 2;

        // Step 3: Create four sub-images by cropping the original image
        topLeft = Bitmap.createBitmap(originalBitmap, 0, 0, newWidth, newHeight);
        topRight = Bitmap.createBitmap(originalBitmap, newWidth, 0, newWidth, newHeight);
        bottomLeft = Bitmap.createBitmap(originalBitmap, 0, newHeight, newWidth, newHeight);
        bottomRight = Bitmap.createBitmap(originalBitmap, newWidth, newHeight, newWidth, newHeight);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                // Step 4: Apply deep learning to the cropped sub-images
                Bitmap leftUp = deepLearingModel.deepLearing(topLeft);
                Bitmap rightUp = deepLearingModel.deepLearing(topRight);
                Bitmap leftDown = deepLearingModel.deepLearing(bottomLeft);
                Bitmap rightDown = deepLearingModel.deepLearing(bottomRight);

                // Step 5: Merge the processed sub-images
                Bitmap mergeBitmap = deepLearingModel.mergeBitmapImage(originalBitmap, leftUp, rightUp, leftDown, rightDown);

                // Step 6: Post the merged bitmap to your result
                resultBitmap.postValue(mergeBitmap);
            }
        }, 1); // 0.001초후
    }

}