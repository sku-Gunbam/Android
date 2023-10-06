package military.gunbam.view.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import military.gunbam.R;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import military.gunbam.model.ChatData;

public class ChattingAdapter extends RecyclerView.Adapter<ChattingAdapter.ViewHolder> {
    //public static FirebaseLogin firebaseClass = new FirebaseLogin();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private List<ChatData> chatDataList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        public void onItemClick(View view);
    }

    public ChattingAdapter(ArrayList<ChatData> chatDataList, OnItemClickListener onItemClickListener) {
        this.chatDataList = chatDataList;
        this.onItemClickListener = onItemClickListener;
    }


    public void setData(List<ChatData> chatDataList) {
        this.chatDataList = chatDataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ViewHolder viewHolder = (ViewHolder) holder;
        Log.d("뷰홀더 이메일", chatDataList.get(position).getUserEmail());
        Log.d("뷰홀더 메시지", chatDataList.get(position).getMessage());
        Log.d("뷰홀더 유저이름", chatDataList.get(position).getUserName());
        Log.d("뷰홀더 보낸사람id", chatDataList.get(position).getSenderUid());
        //Log.d("뷰홀더 보낸시간", chatDataList.get(position).getSending_time());


        // 자신인 경우 - 오른쪽 정렬, 왼쪽이미지, 오른쪽 시간 안보임
        if (chatDataList.get(position).getUserEmail().equals(currentUser.getEmail())) {
            Log.d("ChattingAdapter", "자신인 경우");
            viewHolder.messageTextView.setText(chatDataList.get(position).getMessage());
            viewHolder.messageTextView.setGravity(Gravity.END);
            viewHolder.message_line.setGravity(Gravity.END);

            viewHolder.senderTextView.setVisibility(View.INVISIBLE); // 자신의 아이디 안보임
            viewHolder.timestampTextView.setText(convertTimeToString(chatDataList.get(position).getTimestamp()));
            //viewHolder.timestampTextView.setText("TEST2");
            viewHolder.whole_message.setGravity(Gravity.END);

            viewHolder.left_image.setVisibility(View.GONE);

            viewHolder.cv.setBackgroundColor(Color.parseColor("#FFFFE95C"));


        }
        // 상대방인경우 - 왼쪽 정렬, 오른쪽이미지, 왼쪽 시간 안보임
        else {
            Log.d("ChattingAdapter", "상대방인 경우");
            viewHolder.messageTextView.setText(chatDataList.get(position).getMessage());
            viewHolder.senderTextView.setGravity(Gravity.START);
            viewHolder.senderTextView.setText(chatDataList.get(position).getUserName());
            //viewHolder.senderTextView.setText("TEST");
            viewHolder.timestampTextView.setText(convertTimeToString(chatDataList.get(position).getTimestamp()));
            viewHolder.whole_message.setGravity(Gravity.START);
            viewHolder.cv.setBackgroundColor(Color.WHITE);

            //상대방 이미지를 클릭했을 시 상대방 정보를 띄워준다.
            viewHolder.left_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v);
                }
            });

        }


    }


    @Override
    public int getItemCount() {
        return chatDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView messageTextView;
        private TextView senderTextView;
        private TextView timestampTextView;
        private ImageView left_image;
        private LinearLayout whole_message;
        private LinearLayout message_line;
        private CardView cv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            senderTextView = itemView.findViewById(R.id.ch_id);
            timestampTextView = itemView.findViewById(R.id.ch_time);
            left_image = itemView.findViewById(R.id.ch_left_image);
            whole_message = itemView.findViewById(R.id.whole_message);
            message_line = itemView.findViewById(R.id.message_line);
            cv = itemView.findViewById(R.id.chat_bubble);
        }

        public void bind(ChatData chatData) {
            messageTextView.setText(chatData.getMessage());
            senderTextView.setText(chatData.getSenderUid());
            timestampTextView.setText(String.valueOf(chatData.getTimestamp()));
        }
    }

    public static String convertTimeToString(long time) {
        LocalDateTime date = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = Instant.ofEpochMilli(time)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        String formattedDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formattedDate = date.format(DateTimeFormatter.ofPattern("a hh:mm"));
        }

        return formattedDate;
    }


}