package com.message.instant.instantmessage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
    private RelativeLayout chat_layout;
    private EditText input_msg;
    private static int msg_id = 0;

    private static final String TAG = "** ChatActivity ** ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chat_layout = (RelativeLayout)findViewById(R.id.chat_layout);
        input_msg = (EditText)findViewById(R.id.msg_input);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
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
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (msg_id == 0) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        }
        params.setMargins(10,15,10,15);

        while (i.hasNext()){
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            name = (String) ((DataSnapshot)i.next()).getValue();
            TextView chat_view = new TextView(this);

            if (name.equals(userName)) {
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                chat_view.setBackgroundResource(R.drawable.chat_text1);
            }
            else {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                chat_view.setBackgroundResource(R.drawable.chat_text2);
            }

            if (msg_id > 0) params.addRule(RelativeLayout.BELOW, msg_id - 1);

            chat_view.setText(name +": "+chat_msg);
            chat_view.setId(msg_id++);
            chat_view.setPadding(10, 10, 10, 10);
            chat_view.setTextSize(20);
            chat_view.setLayoutParams(params);
            chat_layout.addView(chat_view);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

