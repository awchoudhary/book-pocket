package com.awchoudhary.bookpocket;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by awaeschoudhary on 2/25/17.
 */

public class SearchBooksTask extends
        AsyncTask<String, Void, ArrayList<Book>> {

    private Context context;
    private ProgressDialog progressDialog;
    private String key = "wl31upk83ZjeZZ3JEQWRQ";
    private String baseUrl = "https://www.goodreads.com/search/index.xml?key=" + key + "&q=";
    private SearchResultsAdaptor adaptor;

    public SearchBooksTask(Context context, SearchResultsAdaptor adaptor){
        this.adaptor = adaptor;
        this.context = context;
    }

    protected void onPreExecute() {
        //show progress dialog
        progressDialog = ProgressDialog.show(context, "", "Loading", true);
    }

    protected ArrayList<Book> doInBackground(String... params) {
        String query = params[0];
        // Do some validation here

        try {
            URL url = new URL(baseUrl + URLEncoder.encode(query, "UTF-8"));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                return parseResponse(urlConnection.getInputStream());
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
        progressDialog.dismiss();
    }

    //parse xml response from goodreads into a books list
    private ArrayList<Book> parseResponse(InputStream is) throws XmlPullParserException, IOException {
        Book book = new Book(); //pointer to current book
        String text = ""; //text between tags
        ArrayList<Book> results = new ArrayList<>(); //list of results

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(is, null);

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (tagName.equalsIgnoreCase("work")) {
                        book = new Book();
                    }
                    break;

                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;

                case XmlPullParser.END_TAG:
                    if (tagName.equalsIgnoreCase("work")) {
                        results.add(book);
                    }else if (tagName.equalsIgnoreCase("title")) {
                        book.setName(text);
                    }  else if (tagName.equalsIgnoreCase("name")) {
                        book.setAuthor(text);
                    } else if (tagName.equalsIgnoreCase("image_url")) {
                        book.setCoverUrl(text);
                    }
                    break;

                default:
                    break;
            }
            eventType = parser.next();
        }

        return results;
    }
}
