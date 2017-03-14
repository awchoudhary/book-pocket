package com.awchoudhary.bookpocket;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by awaeschoudhary on 3/5/17.
 * Handles logic for creating a new book.
 */

public class CreateBookActivity extends AppCompatActivity {
    //True if book is being created and False if it is being edited. True by default.
    boolean newBook = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_book);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        //if a book was passed to the view, populate inputs. Book is passed to view on add by search and edit.
        if(intent.hasExtra("book")){
            Book book = (Book)intent.getSerializableExtra("book");

            //if book has an ID, it already exists in the DB and is therefore being edited.
            if(book.getId() != null){
                newBook = false;
            }

            populate(book);
        }

        //set the date edit texts as date pickers
        new DatePickerCustom(this, R.id.dateStartedInput);
        new DatePickerCustom(this, R.id.dateCompletedInput);

        //attached even handlers
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //perform save if we have permission to access storage to download cover image.
                if(isStoragePermissionGranted()){
                    save();
                    Intent intent = new Intent(CreateBookActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    //called after user responds to storage access request.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
            save();
            Intent intent = new Intent(CreateBookActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else{
            //phir lpc. Nahi banti book tumhaari.
            Toast.makeText(getApplicationContext(), "Failed to create book.", Toast.LENGTH_SHORT).show();
        }
    }

    //populates inputs in view with provided parameter
    private void populate(Book book){
        //used for parsing dates to string
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");

        //populate text inputs
        ((EditText) findViewById(R.id.bookId)).setText(book.getId());
        ((EditText) findViewById(R.id.titleInput)).setText(book.getName());
        ((EditText) findViewById(R.id.subtitleInput)).setText(book.getSubtitle());
        ((EditText) findViewById(R.id.authorInput)).setText(book.getAuthor());
        ((EditText) findViewById(R.id.numPagesInput)).setText(Integer.toString(book.getNumPages()));
        ((EditText) findViewById(R.id.descriptionInput)).setText(book.getDescription());
        ((EditText) findViewById(R.id.notesInput)).setText(book.getNotes());

        //set dates if non null
        ((EditText) findViewById(R.id.dateStartedInput)).setText((book.getDateStarted() != null) ? dateFormatter.print(book.getDateStarted()) : "");
        ((EditText) findViewById(R.id.dateCompletedInput)).setText((book.getDateCompleted() != null) ? dateFormatter.print(book.getDateCompleted()) : "");

        ImageView cover = (ImageView) findViewById(R.id.coverImage);
        Glide.with(this)
                .load(book.getCoverUrl())
                .placeholder(R.drawable.default_cover_image_big)
                .error(R.drawable.default_cover_image_big)
                .override(257, 389) //TODO: Needs to be responsive
                .into(cover);
    }

    private void save() {
        //handles db interactions
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        //date formatter used to parse date strings
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");


        //get all text field inputs
        String id = ((EditText) findViewById(R.id.bookId)).getText().toString();
        String title = ((EditText) findViewById(R.id.titleInput)).getText().toString();
        String subtitle = ((EditText) findViewById(R.id.subtitleInput)).getText().toString();
        String author = ((EditText) findViewById(R.id.authorInput)).getText().toString();
        String numPages = ((EditText) findViewById(R.id.numPagesInput)).getText().toString();
        String dateStarted = ((EditText) findViewById(R.id.dateStartedInput)).getText().toString();
        String dateCompleted = ((EditText) findViewById(R.id.dateCompletedInput)).getText().toString();
        String description = ((EditText) findViewById(R.id.descriptionInput)).getText().toString();
        String notes = ((EditText) findViewById(R.id.notesInput)).getText().toString();

        //TODO: Validation

        //populate new book object with inputs
        Book book = new Book();
        //It doesn't matter what the ID is when the book is being created, so don't worry about null id.
        //ID should be non-null when book is being edited. Tab bhi null hui tou mai kya karoon?
        book.setId(id);
        book.setName(title);
        book.setSubtitle(subtitle);
        book.setAuthor(author);
        book.setNumPages((!numPages.equals("")) ? Integer.parseInt(numPages) : 0);
        book.setDateStarted((!dateStarted.equals("")) ? dateFormatter.parseDateTime(dateStarted) : null);
        book.setDateCompleted((!dateCompleted.equals("")) ? dateFormatter.parseDateTime(dateCompleted) : null);
        book.setDescription(description);
        book.setNotes(notes);

        //TODO: Need to download image into device when book is created.
        //set the local image path (where image is going to be downloaded) as cover url
        File localImageFile = getOutputImageFile();
        if(localImageFile == null){
            Toast.makeText(getApplicationContext(), "Failed to create book.", Toast.LENGTH_SHORT).show();
            return;
        }
        book.setCoverUrl(localImageFile.getPath());

        //get bitmap for the image in imageview
        ImageView imageView = (ImageView) findViewById(R.id.coverImage);
        Bitmap bitmap;
        //The bitmap for default cover image is of type BitmapDrawable whereas images obtained from the api are GlideBitmapDrawables.
        if(imageView.getDrawable() instanceof GlideBitmapDrawable){
            bitmap = ((GlideBitmapDrawable)imageView.getDrawable()).getBitmap();
        }
        else{
            bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        }

        //save image to local path
        SaveImageTask task = new SaveImageTask(this, bitmap);
        task.execute(localImageFile);

        if(newBook){
            dbHandler.createBook(book);
        }
        else{
            dbHandler.updateBook(book);
        }
    }

    /* Create a File for saving an image */
    private File getOutputImageFile(){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getPath()
                                        + "/Android/data/"
                                        + getApplicationContext().getPackageName()
                                        + "/Files");

        //if directory doesn't exist, create one.
        if (! mediaStorageDir.exists()){
            mediaStorageDir.mkdirs();
        }

        // Create a media file name. Use current date and time to ensure unique name for all images.
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    //check if permission was granted to access storage. If not, ask for it. Only for > Marshmallow.
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                //ask for permission from user to access storage.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }


}
