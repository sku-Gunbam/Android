package military.gunbam.view.activity;

import static military.gunbam.utils.Util.showToast;
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
import military.gunbam.viewmodel.UserViewModel;

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
    public static String parentCommentId;
    private UserViewModel userViewModel;

    String TAG = "PostActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postViewModel = new ViewModelProvider(this, new PostViewModelFactory(this)).get(PostViewModel.class);
        postViewModel.loadCollection("comments");
        postViewModel.getFirebaseStoreCollection().observe(this, comments -> {

        });
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.loadCurrentUser();
        userViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {

            } else {
            }
        });
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

                    Log.d("테스트 PostActivity", "실행됨");
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

    private void countUpdate() {
        countCommentsWithId(postInfo.getId(), tvCommentCount);
        tvRecommendCount.setText("" + postInfo.getRecommend().size());
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

    public static void ReplyButtonEvent(String parentCommentId) {
        PostActivity.commentEditText.setHint("대댓글을 입력하세요.");
        PostActivity.parentCommentId = parentCommentId;
    }

    public void countCommentsWithId(String postId, TextView tvCommentCount) {
        postViewModel.countCommentsWithId(postId,tvCommentCount);
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
                    String commentAuthor = userViewModel.getCurrentUser().getValue().getUid();
                    Timestamp commentUploadTime = Timestamp.now();
                    ArrayList<String> recommend = new ArrayList<>();

                    boolean isAnonymous = anonymousCheckBox.isChecked();

                    if (!TextUtils.isEmpty(commentContent)) {
                        CommentInfo newComment = new CommentInfo(postId, commentContent, commentAuthor, isAnonymous, parentCommentId, commentUploadTime, recommend);

                        postViewModel.writeComments(newComment,
                                documentReference -> {
                                    showToast(PostActivity.this, "댓글이 작성되었습니다.");
                                    commentEditText.setText("");
                                    hideKeyboard();
                                }, e->{
                                    showToast(PostActivity.this, "댓글 작성에 실패했습니다.");
                                    hideKeyboard();
                                });
                        loadComments(postId);
                        countUpdate();
                    } else {
                        showToast(PostActivity.this, "댓글을 입력해주세요.");
                    }

                    parentCommentId = null;
                    commentEditText.setHint("댓글을 입력하세요.");
                    break;
                }

                case R.id.cardView_recommend: {
                    String user = userViewModel.getCurrentUser().getValue().getUid();
                    ArrayList<String> recommend = postInfo.getRecommend();

                    // 이미 추천한 경우
                    if (recommend.contains(user)) {
                        // 추천 리스트에서 사용자 삭제
                        recommend.remove(user);
                        String postId = postInfo.getId();

                        postViewModel.deleteRecommend("posts",postId, "recommend" , recommend,
                                aVoid -> {
                                    showToast(PostActivity.this, "추천 취소");
                                },
                                e ->{
                                    showToast(PostActivity.this, "추천 업데이트 중 오류: " + e.getMessage());
                                }
                        );


                    } else {
                        // 추천 리스트에 사용자 추가
                        recommend.add(user);
                        String postId = postInfo.getId();
                        postViewModel.addRecommend("posts",postId,"recommend",recommend,
                                aVoid ->{
                                    showToast(PostActivity.this, "추천 완료");
                                },
                                e -> {
                                    showToast(PostActivity.this, "추천 업데이트 중 오류: " + e.getMessage());
                                }
                        );
                    }
                    countUpdate();
                }
            }
        }
    };
    // 키보드 숨기는 함수
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
    }
}
