package military.gunbam.viewmodel;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import military.gunbam.model.CommentInfo;
import military.gunbam.model.login.LoginResult;
import military.gunbam.view.adapter.CommentAdapter;

public class CommentListViewModel extends ViewModel {
    private MutableLiveData<List<CommentInfo>> commentListLiveData = new MutableLiveData<>();
    private FirebaseFirestore firestore;
    private List<CommentInfo> commentList;

    public LiveData<List<CommentInfo>> getCommentListLiveData() {
        return commentListLiveData;
    }

    public CommentListViewModel() {
        firestore = FirebaseFirestore.getInstance();
        commentList = new ArrayList<>();
    }
    public void loadComments(String postId) {
        firestore.collection("comments")
                .whereEqualTo("commentId", postId)
                .orderBy("commentUploadTime", Query.Direction.DESCENDING) // 색인 사용
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            commentList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String commentId = document.getId();
                                String commentContent = document.getString("commentContent");
                                String commentAuthor = document.getString("commentAuthor");
                                boolean commentIsAnonymous = document.getBoolean("commentIsAnonymous"); // 익명 여부 가져오기
                                String parentCommentId = document.getString("parentCommentId"); // 부모 댓글 ID 가져오기
                                Timestamp commentUploadTime = document.getTimestamp("commentUploadTime");

                                firestore.collection("users")
                                        .document(commentAuthor)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot userDocument) {
                                                if (userDocument.exists()) {
                                                    String nickName = userDocument.getString("nickName");

                                                    // Create WriteInfo object based on the condition
                                                    CommentInfo commentInfo;
                                                    if (commentIsAnonymous) {
                                                        nickName = "익명";
                                                        commentInfo = new CommentInfo(commentId, commentContent, nickName, commentIsAnonymous, parentCommentId, commentUploadTime);
                                                    } else {
                                                        commentInfo = new CommentInfo(commentId, commentContent, nickName, commentIsAnonymous, parentCommentId, commentUploadTime);
                                                    }
                                                    commentList.add(commentInfo);
                                                    // 댓글 목록이 업데이트되었음을 LiveData에 알립니다.
                                                    commentListLiveData.setValue(commentList);
                                                }
                                            }
                                        });
                            }
                        } else {
                            // Handle error
                        }
                    }
                });
    }
}