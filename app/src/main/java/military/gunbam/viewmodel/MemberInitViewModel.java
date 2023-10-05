package military.gunbam.viewmodel;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

import military.gunbam.model.member.MemberInfo;
import military.gunbam.model.member.MemberModel;

public class MemberInitViewModel extends ViewModel {
    private FirebaseUser user;
    private MemberModel memberModel = new MemberModel();
    String errorMessage = "";
    String uploadMessage= "";
    //private MutableLiveData<MemberUploadResult> uploadResultMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isUploaded = new MutableLiveData<>();
    private MutableLiveData<String> toastMessage = new MutableLiveData<>();
    public MutableLiveData<String> getToastMessage() {
        return toastMessage;
    }

    public String getUploadMessage() {
        return uploadMessage;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public LiveData<Boolean> getIsUploaded() {
        return isUploaded;
    }

    public boolean isStringValid(String input) {
        return memberModel.isStringValid(input);
    }

    public void profileUpdate(String nickName,String name, String phoneNumber, String birthDate, String joinDate, String dischargeDate, String rank, String draftPath){

        // 유효성 검사
        if (nickName.length() == 0 || name.length() == 0 || phoneNumber.length() < 0 || birthDate.length() < 0) {
            errorMessage = "모든 회원정보를 입력해주세요.";
        } else if (joinDate.length() != 6 && joinDate.length() != 0) {
            errorMessage = "올바르지 않은 입대일입니다.\nex)230101";
        } else if (dischargeDate.length() != 6 && dischargeDate.length() != 0) {
            errorMessage ="올바르지 않은 제대일입니다.\nex)250101";
        } else if (phoneNumber.length() > 11 || phoneNumber.length() < 10) {
            errorMessage = "유효하지 않은 전화번호입니다.\n'-' 없이 숫자만 입력해주세요.";
        } else if (birthDate.length() != 6) {
            errorMessage = "생년월일은 6자리로 입력해주세요.";
        } else if (isNicknameValid(nickName)) {
            errorMessage = "불건전한 닉네임입니다.\n다른 닉네임을 입력해주세요.";
        } else if (!isStringValid(name)) {
            errorMessage = "닉네임과 이름에는 특수문자를 포함하지 않아야 합니다.";
        } else if (nickName.length() >= 10 ||  name.length() >= 10) {
            errorMessage = "닉네임 또는 이름은 최대 10자까지 가능합니다.";
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
                                errorMessage = "회원정보 등록에 실패하였습니다.";
                            }
                        }
                    });
                }catch (FileNotFoundException e){
                    errorMessage = "사진을 찾을 수 없습니다.";
                }
            }

        }
        toastMessage.setValue(errorMessage);
    }

    public void uploader(MemberInfo memberInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (user != null) {
            String uid = user.getUid();
            db.collection("users").document(uid).set(memberInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            uploadMessage = "회원정보 등록에 성공하였습니다.";
                            isUploaded.setValue(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            uploadMessage = "회원정보 등록에 실패하였습니다.";
                            isUploaded.setValue(false);
                        }
                    });
        } else {
            uploadMessage = "로그인 상태를 확인할 수 없습니다. 다시 로그인해주세요.";
            isUploaded.setValue(false);
        }
    }

    public boolean isNicknameValid(String nickname) {
        return memberModel.isNicknameValid(nickname);
    }

}
