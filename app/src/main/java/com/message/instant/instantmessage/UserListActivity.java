package com.message.instant.instantmessage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by fujiaoyang1 on 11/15/16.
 */

public class UserListActivity extends AppCompatActivity {
    private String groupKey;
    private DatabaseReference connectedRef,groupsRef;
    private DatabaseReference  currentGroupUserListRef, currentGroupRef;
    private ValueEventListener connectListener, currentGroupListener;
    private LinearLayout user_list_layout;
    private static int user_id = 0;

    private static final String TAG = "** UserListActivity ** ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        user_list_layout = (LinearLayout)findViewById(R.id.user_list_layout);
        groupKey = getIntent().getExtras().get("group_key").toString();

        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        groupsRef.keepSynced(true);
        currentGroupRef = groupsRef.child(groupKey);
        currentGroupUserListRef = currentGroupRef.child("userList");
        detectConnection();
        readUserList();
    }

    private void detectConnection() {
        connectListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    setTitle("User list" + "  Status: UP");
                } else {
                    setTitle("User list" + "  Status: Down");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        };
        connectedRef.addValueEventListener(connectListener);
    }

    private void readUserList() {
        currentGroupListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) return;
                Group group= dataSnapshot.getValue(Group.class);
                List<String> userList = group.getUserList();
                Log.v(TAG, "read data");
                showUserList(userList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        currentGroupRef.addListenerForSingleValueEvent(currentGroupListener);
    }

    private void showUserList(List<String> userList) {
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,15,10,15);

        for (String name : userList) {
            Log.v(TAG, "=====>" + name);
            TextView user_view = new TextView(this);
            user_view.setBackgroundResource(R.drawable.chat_text1);
            user_view.setText(name);
            user_view.setPadding(10, 10, 10, 10);
            user_view.setTextSize(20);
            user_view.setLayoutParams(params);
            user_list_layout.addView(user_view);
        }
    }
}
