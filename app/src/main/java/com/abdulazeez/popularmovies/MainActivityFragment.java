package com.abdulazeez.popularmovies;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.abdulazeez.popularmovies.data.MovieContract;
import com.abdulazeez.popularmovies.data.MovieDbHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    //Declare Global Variables
    ImageAdapter mMovieAdapter;
    ImageView iv_posters;
    View rootView;
    GridView gridview;
    ArrayList<String> movie_id = new ArrayList<>();
    ArrayList<String> backdrop = new ArrayList<>();
    ArrayList<String> original_title = new ArrayList<>();
    ArrayList<String> overview = new ArrayList<>();
    ArrayList<String> vote_average = new ArrayList<>();
    ArrayList<String> release_date = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridview = (GridView) rootView.findViewById(R.id.grid_view);
        iv_posters = (ImageView) rootView.findViewById(R.id.iv_posters);

        //Get the default/saved settings data
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = sp.getString("sort_list", "popularity.desc");
if(savedInstanceState != null){
    //List<String> backdrop = new ArrayList<>();
    movie_id = savedInstanceState.getStringArrayList("id");
    backdrop = savedInstanceState.getStringArrayList("backdrop");
    original_title = savedInstanceState.getStringArrayList("original_title");
    overview = savedInstanceState.getStringArrayList("overview");
    vote_average = savedInstanceState.getStringArrayList("vote_average");
    release_date = savedInstanceState.getStringArrayList("release_date");
    mMovieAdapter = new ImageAdapter(getActivity(), backdrop);
    gridview.setAdapter(mMovieAdapter);

}
            else {
                    getPoster();
                        }



        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent intent = new Intent(getActivity(), DetailsActivity.class)
                        .putExtra("id", movie_id.get(position))
                        .putExtra("backdrop", backdrop.get(position))
                        .putExtra("original_title", original_title.get(position))
                        .putExtra("overview", overview.get(position))
                        .putExtra("vote_average", vote_average.get(position))
                        .putExtra("release_date", release_date.get(position));
                startActivity(intent);
                // Toast.makeText(getActivity(), "" + position,
                //         Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private void getPoster() {
        /*FetchMovieTask movieTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        movieTask.execute(location);*/
        //Get the default/saved settings data
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = sp.getString("sort_list", "popularity.desc");
        //Check that there exists an internet connection on the phone
            ConnectivityManager connMgr = (ConnectivityManager)
                    getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                //Send current sort preference to background task
                //new FetchMovieTask().execute(sort);

                FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity(), gridview);
                movie_id = fetchMovieTask.getMovieId();
                backdrop = fetchMovieTask.getBackdrop();
                original_title = fetchMovieTask.getOriginalTitle();
                overview = fetchMovieTask.getOverview();
                vote_average = fetchMovieTask.getVoteAverage();
                release_date = fetchMovieTask.getReleaseDate();
                fetchMovieTask.execute(sort);
            } else {
                MovieDbHelper mOpenHelper = new MovieDbHelper(getActivity());
                //Log.v("bulkInsert", "Show Context " + mOpenHelper);
                final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                String movie_id_string;
                String backdrop_string;
                String original_title_string;
                String overview_string;
                String vote_average_string;
                String release_date_string;
                Cursor MovieCursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,  // Table to Query
                        null, // leaving "columns" null just returns all the columns.
                        null, // cols for "where" clause
                        null, // values for "where" clause
                        null, // columns to group by
                        null, // columns to filter by row groups
                        null  // sort order
                );

                if (MovieCursor != null ) {
                    if  (MovieCursor.moveToFirst()) {
                        do {
                            movie_id_string = MovieCursor.getString(MovieCursor.getColumnIndex("id"));
                            movie_id.add(movie_id_string);
                            //Log.v("Movie ID", " "+movie_id);
                            backdrop_string = MovieCursor.getString(MovieCursor.getColumnIndex("backdrop"));
                            //Log.v("BackDrop Cursor", backdrop_string);
                            backdrop.add("" + backdrop_string);
                            original_title_string = MovieCursor.getString(MovieCursor.getColumnIndex("movie_title"));
                            original_title.add(original_title_string);
                            overview_string = MovieCursor.getString(MovieCursor.getColumnIndex("synopsis"));
                            overview.add(overview_string);
                            vote_average_string = MovieCursor.getString(MovieCursor.getColumnIndex("vote_average"));
                            vote_average.add(vote_average_string);
                            release_date_string = MovieCursor.getString(MovieCursor.getColumnIndex("release_date"));
                            release_date.add(release_date_string);
                        }while (MovieCursor.moveToNext());
                    }
                }
               /* MovieCursor.moveToFirst();
                while (MovieCursor.isAfterLast() == false){
                   // movie_id_string = MovieCursor.getString(MovieCursor.getColumnIndex("id"));
                   // movie_id.add(movie_id_string);
                    backdrop_string = MovieCursor.getString(MovieCursor.getColumnIndex("backdrop"));
                    Log.v("BackDrop Cursor", backdrop_string);
                    backdrop.add(backdrop_string);
                    //original_title_string = MovieCursor.getString(MovieCursor.getColumnIndex("movie_title"));
                    //original_title.add(original_title_string);
                    //overview_string = MovieCursor.getString(MovieCursor.getColumnIndex("synopsis"));
                    //overview.add(overview_string);
                    //vote_average_string = MovieCursor.getString(MovieCursor.getColumnIndex("vote_average"));
                    //vote_average.add(vote_average_string);
                    //release_date_string = MovieCursor.getString(MovieCursor.getColumnIndex("release_date"));
                    //release_date.add(release_date_string);
                    }*/
                mMovieAdapter = new ImageAdapter(getActivity(), backdrop);
                gridview.setAdapter(mMovieAdapter);
                Toast.makeText(getActivity(), "No Internet Connectivity Detected",
                        Toast.LENGTH_SHORT).show();

            }
        }

    @Override
    public void onStart() {
        super.onStart();
        //getPoster();
    }

 /*   //Fetch data from internet using AsyncTask
    public class FetchMovieTask extends AsyncTask<String, Void, List<String>> {
        final String LOG_TAG = MainActivityFragment.class.getSimpleName();
        ProgressDialog progress;

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
                        //.appendQueryParameter(API_KEY, "INSERT_API_KEY")
                        .appendQueryParameter(API_KEY, "173bbb14e0161501f7da931746c0d538")
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
                //Log.v(LOG_TAG, "jsonArray "+jarray);
                String backdrop_text;
                String original_title_text;
                String overview_text;
                String vote_average_text;
                String release_date_text;
                String id_text;

                for(int i = 0; i < jarray.length(); i++){
                    JSONObject ret = jarray.getJSONObject(i);
                    backdrop_text = ret.getString("poster_path");
                    backdrop.add(backdrop_text);
                    original_title_text = ret.getString("original_title");
                    original_title.add(original_title_text);
                    overview_text = ret.getString("overview");
                    overview.add(overview_text);
                    vote_average_text = ret.getString("vote_average");
                    vote_average.add(vote_average_text);
                    release_date_text = ret.getString("release_date");
                    release_date.add(release_date_text);
                    id_text = ret.getString("id");
                    movie_id.add(id_text);

                    //Log.v(LOG_TAG, "json "+backdrop);
                }
                return backdrop;
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "doInBackground ",e);
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
            mMovieAdapter = new ImageAdapter(getActivity(), string);
            gridview.setAdapter(mMovieAdapter);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(getActivity());
            progress.setMessage("Loading... Please wait.");
            progress.show();
        }
    }
    */

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putStringArrayList("id", movie_id);
      outState.putStringArrayList("backdrop", backdrop);
        outState.putStringArrayList("original_title", original_title);
        outState.putStringArrayList("overview", overview);
        outState.putStringArrayList("vote_average", vote_average);
        outState.putStringArrayList("release_date", release_date);


        super.onSaveInstanceState(outState);
    }


}
