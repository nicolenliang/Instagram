package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity
{
    public static final String TAG = "FeedActivity";
    RecyclerView rvPosts;
    protected PostsAdapter postsAdapter;
    protected List<Post> allPosts;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        rvPosts = findViewById(R.id.rvPosts);
        swipeContainer = findViewById(R.id.swipeContainer);

        // initialize the array that holds all posts; create the adapter
        allPosts = new ArrayList<>();
        postsAdapter = new PostsAdapter(this, allPosts);

        // set adapter; set layout manager
        rvPosts.setAdapter(postsAdapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        // query posts
        queryPosts();

        // setup refresh listener: triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                postsAdapter.clear();
                queryPosts();
                swipeContainer.setRefreshing(false); // signals refresh has finished
            }
        });
    }

    private void queryPosts()
    {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class); // specify which class to query from
        query.include(Post.KEY_USER); // specify object ID
        query.setLimit(20); // set limit to 20 posts
        query.addDescendingOrder("createdAt"); // order posts by creation date (newest first)
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
                for (Post post : objects) // debugging purposes
                    Log.i(TAG, "Post: " + post.getDescription() + "; Username: " + post.getUser().getUsername());
                // save posts to list; NOTIFY ADAPTER OF NEW DATA WOOWOOWOOGOHOHOO
                allPosts.addAll(objects);
                postsAdapter.notifyDataSetChanged();
            }
        });
    }
}