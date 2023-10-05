package military.gunbam.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Date;

import military.gunbam.R;
import military.gunbam.listener.OnPostListener;
import military.gunbam.model.Post.PostInfo;
import military.gunbam.view.activity.AdminActivity;
import military.gunbam.view.activity.LoginActivity;
import military.gunbam.view.activity.SignUpActivity;
import military.gunbam.view.activity.WritePostActivity;
import military.gunbam.view.adapter.HomeAdapter;
import military.gunbam.viewmodel.HomeViewModel;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private FirebaseFirestore firebaseFirestore;
    private HomeAdapter homeAdapter;
    private ArrayList<PostInfo> postList;
    private boolean updating;
    private boolean topScrolled;

    private Button logoutButton, adminButton;

    private HomeViewModel homeViewModel;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // postList 초기화
        postList = new ArrayList<>();

        // ViewModel 초기화
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // RecyclerView 및 Adapter 설정
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeAdapter = new HomeAdapter(getActivity(), new ArrayList<>());
        recyclerView.setAdapter(homeAdapter);

        // ViewModel에서 LiveData를 관찰하여 데이터 업데이트를 처리
        homeViewModel.getPostListLiveData().observe(getViewLifecycleOwner(), postList -> {
            // Adapter에 데이터를 설정하여 화면 업데이트
            homeAdapter.setPostList(postList);
        });

        // isAdmin LiveData를 관찰하여 사용자의 "admin" 권한 여부를 확인합니다.
        adminButton =  view.findViewById(R.id.adminButton);
        homeViewModel.checkUserIsAdmin();
        homeViewModel.getIsAdmin().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isAdmin) {
                if (isAdmin) {
                    // 사용자가 "admin"인 경우, 관련 UI 또는 기능을 활성화할 수 있습니다.
                    // 예: 특정 버튼을 활성화하거나 특정 화면으로 이동하는 로직을 추가합니다.
                    adminButton.setVisibility(View.VISIBLE);
                } else {
                    // 사용자가 "admin"이 아닌 경우, 해당 UI 또는 기능을 비활성화할 수 있습니다.
                    // 예: "admin" 전용 기능을 숨기거나 다른 화면으로 이동하는 로직을 추가합니다.
                    adminButton.setVisibility(View.GONE);
                }
            }
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
        homeViewModel.loadPosts(false);

        // 어드민 버튼 클릭 이벤트
        view.findViewById(R.id.adminButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myStartActivity(AdminActivity.class);
            }
        });

        // 로그아웃 버튼 클릭 이벤트
        view.findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeViewModel.logout();
                myStartActivity(LoginActivity.class);
            }
        });

        // 게시물 작성 버튼 클릭 이벤트
        view.findViewById(R.id.mainFloatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeViewModel.navigateToWritePost(getActivity());
            }
        });

        // 게시물 수정 및 삭제 버튼 클릭 이벤트
        homeAdapter.setOnPostListener(new OnPostListener() {
            @Override
            public void onDelete(PostInfo postInfo) {
                postList.remove(postInfo);
                homeAdapter.notifyDataSetChanged();
                homeViewModel.refreshPosts();
            }

            @Override
            public void onModify() {
            }
        });
        return view;
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        startActivityForResult(intent, 0);
    }
}