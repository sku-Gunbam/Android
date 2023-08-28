package military.gunbam.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import military.gunbam.R;
import military.gunbam.model.CommentInfo;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<CommentInfo> commentList;
    private Context context;

    public CommentAdapter(Context context, List<CommentInfo> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentInfo comment = commentList.get(position);

        holder.commentContentTextView.setText(comment.getCommentContent());
        holder.commentAuthorTextView.setText(comment.getCommentAuthor());

        // Convert Timestamp to desired date format (e.g., "yyyy-MM-dd HH:mm:ss")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedUploadTime = sdf.format(comment.getCommentUploadTime().toDate());
        holder.commentUploadTimeTextView.setText(formattedUploadTime);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentContentTextView, commentAuthorTextView, commentUploadTimeTextView;

        public CommentViewHolder(View itemView) {
            super(itemView);
            commentContentTextView = itemView.findViewById(R.id.commentContentTextView);
            commentAuthorTextView = itemView.findViewById(R.id.commentAuthorTextView);
            commentUploadTimeTextView = itemView.findViewById(R.id.commentUploadTimeTextView);
        }
    }
}