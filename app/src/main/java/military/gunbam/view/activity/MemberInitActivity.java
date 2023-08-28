package military.gunbam.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import military.gunbam.R;
import military.gunbam.model.MemberInfo;

import static military.gunbam.utils.Util.showToast;

public class MemberInitActivity extends BasicActivity {
    private static final String TAG = "MemberInitActivity";
    private ImageView draftImageView;
    private String draftPath;
    private FirebaseUser user;

    private CardView cardView;

    // 금칙어 리스트
    public String[] forbiddenWords = {
            "18년", "18놈", "18새끼", "ㄱㅐㅅㅐㄲl", "ㄱㅐㅈㅏ", "가슴만져", "가슴빨아", "가슴빨어", "가슴조물락", "가슴주물럭", "가슴쪼물딱", "가슴쪼물락", "가슴핧아", "가슴핧어", "강간", "개가튼년", "개가튼뇬", "개같은년", "개걸레", "개고치", "개너미", "개넘", "개년", "개놈", "개늠", "개똥", "개떵", "개떡", "개라슥", "개보지", "개부달", "개부랄", "개불랄", "개붕알", "개새", "개세", "개쓰래기", "개쓰레기", "개씁년", "개씁블", "개씁자지", "개씨발", "개씨블", "개자식", "개자지", "개잡년", "개젓가튼넘", "개좆", "개지랄", "개후라년", "개후라들놈", "개후라새끼", "걔잡년", "거시기", "걸래년", "걸레같은년", "걸레년", "걸레핀년", "게부럴", "게세끼", "게이", "게새끼", "게늠", "게자식", "게지랄놈", "고환", "공지", "공지사항", "귀두", "깨쌔끼", "난자마셔", "난자먹어", "난자핧아", "내꺼빨아", "내꺼핧아", "내버지", "내자지", "내잠지", "내조지", "너거애비", "노옴", "누나강간", "니기미", "니뿡", "니뽕", "니씨브랄", "니아범", "니아비", "니애미", "니애뷔", "니애비", "니할애비", "닝기미", "닌기미", "니미", "닳은년", "덜은새끼", "돈새끼", "돌으년", "돌은넘", "돌은새끼", "동생강간", "동성애자", "딸딸이", "똥구녁", "똥꾸뇽", "똥구뇽", "똥", "띠발뇬", "띠팔", "띠펄", "띠풀", "띠벌", "띠벨", "띠빌", "마스터", "막간년", "막대쑤셔줘", "막대핧아줘", "맛간년", "맛없는년", "맛이간년", "멜리스", "미친구녕", "미친구멍", "미친넘", "미친년", "미친놈", "미친눔", "미친새끼", "미친쇄리", "미친쇠리", "미친쉐이", "미친씨부랄", "미튄", "미티넘", "미틴", "미틴넘", "미틴년", "미틴놈", "미틴것", "백보지", "버따리자지", "버지구녕", "버지구멍", "버지냄새", "버지따먹기", "버지뚫어", "버지뜨더", "버지물마셔", "버지벌려", "버지벌료", "버지빨아", "버지빨어", "버지썰어", "버지쑤셔", "버지털", "버지핧아", "버짓물", "버짓물마셔", "벌창같은년", "벵신", "병닥", "병딱", "병신", "보쥐", "보지", "보지핧어", "보짓물", "보짓물마셔", "봉알", "부랄", "불알", "붕알", "붜지", "뷩딱", "븅쉰", "븅신", "빙띤", "빙신", "빠가십새", "빠가씹새", "빠구리", "빠굴이", "뻑큐", "뽕알", "뽀지", "뼝신", "사까시", "상년", "새꺄", "새뀌", "새끼", "색갸", "색끼", "색스", "색키", "샤발", "서버", "써글", "써글년", "성교", "성폭행", "세꺄", "세끼", "섹스", "섹스하자", "섹스해", "섹쓰", "섹히", "수셔", "쑤셔", "쉐끼", "쉑갸", "쉑쓰", "쉬발", "쉬방", "쉬밸년", "쉬벌", "쉬불", "쉬붕", "쉬빨", "쉬이발", "쉬이방", "쉬이벌", "쉬이불", "쉬이붕", "쉬이빨", "쉬이팔", "쉬이펄", "쉬이풀", "쉬팔", "쉬펄", "쉬풀", "쉽쌔", "시댕이", "시발", "시발년", "시발놈", "시발새끼", "시방새", "시밸", "시벌", "시불", "시붕", "시이발", "시이벌", "시이불", "시이붕", "시이팔", "시이펄", "시이풀", "시팍새끼", "시팔", "시팔넘", "시팔년", "시팔놈", "시팔새끼", "시펄", "실프", "십8", "십때끼", "십떼끼", "십버지", "십부랄", "십부럴", "십새", "십세이", "십셰리", "십쉐", "십자석", "십자슥", "십지랄", "십창녀", "십창", "십탱", "십탱구리", "십탱굴이", "십팔새끼", "ㅆㅂ", "ㅆㅂㄹㅁ", "ㅆㅂㄻ", "ㅆㅣ", "쌍넘", "쌍년", "쌍놈", "쌍눔", "쌍보지", "쌔끼", "쌔리", "쌕스", "쌕쓰", "썅년", "썅놈", "썅뇬", "썅늠", "쓉새", "쓰바새끼", "쓰브랄쉽세", "씌발", "씌팔", "씨가랭넘", "씨가랭년", "씨가랭놈", "씨발", "씨발년", "씨발롬", "씨발병신", "씨방새", "씨방세", "씨밸", "씨뱅가리", "씨벌", "씨벌년", "씨벌쉐이", "씨부랄", "씨부럴", "씨불", "씨불알", "씨붕", "씨브럴", "씨블", "씨블년", "씨븡새끼", "씨빨", "씨이발", "씨이벌", "씨이불", "씨이붕", "씨이팔", "씨파넘", "씨팍새끼", "씨팍세끼", "씨팔", "씨펄", "씨퐁넘", "씨퐁뇬", "씨퐁보지", "씨퐁자지", "씹년", "씹물", "씹미랄", "씹버지", "씹보지", "씹부랄", "씹브랄", "씹빵구", "씹뽀지", "씹새", "씹새끼", "씹세", "씹쌔끼", "씹자석", "씹자슥", "씹자지", "씹지랄", "씹창", "씹창녀", "씹탱", "씹탱굴이", "씹탱이", "씹팔", "아가리", "애무", "애미", "애미랄", "애미보지", "애미씨뱅", "애미자지", "애미잡년", "애미좃물", "애비", "애자", "양아치", "어미강간", "어미따먹자", "어미쑤시자", "영자", "엄창", "에미", "에비", "엔플레버", "엠플레버", "염병", "염병할", "염뵹", "엿먹어라", "오랄", "오르가즘", "왕버지", "왕자지", "왕잠지", "왕털버지", "왕털보지", "왕털자지", "왕털잠지", "우미쑤셔", "운디네", "운영자", "유두", "유두빨어", "유두핧어", "유방", "유방만져", "유방빨아", "유방주물럭", "유방쪼물딱", "유방쪼물럭", "유방핧아", "유방핧어", "육갑", "이그니스", "이년", "이프리트", "자기핧아", "자지", "자지구녕", "자지구멍", "자지꽂아", "자지넣자", "자지뜨더", "자지뜯어", "자지박어", "자지빨아", "자지빨아줘", "자지빨어", "자지쑤셔", "자지쓰레기", "자지정개", "자지짤라", "자지털", "자지핧아", "자지핧아줘", "자지핧어", "작은보지", "잠지", "잠지뚫어", "잠지물마셔", "잠지털", "잠짓물마셔", "잡년", "잡놈", "저년", "점물", "젓가튼", "젓가튼쉐이", "젓같내", "젓같은", "젓까", "젓나", "젓냄새", "젓대가리", "젓떠", "젓마무리", "젓만이", "젓물", "젓물냄새", "젓밥", "정액마셔", "정액먹어", "정액발사", "정액짜", "정액핧아", "정자마셔", "정자먹어", "정자핧아", "젖같은", "젖까", "젖밥", "젖탱이", "조개넓은년", "조개따조", "조개마셔줘", "조개벌려조", "조개속물", "조개쑤셔줘", "조개핧아줘", "조까", "조또", "족같내", "족까", "족까내", "존나", "존나게", "존니", "졸라", "좀마니", "좀물", "좀쓰레기", "좁빠라라", "좃가튼뇬", "좃간년", "좃까", "좃까리", "좃깟네", "좃냄새", "좃넘", "좃대가리", "좃도", "좃또", "좃만아", "좃만이", "좃만한것", "좃만한쉐이", "좃물", "좃물냄새", "좃보지", "좃부랄", "좃빠구리", "좃빠네", "좃빠라라", "좃털", "좆같은놈", "좆같은새끼", "좆까", "좆까라", "좆나", "좆년", "좆도", "좆만아", "좆만한년", "좆만한놈", "좆만한새끼", "좆먹어", "좆물", "좆밥", "좆빨아", "좆새끼", "좆털", "좋만한것", "주글년", "주길년", "쥐랄", "지랄", "지랼", "지럴", "지뢀", "쪼까튼", "쪼다", "쪼다새끼", "찌랄", "찌질이", "창남", "창녀", "창녀버지", "창년", "처먹고", "처먹을", "쳐먹고", "쳐쑤셔박어", "촌씨브라리", "촌씨브랑이", "촌씨브랭이", "크리토리스", "큰보지", "클리토리스", "트랜스젠더", "페니스", "항문수셔", "항문쑤셔", "허덥", "허버리년", "허벌년", "허벌보지", "허벌자식", "허벌자지", "허접", "허젚", "허졉", "허좁", "헐렁보지", "혀로보지핧기", "호냥년", "호로", "호로새끼", "호로자슥", "호로자식", "호로짜식", "호루자슥", "호모", "호졉", "호좁", "후라덜넘", "후장", "후장꽂아", "후장뚫어", "흐접", "흐젚", "흐졉", "bitch", "fuck", "fuckyou", "nflavor", "penis", "pennis", "pussy", "sex"
    };

