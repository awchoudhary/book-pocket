package com.awchoudhary.bookpocket.ui.editbookscreen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.awchoudhary.bookpocket.util.DateTimeHelper;
import com.awchoudhary.bookpocket.ui.mybooksscreen.MainActivity;
import com.awchoudhary.bookpocket.R;
import com.awchoudhary.bookpocket.ui.mybooksscreen.Book;
import com.awchoudhary.bookpocket.util.DatabaseHandler;
import com.awchoudhary.bookpocket.util.DatePickerCustom;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by awaeschoudhary on 3/5/17.
 * Handles logic for creating a new book and editing book. TODO: Class name is misleading. Change it.
 */
public class CreateBookActivity extends AppCompatActivity {
    private DateTimeHelper dateTimeHelper = new DateTimeHelper();
    //book that is being created/edited
    private Book book = new Book();

    //True if book is being created and False if it is being edited. True by default.
    private boolean newBook = true;

    //indicates if a new cover image was uploaded
    private boolean isNewCoverImage = false;

    //code for intents
    private static final int SELECT_IMAGE = 1;
    private static final int WRITE_IMAGE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_book);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        //if a book was passed to the view, populate inputs. Book is passed to view on add by search and edit.
        if(intent.hasExtra("book")){
            book = (Book)intent.getSerializableExtra("book");

            //if book has an ID, it already exists in the DB and is therefore being edited.
            if(book.getId() != null){
                newBook = false;
            }

            populate(book);
        }

        //set the date edit texts as date pickers
        new DatePickerCustom(this, (EditText) findViewById(R.id.datePublishedInput));

        //attached even handlers
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //perform save if we have permission to access storage to download cover image.
                if(isStoragePermissionGranted()){
                    boolean isSaved = save();
                    if(isSaved){
                        Intent intent = new Intent(CreateBookActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        //click event on cover imageview to upload cover image
        findViewById(R.id.coverImage).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //start gallery intent for user to select image
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_IMAGE);
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
            showMessage("Failed to create book");
        }
    }

    //called on resturn from dialog activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {
                Uri selectedImageUri = data.getData();
                String selectedImagePath = getPath(selectedImageUri);

                //download image into imageview.
                Glide.with(this)
                        .load(selectedImagePath)
                        .asBitmap() //ensures that bitmap is a BitmapDrawable object
                        .placeholder(R.drawable.default_cover_image_big)
                        .error(R.drawable.default_cover_image_big)
                        .override(257, 389) //TODO: Needs to be responsive
                        .into((ImageView)findViewById(R.id.coverImage));

                //mark our image uploaded flag as true
                isNewCoverImage = true;
            }
        }
    }

    //populates inputs in view with provided parameter
    private void populate(Book book){
        //populate text inputs
        ((EditText) findViewById(R.id.titleInput)).setText(book.getName());
        ((EditText) findViewById(R.id.subtitleInput)).setText(book.getSubtitle());
        ((EditText) findViewById(R.id.authorInput)).setText(book.getAuthor());
        ((EditText) findViewById(R.id.isbnInput)).setText(book.getIsbn());
        ((EditText) findViewById(R.id.publisherInput)).setText(book.getPublisher());
        ((EditText) findViewById(R.id.datePublishedInput)).setText(dateTimeHelper.toString(book.getDatePublished()));
        ((EditText) findViewById(R.id.numPagesInput)).setText(Integer.toString(book.getNumPages()));
        ((EditText) findViewById(R.id.descriptionInput)).setText(book.getDescription());

        //load cover image
        ImageView cover = (ImageView) findViewById(R.id.coverImage);
        Glide.with(this)
                .load(book.getCoverUrl())
                .placeholder(R.drawable.default_cover_image_big)
                .error(R.drawable.default_cover_image_big)
                .override(257, 389) //TODO: Needs to be responsive
                .into(cover);
    }

    private boolean save() {
        //handles db interactions
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        //get all text field inputs
        String title = ((EditText) findViewById(R.id.titleInput)).getText().toString();
        String subtitle = ((EditText) findViewById(R.id.subtitleInput)).getText().toString();
        String author = ((EditText) findViewById(R.id.authorInput)).getText().toString();
        String isbn = ((EditText) findViewById(R.id.isbnInput)).getText().toString();
        String publisher = ((EditText) findViewById(R.id.publisherInput)).getText().toString();
        String datePublished = ((EditText) findViewById(R.id.datePublishedInput)).getText().toString();
        String numPages = ((EditText) findViewById(R.id.numPagesInput)).getText().toString();
        String description = ((EditText) findViewById(R.id.descriptionInput)).getText().toString();

        //Validation of inputs
        if(title.equals("")){
            showMessage("Book must have a title.");
            return false;
        }
        if(author.equals("")){
            showMessage("Book must have an author.");
            return false;
        }

        //update book with text inputs
        book.setName(title);
        book.setSubtitle(subtitle);
        book.setAuthor(author);
        book.setNumPages((!numPages.equals("")) ? Integer.parseInt(numPages) : 0);
        book.setIsbn(isbn);
        book.setPublisher(publisher);
        book.setDatePublished(dateTimeHelper.toDateTime(datePublished));
        book.setDescription(description);

        //save the cover image if it was changed
        if(isNewCoverImage){
            saveCoverImage(book);
        }

        //update or create a new book
        if(newBook){
            dbHandler.createBook(book);
        }
        else{
            dbHandler.updateBook(book);
        }

        return true;
    }

    //save cover image on device and set book coverUrl to the local path TODO: Error checking of some sort
    private void saveCoverImage(Book book){
        //set the local image path (where image is going to be downloaded) as cover url
        File localImageFile = getOutputImageFile();

        book.setCoverUrl(localImageFile.getPath());

        //get bitmap for the image in imageview
        ImageView imageView = (ImageView) findViewById(R.id.coverImage);
        Bitmap bitmap;

        //The bitmap for default cover image is of type BitmapDrawable whereas images obtained from the api are GlideBitmapDrawables.
        if(imageView.getDrawable() instanceof GlideBitmapDrawable){
            bitmap = ((GlideBitmapDrawable)imageView.getDrawable()).getBitmap();
        }
        else {
            bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        }

        //save image to local path
        SaveImageTask task = new SaveImageTask(this, bitmap);
        task.execute(localImageFile);
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
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    //check if permission was granted to access storage. If not, ask for it. Only for > Marshmallow.
    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                //ask for permission from user to access storage.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_IMAGE);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    private String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    //display toast
    private void showMessage(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}
