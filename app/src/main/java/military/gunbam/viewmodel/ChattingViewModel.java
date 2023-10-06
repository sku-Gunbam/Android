package military.gunbam.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import military.gunbam.model.ChatData;
import military.gunbam.model.ChattingModel;


public class ChattingViewModel extends ViewModel {
    private DatabaseReference messageRef;
    private ValueEventListener valueEventListener;
    private MutableLiveData<ValueEventListener> valueEventListenerLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ChatData>> chatDataListLiveData = new MutableLiveData<>();
    private ChattingModel model = new ChattingModel();

    public void setValue(ChatData chatData){
        model.setValue(chatData);

    }

    public void initMessageReference(String path, String roomID) {
        if (valueEventListener != null) {
            messageRef.removeEventListener(valueEventListener);
        }
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ChatData> chatDataList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String message = snapshot.child("message").getValue(String.class);
                    String senderUid = snapshot.child("senderUid").getValue(String.class);
                    long timestamp = snapshot.child("timestamp").getValue(Long.class);
                    String userEmail = snapshot.child("userEmail").getValue(String.class);
                    String userName = snapshot.child("userName").getValue(String.class);

                    ChatData chatData = new ChatData(message, senderUid, timestamp, userName, userEmail);
                    chatDataList.add(chatData);
                }
                chatDataListLiveData.setValue(chatDataList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle onCancelled
            }
        };
        model.initMessageReference(path, roomID,valueEventListener);
    }



    public void onDestroy(){
        model.onDestroy();
    }
    public LiveData<List<ChatData>> getChatDataListLiveData() {
        return chatDataListLiveData;
    }
    public LiveData<ValueEventListener> getValueEventListenerLiveData() {return valueEventListenerLiveData; }
}
