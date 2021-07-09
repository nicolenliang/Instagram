package com.example.instagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.Post;
import com.example.instagram.PostsAdapter;
import com.example.instagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment
{
    public static final String TAG = "PostsFragment";
    RecyclerView rvPosts;
    protected PostsAdapter postsAdapter;
    protected List<Post> allPosts;
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;

    public PostsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        rvPosts = view.findViewById(R.id.rvPosts);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        allPosts = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        // 1. create layout for one row of RV:
        //    see item_post.xml
        // 2. create adapter
        postsAdapter = new PostsAdapter(getContext(), allPosts);
        // 3. create the data source?:
        //    see queryPosts()
        // 4. set adapter on the recycler view
        rvPosts.setAdapter(postsAdapter);
        // 5. set the layout manager on the recycler view
        rvPosts.setLayoutManager(linearLayoutManager);
        queryPosts();

        // set up refresh listener: triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                postsAdapter.clear();
                queryPosts();
                postsAdapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false); // signals that refreshing has finished
            }
        });

        // set up infinite scroll listener
        /*scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager)
        {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view)
            {
                // ??? WAT DO HERE
            }
        };
        rvPosts.addOnScrollListener(scrollListener); // adding endless scroll listener to our RV
*/
    }

    protected void queryPosts()
    {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class); // specify which class to query
        query.include(Post.KEY_USER); // specify object ID
        query.setLimit(20); // limit posts to 20 most recent
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