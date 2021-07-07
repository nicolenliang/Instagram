package com.example.instagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();

        // must register Parse model before initializing
        ParseObject.registerSubclass(Post.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("4DC4JknX6yi14XsA676zgFvRSvJ8t4DWOTpRZuzP")
                .clientKey("eRr0pvMNXW9z2M58zbi8Yi7keYoSqWXmILdFFOT7")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
