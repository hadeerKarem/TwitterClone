package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    ListView listViewUsers;
    ArrayList<String> usersList = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        setTitle("UserList");

        listViewUsers = findViewById(R.id.listViewUsers);
        listViewUsers.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

//        usersList.add("searching for users...");
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, usersList);
        listViewUsers.setAdapter(arrayAdapter);

        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = (CheckedTextView) view;

                if (checkedTextView.isChecked()) {
                    Log.i("UserActivityLog", "Checked!");
                    ParseUser.getCurrentUser().add("isFollowing", usersList.get(position));
                } else {
                    Log.i("UserActivityLog", "Not Checked!");
                    ParseUser.getCurrentUser().getList("isFollowing").remove(usersList.get(position));
                    List tempUsersList = ParseUser.getCurrentUser().getList("isFollowing");
                    ParseUser.getCurrentUser().remove("isFollowing");
                    ParseUser.getCurrentUser().add("isFollowing", tempUsersList);
                }
                ParseUser.getCurrentUser().saveInBackground();
            }
        });

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                Log.i("UserActivityLog", "query found in background");
                if (e == null && objects.size() > 0 ) {
//                    usersList.clear();
                    Log.i("UserActivityLog", "no ParseExceptions, and usersList size > 0");
                    for (ParseUser user : objects) {
                        Log.i("UserActivityLog", "Looping over the ParseUsersList");
                        usersList.add(user.getUsername());
                        Log.i("UserActivityLog", "adding User to our usersList");
                    }

                    arrayAdapter.notifyDataSetChanged();
                    Log.i("UserActivityLog", "ArrayAdapter Notified");

                    for (String username : usersList) {
                        Log.i("UserActivityLog", "userList size = " + usersList.size());
                        Log.i("UserActivityLog", "Looping over our usersList: " + username);
                        if (ParseUser.getCurrentUser().getList("isFollowing").contains(username)){
                            Log.i("UserActivityLog", "current user is following " + username);
                            listViewUsers.setItemChecked(usersList.indexOf(username), true);
                        }
                        else {

                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.twitter_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.tweet) {
            Log.i("UserActivityLog", "Tweet Pressed");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Send a Tweet");

            final EditText editTextTweet = new EditText(this);
            builder.setView(editTextTweet);

            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("UserActivityLog", editTextTweet.getText().toString() + "       Sent!");
                    ParseObject tweet = new ParseObject("Tweet");
                    tweet.put("tweet", editTextTweet.getText().toString());
                    tweet.put("username", ParseUser.getCurrentUser().getUsername());
                    tweet.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(UsersActivity.this, "Tweet Sent!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(UsersActivity.this, "Tweet Failed!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("UserActivityLog", "Tweet Canceled!");
                    dialog.cancel();
                }
            });

            builder.show();
        }
        else if (item.getItemId() == R.id.viewFeed){
            Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.logout) {
            Log.i("UserActivityLog", "Logout Pressed");
            ParseUser.logOut();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}