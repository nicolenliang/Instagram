package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class PostDetailsActivity extends AppCompatActivity
{
    TextView tvUsername, tvDescription, tvTimestamp, tvLikes, tvUsernameCaption;
    ImageView ivImage, ivProfile;
    Post post;
    Toolbar toolbar;
    ImageButton ibLike, ibComment, ibDm, ibSave;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        tvLikes = findViewById(R.id.tvLikes);
        tvUsernameCaption = findViewById(R.id.tvUsernameCaption);
        ivImage = findViewById(R.id.ivImage);
        ivProfile = findViewById(R.id.ivProfile);
        ibLike = findViewById(R.id.ibLike);
        ibComment = findViewById(R.id.ibComment);
        ibDm = findViewById(R.id.ibDm);
        ibSave = findViewById(R.id.ibSave);

        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        String timestamp = post.calculateTimeAgo(post.getCreatedAt());
        tvTimestamp.setText(timestamp);
        tvLikes.setText(post.getLikes() + " likes");
        tvUsernameCaption.setText(post.getUser().getUsername());
        ParseFile image = post.getImage();
        if (image != null)
            Glide.with(this).load(image.getUrl()).into(ivImage);
        else
            ivImage.setVisibility(View.GONE);
        ParseFile profile = post.getUser().getParseFile("profilepic");
        if (profile != null)
            Glide.with(this).load(profile.getUrl()).transform(new CircleCrop()).into(ivProfile);
        ibLike.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            { onClickIb(ibLike, post); }
        });
        ibComment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            { onClickIb(ibComment, post); }
        });

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
    }

    public void onClickIb(ImageButton ib, Post post)
    {
        if (ib.getId() == ibLike.getId()) // likes button
        {
            int likes = post.getLikes();
            post.setLikes(likes + 1);
        }
        if (ib.getId() == ibComment.getId())
        {
            int comments = post.getComments();
            post.setComments(comments + 1);
        }
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