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
import military.gunbam.view.ContentsItemView;
import military.gunbam.viewmodel.WritePostViewModel;

public class WritePostActivity extends AppCompatActivity {

    private WritePostViewModel writePostViewModel;

    private static final String TAG = "WritePostActivity";
    private FirebaseUser user;
    private StorageReference storageRef;
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    private RelativeLayout buttonsBackgroundLayout;
    private RelativeLayout loaderLayout;
    private ImageView selectedImageVIew;
    private EditText selectedEditText;
    private EditText contentsEditText;
    private EditText titleEditText;
    private PostInfo postInfo;
    private int pathCount, successCount;
    private CheckBox anonymousCheckBox;
    private boolean isAnonymous = false;
    private ImageButton writePostBackButton;

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

        // 기존 코드 중 필요한 부분만 남기고 나머지는 삭제
        // ...

        // 편집 모드인 경우 데이터 로드 및 초기화
        PostInfo postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");
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
                    storageUpload();
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
                        StorageReference desertRef = storageRef.child("posts/" + postInfo.getId() + "/" + storageUrlToName(path));
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showToast(WritePostActivity.this, "파일을 삭제하였습니다.");
                                pathList.remove(parent.indexOfChild(selectedView) - 1);
                                parent.removeView(selectedView);
                                buttonsBackgroundLayout.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
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

    private void storageUpload() {
        final String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString();
        if (title.length() > 0) {
            int recommendationCount = 0;

            String boardName = getIntent().getStringExtra("boardName");

            loaderLayout.setVisibility(View.VISIBLE);
            final ArrayList<String> contentsList = new ArrayList<>();
            final ArrayList<String> formatList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            final DocumentReference documentReference = postInfo == null ? firebaseFirestore.collection("posts").document() : firebaseFirestore.collection("posts").document(postInfo.getId());
            final Date date = postInfo == null ? new Date() : postInfo.getCreatedAt();

            for (int i = 0; i < parent.getChildCount(); i++) {
                LinearLayout linearLayout = (LinearLayout) parent.getChildAt(i);
                for (int ii = 0; ii < linearLayout.getChildCount(); ii++) {
                    View view = linearLayout.getChildAt(ii);
                    if (view instanceof EditText) {
                        String text = ((EditText) view).getText().toString();
                        if (text.length() > 0) {
                            contentsList.add(text);
                            formatList.add("text");
                        }
                    } else if (!isStorageUrl(pathList.get(pathCount))) {
                        String path = pathList.get(pathCount);
                        successCount++;
                        contentsList.add(path);
                        if(isImageFile(path)){
                            formatList.add("image");

                            // 이미지 파일을 비트맵으로 디코딩
                            Bitmap bitmap = decodeImageFile(path);

                            Log.e(TAG,"" + bitmap);
                            bitmap = deepLearing(bitmap);
                            // TODO: bitmap을 사용하여 원하는 작업 수행

                            // 비트맵을 ByteArrayOutputStream에 압축하여 byte 배열로 변환
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            // Firebase Storage에 업로드
                            StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + ".jpg");
                            UploadTask uploadTask = mountainImagesRef.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // 실패 처리
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // 성공 처리
                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            successCount--;
                                            contentsList.set(index, uri.toString());
                                            if (successCount == 0) {
                                                PostInfo postInfo = new PostInfo(title, contentsList, formatList, user.getUid(), date, isAnonymous, recommendationCount, boardName);
                                                storeUpload(documentReference, postInfo);
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            formatList.add("text");
                        }
                        String[] pathArray = path.split("\\.");
                        final StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + "." + pathArray[pathArray.length - 1]);
                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentsList.size() - 1)).build();
                            UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            successCount--;
                                            contentsList.set(index, uri.toString());
                                            if (successCount == 0) {
                                                PostInfo postInfo = new PostInfo(title, contentsList, formatList, user.getUid(), date, isAnonymous, recommendationCount, boardName);
                                                storeUpload(documentReference, postInfo);
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (FileNotFoundException e) {
                            Log.e("로그", "에러: " + e.toString());
                        }
                        pathCount++;
                    }
                }
            }
            // ViewModel에 데이터 업데이트
            writePostViewModel.setTitle(title);
            writePostViewModel.setContents(contentsList);
            writePostViewModel.setAnonymous(isAnonymous);

            if (successCount == 0) {
                storeUpload(documentReference, new PostInfo(title, contentsList, formatList, user.getUid(), date, isAnonymous, recommendationCount, boardName));
            }
        } else {
            showToast(WritePostActivity.this, "제목을 입력해주세요.");
        }
    }

    private void storeUpload(DocumentReference documentReference, final PostInfo postInfo) {
        documentReference.set(postInfo.getPostInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        loaderLayout.setVisibility(View.GONE);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("postinfo", postInfo);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        loaderLayout.setVisibility(View.GONE);
                    }
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

    private String[] labels = {"CAR_NUM", "RANK", "MARK", "KOREA", "NAME"};
    private static final int WIDTH = 320;
    private static final int HEIGHT = 320;
    private static final int CHANNELS = 3;

    private ObjectDetector objectDetector;
    private ObjectDetector.ObjectDetectorOptions options;
    private ObjectDetectorResult detectionResult;
    private ImageView deepLearningImageView;
    private Bitmap processedBitmap, originalBitmap;

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
}