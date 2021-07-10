package com.example.instagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instagram.BitmapScaler;
import com.example.instagram.Post;
import com.example.instagram.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends Fragment
{
    public static final String TAG = "ComposeFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 11;
    private File photoFile;
    public String photoFileName = "photo.jpg";
    EditText etDescription;
    ImageView ivUpload;
    Button btnUpload, btnSubmit;

    public ComposeFragment()
    {
        // Required empty public constructor
    }

    // called when Fragment should create its view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    // called when view is created; do actions now
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        etDescription = view.findViewById(R.id.etDescription);
        ivUpload = view.findViewById(R.id.ivUpload);
        btnUpload = view.findViewById(R.id.btnUpload);
        btnSubmit = view.findViewById(R.id.btnSubmit);

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
                    Toast.makeText(getContext(), "description cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (photoFile == null || ivUpload.getDrawable() == null)
                {
                    Toast.makeText(getContext(), "there is no image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(description, currentUser, photoFile);
            }
        });
    }

    public Bitmap rotateBitmapOrientation(String photoFilePath)
    {
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
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        // places output from camera (wrapped fileProvider obj) into intent
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // check for nullity; app will crash if startActivityForResult() is called on an intent the app cannot handle (if it has camera)
        if (intent.resolveActivity(getContext().getPackageManager()) != null)
        {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) // activity matches camera activity
        {
            if (resultCode == RESULT_OK) // everything work gud
            {
                // by this point we have the camera photo on disk
                // rotate and then store image
                Bitmap takenImage = rotateBitmapOrientation(photoFile.getPath());
                //Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getPath());
                // RESIZE BITMAP, see section below
                Bitmap resizedImage = BitmapScaler.scaleToFitWidth(takenImage, 750);
                // load taken image into the uploaded image ImageView
                ivUpload.setImageBitmap(resizedImage);
            }
            else // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
        }
    }

    // returns photo stored on disk given photoFileName
    // URI: univeral resource identifier -- a string that unambiguously identifies a resource
    private File getPhotoFileUri(String fileName)
    {
        // get storage directory for photos; getExternalFilesDir gets package-specific directories
        // ensures no need to request external read/write permissions
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

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
}