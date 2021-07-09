package com.example.instagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.instagram.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends PostsFragment
{
    private RecyclerView rvPosts;
    private SwipeRefreshLayout swipeContainer;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.rvPosts = super.rvPosts;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        rvPosts.setAdapter(postsAdapter);
        rvPosts.setLayoutManager(gridLayoutManager);

        this.swipeContainer = super.swipeContainer;
        this.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                postsAdapter.clear();
                queryPosts();
                swipeContainer.setRefreshing(false); // signals that refreshing has finished
            }
        });
    }

    @Override
    protected void queryPosts()
    {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class); // specify which class to query
        query.include(Post.KEY_USER); // specify object ID
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser()); // filters posts by current user
        query.setLimit(20);
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
                for (Post post : objects)
                    Log.i(TAG, "Post: " + post.getDescription() + "; Username: " + post.getUser().getUsername());
                allPosts.clear();
                allPosts.addAll(objects);
                postsAdapter.notifyDataSetChanged();
            }
        });
    }
}
