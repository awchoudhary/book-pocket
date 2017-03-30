package com.awchoudhary.bookpocket;

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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.app.Activity.RESULT_OK;

/**
 * Created by awaeschoudhary on 3/23/17.
 */

public class EditDetailsTabFragment extends Fragment {
    View view;
    boolean newBook = true; //True if book is being created and False if it is being edited. True by default.
    Book book = new Book();
    private boolean isNewCoverImage = false; //indicates if a new cover image was uploaded

    //codes for intents
    private static final String BOOK_KEY = "book_key";
    private static final int SELECT_IMAGE = 1;
    private static final int WRITE_IMAGE = 2;

    public static EditDetailsTabFragment newInstance(Book book) {
        EditDetailsTabFragment fragment = new EditDetailsTabFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BOOK_KEY, book);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.edit_details_tab_fragment, container, false);
        book = (Book) getArguments().getSerializable(BOOK_KEY); // get Book to be Edited

        //if book has an ID, it already exists in the DB and is therefore being edited.
        if(book.getId() != null){
            newBook = false;
        }
        populate(book);

        //attached even handlers
        Button saveButton = (Button) view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //perform save if we have permission to access storage to download cover image.
                if(isStoragePermissionGranted()){
                    boolean isSaved = save();
                    if(isSaved){ //TODO: Navigate to view book instead. When back is pressed from there, we must load the main activity.
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        //click event on cover imageview to upload cover image
        view.findViewById(R.id.coverImage).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //start gallery intent for user to select image
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_IMAGE);
            }
        });
        return view;
    }

    //called after user responds to storage access request.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
            save();
            Intent intent = new Intent(getActivity(), MainActivity.class);
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
                        .into((ImageView)view.findViewById(R.id.coverImage));

                //mark our image uploaded flag as true
                isNewCoverImage = true;
            }
        }
    }

    //populates inputs in view with provided parameter
    private void populate(Book book){
        //used for parsing dates to string
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");

        //populate text inputs
        ((EditText) view.findViewById(R.id.titleInput)).setText(book.getName());
        ((EditText) view.findViewById(R.id.subtitleInput)).setText(book.getSubtitle());
        ((EditText) view.findViewById(R.id.authorInput)).setText(book.getAuthor());
        ((EditText) view.findViewById(R.id.numPagesInput)).setText(Integer.toString(book.getNumPages()));
        ((EditText) view.findViewById(R.id.descriptionInput)).setText(book.getDescription());
        ((EditText) view.findViewById(R.id.notesInput)).setText(book.getNotes());

        //set dates if non null
        ((EditText) view.findViewById(R.id.dateStartedInput)).setText((book.getDateStarted() != null) ? dateFormatter.print(book.getDateStarted()) : "");
        ((EditText) view.findViewById(R.id.dateCompletedInput)).setText((book.getDateCompleted() != null) ? dateFormatter.print(book.getDateCompleted()) : "");

        ImageView cover = (ImageView) view.findViewById(R.id.coverImage);
        Glide.with(this)
                .load(book.getCoverUrl())
                .placeholder(R.drawable.default_cover_image_big)
                .error(R.drawable.default_cover_image_big)
                .override(257, 389) //TODO: Needs to be responsive
                .into(cover);
    }

    private boolean save() {
        //handles db interactions
        DatabaseHandler dbHandler = new DatabaseHandler(getActivity());

        //date formatter used to parse date strings
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");


        //get all text field inputs
        String title = ((EditText) view.findViewById(R.id.titleInput)).getText().toString();
        String subtitle = ((EditText) view.findViewById(R.id.subtitleInput)).getText().toString();
        String author = ((EditText) view.findViewById(R.id.authorInput)).getText().toString();
        String numPages = ((EditText) view.findViewById(R.id.numPagesInput)).getText().toString();
        String dateStarted = ((EditText) view.findViewById(R.id.dateStartedInput)).getText().toString();
        String dateCompleted = ((EditText) view.findViewById(R.id.dateCompletedInput)).getText().toString();
        String description = ((EditText) view.findViewById(R.id.descriptionInput)).getText().toString();
        String notes = ((EditText) view.findViewById(R.id.notesInput)).getText().toString();

        //Validation of inputs
        if(title.equals("")){
            showMessage("Book must have a title.");
            return false;
        }
        if(author.equals("")){
            showMessage("Book must have an author.");
            return false;
        }
        if(dateStarted.equals("") && !dateCompleted.equals("")){
            showMessage("Book must have start date if it has a complete date.");
            return false;
        }

        //update book with text inputs
        book.setName(title);
        book.setSubtitle(subtitle);
        book.setAuthor(author);
        book.setNumPages((!numPages.equals("")) ? Integer.parseInt(numPages) : 0);
        book.setDateStarted((!dateStarted.equals("")) ? dateFormatter.parseDateTime(dateStarted) : null);
        book.setDateCompleted((!dateCompleted.equals("")) ? dateFormatter.parseDateTime(dateCompleted) : null);
        book.setDescription(description);
        book.setNotes(notes);

        //save and set cover image. For now we are just not saving if edit. TODO: Take care of image uploading and updating
        if(isNewCoverImage){
            saveCoverImage(book);
        }

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
        ImageView imageView = (ImageView) view.findViewById(R.id.coverImage);
        Bitmap bitmap;

        //The bitmap for default cover image is of type BitmapDrawable whereas images obtained from the api are GlideBitmapDrawables.
        if(imageView.getDrawable() instanceof GlideBitmapDrawable){
            bitmap = ((GlideBitmapDrawable)imageView.getDrawable()).getBitmap();
        }
        else {
            bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        }

        //save image to local path
        SaveImageTask task = new SaveImageTask(getActivity(), bitmap);
        task.execute(localImageFile);
    }

    /* Create a File for saving an image */
    private File getOutputImageFile(){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getPath()
                + "/Android/data/"
                + getActivity().getApplicationContext().getPackageName()
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
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                //ask for permission from user to access storage.
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_IMAGE);
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
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getActivity().getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    //display toast
    private void showMessage(String message){
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
