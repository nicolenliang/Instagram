package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity
{
    public final static String TAG = "SignupActivity";
    EditText etUsername, etPassword;
    Button btnSignupS, btnLoginS;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignupS = findViewById(R.id.btnSignupS);
        btnLoginS = findViewById(R.id.btnLoginS);

        // onClickListener to sign up new user
        btnSignupS.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i(TAG, "onClick signup button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                signupUser(username, password);
            }
        });
        // onClickListener to navigate to login activity
        btnLoginS.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i(TAG, "onClick login button");
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void signupUser(String username, String password)
    {
        // create new ParseUser obj
        ParseUser user = new ParseUser();
        // set properties
        user.setUsername(username);
        user.setPassword(password);
        // signUpInBackground (like logInInBackground)
        user.signUpInBackground(new SignUpCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if (e != null) // there is an exception
                {
                    Log.e(TAG, "signupUser issue with signup: " + e.getLocalizedMessage());
                    Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                goMainActivity();
                Toast.makeText(SignupActivity.this, "signup successful!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity()
    {
        Intent i = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(i);
        finish(); // so user cant go back to signup page
    }
}