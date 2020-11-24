/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

  ConstraintLayout constraintLayout;
  ImageView imageViewTwitter;
  EditText editTextUserName;
  EditText editTextPassword;
  Button buttonSignupLogin;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setTitle("Twitter");

    constraintLayout = findViewById(R.id.constraintLayout);
    imageViewTwitter = findViewById(R.id.imageViewTwitter);
    editTextUserName = findViewById(R.id.editTextUserName);
    editTextPassword = findViewById(R.id.editTextPassword);
    buttonSignupLogin = findViewById(R.id.buttonSignupLogin);

    constraintLayout.setOnClickListener(this);
    imageViewTwitter.setOnClickListener(this);
    editTextPassword.setOnKeyListener(this);

    redirectUser();

    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

  public void buttonClicked(View view) {
    ParseUser.logInInBackground(editTextUserName.getText().toString(),
            editTextPassword.getText().toString(), new LogInCallback() {
              @Override
              public void done(ParseUser user, ParseException e) {
                if (e == null) {
                  Toast.makeText(MainActivity.this, "Login Done!", Toast.LENGTH_LONG).show();
                  redirectUser();
                } else {
                  e.printStackTrace();
                  ParseUser newUser = new ParseUser();
                  newUser.setUsername(editTextUserName.getText().toString());
                  newUser.setPassword(editTextPassword.getText().toString());
                  newUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                      if (e == null) {
                        Toast.makeText(MainActivity.this, "SignUp Done!", Toast.LENGTH_LONG).show();
                        redirectUser();
                      } else {
                        Log.i("MainActivityLog", e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_LONG).show();
                      }
                    }
                  });
                }
              }
            });
  }

  @Override
  public boolean onKey(View v, int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
      Log.i("MainActivityLog", "Enter clicked");
      buttonClicked(v);
    }
    return false;
  }

  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.constraintLayout || view.getId() == R.id.imageViewTwitter) {
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
  }

  public void redirectUser() {
    if (ParseUser.getCurrentUser() != null) {
      Log.i("MainActivityLog", "LoggedIn user: " + ParseUser.getCurrentUser());
      Log.i("MainActivityLog", "LoggedIn userName: " + ParseUser.getCurrentUser().getUsername());
      Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
      startActivity(intent);
    }
  }
}