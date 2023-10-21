package military.gunbam.view.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import military.gunbam.R;
import military.gunbam.viewmodel.CalculatorViewModel;

public class CalculatorFragment extends Fragment {

    String TAG = "CalculatorFragment";

    private CalculatorViewModel viewModel;

    // ... (기존의 변수 및 상수들은 그대로 유지)
    public TextView account_tv_nickname, account_count, account_tv_progressbar;
    public ProgressBar account_progressbar;
    public ImageView account_iv_profile;

    public Context context;

    // 갤러리에서 이미지를 선택하는 요청 코드
    private static final int PICK_IMAGE_REQUEST = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator_main, container, false);

        // ViewModelProvider를 사용하여 ViewModel 인스턴스 생성
        viewModel = new ViewModelProvider(this).get(CalculatorViewModel.class);

        context = getContext();

        account_iv_profile = view.findViewById(R.id.account_iv_profile);
        account_tv_nickname = view.findViewById(R.id.account_tv_nickname);
        account_count = view.findViewById(R.id.account_count);
        account_progressbar = view.findViewById(R.id.account_progressbar);
        account_tv_progressbar = view.findViewById(R.id.account_tv_progressbar);

        account_iv_profile.setOnClickListener(onClickListener);

        // LiveData를 observe하여 UI 업데이트
        viewModel.getUserLiveData(account_iv_profile, getContext()).observe(getViewLifecycleOwner(), userLiveData -> {
            // 사용자 데이터 업데이트 처리
            updateUserData(userLiveData);
        });

        viewModel.getDdayLiveData().observe(getViewLifecycleOwner(), ddayLiveData -> {
            // D-DAY 업데이트 처리
            updateDday(ddayLiveData);
        });

        viewModel.getProgressLiveData().observe(getViewLifecycleOwner(), progressLiveData -> {
            // 프로그레스 업데이트 처리
            updateProgress(progressLiveData);
        });

        // ... (기존의 코드는 그대로 유지)

        return view;
    }

    // ... (기존의 메서드는 그대로 유지)

    private void updateUserData(String userLiveData) {
        // 사용자 데이터 업데이트 로직
        account_tv_nickname.setText(userLiveData);
    }

    private void updateDday(String ddayLiveData) {
        // D-DAY 업데이트 로직
        account_count.setText(ddayLiveData);
    }

    private void updateProgress(String progressLiveData) {
        // 프로그레스 업데이트 로직
        if (progressLiveData == "없음") {
            account_tv_progressbar.setVisibility(View.GONE);
            account_progressbar.setVisibility(View.GONE);
        } else {
            account_progressbar.setMax(100);
            account_progressbar.setProgress((int)Double.parseDouble(progressLiveData));
            account_tv_progressbar.setText(progressLiveData + "%");
        }

    }

    // onActivityResult 메서드에서 이미지가 선택되었을 때 uploadImage 메서드 호출
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // 이미지 선택 성공
            Uri selectedImageUri = data.getData();
            CalculatorViewModel.uploadImage(selectedImageUri, context);
        } else {
            Log.e(TAG, requestCode + " / " + resultCode + " / " + data + " / " + data.getData());
        }
    }

    // 이미지를 선택하는 메서드
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.account_iv_profile:
                    openGallery();
                    break;
            }
        }
    };
}


