package military.gunbam.view.activity;

import static military.gunbam.view.fragment.CommentListFragment.loadComments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import military.gunbam.R;
import military.gunbam.model.CommentInfo;
import military.gunbam.model.Post.PostInfo;
import military.gunbam.view.ReadContentsView;
import military.gunbam.view.fragment.CommentListFragment;
import military.gunbam.viewmodel.PostViewModel;
import military.gunbam.viewmodel.PostViewModelFactory;

public class PostActivity extends BasicActivity {
    private PostInfo postInfo;

    private CardView cardView_recommend;
    private ReadContentsView readContentsVIew;
    private LinearLayout contentsLayout;
    private static EditText commentEditText;
    private ImageButton postCommentButton;
    private CheckBox anonymousCheckBox;
    private TextView titleTextView, boardNameTextView, tvCommentCount, tvRecommendCount;

    private PostViewModel postViewModel;
    private ArrayList<PostInfo> postList;

    public static String parentCommentId;

    String TAG = "PostActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postViewModel = new ViewModelProvider(this, new PostViewModelFactory(this)).get(PostViewModel.class);

        postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");
        readContentsVIew = findViewById(R.id.readContentsView);

        findViewById(R.id.viewPostBackButton).setOnClickListener(onClickListener);
        findViewById(R.id.postCommentButton).setOnClickListener(onClickListener);
        findViewById(R.id.cardView_recommend).setOnClickListener(onClickListener);

        commentEditText = findViewById(R.id.commentEditText);
        anonymousCheckBox = findViewById(R.id.commentAnonymousCheckBox);
        titleTextView = findViewById(R.id.titleTextView);
        boardNameTextView = findViewById(R.id.boardNameTextView);
        tvCommentCount = findViewById(R.id.tvCommentCount);
        tvRecommendCount = findViewById(R.id.tvRecommendCount);

        uiUpdate();

        CommentListFragment fragment = CommentListFragment.newInstance(postInfo.getId());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.commentListFragment, fragment)
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    postInfo = (PostInfo)data.getSerializableExtra("postinfo");
                    contentsLayout.removeAllViews();
                    uiUpdate();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                storageDelete(postInfo);
                return true;
            case R.id.modify:
                myStartActivity(WritePostActivity.class, postInfo);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void uiUpdate(){
        if (postInfo != null && !TextUtils.isEmpty(postInfo.getTitle())) {
            titleTextView.setText(postInfo.getTitle());
            boardNameTextView.setText(postInfo.getBoardName());
            readContentsVIew.setPostInfo(postInfo);
            countCommentsWithId(postInfo.getId(), tvCommentCount);
            tvRecommendCount.setText("" + postInfo.getRecommend().size());

        } else {
            showToast(PostActivity.this, "게시물을 불러오지 못하였습니다.");
            finish();
        }
    }

    private void myStartActivity(Class c, PostInfo postInfo) {
        Intent intent = new Intent(this, c);
        intent.putExtra("postInfo", postInfo);
        startActivityForResult(intent, 0);
    }

    private void storageDelete(PostInfo postInfo){
        postViewModel.firebaseHelperStorageDelete(postInfo);
    }
    private CollectionReference collection(String comment) {
        return postViewModel.firebaseStoreCollection(comment);
    }

    public static void ReplyButtonEvent(String parentCommentId) {
        // Update UI or perform any necessary actions in response to the reply button click
        PostActivity.commentEditText.setHint("대댓글을 입력하세요.");
        PostActivity.parentCommentId = parentCommentId;
        Log.e("테스트", "" + parentCommentId);
    }

    public static void countCommentsWithId(String postId, TextView tvCommentCount) {
        // Firestore 인스턴스 얻기
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 'comments' 컬렉션 참조
        CollectionReference commentsRef = db.collection("comments");

        // postInfo.getId()와 같은 commentId를 가진 문서를 찾기 위한 쿼리
        Query query = commentsRef.whereEqualTo("commentId", postId);

        // 쿼리 실행
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // 쿼리 결과로부터 문서 개수 가져오기
                    int commentCount = task.getResult().size();

                    // 결과 사용 예시
                    // count 값을 원하는 대로 활용하면 됩니다.
                    // 예: TextView에 출력하거나 다른 처리 수행
                    System.out.println("Comment count for postId " + postId + ": " + commentCount);
                    tvCommentCount.setText("" + commentCount);
                } else {
                    // 쿼리 실패 시 예외 처리
                    Exception e = task.getException();
                    if (e != null) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case(R.id.viewPostBackButton) : {
                    finish();
                    break;
                }

                case R.id.postCommentButton: {
                    String postId = postInfo.getId();
                    String commentContent = commentEditText.getText().toString();
                    String commentAuthor;
                    Timestamp commentUploadTime = Timestamp.now();

                    boolean isAnonymous = anonymousCheckBox.isChecked(); // Get the state of the anonymous checkbox

                    commentAuthor = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    if (!TextUtils.isEmpty(commentContent)) {
                        CommentInfo newComment = new CommentInfo(postId, commentContent, commentAuthor, isAnonymous, parentCommentId, commentUploadTime);

                        CollectionReference commentsCollection = collection("comments");
                        commentsCollection.add(newComment)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        showToast(PostActivity.this, "댓글이 작성되었습니다.");
                                        commentEditText.setText(""); // Clear the comment input
                                        loadComments(postId);

                                        // Hide the keyboard
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showToast(PostActivity.this, "댓글 작성에 실패했습니다.");
                                        // Hide the keyboard
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
                                    }
                                });
                    } else {
                        showToast(PostActivity.this, "댓글을 입력해주세요.");
                    }

                    parentCommentId = null;
                    commentEditText.setHint("댓글을 입력하세요.");
                    break;
                }

                case R.id.cardView_recommend: {
                    String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    ArrayList<String> recommend = postInfo.getRecommend();

                    // 이미 추천한 경우
                    if (recommend.contains(user)) {
                        // 추천 리스트에서 사용자 삭제
                        recommend.remove(user);

                        // Firebase Firestore 참조
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        // posts 컬렉션에서 해당 문서의 recommend 필드 업데이트
                        db.collection("posts")
                                .document(postInfo.getId())
                                .update("recommend", recommend)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // 성공적으로 업데이트된 경우
                                        Log.e(TAG, "추천 취소");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // 업데이트 실패 시 오류 메시지 출력
                                        Log.e(TAG, "추천 업데이트 중 오류: " + e.getMessage());
                                    }
                                });
                        loadComments(postInfo.getId());
                    } else {
                        // 추천 리스트에 사용자 추가
                        recommend.add(user);

                        // Firebase Firestore 참조
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        // posts 컬렉션에서 해당 문서의 recommend 필드 업데이트
                        db.collection("posts")
                                .document(postInfo.getId())
                                .update("recommend", recommend)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // 성공적으로 업데이트된 경우
                                        Log.e(TAG, "추천 완료");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // 업데이트 실패 시 오류 메시지 출력
                                        Log.e(TAG, "추천 업데이트 중 오류: " + e.getMessage());
                                    }
                                });
                        loadComments(postInfo.getId());
                    }
                }
            }
        }
    };
}
