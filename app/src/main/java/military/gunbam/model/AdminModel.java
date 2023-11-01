package military.gunbam.model;

import static military.gunbam.utils.Util.showToast;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import military.gunbam.view.activity.AdminActivity;

public class AdminModel {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public AdminModel(){}

    public void setCollection(String collectionPath, Map<String, Object> postMap, Context context){
        // 게시물 Firestore에 추가
        firestore.collection(collectionPath)
                .add(postMap)
                .addOnSuccessListener(documentReference -> {
                    showToast((Activity) context, "게시물이 추가되었습니다");
                })
                .addOnFailureListener(e -> {
                    showToast((Activity) context, "게시물 추가 실패");
                });
    }

    public void deleteTestPost(){
        CollectionReference postsRef = firestore.collection("posts");

        // "boardName" 필드가 "테스트"인 데이터 찾기
        //Query query = postsRef.whereEqualTo("boardName", "테스트");

        // "contents" 배열에 "이것은 텍스트 내용입니다."를 포함한 데이터 찾기
        Query query = postsRef.whereArrayContains("contents", "이것은 텍스트 내용입니다.");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // 각 문서를 삭제
                        firestore.collection("posts").document(document.getId()).delete();
                    }
                } else {
                    // 오류 처리
                }
            }
        });
    }
    public String getKeyHash(final Context context){
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            if (packageInfo == null)
                return null;

            for (Signature signature : packageInfo.signatures) {
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    return android.util.Base64.encodeToString(md.digest(), android.util.Base64.NO_WRAP);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
