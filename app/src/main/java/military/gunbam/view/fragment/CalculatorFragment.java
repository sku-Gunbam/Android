package military.gunbam.view.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import military.gunbam.R;

public class CalculatorFragment extends Fragment {

    private View view;

    private String ddayText;
    private String TAG = "프래그먼트";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    public ProgressBar account_progressbar;
    public ImageView account_iv_profile;
    public TextView account_tv_nickname, account_count, account_tv_progressbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        view = inflater.inflate(R.layout.fragment_calculator_main, container, false);

        account_iv_profile = view.findViewById(R.id.account_iv_profile);
        account_tv_nickname = view.findViewById(R.id.account_tv_nickname);
        account_count = view.findViewById(R.id.account_count);
        account_progressbar = view.findViewById(R.id.account_progressbar);
        account_tv_progressbar = view.findViewById(R.id.account_tv_progressbar);

        if (currentUser != null) {
            // 사용자가 로그인되어 있으면 UID를 사용하여 Firestore에서 nickName을 가져옴
            getUserData(currentUser.getUid());
        }

        return view;
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
