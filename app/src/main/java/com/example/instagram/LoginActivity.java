package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity
{
    public static final String TAG = "LoginActivity";
    EditText etUsername, etPassword;
    Button btnLoginL, btnSignupL;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ParseUser.getCurrentUser() != null) // someone is already logged in
            goMainActivity(); // go directly to main screen; don't show login screen again

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLoginL = findViewById(R.id.btnLoginL);
        btnSignupL = findViewById(R.id.btnSignupL);
        // set onClickListener to navigate to home screen once logged in
        btnLoginL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i(TAG, "onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });
        // set onClickListener to navigate to sign up page
        btnSignupL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i(TAG, "onClick signup button");
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
    }

    private void loginUser(String username, String password)
    {
        Log.i(TAG, "attempting to log in user: " + username);
        // TO-DO: navigate to main screen if user logs in successfully
        // logInInBackground runs on background thread, rather than main or UI thread = better UI exp
        ParseUser.logInInBackground(username, password, new LogInCallback()
        {
            @Override
            public void done(ParseUser user, ParseException e)
            {
                if (e != null) // parseexception returns non-null if there IS an exception
                {
                    // TO-DO: detailed error msg; better error handling
                    Log.e(TAG, "loginUser issue with login: " + e.getMessage());
                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                goMainActivity();
                Toast.makeText(LoginActivity.this, "login successful!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // use Intent to navigate to main activity
    private void goMainActivity()
    {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish(); // finishes loginactivity so user cannot go back to it
    }
}