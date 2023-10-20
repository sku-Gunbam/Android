package military.gunbam.view.activity;

import static military.gunbam.utils.Util.GALLERY_IMAGE;
import static military.gunbam.utils.Util.INTENT_MEDIA;
import static military.gunbam.utils.Util.INTENT_PATH;
import static military.gunbam.utils.Util.isImageFile;
import static military.gunbam.utils.Util.isStorageUrl;
import static military.gunbam.utils.Util.showToast;
import static military.gunbam.utils.Util.storageUrlToName;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.components.containers.Category;
import com.google.mediapipe.tasks.components.containers.Detection;
import com.google.mediapipe.tasks.core.OutputHandler;
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector;
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import military.gunbam.R;
import military.gunbam.model.Post.PostInfo;
import military.gunbam.model.SuccessCountSingleton;
import military.gunbam.view.ContentsItemView;
import military.gunbam.viewmodel.DeepLearningViewModel;
import military.gunbam.viewmodel.DeepLearningViewModelFactory;
import military.gunbam.viewmodel.UserViewModel;
import military.gunbam.viewmodel.WritePostViewModel;

public class WritePostActivity extends AppCompatActivity {

    private WritePostViewModel writePostViewModel;
    private DeepLearningViewModel deepLearningViewModel;
    private UserViewModel userViewModel;
    private static final String TAG = "WritePostActivity";
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    private RelativeLayout buttonsBackgroundLayout;
    private RelativeLayout loaderLayout;
    private ImageView selectedImageVIew;
    private EditText selectedEditText;
    private EditText contentsEditText;
    private EditText titleEditText;
    private PostInfo postInfo;
    private int pathCount=0;
    private CheckBox anonymousCheckBox;
    private boolean isAnonymous = false;
    private ImageButton writePostBackButton;
    private Bitmap deepLearningResultBitmap;
    private final ArrayList<String> contentsList = new ArrayList<>();
    private final ArrayList<String> formatList = new ArrayList<>();
    private SuccessCountSingleton successCountSingleton = SuccessCountSingleton.getInstance();
    private static final String modelPath = "model2.tflite";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        parent = findViewById(R.id.contentsLayout);
        buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);
        loaderLayout = findViewById(R.id.loaderLyaout);
        contentsEditText = findViewById(R.id.contentsEditText);
        titleEditText = findViewById(R.id.titleEditText);
        anonymousCheckBox = findViewById(R.id.writePostAnonymousCheckBox);

        findViewById(R.id.writePostBackButton).setOnClickListener(onClickListener);
        findViewById(R.id.writePostButton).setOnClickListener(onClickListener);
        findViewById(R.id.image).setOnClickListener(onClickListener);
        findViewById(R.id.imageModify).setOnClickListener(onClickListener);
        findViewById(R.id.delete).setOnClickListener(onClickListener);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.loadCurrentUser();
        userViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {

            } else {
            }
        });
        deepLearningViewModel = new ViewModelProvider(this,new DeepLearningViewModelFactory(this, modelPath)).get(DeepLearningViewModel.class);
        deepLearningViewModel = new DeepLearningViewModel(this, modelPath);
        deepLearningViewModel.getResultBitmap().observe(this, bitmap ->{
                deepLearningResultBitmap = bitmap;
            }
        );
        writePostViewModel = new ViewModelProvider(this).get(WritePostViewModel.class);

        // UI 요소와 ViewModel 데이터를 바인딩
        titleEditText = findViewById(R.id.titleEditText);
        contentsEditText = findViewById(R.id.contentsEditText);
        anonymousCheckBox = findViewById(R.id.writePostAnonymousCheckBox);

        // ViewModel의 데이터를 UI에 연결
        writePostViewModel.getTitleLiveData().observe(this, title -> {
            titleEditText.setText(title);
        });

        writePostViewModel.getContentsLiveData().observe(this, contents -> {
            contentsEditText.setText(contents);
        });

        writePostViewModel.getAnonymousLiveData().observe(this, isAnonymous -> {
            anonymousCheckBox.setChecked(isAnonymous);
        });

        anonymousCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isAnonymous = isChecked;
            writePostViewModel.setAnonymous(isAnonymous); // ViewModel에 변경된 데이터 업데이트
        });

        postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");
        if (postInfo != null) {
            writePostViewModel.setTitle(postInfo.getTitle()); // ViewModel에 데이터 설정
            writePostViewModel.setContents(postInfo.getContents()); // ViewModel에 데이터 설정
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.add(path);

                    ContentsItemView contentsItemView = new ContentsItemView(this);
                    contentsItemView.setDeepLearningViewModel(deepLearningViewModel);

                    if (selectedEditText == null) {
                        parent.addView(contentsItemView);
                    } else {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            if (parent.getChildAt(i) == selectedEditText.getParent()) {
                                parent.addView(contentsItemView, i + 1);
                                break;
                            }
                        }
                    }

                    contentsItemView.setImage(path);
                    deepLearningResultBitmap = contentsItemView.getImage();
                    contentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageVIew = (ImageView) v;
                        }
                    });

                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);
                }
                break;
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.set(parent.indexOfChild((View) selectedImageVIew.getParent()) - 1, path);
                    Glide.with(this).load(path).override(1000).into(selectedImageVIew);
                }
                break;
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.writePostBackButton:
                    onBackPressed();
                    break;
                case R.id.writePostButton:
                    postUpload();
                    break;
                case R.id.image:
                    myStartActivity(GalleryActivity.class, GALLERY_IMAGE, 0);
                    break;
                case R.id.buttonsBackgroundLayout:
                    if (buttonsBackgroundLayout.getVisibility() == View.VISIBLE) {
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.imageModify:
                    myStartActivity(GalleryActivity.class, GALLERY_IMAGE, 1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.delete:
                    final View selectedView = (View) selectedImageVIew.getParent();
                    String path = pathList.get(parent.indexOfChild(selectedView) - 1);
                    if(isStorageUrl(path)){
                        deletePost(path, postInfo, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showToast(WritePostActivity.this, "파일을 삭제하였습니다.");
                                pathList.remove(parent.indexOfChild(selectedView) - 1);
                                parent.removeView(selectedView);
                                buttonsBackgroundLayout.setVisibility(View.GONE);
                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                showToast(WritePostActivity.this, "파일을 삭제하는데 실패하였습니다.");
                            }
                        });
                    }else{
                        pathList.remove(parent.indexOfChild(selectedView) - 1);
                        parent.removeView(selectedView);
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                selectedEditText = (EditText) v;
            }
        }
    };

    private void postUpload(){
        final String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString();
        if (title.length() > 0) {
            ArrayList<String> recommend = new ArrayList<>();
            String boardName = getIntent().getStringExtra("boardName");
            Log.d("테스트 boardName",boardName);
            loaderLayout.setVisibility(View.VISIBLE);
            final Date date;
            if (postInfo == null) {
                setDocumentReference("posts");
                date = new Date();
            } else {
                setDocumentReference("posts",postInfo.getId());
                date = postInfo.getCreatedAt();

            }
            //final Date date = postInfo == null ? new Date() : postInfo.getCreatedAt();

            for (int i = 0; i < parent.getChildCount(); i++) {
                LinearLayout linearLayout = (LinearLayout) parent.getChildAt(i);
                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View view = linearLayout.getChildAt(j);
                    if (view instanceof EditText) {
                        String text = ((EditText) view).getText().toString();
                        if (text.length() > 0) {
                            contentsList.add(text);
                            formatList.add("text");
                        } else if (!isStorageUrl(pathList.get(pathCount))) {
                            String path = pathList.get(pathCount);
                            successCountSingleton.increaseSuccessCount();
                            contentsList.add(path);

                            Log.d("이미지 path 테스트",isImageFile(path) + path);

                            if (isImageFile(path)) {
                                formatList.add("image");
                                Log.d("WritePostActivity",isImageFile(path) + path);

                                processImage(deepLearningResultBitmap, new PostInfo(title,contentsList,formatList,userViewModel.getCurrentUser().getValue().getUid(),date,isAnonymous,recommend,boardName));

                            }
                            processText(path, new PostInfo(title,contentsList,formatList,userViewModel.getCurrentUser().getValue().getUid(),date,isAnonymous,recommend,boardName)); //

                        } else {
                            formatList.add("text");
                        }


                    }
                }
            }
            writePostViewModel.setTitle(title);
            writePostViewModel.setContents(contentsList);
            writePostViewModel.setAnonymous(isAnonymous);

            if(successCountSingleton.getSuccessCount()==0){
                storeUpload(new PostInfo(title,contentsList,formatList,userViewModel.getCurrentUser().getValue().getUid(),date,isAnonymous,recommend,boardName));
            }


        } else{
            showToast(WritePostActivity.this, "제목을 입력해주세요.");
        }
    }

    private void storeUpload(final PostInfo postInfo) {
        writePostViewModel.storeUpload(postInfo,
                aVoid -> {
                    processSuccess();
                },
                e->{
                    processFailure();
                });
    }

    private Bitmap decodeImageFile(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // 설정에 맞게 수정
        return BitmapFactory.decodeFile(filePath, options);
    }

    private void myStartActivity(Class c, int media, int requestCode) {
        Intent intent = new Intent(this, c);
        intent.putExtra(INTENT_MEDIA, media);
        startActivityForResult(intent, requestCode);
    }
    public void setDocumentReference(String collectionPath, String documentID){
        writePostViewModel.setDocumentReference(collectionPath, documentID);
    }
    public void setDocumentReference(String collectionPath){
        writePostViewModel.setDocumentReference(collectionPath);
    }
    private void processText(String path, PostInfo postInfo) {
        writePostViewModel.processText(path, pathList, contentsList, formatList, postInfo, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("WritePostActivity 테스트","processText 성공");
                processSuccess();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("WritePostActivity 테스트","processText 실패");
                processFailure();
            }
        });
    }
    private void processImage(Bitmap deepLearningResultBitmap, PostInfo postInfo) {
        //Bitmap bitmap = decodeImageFile(path);
        Bitmap bitmap = deepLearningResultBitmap;
        Log.d("processImage 실행됨","테스트");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        writePostViewModel.uploadImage(contentsList, data, postInfo, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid){
                Log.d("WritePostActivity","processImage 테스트 성공");
                processSuccess();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("WritePostActivity","processImage 테스트 실패");
                processFailure();
            }
        });
    }
    private void deletePost(String path, PostInfo postInfo, OnSuccessListener<Void> voidOnSuccessListener, OnFailureListener onFailureListener){
        writePostViewModel.deletePost(path, postInfo, voidOnSuccessListener, onFailureListener);
    }
    private void processSuccess(){
        Log.d(TAG, "processSuccess 함수 테스트 성공");
        loaderLayout.setVisibility(View.GONE);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("postinfo", postInfo);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
    private void processFailure(){
        Log.d(TAG, "processFaileur 함수 테스트 실패");
        loaderLayout.setVisibility(View.GONE);
    }
}