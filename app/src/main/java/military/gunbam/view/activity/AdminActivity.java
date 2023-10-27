package military.gunbam.view.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import military.gunbam.R;
import military.gunbam.view.fragment.ChattingFragment;
import military.gunbam.viewmodel.AdminViewModel;
import military.gunbam.viewmodel.UserViewModel;

//TODO Mock으로 해야할 듯...
public class AdminActivity extends BasicActivity {
    private UserViewModel userViewModel;
    private AdminViewModel adminViewModel;
    private String keyHash = "";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        adminViewModel.getKeyHash().observe(this, hash -> {
            keyHash = hash;
        });

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.loadCurrentUser();
        userViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {

            } else {
            }
        });
        findViewById(R.id.admin_menu_1_button).setOnClickListener(onClickListener);
        findViewById(R.id.admin_menu_2_button).setOnClickListener(onClickListener);
        findViewById(R.id.admin_menu_3_button).setOnClickListener(onClickListener);
        findViewById(R.id.admin_menu_4_button).setOnClickListener(onClickListener);
        findViewById(R.id.admin_menu_5_button).setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.admin_menu_1_button:
                    // if( adminViewModel.getUserInfo() != null)
                    // if( user << -- >> userViewModel.getCurrentUser().getValue()
                    if (userViewModel.getCurrentUser().getValue() != null) {
                        String userUid = userViewModel.getCurrentUser().getValue().getUid();

                        String[] boardNames = {"자유게시판", "정보게시판", "군인게시판", "신병게시판", "비밀게시판"};
                        for (String boardName : boardNames) {
                            // 테스트 게시물 5개씩 작성합니다.
                            for (int i = 0; i < 5; i++) {
                                // 게시물 데이터 생성
                                String title = "테스트 " + boardName + "_" + (i + 1);
                                Date date = new Date();
                                ArrayList<String> recommend = new ArrayList<>();
                                Boolean isAnonymous;
                                if (i % 2 == 1) {
                                    isAnonymous = true;
                                } else {
                                    isAnonymous = false;
                                }

                                // contentsList와 formatList를 생성
                                ArrayList<String> contentsList = new ArrayList<>();
                                ArrayList<String> formatList = new ArrayList<>();

                                // 텍스트 추가
                                String textContent = "이것은 텍스트 내용입니다.";
                                contentsList.add(textContent);
                                formatList.add("text");

                                if (i % 5 == 0) {
                                    // 이미지 추가
                                    String imageUrl = "http://luxblock.co.kr/file_data/luxblook/2020/08/17/4b0708ca352f2f903ed0ef0162bac4f2.png"; // 이미지 URL 예시
                                    contentsList.add(imageUrl);
                                    formatList.add("image");
                                }

                                // 게시물 데이터를 Map으로 매핑
                                Map<String, Object> postMap = new HashMap<>();
                                postMap.put("title", title);
                                postMap.put("contents", contentsList);
                                postMap.put("formats", formatList);
                                postMap.put("publisher", userUid);

                                postMap.put("createdAt", date);
                                postMap.put("isAnonymous", isAnonymous);
                                postMap.put("recommend", recommend);
                                postMap.put("boardName", boardName);

                                adminViewModel.setCollection("posts", postMap, AdminActivity.this);

                                // ArrayList 초기화
                                contentsList.clear();
                                formatList.clear();
                            }
                        }
                    } else {
                        showToast(AdminActivity.this, "사용자가 로그인되어 있지 않습니다.");
                    }
                    finish();
                    break;

                case R.id.admin_menu_2_button:
                    startActivity(ChattingFragment.class);
                    break;

                case R.id.admin_menu_3_button:
                    startActivity(TestDeepLearningActivity.class);
                    break;

                case R.id.admin_menu_4_button:
                    adminViewModel.loadKeyHash(AdminActivity.this);
                    Log.e("getKeyHash", ""+ keyHash);
                    showToast(AdminActivity.this,"getKeyHash" + keyHash);
                    break;

                case R.id.admin_menu_5_button:
                    // "posts" 컬렉션에 대한 참조 가져오기

                    adminViewModel.deleteTestPost();
                    finish();
                    break;
            }
        }
    };



}
