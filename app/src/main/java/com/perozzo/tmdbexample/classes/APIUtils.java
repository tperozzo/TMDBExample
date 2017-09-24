package com.perozzo.tmdbexample.classes;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Perozzo on 15/09/2017.
 * APIUtils class to communicate with the server and parse data
 */

public final class APIUtils {
    public static String LOG_TAG = APIUtils.class.getSimpleName();
    private static String GET = "GET";
    private static String IMAGE_URL = "https://image.tmdb.org/t/p/w500/";

    private APIUtils() {
    }

    //Method to search Movies
    public static List<Movie> fetchMovies(String requestUrl){
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try
        {
            jsonResponse = makehttpRequest(GET, url);
        }catch (IOException e){
            Log.e(LOG_TAG,"Error in making http request",e);
        }
        List<Movie> result = extractMovies(jsonResponse);
        return result;
    }

    //Method to create an URL
    private static URL createUrl(String stringUrl){
        URL url = null;
        try
        {
            url = new URL(stringUrl);
        }catch (MalformedURLException e){
            Log.e(LOG_TAG,"Error in Creating URL",e);
        }
        return url;
    }

    //Method to connect and request the server
    private static String makehttpRequest(String requestMethod, URL url) throws IOException{
        String jsonResponse = "";
        if(url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod(requestMethod);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200 || urlConnection.getResponseCode() == 201) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error in connection!! Bad Response ");
            }

        }catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the movies JSON results.", e);
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //Method to parse movies
    private static List<Movie> extractMovies(String moviesJSON){
        if (TextUtils.isEmpty(moviesJSON)) {
            return null;
        }
        ArrayList<Movie>  movies = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(moviesJSON);
            JSONArray moviesArray = baseJsonResponse.getJSONArray("results");

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject currentMovie = moviesArray.getJSONObject(i);

                String title = currentMovie.getString("title");
                String year;
                if(currentMovie.getString("release_date").length() > 4)
                    year = currentMovie.getString("release_date").substring(0,4);
                else
                    year = "";
                String poster_path = IMAGE_URL + currentMovie.getString("poster_path");
                String backdrop_path = IMAGE_URL + currentMovie.getString("backdrop_path");
                int vote_count = currentMovie.getInt("vote_count");
                double vote_average = currentMovie.getDouble("vote_average");
                String overview = currentMovie.getString("overview");

                Movie movie = new Movie(title, year, poster_path, backdrop_path, vote_count, vote_average, overview);
                movies.add(movie);
            }

        }catch (JSONException e){
            Log.e(LOG_TAG,"Error in fetching data",e);
        }
        return movies;
    }
}
