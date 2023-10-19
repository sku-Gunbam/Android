package military.gunbam.viewmodel;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import military.gunbam.FirebaseHelper;
import military.gunbam.listener.OnPostListener;
import military.gunbam.model.CommentInfo;
import military.gunbam.model.Post.PostInfo;
import military.gunbam.model.Post.PostModel;

public class PostViewModel extends ViewModel {
    private FirebaseHelper firebaseHelper;
    private MutableLiveData<ArrayList<PostInfo>> postListLiveData = new MutableLiveData<>();
    private PostModel postModel = new PostModel();
    private MutableLiveData<CollectionReference> collectionLiveData = new MutableLiveData<CollectionReference>();
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
        //postModel.
    }
    public void firebaseHelperStorageDelete(PostInfo postInfo) {
        firebaseHelper.storageDelete(postInfo);
    }
    public void loadCollection(String comment){
        collectionLiveData.setValue(postModel.firebaseStoreCollection(comment));
    }
    public LiveData<CollectionReference> getFirebaseStoreCollection() {
        return collectionLiveData;
    }

    public LiveData<ArrayList<PostInfo>> getPostListLiveData() {
        return postListLiveData;
    }

    public void countCommentsWithId(String postId, TextView tvCommentCount){
        postModel.countCommentsWithId(postId,tvCommentCount);
    }
    public void writeComments(CommentInfo newComment, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener){
        postModel.writeComments(newComment, onSuccessListener,onFailureListener);

    }
    public void deleteRecommend(String collectionPath, String postId, String filed, ArrayList<String> recommend, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener){
        postModel.deleteRecommend(collectionPath,postId,filed,recommend,onSuccessListener,onFailureListener);
    }
    public void addRecommend(String collectionPath, String postId, String filed, ArrayList<String> recommend, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener){
        postModel.addRecommend(collectionPath,postId,filed,recommend,onSuccessListener,onFailureListener);
    }
}