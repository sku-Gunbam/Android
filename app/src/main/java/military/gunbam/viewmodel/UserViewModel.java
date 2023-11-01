package military.gunbam.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserViewModel extends ViewModel {
    private MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();

    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }
    public void loadCurrentUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        currentUser.setValue(user);
    }

}
