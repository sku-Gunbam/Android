package military.gunbam.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import military.gunbam.viewmodel.CommentListViewModel;
import military.gunbam.viewmodel.MemberInitViewModel;

public class CommentListFragment extends Fragment {
    private static final String TAG = "CommentListFragment";
    private RecyclerView commentRecyclerView;
    private static CommentAdapter commentAdapter;
    private static ProgressBar progressBar;
    private static CommentListViewModel commentListViewModel;

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

        commentListViewModel = new ViewModelProvider(this).get(CommentListViewModel.class);
        commentListViewModel.getCommentListLiveData().observe(getViewLifecycleOwner(), new Observer<List<CommentInfo>>() {
            @Override
            public void onChanged(List<CommentInfo> commentList) {
                commentAdapter.updateData(commentList);
            }
        });
        commentAdapter = new CommentAdapter(getContext(), new ArrayList<>());
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
        progressBar.setVisibility(View.VISIBLE);
        commentListViewModel.loadComments(postId);
        progressBar.setVisibility(View.GONE);
    }
}