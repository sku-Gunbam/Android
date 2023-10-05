package military.gunbam.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChattingModel {
    private DatabaseReference messageRef;
    private ValueEventListener valueEventListener;

    public ChattingModel() {

    }
    public void initMessageReference(String path, String roodID, ValueEventListener valueEventListener) {
        messageRef = FirebaseDatabase.getInstance().getReference(path).child(roodID);
        messageRef.addValueEventListener(valueEventListener);
    }
    public void setValue(ChatData chatData){
        messageRef.push().setValue(chatData);
    }

    public void onDestroy(){
        messageRef.removeEventListener(valueEventListener);
    }







}
