package military.gunbam.view.adapter;

import static military.gunbam.utils.Util.INTENT_PATH;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import military.gunbam.R;
import military.gunbam.viewmodel.DeepLearningViewModel;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private ArrayList<String> mDataset;
    private Activity activity;
    private DeepLearningViewModel deepLearningViewModel;

    static int cnt = 0;
    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;

        public GalleryViewHolder(CardView view) {
            super(view);
            cardView = view;
        }
    }

    public GalleryAdapter(Activity galleryActivity, ArrayList<String> myDataSet) {
        mDataset = myDataSet;
        this.activity = galleryActivity;
    }

    @Nonnull
    @Override
    public GalleryAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gallery, viewGroup, false);

        final GalleryViewHolder galleryViewHolder = new GalleryViewHolder(cardView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(INTENT_PATH,mDataset.get(galleryViewHolder.getAdapterPosition()));
                activity.setResult(Activity.RESULT_OK, resultIntent);
                activity.finish();
            }
        });

        return galleryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final GalleryViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        ImageView imageView = cardView.findViewById(R.id.galleryItemImageView);

        Log.d("Glide:","posit:"+position+"mData:"+mDataset.get(position));
        Glide.with(activity)
                .load(mDataset.get(position))
                .centerCrop()
                .override(500)
                .into(imageView);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
