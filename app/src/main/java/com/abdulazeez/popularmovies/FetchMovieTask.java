package com.abdulazeez.popularmovies;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.abdulazeez.popularmovies.data.MovieContract;
import com.abdulazeez.popularmovies.data.MovieDbHelper;
import com.abdulazeez.popularmovies.data.MovieProvider;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by ABDULAZEEZ on 9/7/2015.
 */
//Fetch data from internet using AsyncTask
public class FetchMovieTask extends AsyncTask<String, Void, List<String>> {
    final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    ProgressDialog progress;
    private ImageAdapter mMovieAdapter;
    private final Context mContext;
    private GridView gridview;

    private ArrayList<String> movie_id = new ArrayList<>();
    private ArrayList<String> backdrop = new ArrayList<>();
    private ArrayList<String> original_title = new ArrayList<>();
    private ArrayList<String> overview = new ArrayList<>();
    private ArrayList<String> vote_average = new ArrayList<>();
    private ArrayList<String> release_date = new ArrayList<>();
   // MovieProvider mP = new MovieProvider();
    ContentValues[] cv;

    static final int MOVIE = 100;
    static final int TRAILER_WITH_MOVIEID = 101;
    static final int REVIEW_WITH_MOVIEID = 102;
    static final int TRAILER = 200;
    static final int REVIEW = 300;


    public FetchMovieTask(Context context, GridView view) {
        mContext = context;
        gridview = view;
        //mMovieAdapter = imageAdapter;
    }

    public ArrayList<String> getMovieId(){
        return movie_id;
    }

    public ArrayList<String> getBackdrop(){
        return backdrop;
    }

    public ArrayList<String> getOriginalTitle(){
        return original_title;
    }

    public ArrayList<String> getOverview(){
        return overview;
    }

    public ArrayList<String> getVoteAverage(){
        return vote_average;
    }

    public ArrayList<String> getReleaseDate(){
        return release_date;
    }

    @Override
    protected List<String> doInBackground(String... view) {

        //String val = view[0];
        final String BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
        final String API_KEY = "api_key";
        final String QUERY_PARAM = "sort_by";

        String sort_by = view[0];
        HttpURLConnection http = null;
        BufferedReader br;
        InputStream is = null;
        String movieJson;

        //Building the URI
        String my_uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, sort_by)
                        .appendQueryParameter(API_KEY, "INSERT_API_KEY")
                .build().toString();
        //Log.v(LOG_TAG, "URI " + my_uri);


        try {
            URL url = new URL(my_uri);
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.connect();
            is = http.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line;
            while((line = br.readLine()) != null){
                sb.append(line).append("\n");
            }
            movieJson = sb.toString();

            JSONObject obj = new JSONObject(movieJson);
            JSONArray jarray = obj.getJSONArray("results");
            Vector<ContentValues> cVVector = new Vector<>(jarray.length());
            String backdrop_text;
            String original_title_text;
            String overview_text;
            String vote_average_text;
            String release_date_text;
            String id_text;
            cv = new ContentValues[jarray.length()];
            for(int i = 0; i < jarray.length(); i++){
                ContentValues movieValues = new ContentValues();
                JSONObject ret = jarray.getJSONObject(i);
                backdrop_text = ret.getString("poster_path");
                backdrop.add(backdrop_text);
                movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP, backdrop_text);
                original_title_text = ret.getString("original_title");
                original_title.add(original_title_text);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, original_title_text);
                overview_text = ret.getString("overview");
                overview.add(overview_text);
                movieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, overview_text);
                vote_average_text = ret.getString("vote_average");
                vote_average.add(vote_average_text);
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, vote_average_text);
                release_date_text = ret.getString("release_date");
                release_date.add(release_date_text);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, release_date_text);
                id_text = ret.getString("id");
                movie_id.add(id_text);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id_text);
                cv[i] = movieValues;
            }


            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + cv.length + " Inserted");
            return backdrop;
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "doInBackground ", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "doInBackground ", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "doInBackground", e);
        } finally {
            assert http != null;
            http.disconnect();
            if(is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "InputStream Closure ", e);
                }
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<String> string) {
        super.onPostExecute(string);
        if(progress != null)
            progress.dismiss();
        mMovieAdapter = new ImageAdapter(mContext, string);
        gridview.setAdapter(mMovieAdapter);
        Uri movie_uri = MovieContract.MovieEntry.CONTENT_URI;
        bulkInsert(movie_uri, cv);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = new ProgressDialog(mContext);
        progress.setMessage("Loading... Please wait.");
        progress.show();
    }

    public int bulkInsert(Uri uri, ContentValues[] values) {
        final UriMatcher sUriMatcher = buildUriMatcher();
        MovieDbHelper mOpenHelper = new MovieDbHelper(mContext);
        Log.v("bulkInsert", "Show Context " + mOpenHelper);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        //normalizeDate(value);
                        Log.v("bulkInsert", "Show Values " + values);
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        Log.v("bulkInsert", "Show _id " + _id);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                mContext.getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return bulkInsert(uri, values);
        }
    }

    static UriMatcher buildUriMatcher() {

        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher matcher  = new UriMatcher(UriMatcher.NO_MATCH);
        final String AUTHORITY = MovieContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.
        matcher.addURI(AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(AUTHORITY, MovieContract.PATH_TRAILER, TRAILER);
        matcher.addURI(AUTHORITY, MovieContract.PATH_REVIEW, REVIEW);
        matcher.addURI(AUTHORITY, MovieContract.PATH_TRAILER+"/#", TRAILER_WITH_MOVIEID);
        matcher.addURI(AUTHORITY, MovieContract.PATH_REVIEW + "/#", REVIEW_WITH_MOVIEID);

        // 3) Return the new matcher!
        return matcher;
    }
}
