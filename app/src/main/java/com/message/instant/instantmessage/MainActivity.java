package com.message.instant.instantmessage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> groupList;
    private String userName;
    private String userKey, groupKey;
    private DatabaseReference connectedRef;
    private DatabaseReference usersRef;
    private DatabaseReference groupsRef;
    private User user;
    private DatabaseReference currentUserRef;
    private ValueEventListener currentUserListener, connectListener;
    private EditText search_input;
    private static final String TAG = "** MainActivity ** ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = getIntent().getExtras().get("user_name").toString();
        userKey = getIntent().getExtras().get("user_key").toString();

        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        usersRef.keepSynced(true);
        groupsRef.keepSynced(true);
        currentUserRef = usersRef.child(userKey);

        groupList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.group_list_view);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, groupList);
        listView.setAdapter(arrayAdapter);

        search_input = (EditText) findViewById(R.id.find_group_exit_text);

        detectConnection();
        updateViewList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String groupName = ((TextView) view).getText().toString();
                Log.v(TAG, "group Name = " + groupName);

                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("group_name", groupName);
                intent.putExtra("user_name", userName);
                intent.putExtra("user_key", userKey);
                Log.v(TAG, "start chat Activity");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        /*// Remove currentUserListener value event listener
        if (currentUserListener != null) {
            currentUserRef.removeEventListener(currentUserListener);
        }
        */
        if (connectListener != null) {
            connectedRef.removeEventListener(connectListener);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                addNewGroup();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void detectConnection() {
        connectListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    setTitle("Status: UP");
                } else {
                    setTitle("Status: Down");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // System.err.println("Listener was cancelled");
            }
        };
        connectedRef.addValueEventListener(connectListener);

    }

    private void updateViewList() {
        currentUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) return;

                user = dataSnapshot.getValue(User.class);
                Toast.makeText(MainActivity.this, "update group list ", Toast.LENGTH_SHORT).show();

                groupList.clear();
                groupList.addAll(user.getGroupList());
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        currentUserRef.addValueEventListener(currentUserListener);
    }

    private void addNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText input_field = new EditText(this);
        builder.setTitle("Enter a group Name:");
        builder.setView(input_field);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = input_field.getText().toString();
                if (!groupName.isEmpty()) {
                    detectGroupExist(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void detectGroupExist(final String groupName) {
        String child = "groupName";
        Query query = groupsRef.orderByChild(child).equalTo(groupName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() == false) {
                    Toast.makeText(MainActivity.this, "create a new group ", Toast.LENGTH_SHORT).show();
                    Group group = new Group(groupName, userName);
                    String key = groupsRef.push().getKey();
                    groupsRef.child(key).setValue(group);
                    user.addNewGroup(groupName);
                    currentUserRef.child("groupList").setValue(user.getGroupList());
                } else {
                    Toast.makeText(MainActivity.this, "This group exists ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void searchGroupToJoin(View view) {
        String child = "groupName";
        final String groupName = search_input.getText().toString();
        //Log.v(TAG, "group Name :" + groupName);
        // if the input group is already in user grouplist stop query
        List<String> groupList = user.getGroupList();
        for (String group : user.getGroupList()) {
            if (group.equals(groupName)) {
                Toast.makeText(MainActivity.this, "In current group List", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Query query = groupsRef.orderByChild(child).equalTo(groupName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                        Group group = itemSnapshot.getValue(Group.class);

                        group.addNewUser(userName);
                        Log.v(TAG, itemSnapshot.getKey());
                        DatabaseReference curGroupRef = groupsRef.child(itemSnapshot.getKey());
                        curGroupRef.child("userList").setValue(group.getUserList());

                        user.addNewGroup(groupName);
                        currentUserRef.child("groupList").setValue(user.getGroupList());
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No this group", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed () {
        finish();
    }
}

