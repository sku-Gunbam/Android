package military.gunbam.view.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import military.gunbam.R;
import military.gunbam.view.activity.MainActivity;

public class CalculatorFragment extends Fragment {

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

    private void getUserData(String uid) {
        db.collection("users")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // nickName 필드를 가져와서 텍스트 뷰에 설정
                                String nickName = document.getString("nickName");
                                account_tv_nickname.setText(nickName);

                                // joinDate와 dischargeDate 필드 가져오기
                                String joinDateStr = document.getString("joinDate");
                                String dischargeDateStr = document.getString("dischargeDate");

                                // joinDate와 dischargeDate가 빈 문자열이 아닌 경우에만 D-DAY 표시
                                if (!TextUtils.isEmpty(joinDateStr) && !TextUtils.isEmpty(dischargeDateStr)) {
                                    // joinDate와 dischargeDate를 D-DAY로 변환하여 표시
                                    showDday(joinDateStr, dischargeDateStr);
                                } else {
                                    // joinDate 또는 dischargeDate가 빈 문자열인 경우 TextView 숨기기
                                    account_tv_progressbar.setVisibility(View.GONE);
                                    account_progressbar.setVisibility(View.GONE);
                                    account_count.setText("없음");
                                }
                            } else {
                                // 문서가 존재하지 않음
                            }
                        } else {
                            // 작업이 실패한 경우 처리
                        }
                    }
                });
    }

    private void showDday(String joinDateStr, String dischargeDateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
        try {
            Date joinDate = format.parse(joinDateStr);
            Date dischargeDate = format.parse(dischargeDateStr);
            long joinDateMillis = joinDate.getTime();
            long dischargeDateMillis = dischargeDate.getTime();
            long currentMillis = System.currentTimeMillis();

            long daysRemaining = (dischargeDateMillis - currentMillis) / (1000 * 60 * 60 * 24);
            if (daysRemaining < 0) {
                // 전역일이 이미 지났으면 "전역 완료" 표시
                account_count.setText("전역 완료");
                account_tv_progressbar.setText("100.00000000000000%");

                // ProgressBar의 최대 값을 전체 초 단위 값으로 설정
                account_progressbar.setMax(1);
                // ProgressBar를 현재까지의 초로 설정
                account_progressbar.setProgress(1);
            } else {
                // D-DAY를 텍스트 뷰에 표시
                ddayText = "D-" + daysRemaining;
                account_count.setText(ddayText);

                // 전체 초 단위 값 계산
                long totalSeconds = (dischargeDateMillis - joinDateMillis) / 1000;

                // 현재까지의 초 단위 값 계산
                long passedSeconds = (currentMillis - joinDateMillis) / 1000;

                // ProgressBar의 최대 값을 전체 초 단위 값으로 설정
                account_progressbar.setMax((int) totalSeconds);

                // ProgressBar를 현재까지의 초로 설정
                account_progressbar.setProgress((int) passedSeconds);

                // 퍼센트 계산 및 TextView에 표시
                double percent = (passedSeconds / (double) totalSeconds) * 100;
                String percentStr = String.format("%.14f%%", percent);
                account_tv_progressbar.setText(percentStr);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
