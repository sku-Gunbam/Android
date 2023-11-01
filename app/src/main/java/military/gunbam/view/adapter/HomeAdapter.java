package military.gunbam.view.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import military.gunbam.R;
import military.gunbam.listener.OnPostListener;
import military.gunbam.model.Post.PostInfo;
import military.gunbam.FirebaseHelper;
import military.gunbam.view.ReadContentsView;
import military.gunbam.view.activity.PostActivity;
import military.gunbam.view.activity.WritePostActivity;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MainViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    private FirebaseHelper firebaseHelper;
    private final int MORE_INDEX = 2;
    String TAG = "HomeAdapter";

    static class MainViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        MainViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public void setPostList(ArrayList<PostInfo> postList) {
        this.mDataset = postList;
        notifyDataSetChanged();
    }

    public HomeAdapter(Activity activity, ArrayList<PostInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;

        firebaseHelper = new FirebaseHelper(activity);
    }

    public void setOnPostListener(OnPostListener onPostListener){
        firebaseHelper.setOnPostListener(onPostListener);
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        final MainViewHolder mainViewHolder = new MainViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PostActivity.class);
                intent.putExtra("postInfo", mDataset.get(mainViewHolder.getAdapterPosition()));
                activity.startActivity(intent);
            }
        });

        cardView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, mainViewHolder.getAdapterPosition());
            }
        });

        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MainViewHolder holder, int position) {
        CardView cardView = holder.cardView;

        TextView titleTextView, tvRecommendCount, tvCommentCount;
        ImageView ivRecommend, ivComment;

        titleTextView = cardView.findViewById(R.id.postTitleTextView);
        tvRecommendCount = cardView.findViewById(R.id.tvRecommendCount);
        tvCommentCount = cardView.findViewById(R.id.tvCommentCount);
        ivComment = cardView.findViewById(R.id.ivComment);
        ivRecommend = cardView.findViewById(R.id.ivRecommend);

        PostInfo postInfo = mDataset.get(position);
        titleTextView.setText(postInfo.getTitle());

        // 추천이 있을 경우에만 표시
        if (postInfo.getRecommend().size() > 0) {
            tvRecommendCount.setVisibility(View.VISIBLE);
            ivRecommend.setVisibility(View.VISIBLE);
            tvRecommendCount.setText("" + postInfo.getRecommend().size());
        } else {
            tvRecommendCount.setVisibility(View.GONE);
            ivRecommend.setVisibility(View.GONE);
        }
        countCommentsWithId(postInfo.getId(),tvCommentCount, ivComment);

        ReadContentsView readContentsVIew = cardView.findViewById(R.id.readContentsView);
        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);

        if (mDataset.get(holder.getAdapterPosition()).getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            cardView.findViewById(R.id.menu).setVisibility(View.VISIBLE);
        } else {
            cardView.findViewById(R.id.menu).setVisibility(View.GONE);
        }

        if (contentsLayout.getTag() == null || !contentsLayout.getTag().equals(postInfo)) {
            contentsLayout.setTag(postInfo);
            contentsLayout.removeAllViews();

            readContentsVIew.setMoreIndex(MORE_INDEX);
            readContentsVIew.setPostInfo(postInfo);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void showPopup(View v, final int position) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.modify:
                        myStartActivity(WritePostActivity.class, mDataset.get(position));
                        return true;
                    case R.id.delete:
                        firebaseHelper.storageDelete(mDataset.get(position));
                        return true;
                    default:
                        return false;
                }
            }
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post, popup.getMenu());
        popup.show();
    }

    public static void countCommentsWithId(String postId, TextView tvCommentCount, ImageView ivComment) {
        // Firestore 인스턴스 얻기
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 'comments' 컬렉션 참조
        CollectionReference commentsRef = db.collection("comments");

        // postInfo.getId()와 같은 commentId를 가진 문서를 찾기 위한 쿼리
        Query query = commentsRef.whereEqualTo("commentId", postId);

        // 쿼리 실행
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // 쿼리 결과로부터 문서 개수 가져오기
                    int commentCount = task.getResult().size();

                    // 댓글이 있을 경우에만 표시
                    if (commentCount > 0) {
                        tvCommentCount.setVisibility(View.VISIBLE);
                        tvCommentCount.setText("" + commentCount);
                        ivComment.setVisibility(View.VISIBLE);
                    } else {
                        tvCommentCount.setVisibility(View.GONE);
                        ivComment.setVisibility(View.GONE);
                    }
                } else {
                    // 쿼리 실패 시 예외 처리
                    Exception e = task.getException();
                    if (e != null) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void myStartActivity(Class c, PostInfo postInfo) {
        Intent intent = new Intent(activity, c);
        intent.putExtra("boardName", postInfo.getBoardName());
        intent.putExtra("postInfo", postInfo);
        activity.startActivity(intent);
    }
}