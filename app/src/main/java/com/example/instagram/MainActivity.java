package com.example.instagram;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "MainActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private File photoFile;
    public String photoFileName = "photo.jpg";
    EditText etDescription;
    ImageView ivUpload;
    Button btnLogout, btnUpload, btnSubmit, btnFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDescription = findViewById(R.id.etDescription);
        ivUpload = findViewById(R.id.ivUpload);
        btnLogout = findViewById(R.id.btnLogout);
        btnUpload = findViewById(R.id.btnUpload);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnFeed = findViewById(R.id.btnFeed);
        btnLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ParseUser.logOut();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        //queryPosts();
        btnUpload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "onClick btnUpload success");
                launchCamera();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String description = etDescription.getText().toString();
                if (description.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "description cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (photoFile == null || ivUpload.getDrawable() == null)
                {
                    Toast.makeText(MainActivity.this, "there is no image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(description, currentUser, photoFile);
            }
        });
        btnFeed.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(MainActivity.this, FeedActivity.class);
                startActivity(i);
            }
        });
    }

    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }

    @SuppressWarnings("deprecation")
    private void launchCamera()
    {
        // intent launches camera app and then returns to original application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // file reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap photoFile obj into a content provider; required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", photoFile);
        // places output from camera (wrapped fileProvider obj) into intent
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // check for nullity; app will crash if startActivityForResult() is called on an intent the app cannot handle (if it has camera)
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) // activity matches camera activity
        {
            if (resultCode == RESULT_OK) // everything work gud
            {
                // by this point we have the camera photo on disk
                // rotate and them store image
                Bitmap takenImage = rotateBitmapOrientation(photoFile.getPath());
                //Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getPath());
                // RESIZE BITMAP, see section below
                Bitmap resizedImage = BitmapScaler.scaleToFitWidth(takenImage, 750);
                // load taken image into the uploaded image ImageView
                ivUpload.setImageBitmap(resizedImage);
            }
            else // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
        }
    }

    // returns photo stored on disk given photoFileName
    // URI: univeral resource identifier -- a string that unambiguously identifies a resource
    private File getPhotoFileUri(String fileName)
    {
        // get storage directory for photos; getExternalFilesDir gets package-specific directories
        // ensures no need to request external read/write permissions
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // if storage dir doesn't exist: create it
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs())
        {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void savePost(String description, ParseUser currentUser, File photoFile)
    {
        Post post = new Post();
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUser);
        post.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if (e != null)
                {
                    Log.e(TAG, "Error in saving post: " + e.getLocalizedMessage());
                    return;
                }
                Log.i(TAG, "Post saved successfully!");
                etDescription.setText(""); // visual indicator of success; makes sure user doesn't save post twice
                ivUpload.setImageResource(0); //resetting image preview too
            }
        });
    }

    private void queryPosts()
    {
        // specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // specify object ID
        query.include(Post.KEY_USER);
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
            }
        });
    }
}