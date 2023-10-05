package military.gunbam.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private MutableLiveData<Boolean> userLoggedIn = new MutableLiveData<>();
    private MutableLiveData<Boolean> userHasInfo = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<Boolean> getUserHasInfo() {
        return userHasInfo;
    }
    public LiveData<Boolean> getUserLoggedIn() {
        return userLoggedIn;
    }

    public void checkUserLoginStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            userLoggedIn.setValue(false);
            userHasInfo.setValue(false);
        } else {
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            userLoggedIn.setValue(true);
                            userHasInfo.setValue(true);
                        } else {
                            userLoggedIn.setValue(true);
                            userHasInfo.setValue(false);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }
}