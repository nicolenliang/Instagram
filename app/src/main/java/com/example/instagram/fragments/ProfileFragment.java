package com.example.instagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.instagram.Post;
import com.example.instagram.PostsAdapter;
import com.example.instagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends PostsFragment
{
    private RecyclerView rvPosts;
    private PostsAdapter postsAdapter;
    private List<Post> allPosts;
    private SwipeRefreshLayout swipeContainer;
    ImageView ivProfile;
    TextView tvUsername, tvPostCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        rvPosts = view.findViewById(R.id.rvProfilePosts);
        allPosts = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);

        // 1. create layout for one row of RV:
        // 2. create adapter
        postsAdapter = new PostsAdapter(getContext(), allPosts, 2);
        // 3. create the data source
        // 4. set adapter on the recycler view
        rvPosts.setAdapter(postsAdapter);
        // 5. set the layout manager on the recycler view
        rvPosts.setLayoutManager(gridLayoutManager);
        queryPosts();

        ivProfile = view.findViewById(R.id.ivProfile);
        ParseFile profile = ParseUser.getCurrentUser().getParseFile("profilepic");
        if (profile != null)
            Glide.with(getContext()).load(profile.getUrl()).transform(new CircleCrop()).into(ivProfile);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        tvPostCount = view.findViewById(R.id.tvPostCount);
        tvPostCount.setText(allPosts.size() + " posts");

        /*swipeContainer = super.swipeContainer;
        this.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                postsAdapter.clear();
                queryPosts();
                swipeContainer.setRefreshing(false); // signals that refreshing has finished
            }
        });*/
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
