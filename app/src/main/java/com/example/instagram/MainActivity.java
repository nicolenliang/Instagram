package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "MainActivity";
    EditText etDescription;
    ImageView ivUpload;
    Button btnLogout, btnUpload, btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDescription = findViewById(R.id.etDescription);
        ivUpload = findViewById(R.id.ivUpload);
        btnLogout = findViewById(R.id.btnLogout);
        btnUpload = findViewById(R.id.btnUpload);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ParseUser.logOut();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        queryPosts();
    }

    private void queryPosts()
    {
        // specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // specify object ID
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>()
        {
            @Override
            public void done(List<Post> objects, ParseException e)
            {
                if (e != null)
                {
                    Log.e(TAG, "queryPosts issue in loading posts: " + e.getLocalizedMessage());
                    return;
                }
                for (Post post : objects)
                    Log.i(TAG, "Post: " + post.getDescription() + "; Username: " + post.getUser().getUsername());
            }
        });
    }
}