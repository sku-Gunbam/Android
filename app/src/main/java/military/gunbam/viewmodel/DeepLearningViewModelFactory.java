package military.gunbam.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class DeepLearningViewModelFactory implements ViewModelProvider.Factory {
    private Context context;
    private String modelPath;

    public DeepLearningViewModelFactory(Context context, String modelPath) {
        this.context = context;
        this.modelPath = modelPath;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DeepLearningViewModel.class)) {
            return (T) new DeepLearningViewModel(context, modelPath);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
