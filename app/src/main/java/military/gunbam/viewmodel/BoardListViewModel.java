package military.gunbam.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

import military.gunbam.FirebaseHelper;
import military.gunbam.listener.OnPostListener;
import military.gunbam.model.Post.PostInfo;
import military.gunbam.view.activity.WritePostActivity;

public class BoardListViewModel extends ViewModel {

    private static final String TAG = "BoardListViewModel";

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private MutableLiveData<ArrayList<PostInfo>> postListLiveData = new MutableLiveData<>();
    private boolean updating;
    private boolean topScrolled;
    private FirebaseHelper firebaseHelper;

    private MutableLiveData<Boolean> isAdmin = new MutableLiveData<>();

    public BoardListViewModel() {
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        postListLiveData.setValue(new ArrayList<>());
    }

    public LiveData<ArrayList<PostInfo>> getPostListLiveData() {
        return postListLiveData;
    }

    // 게시물 목록 다시 불러오기
    public void refreshPosts(String field, String boardName) {
        loadPosts(true, field, boardName); // true를 전달하여 데이터를 초기화하고 새로 불러오도록 합니다.
    }

    // 게시물 작성 화면으로 이동
    public void navigateToWritePost(Context context) {
        Intent intent = new Intent(context, WritePostActivity.class);
        intent.putExtra("boardName", "자유게시판");
        context.startActivity(intent);
    }

    // 게시물 삭제 메서드
    public void deletePost(PostInfo postInfo) {
        // FirebaseHelper를 사용하여 게시물 삭제
        firebaseHelper.setOnPostListener(new OnPostListener() {
            @Override
            public void onDelete(PostInfo postInfo) {
                // 게시물 삭제 후에 필요한 처리
                // 예: 화면 업데이트 등
                Log.d("로그: ", "삭제 성공");
            }

            @Override
            public void onModify() {
                // 게시물 수정 후에 필요한 처리
                Log.d("로그: ", "수정 성공");
            }
        });

        // 게시물 삭제 로직 호출
        firebaseHelper.storageDelete(postInfo);
    }

    public void loadPosts(final boolean clear, String field, String fieldValue) {
        if (updating) return;
        updating = true;

        ArrayList<PostInfo> postList = postListLiveData.getValue();
        Date date = postList == null || postList.size() == 0 || clear ? new Date() : postList.get(postList.size() - 1).getCreatedAt();

        CollectionReference collectionReference = firebaseFirestore.collection("posts");
        collectionReference
                .whereEqualTo(field, fieldValue)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .whereLessThan("createdAt", date)
                //.limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (clear) {
                            postListLiveData.setValue(new ArrayList<>());
                        }
                        ArrayList<PostInfo> newPostList = postListLiveData.getValue();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            newPostList.add(new PostInfo(
                                    document.getData().get("title").toString(),
                                    (ArrayList<String>) document.getData().get("contents"),
                                    (ArrayList<String>) document.getData().get("formats"),
                                    document.getData().get("publisher").toString(),
                                    new Date(document.getDate("createdAt").getTime()),
                                    Boolean.parseBoolean(document.getData().get("isAnonymous").toString()),
                                    (ArrayList<String>) document.getData().get("recommend"),
                                    document.getData().get("boardName").toString(),
                                    document.getId()
                            ));
                        }
                        postListLiveData.setValue(newPostList);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    updating = false;
                });
    }
    public void loadCommentPosts(final boolean clear, String documentName) {
        if (updating) return;
        updating = true;

        ArrayList<PostInfo> postList = postListLiveData.getValue();
        Date date = postList == null || postList.size() == 0 || clear ? new Date() : postList.get(postList.size() - 1).getCreatedAt();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts")
                .whereEqualTo(FieldPath.documentId(), documentName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (clear) {
                        postListLiveData.setValue(new ArrayList<>());
                    }
                    ArrayList<PostInfo> newPostList = postListLiveData.getValue();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        newPostList.add(new PostInfo(
                                document.getData().get("title").toString(),
                                (ArrayList<String>) document.getData().get("contents"),
                                (ArrayList<String>) document.getData().get("formats"),
                                document.getData().get("publisher").toString(),
                                new Date(document.getDate("createdAt").getTime()),
                                Boolean.parseBoolean(document.getData().get("isAnonymous").toString()),
                                (ArrayList<String>) document.getData().get("recommend"),
                                document.getData().get("boardName").toString(),
                                document.getId()
                        ));
                        postListLiveData.setValue(newPostList);
                    }
                })
                .addOnFailureListener(e -> {
                });
        updating = false;
    }

}