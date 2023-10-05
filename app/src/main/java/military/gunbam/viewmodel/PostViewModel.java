package military.gunbam.viewmodel;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import military.gunbam.FirebaseHelper;
import military.gunbam.listener.OnPostListener;
import military.gunbam.model.Post.PostInfo;

public class PostViewModel extends ViewModel {
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseHelper firebaseHelper;
    private MutableLiveData<ArrayList<PostInfo>> postListLiveData = new MutableLiveData<>();
    public PostViewModel(Activity activity){
        firebaseHelper = new FirebaseHelper(activity);
        firebaseHelper.setOnPostListener(new OnPostListener() {
            @Override
            public void onDelete(PostInfo postInfo) {
                Log.e("로그 ", "삭제 성공");
            }

            @Override
            public void onModify() {
                Log.e("로그 ", "수정 성공");
            }
        });
    }
    public void firebaseHelperStorageDelete(PostInfo postInfo) {
        firebaseHelper.storageDelete(postInfo);
    }
    public CollectionReference firebaseStoreCollection(String comment) {
        return firestore.collection(comment);
    }
    public LiveData<ArrayList<PostInfo>> getPostListLiveData() {
        return postListLiveData;
    }
}