package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;
import java.util.concurrent.RecursiveAction;

public class PostsAdapter extends RecyclerView.Adapter
{
    private Context context;
    private List<Post> posts;
    int type;

    public PostsAdapter(Context context, List<Post> posts, int type)
    {
        this.context = context;
        this.posts = posts;
        this.type = type;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;
        // for every visible item on screen, we want to inflate(create) a view; using item_post as "blueprint"
        // wrap it in a ViewHolder for easy access later on
        if (viewType == 1)
        {
            view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
            return new ViewHolderPosts(view, viewType);
        }
        else
        {
            view = LayoutInflater.from(context).inflate(R.layout.item_post_profile, parent, false);
            return new ViewHolderProfile(view, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof ViewHolderPosts)
        {
            Post post = posts.get(position);
            ((ViewHolderPosts)holder).bind(post);
        }
        else if (holder instanceof ViewHolderProfile)
        {
            Post post = posts.get(position);
            ((ViewHolderProfile)holder).bind(post);
        }
    }

    @Override
    public int getItemCount()
    {
        return posts.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return type;
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
    public class ViewHolderPosts extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView tvUsername, tvDescription, tvTimestamp, tvUsernameCaption;
        ImageView ivImage, ivProfile;
        ImageButton ibLike, ibComment, ibDm, ibSave;

        public ViewHolderPosts(@NonNull View itemView, int viewType)
        {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvUsernameCaption = itemView.findViewById(R.id.tvUsernameCaption);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            ibLike = itemView.findViewById(R.id.ibLike);
            ibComment = itemView.findViewById(R.id.ibComment);
            ibDm = itemView.findViewById(R.id.ibDm);
            ibSave = itemView.findViewById(R.id.ibSave);

            // click on post to open new activity and view details
            itemView.setOnClickListener(this);
        }

        // bind post data to view elements
        public void bind(Post post)
        {
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            String timestamp = post.calculateTimeAgo(post.getCreatedAt());
            tvTimestamp.setText(timestamp);
            tvUsernameCaption.setText(post.getUser().getUsername());
            ParseFile image = post.getImage();
            if (image != null)
                Glide.with(context).load(image.getUrl()).into(ivImage);
            else
                ivImage.setVisibility(View.GONE);
            ParseFile profile = post.getUser().getParseFile("profilepic");
            if (profile != null)
                Glide.with(context).load(profile.getUrl()).transform(new CircleCrop()).into(ivProfile);

            ibLike.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //onClickIb(ibLike, post);
                    int likes = post.getLikes();
                    post.setLikes(likes + 1);
                }
            });
            ibComment.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                { onClickIb(ibComment, post); }
            });
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
        public void onClick(View v)
        {
            // grab post position; check nullity; wrap with Parcel; pass into activity
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION)
            {
                Post post = posts.get(position);
                Intent i = new Intent(context, PostDetailsActivity.class);
                i.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                context.startActivity(i);
            }
        }
    }

    // viewholder class for item_post_profile layout
    public class ViewHolderProfile extends RecyclerView.ViewHolder
    {
        ImageView ivImage;

        public ViewHolderProfile(@NonNull View itemView, int viewType)
        {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
        }

        // bind post data to view elements
        public void bind(Post post)
        {
            ParseFile image = post.getImage();
            if (image != null)
                Glide.with(context).load(image.getUrl()).into(ivImage);
            else
                ivImage.setVisibility(View.GONE);
        }

    }
}
