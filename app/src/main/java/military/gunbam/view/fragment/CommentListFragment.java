package military.gunbam.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import military.gunbam.R;
import military.gunbam.model.CommentInfo;
import military.gunbam.view.adapter.CommentAdapter;

public class CommentListFragment extends Fragment {
    private static final String TAG = "CommentListFragment";
    private RecyclerView commentRecyclerView;
    private static CommentAdapter commentAdapter;
    private static List<CommentInfo> commentList;
    private static ProgressBar progressBar;
    private static FirebaseFirestore firestore;

    public static CommentListFragment newInstance(String postId) {
        CommentListFragment fragment = new CommentListFragment();
        Bundle args = new Bundle();
        args.putString("postId", postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        commentRecyclerView = view.findViewById(R.id.commentRecyclerView);
        progressBar = view.findViewById(R.id.commentProgressBar);

        // Set up Firestore instance
        firestore = FirebaseFirestore.getInstance();

        // Initialize commentList and commentAdapter
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(getContext(), commentList);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentRecyclerView.setAdapter(commentAdapter);

        Bundle args = getArguments();
        if (args != null) {
            String postId = args.getString("postId");
            if (postId != null) {
                loadComments(postId);
            }
        }
        return view;
    }

    public static void loadComments(String postId) {
        progressBar.setVisibility(View.VISIBLE); // 프로그레스 바를 보이도록 설정

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
                                                    commentAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });

                            }
                        } else {
                            // Handle error
                        }

                        progressBar.setVisibility(View.GONE); // 프로그레스 바를 감추도록 설정
                    }
                });
    }
}