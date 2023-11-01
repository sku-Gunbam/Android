package military.gunbam.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

import military.gunbam.FirebaseHelper;
import military.gunbam.model.BoardInfo;

public class BoardListViewModel extends ViewModel {
    private boolean loadingBasic;
    private boolean loadingUnit;
    private final String TAG = "BoardListViewModel";

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private MutableLiveData<ArrayList<BoardInfo>> boardListLiveDataBasic = new MutableLiveData<>();
    private MutableLiveData<ArrayList<BoardInfo>> boardListLiveDataUnit = new MutableLiveData<>();
    private boolean updating;
    private FirebaseHelper firebaseHelper;

    private MutableLiveData<Boolean> isAdmin = new MutableLiveData<>();

    public BoardListViewModel() {
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        boardListLiveDataBasic.setValue(new ArrayList<>());
        boardListLiveDataUnit.setValue(new ArrayList<>());
    }

    public LiveData<ArrayList<BoardInfo>> getBoardListLiveData(boolean isBasic) {
        return isBasic ? boardListLiveDataBasic : boardListLiveDataUnit;
    }

    public void loadBoardBasic(boolean clear, boolean isBasic) {
        if ((isBasic && loadingBasic) || (!isBasic && loadingUnit)) {
            return; // 이미 데이터를 로딩 중이면 무시
        }

        if (isBasic) {
            loadingBasic = true;
        } else {
            loadingUnit = true;
        }
        String documentName = isBasic ? "boardBasic" : "boardUnit";
        Log.d(TAG, "loadBoardBasic - Document Name: " + documentName);

        DocumentReference documentReference = firebaseFirestore.collection("boards")
                .document(documentName);

        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (clear) {
                    ArrayList<BoardInfo> newBoardList = new ArrayList<>();
                    if (isBasic) {
                        boardListLiveDataBasic.setValue(newBoardList);
                    } else {
                        boardListLiveDataUnit.setValue(newBoardList);
                    }
                }

                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ArrayList<BoardInfo> newBoardList = new ArrayList<>();
                    ArrayList<String> boardListData = (ArrayList<String>) document.getData().get("boardList");

                    if (boardListData != null) {
                        for (String item : boardListData) {
                            newBoardList.add(new BoardInfo(item));
                        }

                        if (isBasic) {
                            boardListLiveDataBasic.setValue(newBoardList);
                        } else {
                            boardListLiveDataUnit.setValue(newBoardList);
                        }
                    }
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "Error getting document: ", task.getException());
            }
            if (isBasic) {
                loadingBasic = false;
            } else {
                loadingUnit = false;
            }
        });
    }
}