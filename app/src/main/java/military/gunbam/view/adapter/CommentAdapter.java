package military.gunbam.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import military.gunbam.R;
import military.gunbam.model.CommentInfo;
import military.gunbam.view.activity.PostActivity;
import military.gunbam.view.fragment.CommentListFragment;

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
            holder.commentContour.setVisibility(View.GONE);
        } else {
            holder.reply_img.setVisibility(View.GONE);
            holder.replyButton.setVisibility(View.VISIBLE);
            holder.commentContour.setVisibility(View.VISIBLE);
        }

        // 추천이 있을 경우에만 표시
        if (comment.getRecommend().size() > 0) {
            holder.ivRecommend.setVisibility(View.VISIBLE);
            holder.tvRecommendCount.setVisibility(View.VISIBLE);

            holder.tvRecommendCount.setText("" + comment.getRecommend().size());
        } else {
            holder.ivRecommend.setVisibility(View.GONE);
            holder.tvRecommendCount.setVisibility(View.GONE);
        }

        // 댓글 답글 버튼 클릭 시 리스너 호출
        holder.replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParentCommentId = comment.getCommentId();
                PostActivity.ReplyButtonEvent(ParentCommentId);
            }
        });

        holder.recommendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                ArrayList<String> recommend = comment.getRecommend();

                // 이미 추천한 경우
                if (recommend.contains(user)) {
                    // 추천 리스트에서 사용자 삭제
                    recommend.remove(user);

                    // Firebase Firestore 참조
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    // comments 컬렉션에서 해당 문서의 recommend 필드 업데이트
                    db.collection("comments")
                            .document(comment.getId())
                            .update("recommend", recommend)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // 성공적으로 업데이트된 경우
                                    Toast.makeText(context, "추천 취소", Toast.LENGTH_SHORT);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // 업데이트 실패 시 오류 메시지 출력
                                    Toast.makeText(context, "추천 업데이트 중 오류: " + e.getMessage(), Toast.LENGTH_SHORT);
                                }
                            });
                } else {
                    // 추천 리스트에 사용자 추가
                    recommend.add(user);

                    // Firebase Firestore 참조
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    // comments 컬렉션에서 해당 문서의 recommend 필드 업데이트
                    db.collection("comments")
                            .document(comment.getId())
                            .update("recommend", recommend)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // 성공적으로 업데이트된 경우
                                    Toast.makeText(context, "추천 완료", Toast.LENGTH_SHORT);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // 업데이트 실패 시 오류 메시지 출력
                                    Toast.makeText(context, "추천 업데이트 중 오류: " + e.getMessage(), Toast.LENGTH_SHORT);
                                }
                            });
                }
                // 추천이 있을 경우에만 표시
                if (comment.getRecommend().size() > 0) {
                    holder.ivRecommend.setVisibility(View.VISIBLE);
                    holder.tvRecommendCount.setVisibility(View.VISIBLE);

                    holder.tvRecommendCount.setText("" + comment.getRecommend().size());
                } else {
                    holder.ivRecommend.setVisibility(View.GONE);
                    holder.tvRecommendCount.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentContentTextView, commentAuthorTextView, commentUploadTimeTextView, tvRecommendCount;
        ImageView reply_img, commentContour, ivRecommend;
        ImageButton replyButton, recommendButton;
        public CommentViewHolder(View itemView) {
            super(itemView);
            commentContentTextView = itemView.findViewById(R.id.commentContentTextView);
            commentAuthorTextView = itemView.findViewById(R.id.commentAuthorTextView);
            commentUploadTimeTextView = itemView.findViewById(R.id.commentUploadTimeTextView);
            commentContour = itemView.findViewById(R.id.commentContour);
            reply_img = itemView.findViewById(R.id.reply_img);
            replyButton = itemView.findViewById(R.id.replyButton);
            tvRecommendCount = itemView.findViewById(R.id.tvRecommendCount);
            ivRecommend = itemView.findViewById(R.id.ivRecommend);
            recommendButton = itemView.findViewById(R.id.recommendButton);
        }
    }

    public void updateData(List<CommentInfo> newCommentList) {
        commentList.clear();
        commentList.addAll(newCommentList);
        notifyDataSetChanged();
    }
}