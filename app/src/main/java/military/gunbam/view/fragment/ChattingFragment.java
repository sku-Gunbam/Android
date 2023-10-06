package military.gunbam.view.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import military.gunbam.R;
import military.gunbam.model.ChatData;
import military.gunbam.view.adapter.ChattingAdapter;
import military.gunbam.viewmodel.ChattingViewModel;

public class ChattingFragment extends Fragment {
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
    public ChattingFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting, container, false);

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
        recyclerView = view.findViewById(R.id.recyclerView);
        messageEditText = view.findViewById(R.id.messageEditText);
        sendButton = view.findViewById(R.id.sendButton);


        adapter = new ChattingAdapter(chatDataList, new ChattingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                //Intent intent = new Intent(getApplicationContext(), Friend_info.class);
                //intent.putExtra("Friend_Id",chatDataList.get(postition).getUserName());
                //startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        //recyclerView.setAdapter(adapter);


        chattingViewModel = new ViewModelProvider(this).get(ChattingViewModel.class);
        chattingViewModel.initMessageReference("chat",roomID);
        chattingViewModel.getChatDataListLiveData().observe(getViewLifecycleOwner(), chatDataList -> {
            adapter.setData(chatDataList);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
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


        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // ValueEventListener를 해제합니다.
        chattingViewModel.onDestroy();
    }
    private String getEmailSliceToID(String email){
        return email.substring(0, email.lastIndexOf("@"));
    }
}
