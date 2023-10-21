package military.gunbam.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import military.gunbam.R;
import military.gunbam.view.activity.BoardListActivity;
import military.gunbam.viewmodel.MainBoardListViewModel;

public class mainBoardListFragment extends Fragment {
    private MainBoardListViewModel viewModel;
    private ViewGroup viewGroup;
    private String TAG = "MainBoardListFragment";
    private final String field = "boardName";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_main_board_list, container, false);

        viewModel = new ViewModelProvider(this).get(MainBoardListViewModel.class);

        for (int i = 1; i <= 5; i++) {
            int resourceId = getResources().getIdentifier("main_to_board_" + i, "id", getContext().getPackageName());
            Button button = viewGroup.findViewById(resourceId);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleButtonClick(button.getText().toString().replace("\n", ""));
                }
            });
        }

        return viewGroup;
    }

    private void handleButtonClick(String boardName) {
        viewModel.navigateToBoardListActivity(field, boardName);
        myStartActivity(BoardListActivity.class, field, boardName);
        Log.e(TAG, "" + boardName);
    }

    private void myStartActivity(Class c, String field, String boardName) {
        Intent intent = new Intent(getActivity(), c);
        intent.putExtra("field", field);
        intent.putExtra("fieldValue", boardName);
        startActivity(intent);
    }
}