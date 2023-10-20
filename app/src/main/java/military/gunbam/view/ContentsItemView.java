package military.gunbam.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import military.gunbam.R;
import military.gunbam.viewmodel.DeepLearningViewModel;
import military.gunbam.viewmodel.DeepLearningViewModelFactory;

public class ContentsItemView extends LinearLayout {
    private ImageView imageView;
    private EditText editText;
    private Bitmap deepLearningResultBitmap;
    private DeepLearningViewModel deepLearningViewModel;
    private static String modelPath = "model2.tflite";
    public ContentsItemView(Context context) {
        super(context);
        initView();
    }

    public ContentsItemView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    public void setDeepLearningViewModel(DeepLearningViewModel deepLearningViewModel) {
        this.deepLearningViewModel = deepLearningViewModel;
    }

    private void initView(){
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addView(layoutInflater.inflate(R.layout.view_contents_image, this, false));
        addView(layoutInflater.inflate(R.layout.view_contents_edit_text, this, false));

        imageView = findViewById(R.id.contentsImageView);
        editText = findViewById(R.id.contentsEditText);
    }

    public void setImage(String path){
        Glide.with(this)
                .asBitmap()
                .load(path)
                .override(1000)
                .into(new SimpleTarget<Bitmap>() {
                    @Override

                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        deepLearningViewModel.run(resource);
                        deepLearningViewModel.getResultBitmap().observe((LifecycleOwner) getContext(), new Observer<Bitmap>() {
                            @Override
                            public void onChanged(Bitmap bitmap) {
                                imageView.setImageBitmap(bitmap);
                                deepLearningResultBitmap = bitmap;
                            }
                        });
                }
                });

    }
    public Bitmap getImage(){
        return deepLearningResultBitmap;
    }
    public void setText(String text){
        editText.setText(text);
    }

    public void setOnClickListener(OnClickListener onClickListener){
        imageView.setOnClickListener(onClickListener);
    }

    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener){
        editText.setOnFocusChangeListener(onFocusChangeListener);
    }
}
