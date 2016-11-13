package com.message.instant.instantmessage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

/**
 * Created by fujiaoyang1 on 11/13/16.
 */

public class ChatActivity extends AppCompatActivity {

    private String userName, groupName;
    private String userKey, groupKey;
    private DatabaseReference connectedRef;
    private DatabaseReference usersRef;
    private DatabaseReference groupsRef;
    private User user;
    private DatabaseReference currentUserRef;
    private ValueEventListener currentUserListener, connectListerner;

    private ImageButton btn_send_msg;
    private EditText input_msg;
    private TextView chat_conversation;

    private static final String TAG = "** ChatActivity ** ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        userName = getIntent().getExtras().get("user_name").toString();
        userKey = getIntent().getExtras().get("user_key").toString();
        groupName = getIntent().getExtras().get("group_name").toString();

        // Log.v(TAG, "reach chat Activity");
        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        currentUserRef = usersRef.child(userKey);

        btn_send_msg = (ImageButton) findViewById(R.id.send_button);
        input_msg = (EditText) findViewById(R.id.msg_input);
        chat_conversation = (TextView) findViewById(R.id.msg_text);

        detectConnection();


    }

    private void detectConnection() {
        connectListerner = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    setTitle(groupName + "  Status: UP");
                } else {
                    setTitle(groupName + "  Status: Down");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // System.err.println("Listener was cancelled");
            }
        };
        connectedRef.addValueEventListener(connectListerner);
    }

    private void initializeGroupKey() {
        String child = "groupName";
        Query query = groupsRef.orderByChild(child).equalTo(groupName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)  {
                    for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()){
                        groupKey = itemSnapshot.getKey();

                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        /*
        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> message;
                message = new  HashMap<String, String>();

                message.put("userName",userName);
                message.put("Msg",input_msg.getText().toString());
                String msg_key = groupsRef.child("message").push().getKey();

                groupsRef.child(msg_key).setValue(message);
                user.addNewGroup(groupName);
            }
        });
        */

    }

    private void append_chat_conversation(DataSnapshot dataSnapshot) {
        String chat_msg,chat_user_name;
        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()){

            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();

            chat_conversation.append(chat_user_name +" : "+chat_msg +" \n");
        }
    }
}

