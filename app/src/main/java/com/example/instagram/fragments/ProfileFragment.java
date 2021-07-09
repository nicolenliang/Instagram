package com.example.instagram.fragments;

import android.util.Log;

import com.example.instagram.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends PostsFragment
{
    @Override
    protected void queryPosts()
    {
        super.queryPosts();
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
                allPosts.addAll(objects);
                postsAdapter.notifyDataSetChanged();
            }
        });
    }
}
