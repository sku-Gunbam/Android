package military.gunbam.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class WritePostViewModel extends ViewModel {

    private MutableLiveData<String> titleLiveData = new MutableLiveData<>();
    private MutableLiveData<String> contentsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> anonymousLiveData = new MutableLiveData<>();

    // 타이틀 설정
    public void setTitle(String title) {
        titleLiveData.setValue(title);
    }

    // 타이틀 가져오기
    public MutableLiveData<String> getTitleLiveData() {
        return titleLiveData;
    }

    // 내용 설정
    public void setContents(ArrayList<String> contents) {
        contentsLiveData.postValue(String.valueOf(contents));
    }

    // 내용 가져오기
    public MutableLiveData<String> getContentsLiveData() {
        return contentsLiveData;
    }

    // 익명 여부 설정
    public void setAnonymous(boolean isAnonymous) {
        anonymousLiveData.setValue(isAnonymous);
    }

    // 익명 여부 가져오기
    public MutableLiveData<Boolean> getAnonymousLiveData() {
        return anonymousLiveData;
    }

}


