package military.gunbam.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import java.util.ArrayList;

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
        originalBitmap = deepLearingModel.deepLearing(bitmap);

        width = originalBitmap.getWidth();
        height = originalBitmap.getHeight();
        newWidth = width / 2;
        newHeight = height / 2;

        topLeft = Bitmap.createBitmap(originalBitmap, 0, 0, newWidth, newHeight);
        topRight = Bitmap.createBitmap(originalBitmap, newWidth, 0, newWidth, newHeight);
        bottomLeft = Bitmap.createBitmap(originalBitmap, 0, newHeight, newWidth, newHeight);
        bottomRight = Bitmap.createBitmap(originalBitmap, newWidth, newHeight, newWidth, newHeight);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                Bitmap leftUp = deepLearingModel.deepLearing(topLeft);
                Bitmap rightUp = deepLearingModel.deepLearing(topRight);
                Bitmap leftDown = deepLearingModel.deepLearing(bottomLeft);
                Bitmap rightDown = deepLearingModel.deepLearing(bottomRight);


                Bitmap mergeBitmap = deepLearingModel.mergeBitmapImage(originalBitmap, leftUp, rightUp, leftDown, rightDown);
                resultBitmap.postValue(mergeBitmap);
            }
        }, 1); // 0.001초후
    }

}