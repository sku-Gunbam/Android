package military.gunbam.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import military.gunbam.R;
import military.gunbam.model.CommentInfo;
import military.gunbam.view.activity.PostActivity;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<CommentInfo> commentList;
    private Context context;
    String ParentCommentId;
    String TAG = "CommentAdapter";

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

        // Check if parent comment id is not null, then show reply view
        if (comment.getParentCommentId() != null) {
            holder.reply_img.setVisibility(View.VISIBLE);
            holder.replyButton.setVisibility(View.GONE);
        } else {
            holder.reply_img.setVisibility(View.GONE);
            holder.replyButton.setVisibility(View.VISIBLE);
        }
        // 댓글 답글 버튼 클릭 시 리스너 호출
        holder.replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParentCommentId = comment.getCommentId();
                PostActivity.ReplyButtonEvent(ParentCommentId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentContentTextView, commentAuthorTextView, commentUploadTimeTextView;
        ImageView reply_img;
        ImageButton replyButton;
        public CommentViewHolder(View itemView) {
            super(itemView);
            commentContentTextView = itemView.findViewById(R.id.commentContentTextView);
            commentAuthorTextView = itemView.findViewById(R.id.commentAuthorTextView);
            commentUploadTimeTextView = itemView.findViewById(R.id.commentUploadTimeTextView);
            reply_img = itemView.findViewById(R.id.reply_img);
            replyButton = itemView.findViewById(R.id.replyButton);
        }
    }

    public void updateData(List<CommentInfo> newCommentList) {
        commentList.clear();
        commentList.addAll(newCommentList);
        notifyDataSetChanged();
    }
}