package military.gunbam.view.adapter;

import android.content.Intent;
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
import military.gunbam.model.WriteInfo;
import military.gunbam.view.activity.ViewPostActivity;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<WriteInfo> postList;
    private static final String TAG = "PostAdapter";

    public PostAdapter(List<WriteInfo> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        WriteInfo post = postList.get(position);

        holder.postTitleTextView.setText(post.getTitle());
        holder.postContentPreviewTextView.setText(post.getContents());
        holder.postPublisherTextView.setText(post.getPublisher());

        // Convert Timestamp to desired date format (e.g., "yyyy-MM-dd HH:mm:ss")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedUploadTime = sdf.format(post.getUploadTime().toDate());
        holder.postUploadTimeTextView.setText(formattedUploadTime);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postId = post.getId();
                Intent intent = new Intent(view.getContext(), ViewPostActivity.class);
                intent.putExtra("postId", postId); // Assuming you have a method to get postId
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postTitleTextView, postContentPreviewTextView, postPublisherTextView, postUploadTimeTextView;

        public PostViewHolder(View itemView) {
            super(itemView);
            postTitleTextView = itemView.findViewById(R.id.postTitleTextView);
            postContentPreviewTextView = itemView.findViewById(R.id.postContentPreviewTextView);
            postPublisherTextView = itemView.findViewById(R.id.postPublisherTextView);
            postUploadTimeTextView = itemView.findViewById(R.id.postUploadTimeTextView);
        }
    }
}