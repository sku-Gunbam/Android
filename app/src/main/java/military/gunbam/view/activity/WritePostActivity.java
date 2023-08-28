package military.gunbam.view.activity;

import static military.gunbam.utils.Util.showToast;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import military.gunbam.R;
import military.gunbam.model.WriteInfo;

public class WritePostActivity extends BasicActivity{

    private static final String TAG = "WritePostActivity";
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        findViewById(R.id.writePostButton).setOnClickListener(onClickListener);
        findViewById(R.id.writePostBackButton).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.writePostButton:
                    postUpdate();
                    finish();
                    break;

                case R.id.writePostBackButton:
                    finish();
                    break;
            }
        }
    };


    private void postUpdate() {
        final String title = ((EditText)findViewById(R.id.titleEditText)).getText().toString();
        final String contents = ((EditText)findViewById(R.id.contentsEditText)).getText().toString();
        final int recommendationCount = 0;

        // 익명 체크박스 상태 가져오기
        CheckBox anonymousCheckBox = findViewById(R.id.writePostAnonymousCheckBox);
        final boolean isAnonymous = anonymousCheckBox.isChecked();

        // 유효성 검사
        if (title.length() == 0 || contents.length() == 0) {
            showToast(WritePostActivity.this, "모든 정보를 입력해주세요.");
        } else {
            user = FirebaseAuth.getInstance().getCurrentUser();
            // 업로드 시간을 현재 시간으로 설정
            Timestamp uploadTime = new Timestamp(new Date());
            WriteInfo writeInfo = new WriteInfo(title, contents, user.getUid(), recommendationCount, isAnonymous, uploadTime);
            uploader(writeInfo);
            showToast(WritePostActivity.this, "게시물 등록을 성공하였습니다.");
        }
    }

    private void uploader(WriteInfo writeInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (user != null) {
            // 여기에 회원 정보를 Firebase에 저장하거나 다른 처리를 수행하는 코드를 추가할 수 있습니다.
            String uid = user.getUid();
            db.collection("posts").add(writeInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        } else {
            showToast(WritePostActivity.this, "로그인 상태를 확인할 수 없습니다. 다시 로그인해주세요.");
        }
    }
}