/*
    private Context context;
    private View view;

    private String ddayText;
    private String TAG = "프래그먼트";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    public ProgressBar account_progressbar;
    public ImageView account_iv_profile;
    public TextView account_tv_nickname, account_count, account_tv_progressbar;

    // Firebase Storage 참조
    private StorageReference storageReference;

    // 갤러리에서 이미지를 선택하는 요청 코드
    private static final int PICK_IMAGE_REQUEST = 1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator_main, container, false);
        Log.i(TAG, "onCreateView");

        context = getContext();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        account_iv_profile = view.findViewById(R.id.account_iv_profile);
        account_tv_nickname = view.findViewById(R.id.account_tv_nickname);
        account_count = view.findViewById(R.id.account_count);
        account_progressbar = view.findViewById(R.id.account_progressbar);
        account_tv_progressbar = view.findViewById(R.id.account_tv_progressbar);


        account_iv_profile.setOnClickListener(onClickListener);

        if (currentUser != null) {
            // 사용자가 로그인되어 있으면 UID를 사용하여 Firestore에서 nickName을 가져옴
            getUserData(currentUser.getUid());
        }
        loadProfileImage();
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.account_iv_profile:
                    openGallery();
                    break;
            }
        }
    };

    // 사용자 프로필 이미지를 로드하고 표시하는 함수
    private void loadProfileImage() {
        // Cloud Firestore에 접근하기 위한 참조 얻기
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // "users/(사용자-uid)" 문서에서 프로필 이미지 URL 가져오기
        db.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 프로필 이미지 URL이 존재하는 경우
                        String imageUrl = documentSnapshot.getString("profileUrl");

                        // 이미지를 ImageView에 로드
                        if (imageUrl != null) {
                            loadAndDisplayImage(imageUrl);
                        } else {
                            // 프로필 이미지 URL이 없는 경우 기본 이미지를 설정하거나 처리
                            loadAndDisplayDefaultImage();
                        }
                    } else {
                        // 문서가 존재하지 않는 경우 기본 이미지를 설정하거나 처리
                        loadAndDisplayDefaultImage();
                    }
                })
                .addOnFailureListener(e -> {
                    // 데이터 로드 실패 시 기본 이미지를 설정하거나 처리
                });
    }

    // 기본 이미지를 ImageView에 로드하고 표시하는 함수
    private void loadAndDisplayDefaultImage() {
        if (getContext() != null) {
            // Glide 라이브러리를 사용하여 기본 이미지 로드 및 표시
            Glide.with(requireActivity())
                    .load(R.drawable.gunbam)  // 기본 이미지 리소스 ID
                    .into(account_iv_profile);
        }
    }

    // 이미지를 ImageView에 로드하고 표시하는 함수
    private void loadAndDisplayImage(String imageUrl) {
        // Firebase Storage에 접근하기 위한 참조 얻기
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

        // StorageReference를 URL로 변환하여 Glide로 이미지 로드 및 표시
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            if (getContext() != null) {
                Glide.with(requireActivity())
                        .load(uri)
                        .into(account_iv_profile);
            }
        }).addOnFailureListener(e -> {
            // URL을 가져오는데 실패한 경우 기본 이미지 표시 또는 에러 처리
            loadAndDisplayDefaultImage();
        });
    }


    // 이미지 저장소를 DB에 넣는 메서드
    private void saveImageUrlToFirestore(String imageUrl) {
        // Cloud Firestore에 접근하기 위한 참조 얻기
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // "users/(사용자-uid)" 문서에 프로필 이미지 URL 저장
        db.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .update("profileUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    // 성공적으로 저장된 경우
                    Toast.makeText(context, "적용 완료! 잠시후 새로고침을 해주세요.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // 저장 실패 시
                    Toast.makeText(context, "프로필 이미지 Uri 저장 실패", Toast.LENGTH_SHORT).show();
                });
    }

    // 이미지를 선택하는 메서드
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    // 이미지를 업로드하는 메서드
    private void uploadImage(Uri imageUri) {
        // Firebase Storage에 업로드할 경로 설정
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 여기에 실제 사용자의 UID를 가져와야 합니다.
        String storagePath = "users/" + uid + "/profile.jpg";

        storageReference = FirebaseStorage.getInstance().getReference(storagePath);

        // 이미지 업로드
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // 이미지 업로드 성공
                    // 여기서 이미지의 다운로드 URL을 가져올 수 있습니다.
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        saveImageUrlToFirestore(downloadUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    // 이미지 업로드 실패
                    Toast.makeText(context, "프로필 이미지 저장 실패", Toast.LENGTH_SHORT).show();
                });
    }

    // onActivityResult 메서드에서 이미지가 선택되었을 때 uploadImage 메서드 호출
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // 이미지 선택 성공
            Uri selectedImageUri = data.getData();
            if (context != null) {
                uploadImage(selectedImageUri);
            }
        }
    }
 */