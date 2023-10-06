package military.gunbam.view.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.C;

import military.gunbam.R;

import java.util.ArrayList;
import java.util.List;

import military.gunbam.model.ChatData;
import military.gunbam.model.login.LoginResult;
import military.gunbam.view.adapter.ChattingAdapter;
import military.gunbam.viewmodel.ChattingViewModel;


public class ChattingActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ChattingAdapter adapter;
    private ChattingViewModel chattingViewModel;
    private String userID;
    private String roomID;
    ArrayList<ChatData> chatDataList = new ArrayList<>();

    private String senderUid;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();

    private static final String ADMINISTRATOR_ID1 = "enddl3788@naver.com";
    private static final String ADMINISTRATOR_ID2 = "rickyhee75@naver.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);


        String userEmail="";
        if(currentUser==null){
            Log.d("유저","널임");
        }
        else{
            userEmail = currentUser.getEmail();
        }
        if(userEmail == ADMINISTRATOR_ID1) {
            // 1. 관리자면 자바코드로 xml 텍스트뷰 하나 보이게 해서 유저 아이디 검색할 수 있도록.
            // 2. 아니면 관리자가 열람하고 싶은 유저 이메일 직접 검색해서 입력
            roomID = getEmailSliceToID(ADMINISTRATOR_ID1);
        }
        else {
            roomID = getEmailSliceToID(userEmail);
        }
        recyclerView = findViewById(R.id.recyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);


        adapter = new ChattingAdapter(chatDataList, new ChattingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                //Intent intent = new Intent(getApplicationContext(), Friend_info.class);
                //intent.putExtra("Friend_Id",chatDataList.get(postition).getUserName());
                //startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(adapter);


        chattingViewModel = new ViewModelProvider(this).get(ChattingViewModel.class);
        chattingViewModel.initMessageReference("chat",roomID);
        chattingViewModel.getChatDataListLiveData().observe(this, chatDataList -> {
            adapter.setData(chatDataList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    ChatData chatData = new ChatData(message, senderUid, System.currentTimeMillis(),getEmailSliceToID(FirebaseAuth.getInstance().getCurrentUser().getEmail()),FirebaseAuth.getInstance().getCurrentUser().getEmail());

                    // 메시지 데이터를 데이터베이스에 저장합니다.
                    Log.d("ChatData 값:",String.valueOf(chatData.getTimestamp()));
                    chattingViewModel.setValue(chatData);

                    // EditText를 초기화합니다.
                    messageEditText.setText("");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // ValueEventListener를 해제합니다.
        chattingViewModel.onDestroy();
    }
    private String getEmailSliceToID(String email){
        return email.substring(0, email.lastIndexOf("@"));
    }

}