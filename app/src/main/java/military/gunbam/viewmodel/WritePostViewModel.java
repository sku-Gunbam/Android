package military.gunbam.viewmodel;

import static military.gunbam.utils.Util.isImageFile;
import static military.gunbam.utils.Util.isStorageUrl;
import static military.gunbam.utils.Util.showToast;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import military.gunbam.R;
import military.gunbam.model.Post.PostInfo;
import military.gunbam.model.Post.PostModel;
import military.gunbam.view.activity.WritePostActivity;

public class WritePostViewModel extends ViewModel {
    private PostModel postModel = new PostModel();
    private MutableLiveData<String> titleLiveData = new MutableLiveData<>();
    private MutableLiveData<String> contentsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> anonymousLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> loadingStatus = new MutableLiveData<>();
    private MutableLiveData<Exception> error = new MutableLiveData<>();
    private MutableLiveData<Bitmap> bitmapLiveData = new MutableLiveData<>();
    // 타이틀 설정
    public void setTitle(String title) {
        titleLiveData.setValue(title);
    }

    // 타이틀 가져오기
    public MutableLiveData<String> getTitleLiveData() {
        return titleLiveData;
    }

    // 내용 설정
    public void setContents(ArrayList<String> contents) {
        contentsLiveData.postValue(String.valueOf(contents));
    }

    // 내용 가져오기
    public MutableLiveData<String> getContentsLiveData() {
        return contentsLiveData;
    }

    // 익명 여부 설정
    public void setAnonymous(boolean isAnonymous) {
        anonymousLiveData.setValue(isAnonymous);
    }

    // 익명 여부 가져오기
    public MutableLiveData<Boolean> getAnonymousLiveData() {
        return anonymousLiveData;
    }

    public LiveData<Exception> getError() {
        return error;
    }



    public void storeUpload(PostInfo postInfo, OnSuccessListener<Void> voidOnSuccessListener, OnFailureListener onFailureListener) {
        loadingStatus.setValue(true);
        postModel.storeUpload(postInfo, voidOnSuccessListener, onFailureListener);

    }
    public void setDocumentReference(String collectionPath, String documentID) {
        postModel.setDocumentReference(collectionPath,documentID);
    }
    public void setDocumentReference(String collectionPath) {
        postModel.setDocumentReference(collectionPath);
    }

    public void uploadImage(int pathCount, ArrayList<String> contentsList, PostInfo postInfo, OnSuccessListener<Void> voidOnSuccessListener, OnFailureListener onFailureListener) {
        postModel.uploadImage(pathCount, contentsList,postInfo,voidOnSuccessListener,onFailureListener);
    }
    public void deletePost(String path, PostInfo postInfo, OnSuccessListener<Void> voidOnSuccessListener, OnFailureListener onFailureListener){
        postModel.deletePost(path, postInfo,voidOnSuccessListener,onFailureListener);
    }

    public void setBimap(Bitmap bitmap) {;
        Log.d("비트맵 WritePost 2차",bitmap.toString());
        postModel.setBitmap(bitmap);
    }

    public void addBitmapList(Bitmap bitmap){
        postModel.addBitmapList(bitmap);
    }

    private DeepLearningViewModel deepLearningViewModel;


    // DeepLearningViewModel을 반환하는 메서드
    public DeepLearningViewModel getDeepLearningViewModel() {
        return deepLearningViewModel;
    }
}


