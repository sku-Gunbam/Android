package military.gunbam.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import military.gunbam.R;
import military.gunbam.model.BoardInfo;
import military.gunbam.model.Post.PostInfo;
import military.gunbam.view.activity.PostListActivity;

public class BoardListAdapter extends RecyclerView.Adapter<BoardListAdapter.BoardViewHolder> {
    private List<BoardInfo> boardList;
    private Context context;
    String TAG = "BoardListAdapter";

    public BoardListAdapter(Context context, List<BoardInfo> boardList) {
        this.context = context;
        this.boardList = boardList;
    }

    public void setBoardList(ArrayList<BoardInfo> boardList) {
        this.boardList = boardList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }


    @NonNull
    @Override
    public BoardListAdapter.BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_board, parent, false);

        final BoardListAdapter.BoardViewHolder boardViewHolder = new BoardListAdapter.BoardViewHolder(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = boardViewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, PostListActivity.class);

                    intent.putExtra("field", "boardName");
                    intent.putExtra("fieldValue", boardList.get(position).getboardTitle());
                    context.startActivity(intent);
                }
            }
        });

        return boardViewHolder;
    }
    
    @Override
    public int getItemCount() {
        return boardList.size();
    }
    
    @Override
    public void onBindViewHolder(@NonNull BoardViewHolder holder, int position) {
        BoardInfo boardInfo = boardList.get(position);

        holder.tvBoardTitle.setText(boardInfo.getboardTitle());

    }
    public class BoardViewHolder extends RecyclerView.ViewHolder {
        TextView tvBoardTitle;
        public BoardViewHolder(View itemView) {
            super(itemView);
            tvBoardTitle = itemView.findViewById(R.id.tvBoardTitle);
        }
    }

    public void updateData(List<BoardInfo> newBoardList) {
        boardList.clear();
        boardList.addAll(newBoardList);
        notifyDataSetChanged();
    }

}