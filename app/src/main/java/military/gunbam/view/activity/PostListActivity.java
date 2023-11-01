package military.gunbam.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import military.gunbam.R;
import military.gunbam.listener.OnPostListener;
import military.gunbam.model.Post.PostInfo;
import military.gunbam.view.adapter.BoardListAdapter;
import military.gunbam.view.adapter.PostListAdapter;
import military.gunbam.viewmodel.BoardListViewModel;
import military.gunbam.viewmodel.CommentListViewModel;
import military.gunbam.viewmodel.PostListViewModel;

public class PostListActivity extends BasicActivity {

    private final String TAG = "PostListActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private ArrayList<PostInfo> postList;
    private ArrayList<PostInfo> commentsPostsList;
    private String commentId;
    private String field;
    private String fieldValue;
    private MutableLiveData<ArrayList<PostInfo>> postListLiveData = new MutableLiveData<>();
    private boolean updating;
    private CommentListViewModel commentListViewModel;
    private PostListAdapter postListAdapter;
    private PostListViewModel postListViewModel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list);

        Intent intent = getIntent();
        field = intent.getStringExtra("field"); // "boardName"이라고 가져와지거나 "publisher"

        fieldValue = intent.getStringExtra("fieldValue"); //"자유게시판",  "사람아이디"
        TextView textViewBoardName = findViewById(R.id.tvTitle);

        if(field.equals("boardName")) {
            textViewBoardName.setText(fieldValue);
        }else if(field.equals("publisher")){
            textViewBoardName.setText("내가 쓴 게시글");
        }else if(field.equals("commentAuthor")){
            textViewBoardName.setText("내가 쓴 댓글");
        }

        // postList 초기화
        postList = new ArrayList<>();

        // ViewModel 초기화
        postListViewModel = new ViewModelProvider(this).get(PostListViewModel.class);
        commentListViewModel = new ViewModelProvider(this).get(CommentListViewModel.class);
        // RecyclerView 및 Adapter 설정
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(PostListActivity.this));
        postListAdapter = new PostListAdapter(PostListActivity.this, new ArrayList<>());
        recyclerView.setAdapter(postListAdapter);

        // ViewModel에서 LiveData를 관찰하여 데이터 업데이트를 처리
        postListViewModel.getPostListLiveData().observe(this, postList -> {
            // Adapter에 데이터를 설정하여 화면 업데이트
            postListAdapter.setPostList(postList);
        });
        commentListViewModel.getCommentListLiveData().observe(this, commentList ->{

        });


        // 스크롤 이벤트 및 초기 데이터 로딩 처리
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            // 스크롤 이벤트 처리 코드...

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 스크롤 이벤트 처리 코드...
            }
        });

        // 초기 데이터 로딩
        postListViewModel.loadPosts(false,field, fieldValue);
        if(field.equals("boardName") || field.equals("publisher")) { // 내가 쓴 글
            // 초기 데이터 로딩
            postListViewModel.loadPosts(false, field, fieldValue);

        }
        else if(field.equals("commentAuthor")){ // 내가 쓴 댓글
            commentListViewModel.loadCommentsPosts(fieldValue);  // commentId
            commentListViewModel.getCommentIdLiveData().observe(this, getCommentId ->{
                postListViewModel.loadCommentPosts(false,getCommentId);
            });

        }
        // 게시물 작성 버튼 클릭 이벤트
        findViewById(R.id.mainFloatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToWritePost(PostListActivity.this);
            }
        });

        // 게시물 수정 및 삭제 버튼 클릭 이벤트
        postListAdapter.setOnPostListener(new OnPostListener() {
            @Override
            public void onDelete(PostInfo postInfo) {
                postList.remove(postInfo);
                postListAdapter.notifyDataSetChanged();
                postListViewModel.refreshPosts(field, fieldValue);
            }

            @Override
            public void onModify() {
            }
        });
    }

    // 게시물 작성 화면으로 이동
    public void navigateToWritePost(Context context) {
        Intent intent = new Intent(context, WritePostActivity.class);
        intent.putExtra(field, fieldValue);
        context.startActivity(intent);
    }
}
