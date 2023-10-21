package military.gunbam.viewmodel;


import static java.security.AccessController.getContext;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.util.Date;

import military.gunbam.R;

public class CalculatorViewModel extends ViewModel {
    String TAG = "CalculatorViewModel";

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private MutableLiveData<String> userLiveData = new MutableLiveData<>();
    private MutableLiveData<String> ddayLiveData = new MutableLiveData<>();
    private MutableLiveData<String> progressLiveData = new MutableLiveData<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<String> getUserLiveData(ImageView account_iv_profile, Context context) {
        loadUserData();
        loadProfileImage(account_iv_profile, context);
        return userLiveData;
    }

    public LiveData<String> getDdayLiveData() {
        calculateDday();
        return ddayLiveData;
    }

    public LiveData<String> getProgressLiveData() {
        return progressLiveData;
    }

    // 이미지를 업로드하는 메서드
    public static void uploadImage(Uri imageUri, Context context) {
        // Firebase Storage에 업로드할 경로 설정
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 여기에 실제 사용자의 UID를 가져와야 합니다.
        String storagePath = "users/" + uid + "/profile.jpg";

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(storagePath);

        // 이미지 업로드
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // 이미지 업로드 성공
                    // 여기서 이미지의 다운로드 URL을 가져올 수 있습니다.
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        saveImageUrlToFirestore(downloadUrl, context);
                    });
                })
                .addOnFailureListener(e -> {
                    // 이미지 업로드 실패
                    Toast.makeText(context, "프로필 이미지 저장 실패", Toast.LENGTH_SHORT).show();
                });
    }

    // 이미지 저장소를 DB에 넣는 메서드
    public static void saveImageUrlToFirestore(String imageUrl, Context context) {
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

    // 사용자 프로필 이미지를 로드하고 표시하는 함수
    public void loadProfileImage(ImageView account_iv_profile, Context context) {
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
                            loadAndDisplayImage(imageUrl, account_iv_profile, context);
                        } else {
                            // 프로필 이미지 URL이 없는 경우 기본 이미지를 설정하거나 처리
                            loadAndDisplayDefaultImage(account_iv_profile, context);
                        }
                    } else {
                        // 문서가 존재하지 않는 경우 기본 이미지를 설정하거나 처리
                        loadAndDisplayDefaultImage(account_iv_profile, context);
                    }
                })
                .addOnFailureListener(e -> {
                    // 데이터 로드 실패 시 기본 이미지를 설정하거나 처리
                });
    }

    // 이미지를 ImageView에 로드하고 표시하는 함수
    public void loadAndDisplayImage(String imageUrl, ImageView account_iv_profile, Context context) {
        // Firebase Storage에 접근하기 위한 참조 얻기
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

        // StorageReference를 URL로 변환하여 Glide로 이미지 로드 및 표시
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            if (getContext() != null) {
                Glide.with(context)
                        .load(uri)
                        .into(account_iv_profile);
            }
        }).addOnFailureListener(e -> {
            // URL을 가져오는데 실패한 경우 기본 이미지 표시 또는 에러 처리
            loadAndDisplayDefaultImage(account_iv_profile, context);
        });
    }

    // 기본 이미지를 ImageView에 로드하고 표시하는 함수
    public void loadAndDisplayDefaultImage(ImageView account_iv_profile, Context context) {
        if (getContext() != null) {
            // Glide 라이브러리를 사용하여 기본 이미지 로드 및 표시
            Glide.with(context)
                    .load(R.drawable.gunbam)  // 기본 이미지 리소스 ID
                    .into(account_iv_profile);
        }
    }

    public void loadUserData() {
        db.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // nickName 필드를 가져와서 텍스트 뷰에 설정
                                String nickName = document.getString("nickName");
                                userLiveData.setValue(nickName);
                            }
                        }
                    }
                });
    }

    public void calculateDday() {
        SimpleDateFormat format = new SimpleDateFormat("yyMMdd");

        db.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // joinDate와 dischargeDate 필드 가져오기
                                String joinDateStr = document.getString("joinDate");
                                String dischargeDateStr = document.getString("dischargeDate");
                                try {
                                    if (joinDateStr != null && dischargeDateStr != null) {
                                        Date joinDate = format.parse(joinDateStr);
                                        Date dischargeDate = format.parse(dischargeDateStr);

                                        long joinDateMillis = joinDate.getTime();
                                        long dischargeDateMillis = dischargeDate.getTime();
                                        long currentMillis = System.currentTimeMillis();

                                        long daysRemaining = (dischargeDateMillis - currentMillis) / (1000 * 60 * 60 * 24);

                                        if (daysRemaining < 0) {
                                            // 전역일이 이미 지났으면 "전역 완료" 표시
                                            mainHandler.post(() -> {
                                                ddayLiveData.setValue("전역 완료");
                                                progressLiveData.setValue("100");
                                            });
                                        } else {
                                            // 전체 초 단위 값 계산
                                            long totalSeconds = (dischargeDateMillis - joinDateMillis) / 1000;

                                            // 현재까지의 초 단위 값 계산
                                            long passedSeconds = (currentMillis - joinDateMillis) / 1000;

                                            // 퍼센트 계산 및 TextView에 표시
                                            if (totalSeconds > 0) {
                                                double percent = (passedSeconds / (double) totalSeconds) * 100;
                                                String percentStr = String.format("%.14f", percent);
                                                mainHandler.post(() -> {
                                                    ddayLiveData.setValue("D-" + daysRemaining);
                                                    progressLiveData.setValue(percentStr);
                                                });
                                            } else {
                                                // 예외 처리: totalSeconds가 0보다 작거나 같은 경우
                                                ddayLiveData.setValue("D-" + daysRemaining);
                                                progressLiveData.setValue("0.00000000000000");
                                            }
                                        }
                                    } else {
                                        // joinDateStr 또는 dischargeDateStr 중 하나 또는 둘 다가 null일 때의 예외 처리
                                        ddayLiveData.setValue("없음");
                                        progressLiveData.setValue("없음");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    // 예외 처리: ParseException이 발생한 경우
                                    ddayLiveData.setValue("없음");
                                    progressLiveData.setValue("없음");
                                }
                            }
                            }
                        }
                    });
                }
}