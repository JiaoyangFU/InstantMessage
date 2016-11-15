package com.message.instant.instantmessage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by fujiaoyang1 on 11/13/16.
 */

public class ChatActivity extends AppCompatActivity {

    private String userName, groupName;
    private String userKey, groupKey;
    private DatabaseReference connectedRef;
    private DatabaseReference usersRef;
    private DatabaseReference groupsRef;
    private DatabaseReference currentUserRef, currentGroupMsgRef, currentGroupRef;
    private ValueEventListener connectListener;
    private ChildEventListener currentGroupMsgListener;

    private EditText input_msg;
    private TextView chat_conversation;

    private static final String TAG = "** ChatActivity ** ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        input_msg = (EditText)findViewById(R.id.msg_input);
        chat_conversation = (TextView)findViewById(R.id.msg_text);

        userName = getIntent().getExtras().get("user_name").toString();
        userKey = getIntent().getExtras().get("user_key").toString();
        groupName = getIntent().getExtras().get("group_name").toString();

        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        usersRef.keepSynced(true);
        groupsRef.keepSynced(true);
        initializeGroupKey(groupName);
        detectConnection();
        Log.v(TAG, "group Name = " + groupName);
    }

    private void initializeGroupKey(String groupName) {
        String child = "groupName";
        Query query = groupsRef.orderByChild(child).equalTo(groupName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                        groupKey = itemSnapshot.getKey();
                        currentGroupRef = groupsRef.child(groupKey);
                        currentGroupMsgRef = currentGroupRef.child("messages");
                        updateMsgHistory();

                        Log.v(TAG, "groupKey = " + groupKey);
                        //Log.v(TAG, "===> groupKey = " + groupKey);
                    }
                } else {
                    Toast.makeText(ChatActivity.this, " cannot find current group ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public void onStop() {
        super.onStop();
        // Remove currentUserListener value event listener
        if (currentGroupMsgListener != null) {
            currentGroupMsgRef.removeEventListener(currentGroupMsgListener);
        }

        if (connectListener != null) {
            connectedRef.removeEventListener(connectListener);
        }
    }

    private void detectConnection() {
        connectListener = new ValueEventListener() {
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
        connectedRef.addValueEventListener(connectListener);
    }


    public void sendMessage(View view) {

        Map<String, String> message;
        message = new HashMap<String, String>();
        message.put("userName",userName);
        message.put("Msg",input_msg.getText().toString());
        String msg_key = currentGroupRef.child("messages").push().getKey();
        currentGroupRef.child("messages").child(msg_key).setValue(message);
        Log.v(TAG, msg_key + "  update message");
    }

    private void updateMsgHistory() {
        currentGroupMsgListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        currentGroupMsgRef.addChildEventListener(currentGroupMsgListener);
    }

    private void append_chat_conversation(DataSnapshot dataSnapshot) {
        String name,chat_msg;

        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()){

            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            name = (String) ((DataSnapshot)i.next()).getValue();
            chat_conversation.append(name +" : "+chat_msg +" \n");
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

