package military.gunbam.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import military.gunbam.R;
import military.gunbam.model.Post.PostInfo;

public class ReadContentsView extends LinearLayout {
    private Context context;
    private LayoutInflater layoutInflater;
    private int moreIndex = -1;

    public ReadContentsView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ReadContentsView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        initView();
    }

    private void initView() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);
        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_post, this, true);
    }

    public void setMoreIndex(int moreIndex) {
        this.moreIndex = moreIndex;
    }

    public void setPostInfo(PostInfo postInfo) {
        TextView createdAtTextView = findViewById(R.id.createAtTextView);
        TextView postPublisherTextView = findViewById(R.id.postPublisherTextView);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(postInfo.getCreatedAt()));

        boolean isAnonymous = postInfo.getIsAnonymous(); // isAnonymous 값 가져오기
        String publisherId = postInfo.getPublisher(); // 게시자의 ID 가져오기

        if (isAnonymous) {
            postPublisherTextView.setText("익명");
        } else {
            // Firebase Firestore 인스턴스 가져오기 (가정)
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // users 컬렉션에서 해당 게시자의 닉네임 조회
            db.collection("users")
                    .document(publisherId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String nickName = document.getString("nickName");
                                postPublisherTextView.setText(nickName);
                            }
                        }
                    });
        }

            LinearLayout contentsLayout = findViewById(R.id.contentsLayout);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ArrayList<String> contentsList = postInfo.getContents();
            ArrayList<String> formatList = postInfo.getFormats();

            for (int i = 0; i < contentsList.size(); i++) {
                if (i == moreIndex) {
                    TextView textView = new TextView(context);
                    textView.setLayoutParams(layoutParams);
                    textView.setText("더보기...");
                    contentsLayout.addView(textView);
                    break;
                }

                String contents = contentsList.get(i);
                String formats = formatList.get(i);

                if (formats.equals("image")) {
                    ImageView imageView = (ImageView) layoutInflater.inflate(R.layout.view_contents_image, this, false);
                    contentsLayout.addView(imageView);
                    Glide.with(this).load(contents).override(1000).thumbnail(0.1f).into(imageView);
                } else {
                    TextView textView = (TextView) layoutInflater.inflate(R.layout.view_contents_text, this, false);

                    // 글의 내용이 2줄 이상인 경우에는 2줄까지만 보여주고 "더보기..." 추가
                    if (contents.split("\n").length > 2) {
                        String[] lines = contents.split("\n", 3); // 3줄까지만 가져옴
                        contents = lines[0] + "\n" + lines[1] + "\n더보기...";
                        int finalI = i;
                        textView.setOnClickListener(v -> {
                            // 클릭 시 전체 내용 표시
                            textView.setText(contentsList.get(finalI));
                            textView.setOnClickListener(null); // 더 이상 클릭 리스너가 필요 없으므로 해제
                        });
                    }

                    // 글의 내용이 50글자 이상인 경우에는 50글자까지만 보여주고 "더보기..." 추가
                    if (contents.length() > 50) {
                        contents = contents.substring(0, 50) + "...\n더보기...";
                        int finalI1 = i;
                        textView.setOnClickListener(v -> {
                            // 클릭 시 전체 내용 표시
                            textView.setText(contentsList.get(finalI1));
                            textView.setOnClickListener(null); // 더 이상 클릭 리스너가 필요 없으므로 해제
                        });
                    }


                    textView.setText(contents);
                    contentsLayout.addView(textView);
                }
            }
        }

    }
