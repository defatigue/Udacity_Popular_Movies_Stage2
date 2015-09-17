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

import javax.security.auth.callback.Callback;

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
    SharedPreference sharedPreference;
    List<Fields> favorites;

    private Callbacks mCallbacks = smyCallbacks;

    public interface Callbacks {
        public void onItemSelected(String id, String backdrop, String title, String overview, String vote, String date);
    }

    private static Callbacks smyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id, String backdrop, String title, String overview, String vote, String date) {
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallbacks = smyCallbacks;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridview = (GridView) rootView.findViewById(R.id.grid_view);
        iv_posters = (ImageView) rootView.findViewById(R.id.iv_posters);

        //Get the default/saved settings data
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = sp.getString("sort_list", "popularity.desc");
        //Log.v("getSort", " "+sort);
        if(sort.matches("favorites")){
            // Get favorite items from SharedPreferences.
            sharedPreference = new SharedPreference();
            favorites = sharedPreference.getFavorites(getActivity());
            //Log.v("getFav", " "+favorites);


            if (favorites == null) {
                Toast.makeText(getActivity(),
                        "Your favourites selection list is empty",
                        Toast.LENGTH_SHORT).show();
            }
                if (favorites.size() == 0) {
                    Toast.makeText(getActivity(),
                            "There is no item in your favorites list",
                            Toast.LENGTH_SHORT).show();
                }

                    for(int i = 0; i< favorites.size(); i++){
                        movie_id.add(favorites.get(i).getId());
                        backdrop.add(favorites.get(i).getBackdrop());
                        original_title.add(favorites.get(i).getOriginalTitle());
                        overview.add(favorites.get(i).getOverview());
                        vote_average.add(favorites.get(i).getVoteAverage());
                        release_date.add(favorites.get(i).getReleaseDate());
                        }

                    mMovieAdapter = new ImageAdapter(getActivity(), backdrop);
                    gridview.setAdapter(mMovieAdapter);
            }

    else {
                if (savedInstanceState != null) {
                    //List<String> backdrop = new ArrayList<>();
                    movie_id = savedInstanceState.getStringArrayList("id");
                    backdrop = savedInstanceState.getStringArrayList("backdrop");
                    original_title = savedInstanceState.getStringArrayList("original_title");
                    overview = savedInstanceState.getStringArrayList("overview");
                    vote_average = savedInstanceState.getStringArrayList("vote_average");
                    release_date = savedInstanceState.getStringArrayList("release_date");
                    mMovieAdapter = new ImageAdapter(getActivity(), backdrop);
                    gridview.setAdapter(mMovieAdapter);

                } else {
                    getPoster();
                }
            }

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                        mCallbacks.onItemSelected(movie_id.get(position), backdrop.get(position), original_title.get(position), overview.get(position),  vote_average.get(position), release_date.get(position));


                }
            });

        return rootView;
    }

    private void getPoster() {
        //Get the default/saved settings data
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = sp.getString("sort_list", "popularity.desc");
        //Check that there exists an internet connection on the phone
            ConnectivityManager connMgr = (ConnectivityManager)
                    getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {

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
