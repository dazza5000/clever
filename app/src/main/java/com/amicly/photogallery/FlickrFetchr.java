package com.amicly.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by darrankelinske on 10/26/17.
 */

public class FlickrFetchr {

    private static final String TAG = FlickrFetchr.class.getSimpleName();

    private static final String API_KEY = "823cd629a3524dc2b836601250969a40";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);

        HttpURLConnection httpURLConnection =
                (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream byteArrayOutputStream =
                    new ByteArrayOutputStream();

            InputStream inputStream = httpURLConnection.getInputStream();

            if (httpURLConnection.getResponseCode() !=  HttpURLConnection.HTTP_OK) {
                throw new IOException(httpURLConnection.getResponseMessage() + ": " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while((bytesRead = inputStream.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } finally {
            httpURLConnection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchItems() {

        List<GalleryItem> items = new ArrayList<>();

        try {
            String url = Uri.parse("https://api.flickr.com/services/rest")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();

            String json = getUrlString(url);
            JSONObject jsonBody = new JSONObject(json);
            parseItems(items, jsonBody);
            Log.i(TAG, "Received JSON: " +json);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON", e);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        return items;
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws IOException,
            JSONException{

        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            if (!photoJsonObject.has("url_s")) {
                continue;
            }

            item.setUrl(photoJsonObject.getString("url_s"));
            items.add(item);
        }
    }
}
