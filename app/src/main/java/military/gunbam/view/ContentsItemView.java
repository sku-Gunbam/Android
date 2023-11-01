package military.gunbam.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import military.gunbam.R;
import military.gunbam.model.Post.PostModel;
import military.gunbam.viewmodel.DeepLearningViewModel;
import military.gunbam.viewmodel.WritePostViewModel;

public class ContentsItemView extends LinearLayout {
    private ImageView imageView;
    private EditText editText;
    private DeepLearningViewModel deepLearningViewModel;
    private WritePostViewModel writePostViewModel;
    private Context context;
    private Bitmap deepLearningBitmap;
    private static String modelPath = "model2.tflite";
    public ContentsItemView(Context context) {
        super(context);
        initView();
    }
    public ContentsItemView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    public void setWritePostViewModel(WritePostViewModel writePostViewModel){
        this.writePostViewModel = writePostViewModel;
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
    public void saveBitmapToJpeg(Bitmap bitmap) {   // 선택한 이미지 내부 저장소에 저장
        File tempFile = new File("", "");    // 파일 경로와 이름 넣기
        try {
            tempFile.createNewFile();   // 자동으로 빈 파일을 생성하기
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            out.close();
        } catch (Exception e) {
        }
    }
    public void setImage(Bitmap bitmap){
        Glide.with(this)
                .load(bitmap)
                .override(1000)
                .into(imageView);
        //writePostViewModel.setBimap(bitmap);
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
