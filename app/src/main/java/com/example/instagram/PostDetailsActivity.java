package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class PostDetailsActivity extends AppCompatActivity
{
    TextView tvUsername, tvDescription, tvTimestamp;
    ImageView ivImage;
    Post post;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        ivImage = findViewById(R.id.ivImage);
        toolbar = findViewById(R.id.toolbar_main);

        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        String timestamp = post.calculateTimeAgo(post.getCreatedAt());
        tvTimestamp.setText(timestamp);
        ParseFile image = post.getImage();
        if (image != null)
            Glide.with(this).load(image.getUrl()).into(ivImage);
        else
            ivImage.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (R.id.logout == item.getItemId()) // item pressed equals logout item
        {
            ParseUser.logOut();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish(); // user cannot navigate back to screen
        }
        return true;
    }
}