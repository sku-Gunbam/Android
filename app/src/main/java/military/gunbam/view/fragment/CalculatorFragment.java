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

        return view;
    }

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