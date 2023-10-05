package military.gunbam.viewmodel;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PostViewModelFactory implements ViewModelProvider.Factory {
    private final Activity mActivity;

    public PostViewModelFactory(Activity activity) {
        mActivity = activity;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PostViewModel.class)) {
            return (T) new PostViewModel(mActivity);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

