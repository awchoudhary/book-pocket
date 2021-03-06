package com.awchoudhary.bookpocket.ui.searchscreen;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.awchoudhary.bookpocket.ui.mybooksscreen.Book;
import com.awchoudhary.bookpocket.util.DateTimeHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by awaeschoudhary on 2/25/17.
 */

public class GoogleBooksSearchTask extends
        AsyncTask<String, Void, ArrayList<Book>> {

    private DateTimeHelper dateTimeHelper = new DateTimeHelper();
    private Context context;
    private ProgressDialog progressDialog;
    private String key = "AIzaSyB_Jum6jlzpm7ayQ3xexXplNVtmurc3Gb4";
    private String baseUrl = "https://www.googleapis.com/books/v1/volumes?key=" + key + "&q=";
    private SearchResultsAdaptor adaptor;

    public GoogleBooksSearchTask(Context context, SearchResultsAdaptor adaptor){
        this.adaptor = adaptor;
        this.context = context;
    }

    /*
    protected void onPreExecute() {
        //show progress dialog
        progressDialog = ProgressDialog.show(context, "", "Loading", true);
    }*/

    protected ArrayList<Book> doInBackground(String... params) {
        String query = params[0];

        // Do some validation here
        if(query.trim().isEmpty()){
            return new ArrayList<Book>();
        }

        try {
            URL url = new URL(baseUrl + URLEncoder.encode(query, "UTF-8"));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                return parseResponse(sb.toString());
            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    protected void onPostExecute(ArrayList<Book> results) {
        //update list view and hide dialog
        adaptor.updateEntries(results);
        //progressDialog.dismiss();
    }

    //parse json response from google books
    private ArrayList<Book> parseResponse(String jsonResponse) throws IOException, JSONException {
        //lists to be returned
        ArrayList<Book> results = new ArrayList<Book>();
        JSONObject reader = new JSONObject(jsonResponse);

        //holds list of json books
        JSONArray items = new JSONArray();

        //get the array of search results
        if(reader.has("items")){
            items = reader.getJSONArray("items");
        }

        //populate results list from items array
        for(int i = 0; i < items.length(); i++){
            Book book = new Book();
            JSONObject jsonBook = items.getJSONObject(i);
            JSONObject volumeInfo = new JSONObject(); //the volumeInfo object contains most information about the book

            if(jsonBook.has("volumeInfo")) {
                volumeInfo = jsonBook.getJSONObject("volumeInfo");
            }

            if(volumeInfo.has("title")) {
                book.setName(volumeInfo.getString("title"));
            }

            //create authors string from the authors json array
            if(volumeInfo.has("authors")) {
                JSONArray authors = volumeInfo.getJSONArray("authors");
                StringBuilder authorsSb = new StringBuilder();
                for (int j = 0; j < authors.length(); j++) {
                    if (j == authors.length() - 1) {
                        authorsSb.append(authors.getString(j));
                    } else {
                        authorsSb.append(authors.getString(j)).append(", ");
                    }
                }
                book.setAuthor(authorsSb.toString());
            }

            if(volumeInfo.has("pageCount")) {
                book.setNumPages(volumeInfo.getInt("pageCount"));
            }
            if(volumeInfo.has("description")){
                book.setDescription(volumeInfo.getString("description"));
            }

            //image links object contains thumbnail urls
            if(volumeInfo.has("imageLinks")){
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                if(imageLinks.has("thumbnail")){
                    book.setCoverUrl(imageLinks.getString("thumbnail"));
                }
            }

            if(volumeInfo.has("subtitle")){
                book.setSubtitle(volumeInfo.getString("subtitle"));
            }

            if(volumeInfo.has("publisher")){
                book.setPublisher(volumeInfo.getString("publisher"));
            }

            if(volumeInfo.has("publishedDate")){
                //convert the date from yyyy-mm-dd to mm/dd/yyyy format
                String[] dateNums = volumeInfo.getString("publishedDate").split("-");
                //make sure we have the numbers for month date and year
                if(dateNums.length == 3){
                    book.setDatePublished(dateNums[1] + "/" + dateNums[2] + "/" + dateNums[0]);
                }
            }

            if(volumeInfo.has("industryIdentifiers")){
                JSONArray industryIdentifiers = (JSONArray) volumeInfo.get("industryIdentifiers");
                if(industryIdentifiers.length() > 0){
                    //the ISBN 13 should be the first object. If not, we'll just grab the 10.
                    book.setIsbn(((JSONObject)industryIdentifiers.get(0)).get("identifier").toString());
                }
            }

            results.add(book);
        }

        return results;

    }
}
