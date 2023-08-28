package military.gunbam.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import military.gunbam.model.WriteInfo;
import military.gunbam.view.adapter.PostAdapter;

public class PostListFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<WriteInfo> postList;
    private FirebaseFirestore firestore;
    private ProgressBar progressBar;

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the post list when the fragment is resumed
        loadPosts();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);

        recyclerView = view.findViewById(R.id.postRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);

        progressBar = view.findViewById(R.id.postListProgressBar);

        firestore = FirebaseFirestore.getInstance();

        return view;
    }

    private void loadPosts() {
        progressBar.setVisibility(View.VISIBLE); // 프로그레스 바를 보이도록 설정
        firestore.collection("posts")
                .orderBy("uploadTime", Query.Direction.DESCENDING)
                //.limit(10)    // 게시물 개수 제한
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            postList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                String title = document.getString("title");
                                String contents = document.getString("contents");
                                String publisherUid = document.getString("publisher");
                                int recommendationCount = document.getLong("recommendationCount").intValue();
                                boolean isAnonymous = document.getBoolean("isAnonymous");
                                // Assuming "uploadTime" is a Firestore Timestamp field
                                Timestamp uploadTime = document.getTimestamp("uploadTime");


                                firestore.collection("users")
                                        .document(publisherUid)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot userDocument) {
                                                if (userDocument.exists()) {
                                                    String nickName = userDocument.getString("nickName");

                                                    // Create WriteInfo object based on the condition
                                                    WriteInfo writeInfo;
                                                    if (isAnonymous) {
                                                        nickName = "익명";
                                                        writeInfo = new WriteInfo(id, title, contents, nickName, recommendationCount, isAnonymous, uploadTime);
                                                    } else {
                                                        writeInfo = new WriteInfo(id, title, contents, nickName, recommendationCount, isAnonymous, uploadTime);
                                                    }
                                                    postList.add(writeInfo);
                                                    postAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });

                            }
                        } else {
                            // Handle error
                        }
                        progressBar.setVisibility(View.GONE); // 프로그레스 바를 보이도록 설정
                    }
                });

    }
}
