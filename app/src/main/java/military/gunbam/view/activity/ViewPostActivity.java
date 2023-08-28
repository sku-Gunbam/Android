package military.gunbam.view.activity;

import static military.gunbam.utils.Util.showToast;
import static military.gunbam.view.fragment.CommentListFragment.loadComments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

import military.gunbam.R;
import military.gunbam.model.CommentInfo;
import military.gunbam.view.fragment.CommentListFragment;

public class ViewPostActivity extends BasicActivity {

    private TextView titleTextView;
    private TextView contentsTextView;
    private TextView publisherTextView;
    private TextView uploadTimeTextView;
    private String publisher;

    private FirebaseFirestore firestore;

    private EditText commentEditText;
    private ImageButton postCommentButton;
    private CheckBox anonymousCheckBox;

    private static final String TAG = "ViewPostActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        findViewById(R.id.viewPostBackButton).setOnClickListener(onClickListener);
        findViewById(R.id.postCommentButton).setOnClickListener(onClickListener);

        firestore = FirebaseFirestore.getInstance();

        titleTextView = findViewById(R.id.postViewTitleTextView);
        contentsTextView = findViewById(R.id.postViewContentsTextView);
        publisherTextView = findViewById(R.id.postViewPublisherTextView);
        uploadTimeTextView = findViewById(R.id.postViewUploadTimeTextView);

        commentEditText = findViewById(R.id.commentEditText);
        anonymousCheckBox = findViewById(R.id.commentAnonymousCheckBox);

        // Get the post ID from the intent
        String postId = getIntent().getStringExtra("postId");

        CommentListFragment fragment = CommentListFragment.newInstance(postId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.commentListFragment, fragment)
                .commit();

        // Retrieve the post details from Firestore
        firestore.collection("posts").document(postId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String title = document.getString("title");
                                String contents = document.getString("contents");
                                boolean isAnonymous = document.getBoolean("isAnonymous");

                                if (isAnonymous) {
                                    publisher = "익명";
                                    publisherTextView.setText(publisher);
                                } else {
                                    String publisherUid = document.getString("publisher");
                                    firestore.collection("users").document(publisherUid)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot userDocument) {
                                                    if (userDocument.exists()) {
                                                        String nickName = userDocument.getString("nickName");
                                                        publisher = nickName;
                                                        publisherTextView.setText(publisher);
                                                    }
                                                }
                                            });
                                }
                                titleTextView.setText(title);
                                contentsTextView.setText(contents);

                                // Convert Timestamp to desired date format
                                Timestamp uploadTimestamp = document.getTimestamp("uploadTime");
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                String formattedUploadTime = sdf.format(uploadTimestamp.toDate());
                                uploadTimeTextView.setText(formattedUploadTime);
                            }
                        } else {
                            // Handle error
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
                    String postId = getIntent().getStringExtra("postId");
                    String commentContent = commentEditText.getText().toString();
                    String commentAuthor;
                    Timestamp commentUploadTime = Timestamp.now();

                    boolean isAnonymous = anonymousCheckBox.isChecked(); // Get the state of the anonymous checkbox

                    commentAuthor = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    if (!TextUtils.isEmpty(commentContent)) {
                        CommentInfo newComment = new CommentInfo(postId, commentContent, commentAuthor, isAnonymous, null, commentUploadTime);
                        // Here, parentCommentId is set to null. You can modify this to add replies.

                        CollectionReference commentsCollection = firestore.collection("comments");
                        commentsCollection.add(newComment)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        showToast(ViewPostActivity.this, "댓글이 작성되었습니다.");
                                        commentEditText.setText(""); // Clear the comment input
                                        loadComments(postId);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showToast(ViewPostActivity.this, "댓글 작성에 실패했습니다.");
                                    }
                                });
                    } else {
                        showToast(ViewPostActivity.this, "댓글을 입력해주세요.");
                    }
                    break;
                }
            }
        }
    };
}



