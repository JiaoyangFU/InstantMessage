package com.message.instant.instantmessage;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> groupList = new ArrayList<>();
    Set<String> groupSet;
    private String userName;
    private DatabaseReference connectedRef;
    private DatabaseReference usersRef;
    private DatabaseReference groupsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        groupsRef = FirebaseDatabase.getInstance().getReference("Groups");

        //UserLogin();
        listView = (ListView) findViewById(R.id.group_list_view);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,groupList);
        listView.setAdapter(arrayAdapter);
        groupSet = new HashSet<String>();
        /*
        groupList.add("Apple");
        groupList.add("Orange");
        groupList.add("Milk");
        arrayAdapter.notifyDataSetChanged();
        */
        connectedRef.addValueEventListener(new ValueEventListener() {
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
        });
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

    private void addNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText input_field = new EditText(this);
        builder.setTitle("Enter a group Id:");
        builder.setView(input_field);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = input_field.getText().toString();
                if(!groupName.isEmpty()) {
                    groupSet.add(groupName);
                    DatabaseReference groupNodeRef = FirebaseDatabase.getInstance().getReference(".Groups");
                    Map<String,Object> map = new HashMap<String, Object>();
                    map.put(groupName,"");
                    groupNodeRef.updateChildren(map);
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

    private void UserLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText input_field = new EditText(this);
        builder.setTitle("Enter your user Id:");
        builder.setView(input_field);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = input_field.getText().toString();
                if(userName.isEmpty())
                    UserLogin();
                else
                    userName = name;
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                UserLogin();
            }
        });
        builder.show();
    }
}


