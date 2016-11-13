package com.message.instant.instantmessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by fujiaoyang1 on 11/12/16.
 */

public class LoginActivity extends Activity {
    private static final String TAG = "** LoginActivity ** ";

    private EditText user_name_txt, password_txt;
    private String userName, password;
    private DatabaseReference usersRef;
    static private boolean calledAlready = false;
    private ChildEventListener childEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        user_name_txt = (EditText) findViewById(R.id.user_name_txt);
        password_txt = (EditText)findViewById(R.id.password_txt);
    }

    public void ClickSignIn(View view) {
        userName = user_name_txt.getText().toString();
        password = password_txt.getText().toString();
        FindUserSignIn();
    }

    public void ClickSignUp(View view) {
        userName = user_name_txt.getText().toString();
        password = password_txt.getText().toString();
        FindUserSignUp();
    }

    private void FindUserSignIn () {
        String child = "userName";
        Query query = usersRef.orderByChild(child).equalTo(userName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)  {
                    for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()){
                        User user = itemSnapshot.getValue(User.class);
                        Log.v(TAG, user.getUserName());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("user_name",userName);
                        intent.putExtra("user_key",itemSnapshot.getKey());
                        startActivity(intent);
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this,"No this user",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void FindUserSignUp () {
        String child = "userName";
        Query query = usersRef.orderByChild(child).equalTo(userName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() == false)  {
                    Toast.makeText(LoginActivity.this,"Sign up for this user",Toast.LENGTH_SHORT).show();

                    User user = new User(userName, password);
                    //user.addNewGroup("Apple");
                    String key = usersRef.push().getKey();
                    usersRef.child(key).setValue(user);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("user_name",userName);
                    intent.putExtra("user_key",key);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(LoginActivity.this,"This user exists",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
















