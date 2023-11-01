package military.gunbam.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import military.gunbam.R;
import military.gunbam.model.BoardInfo;
import military.gunbam.view.adapter.BoardListAdapter;
import military.gunbam.viewmodel.BoardListViewModel;

public class BoardListFragment extends Fragment {
    private static ProgressBar progressBar;
    private static BoardListViewModel boardListViewModelBasic;
    private static BoardListViewModel boardListViewModelUnit;
    private BoardListAdapter boardListAdapterBasic;
    private BoardListAdapter boardListAdapterUnit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_list, container, false);

        // RecyclerView 및 Adapter 설정
        RecyclerView recyclerViewBasic = view.findViewById(R.id.recyclerView_basic);
        RecyclerView recyclerViewUnit = view.findViewById(R.id.recyclerView_unit);
        progressBar = view.findViewById(R.id.boardProgressBar);

        boardListViewModelBasic = new ViewModelProvider(this).get(BoardListViewModel.class);
        boardListViewModelUnit = new ViewModelProvider(this).get(BoardListViewModel.class);

        // Initialize RecyclerView and its adapter for recyclerView_basic
        boardListAdapterBasic = new BoardListAdapter(getContext(), new ArrayList<>());
        recyclerViewBasic.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewBasic.setAdapter(boardListAdapterBasic);

        // Initialize RecyclerView and its adapter for recyclerView_unit
        boardListAdapterUnit = new BoardListAdapter(getContext(), new ArrayList<>());
        recyclerViewUnit.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUnit.setAdapter(boardListAdapterUnit);

        // Observer for recyclerView_basic
        boardListViewModelBasic.getBoardListLiveData(true).observe(getViewLifecycleOwner(), new Observer<List<BoardInfo>>() {
            @Override
            public void onChanged(List<BoardInfo> boardList) {
                progressBar.setVisibility(View.GONE);
                boardListAdapterBasic.updateData(boardList);
            }
        });

        // Observer for recyclerView_unit
        boardListViewModelUnit.getBoardListLiveData(false).observe(getViewLifecycleOwner(), new Observer<List<BoardInfo>>() {
            @Override
            public void onChanged(List<BoardInfo> boardList) {
                progressBar.setVisibility(View.GONE);
                boardListAdapterUnit.updateData(boardList);
            }
        });

        // Load board data
        progressBar.setVisibility(View.VISIBLE);
        boardListViewModelBasic.loadBoardBasic(true, true); // true: clear, true: isBasic
        boardListViewModelUnit.loadBoardBasic(true, false); // true: clear, false: isBasic

        return view;
    }

    private void myStartActivity(Class c, String field, String boardName) {
        Intent intent = new Intent(getActivity(), c);
        intent.putExtra("field", field);
        intent.putExtra("fieldValue", boardName);
        startActivity(intent);
    }
}