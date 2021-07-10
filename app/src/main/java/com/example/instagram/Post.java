package com.example.instagram;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject
{
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_LIKES = "numLikes";
    public static final String KEY_COMMENTS = "numComments";

    public String getDescription() { return getString(KEY_DESCRIPTION); }
    public void setDescription(String description) { put(KEY_DESCRIPTION, description); }

    public ParseFile getImage() { return getParseFile(KEY_IMAGE); }
    public void setImage(ParseFile parseFile) { put(KEY_IMAGE, parseFile); }

    public ParseUser getUser() { return getParseUser(KEY_USER); }
    public void setUser(ParseUser parseUser) { put(KEY_USER, parseUser); }

    public Date getTime() { return getDate(KEY_CREATED_AT); }

    public int getLikes() { return getInt(KEY_LIKES); }
    public void setLikes(int likes) { put(KEY_LIKES, likes); }

    public int getComments() { return getInt(KEY_COMMENTS); }
    public void setComments(int comments) { put(KEY_COMMENTS, comments); }

    public static String calculateTimeAgo(Date createdAt)
    {
        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try
        {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS)
                return "just now";
            else if (diff < 2 * MINUTE_MILLIS)
                return "a minute ago";
            else if (diff < 50 * MINUTE_MILLIS)
                return diff / MINUTE_MILLIS + " minutes ago";
            else if (diff < 90 * MINUTE_MILLIS)
                return "an hour ago";
            else if (diff < 24 * HOUR_MILLIS)
                return diff / HOUR_MILLIS + " hours ago";
            else if (diff < 48 * HOUR_MILLIS)
                return "yesterday";
            else
                return diff / DAY_MILLIS + " days ago";
        }
        catch (Exception e)
        {
            Log.i("Error: ", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }
        return "";
    }
}

