package com.example.instagram;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>
{
    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts)
    {
        this.context = context;
        this.posts = posts;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // for every visible item on screen, we want to inflate(create) a view; using item_post as "blueprint"
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        // wrap it in a ViewHolder for easy access later on
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount()
    {
        return posts.size();
    }

    public void clear()
    {
        posts.clear();
        notifyDataSetChanged();
    }
    public void addAll(List<Post> list)
    {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    // subclass ViewHolder holds item_post layout
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvUsername, tvDescription;
        ImageView ivImage;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivImage = itemView.findViewById(R.id.ivImage);
        }

        // bind post data to view elements
        public void bind(Post post)
        {
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            ParseFile image = post.getImage();
            if (image != null)
                Glide.with(context).load(image.getUrl()).into(ivImage);
            else
                ivImage.setVisibility(View.GONE);
        }
    }
}
