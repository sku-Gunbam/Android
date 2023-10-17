package military.gunbam.model.Post;

import static military.gunbam.utils.Util.showToast;
import static military.gunbam.utils.Util.storageUrlToName;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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

import military.gunbam.view.activity.WritePostActivity;

public class PostModel {
    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;
    int successCount= 0,pathCount=0;
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
        postInfo.setPublisher(user.getUid());
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
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            successCount--;
                            contentsList.set(index, uri.toString());
                            if (successCount == 0) {
                                PostInfo successPostInfo = postInfo; //new PostInfo(title, contentsList, formatList, user.getUid(), date, isAnonymous, recommendationCount, boardName);
                                storeUpload(successPostInfo,voidOnSuccessListener,onFailureListener);
                            }
                        }
                    });
                }
            });
        } catch (FileNotFoundException e) {
            Log.e("로그", "에러: " + e.toString());
        }
    }
    public void uploadImage(ArrayList<String> contentsList,byte[] data, PostInfo postInfo, OnSuccessListener<Void> voidOnSuccessListener, OnFailureListener onFailureListener){
        Log.d("PostModel 테스트 docum",documentReference.getId());
        Log.d("PostModel 테스트 pathCount",Integer.toString(pathCount));
        StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + ".jpg");
        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.getResult();
        Log.d("PostModel 테스트",uploadTask.toString());

        postInfo.setPublisher(user.getUid());
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // 실패 처리
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        successCount--;
                        contentsList.set(index, uri.toString());
                        if (successCount == 0) {
                            PostInfo successPostInfo = postInfo;//new PostInfo(title, contentsList, formatList, user.getUid(), date, isAnonymous, recommendationCount, boardName);
                            storeUpload(successPostInfo,voidOnSuccessListener,onFailureListener);
                        }
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
    public void storageUpload(ArrayList<String> contentsList, byte[] data, PostInfo postInfo, OnSuccessListener<Void> successListener, OnFailureListener failureListener){
        // Firebase Storage에 업로드
        StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + ".jpg");
        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // 실패 처리
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // 성공 처리
                final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        successCount--;
                        contentsList.set(index, uri.toString());
                        if (successCount == 0) {
                            PostInfo successPostInfo = postInfo;//new PostInfo(title, contentsList, formatList, user.getUid(), date, isAnonymous, recommendationCount, boardName);
                            postInfo.setPublisher(user.getUid());
                            storeUpload(successPostInfo,successListener,failureListener);
                        }
                    }
                });
            }
        });
    }
    public void deletePost(String path, PostInfo postInfo, OnSuccessListener<Void> voidOnSuccessListener, OnFailureListener onFailureListener){
        StorageReference desertRef = storageRef.child("posts/" + postInfo.getId() + "/" + storageUrlToName(path));
        desertRef.delete().addOnSuccessListener(voidOnSuccessListener).addOnFailureListener(onFailureListener);
    }

}
