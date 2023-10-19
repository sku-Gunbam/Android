package military.gunbam.model.Post;

import static military.gunbam.utils.Util.showToast;
import static military.gunbam.utils.Util.storageUrlToName;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import military.gunbam.FirebaseHelper;
import military.gunbam.model.CommentInfo;
import military.gunbam.model.SuccessCountSingleton;
import military.gunbam.view.activity.PostActivity;
import military.gunbam.view.activity.WritePostActivity;

public class PostModel {
    private FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private int pathCount = 0;
    private SuccessCountSingleton successCountSingleton = SuccessCountSingleton.getInstance();
    private final static String key = "index";
    public PostModel(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void setDocumentReference(String collectionPath){
        documentReference = firebaseFirestore.collection(collectionPath).document();
    }
    public void setDocumentReference(String collectionPath, String documentID){
        documentReference = firebaseFirestore.collection(collectionPath).document(documentID);
    }
    public void processText(String path,ArrayList<String> pathList, ArrayList<String> contentsList, ArrayList<String> formatList, PostInfo postInfo, OnSuccessListener<Void> voidOnSuccessListener, OnFailureListener onFailureListener) {
        formatList.add("text");
        String[] pathArray = path.split("\\.");
        StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + "." + pathArray[pathArray.length - 1]);

        try {
            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentsList.size() - 1)).build();
            UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // 실패 처리
                    Log.d("PostModel 텍스트 업로드","실패");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            successCountSingleton.decreaseSuccessCount();
                            contentsList.set(index, uri.toString());
                            if (successCountSingleton.getSuccessCount() == 0) {
                                PostInfo successPostInfo = postInfo; //new PostInfo(title, contentsList, formatList, user.getUid(), date, isAnonymous, recommendationCount, boardName);
                                storeUpload(successPostInfo,voidOnSuccessListener,onFailureListener);
                                Log.d("PostModel 텍스트 업로드","1차");
                            }
                            Log.d("PostModel 텍스트 업로드","2차");
                        }
                    });
                }
            });
        } catch (FileNotFoundException e) {
            Log.e("PostModel", "에러: " + e.toString());
        }
    }
    public void uploadImage(ArrayList<String> contentsList,byte[] data, PostInfo postInfo, OnSuccessListener<Void> voidOnSuccessListener, OnFailureListener onFailureListener){
        StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + ".jpg");
        StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentsList.size() - 1)).build();
        UploadTask uploadTask = mountainImagesRef.putBytes(data,metadata);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // 실패 처리
                Log.d("PostModel 이미지 업로드","실패");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata(key));
                mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        successCountSingleton.decreaseSuccessCount();
                        contentsList.set(index, uri.toString());
                        if (successCountSingleton.getSuccessCount() == 0) {
                            PostInfo successPostInfo = postInfo;//new PostInfo(title, contentsList, formatList, user.getUid(), date, isAnonymous, recommendationCount, boardName);
                            storeUpload(successPostInfo,voidOnSuccessListener,onFailureListener);
                            Log.d("PostModel 이미지 업로드","1차");
                        }
                        Log.d("PostModel 이미지 업로드","2차");
                    }
                });
            }
        });
    }
    public void storeUpload(PostInfo postInfo, OnSuccessListener<Void> voidOnSuccessListener, OnFailureListener onFailureListener){
        documentReference.set(postInfo.getPostInfo())
                .addOnSuccessListener(voidOnSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void deletePost(String path, PostInfo postInfo, OnSuccessListener<Void> voidOnSuccessListener, OnFailureListener onFailureListener){
        StorageReference desertRef = storageRef.child("posts/" + postInfo.getId() + "/" + storageUrlToName(path));
        desertRef.delete().addOnSuccessListener(voidOnSuccessListener).addOnFailureListener(onFailureListener);
    }
    public void countCommentsWithId(String postId, TextView tvCommentCount){

        // 'comments' 컬렉션 참조
        CollectionReference commentsRef = firebaseFirestore.collection("comments");

        // postInfo.getId()와 같은 commentId를 가진 문서를 찾기 위한 쿼리
        Query query = commentsRef.whereEqualTo("commentId", postId);

        // 쿼리 실행
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // 쿼리 결과로부터 문서 개수 가져오기
                    int commentCount = task.getResult().size();

                    // 결과 사용 예시
                    // count 값을 원하는 대로 활용하면 됩니다.
                    // 예: TextView에 출력하거나 다른 처리 수행
                    System.out.println("Comment count for postId " + postId + ": " + commentCount);
                    tvCommentCount.setText("" + commentCount);
                } else {
                    // 쿼리 실패 시 예외 처리
                    Exception e = task.getException();
                    if (e != null) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void writeComments(CommentInfo newComment, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener){
        CollectionReference commentsCollection = firebaseStoreCollection("comments");
        commentsCollection.add(newComment)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }
    public CollectionReference firebaseStoreCollection(String comment) {
        return firebaseFirestore.collection(comment);
    }
    public void deleteRecommend(String collectionPath, String postId, String filed, ArrayList<String> recommend, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener){
        firebaseFirestore.collection(collectionPath)
                .document(postId)
                .update(filed, recommend)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);

    }
    public void addRecommend(String collectionPath, String postId, String filed, ArrayList<String> recommend, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener){
        // posts 컬렉션에서 해당 문서의 recommend 필드 업데이트
        firebaseFirestore.collection(collectionPath)
                .document(postId)
                .update(filed, recommend)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }
}
