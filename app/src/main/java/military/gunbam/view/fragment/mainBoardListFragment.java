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

import military.gunbam.R;
import military.gunbam.view.activity.BoardListActivity;

public class mainBoardListFragment extends Fragment {
    ViewGroup viewGroup;
    String TAG = "mainBoardListFragment";
    final String field = "boardName";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_main_board_list, container,false);

        for (int i = 1; i <= 5; i++) {
            int resourceId = getResources().getIdentifier("main_to_board_" + i, "id", getContext().getPackageName());
            Button Button = viewGroup.findViewById(resourceId);

            Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myStartActivity(BoardListActivity.class, field, Button.getText().toString().replace("\n", ""));
                    Log.e(TAG, "" + Button.getText().toString());
                }
            });
        }

        return viewGroup;
    }

    private void myStartActivity(Class c, String field, String boardName) {
        Intent intent = new Intent(getActivity(), c);
        intent.putExtra("field", field);
        intent.putExtra("fieldValue", boardName);
        startActivity(intent);
    }
}