package military.gunbam.viewmodel;

import android.widget.Button;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import military.gunbam.view.activity.BoardListActivity;

public class MainBoardListViewModel extends ViewModel {
    private MutableLiveData<String> selectedBoardName = new MutableLiveData<>();

    public LiveData<String> getSelectedBoardName() {
        return selectedBoardName;
    }

    public void navigateToBoardListActivity(String field, String boardName) {
        selectedBoardName.setValue(boardName);
    }
}