    public boolean isStringValid(String input) {
        // 닉네임과 이름에 사용 가능한 문자를 정의합니다. (예: 영문 대/소문자, 숫자, 한글)
        String regex = "^[a-zA-Z0-9가-힣]*$";
        return input.matches(regex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

        showToast(MemberInitActivity.this, "회원정보를 입력해주세요.");

        draftImageView = findViewById(R.id.draftImageView);
        draftImageView.setOnClickListener(onClickListener);

        findViewById(R.id.checkButton).setOnClickListener(onClickListener);
        findViewById(R.id.pictureButton).setOnClickListener(onClickListener);
        findViewById(R.id.galleryButton).setOnClickListener(onClickListener);

        cardView = findViewById(R.id.draftCardView);
    }

    @Override
    // 뒤로가기 로그인 방지
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    draftPath = data.getStringExtra("draftPath");
                    Glide.with(this)
                            .load(draftPath)
                            .centerCrop()
                            .override(500)
                            .into(draftImageView);
                }
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.checkButton:
                    profileUpdate();
                    break;
                case R.id.draftImageView:
                    if (cardView.getVisibility() == View.VISIBLE) {
                        cardView.setVisibility(View.GONE);
                    } else {
                        cardView.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.pictureButton:
                    if (ContextCompat.checkSelfPermission(MemberInitActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(MemberInitActivity.this,
                                Manifest.permission.CAMERA)) {
                            showToast(MemberInitActivity.this, "카메라 권한이 필요합니다.\n권한을 허용해 주세요.");
                        }

                        ActivityCompat.requestPermissions(MemberInitActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                2); // 다른 권한 요청과 겹치지 않도록 새로운 requestCode인 2로 변경합니다.
                    } else {
                        cardView.setVisibility(View.GONE);
                        startActivity(CameraActivity.class);
                    }
                    break;
                case R.id.galleryButton:
                    if (ContextCompat.checkSelfPermission(MemberInitActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(MemberInitActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            showToast(MemberInitActivity.this, "외부 저장소 읽기 권한이 필요합니다.\n권한을 허용해 주세요.");
                        }

                        ActivityCompat.requestPermissions(MemberInitActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);
                    } else {
                        cardView.setVisibility(View.GONE);
                        startActivity(GalleryActivity.class);
                    }
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(GalleryActivity.class);
                } else {
                    showToast(MemberInitActivity.this, "파일 읽기 권한을 허용해 주세요.");
                }
                break;
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(CameraActivity.class);
                } else {
                    showToast(MemberInitActivity.this, "카메라 권한을 허용해 주세요.");
                }
                break;
            }
            default:
                // Handle other permission request results if needed.
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void profileUpdate() {
        final String nickName = ((EditText)findViewById(R.id.nickNameEditText)).getText().toString();
        final String name = ((EditText)findViewById(R.id.nameEditText)).getText().toString();
        final String phoneNumber = ((EditText)findViewById(R.id.phoneNumberEditText)).getText().toString();
        final String birthDate = ((EditText)findViewById(R.id.birthDateEditText)).getText().toString();
        final String joinDate = ((EditText)findViewById(R.id.joinDateEditText)).getText().toString();
        final String dischargeDate = ((EditText)findViewById(R.id.dischargeDateEditText)).getText().toString();
        final String rank = "user";

        // 유효성 검사
        if (nickName.length() == 0 || name.length() == 0 || phoneNumber.length() < 0 || birthDate.length() < 0) {
            showToast(MemberInitActivity.this, "모든 회원정보를 입력해주세요.");
        } else if (joinDate.length() != 6 && joinDate.length() != 0) {
            showToast(MemberInitActivity.this, "올바르지 않은 입대일입니다.\nex)230101");
        } else if (dischargeDate.length() != 6 && dischargeDate.length() != 0) {
            showToast(MemberInitActivity.this, "올바르지 않은 제대일입니다.\nex)250101");
        } else if (phoneNumber.length() > 11 || phoneNumber.length() < 10) {
            showToast(MemberInitActivity.this, "유효하지 않은 전화번호입니다.\n'-' 없이 숫자만 입력해주세요.");
        } else if (birthDate.length() != 6) {
            showToast(MemberInitActivity.this, "생년월일은 6자리로 입력해주세요.");
        } else if (nickName.length() > 0 && isNicknameValid(nickName)) {
            showToast(MemberInitActivity.this, "불건전한 닉네임입니다.\n다른 닉네임을 입력해주세요.");
        } else if (!(isStringValid(nickName) && isStringValid(name))) {
            showToast(MemberInitActivity.this, "닉네임과 이름에는 특수문자를 포함하지 않아야 합니다.");
        } else {
            // 회원정보가 유효한 경우 처리
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();
            final StorageReference mountainImagesRef = storageRef.child("users/"+user.getUid()+"/draftNotice.jpg");

            if (draftPath == null) {
                MemberInfo memberInfo = new MemberInfo(nickName, name, phoneNumber, birthDate, joinDate, dischargeDate, rank);
                uploader(memberInfo);
            } else {
                try {
                    InputStream stream = new FileInputStream(new File(draftPath));
                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                MemberInfo memberInfo = new MemberInfo(nickName, name, phoneNumber, birthDate, joinDate, dischargeDate, rank, downloadUri.toString());
                                uploader(memberInfo);
                            } else {
                                showToast(MemberInitActivity.this, "회원정보 등록에 실패하였습니다.");
                            }
                        }
                    });
                }catch (FileNotFoundException e){
                    showToast(MemberInitActivity.this, "사진을 찾을 수 없습니다.");
                }
            }

        }
    }

    private void uploader(MemberInfo memberInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (user != null) {
            // 여기에 회원 정보를 Firebase에 저장하거나 다른 처리를 수행하는 코드를 추가할 수 있습니다.
            String uid = user.getUid();
            db.collection("users").document(uid).set(memberInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(MemberInitActivity.this, "회원정보 등록에 성공하였습니다.");
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast(MemberInitActivity.this, "회원정보 등록에 실패하였습니다.");
                        }
                    });
        } else {
            showToast(MemberInitActivity.this, "로그인 상태를 확인할 수 없습니다. 다시 로그인해주세요.");
        }
    }

    // 금칙어 필터링 함수
    private boolean isNicknameValid(String nickname) {
        for (String forbiddenWord : forbiddenWords) {
            if (nickname.contains(forbiddenWord)) {
                return true; // 불건전한 닉네임이 포함되어 있음
            }
        }
        return false; // 불건전한 닉네임이 없음
    }
}