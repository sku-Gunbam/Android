package military.gunbam.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

import military.gunbam.model.AdminModel;

public class AdminViewModel extends ViewModel {
    private AdminModel adminModel = new AdminModel();
    private MutableLiveData<String> keyHashLiveData =new MutableLiveData<>();

    public void setCollection(String collectionPath, Map<String, Object> postMap, Context context){
        adminModel.setCollection(collectionPath,postMap,context);
    }

    public void deleteTestPost(){
        adminModel.deleteTestPost();
    }
    public void loadKeyHash(final Context context){
        String hash = adminModel.getKeyHash(context);
        keyHashLiveData.setValue(hash);
    }
    public LiveData<String> getKeyHash(){
        return keyHashLiveData;
    }
}
