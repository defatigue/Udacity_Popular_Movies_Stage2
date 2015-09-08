package com.abdulazeez.popularmovies;

//import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {

    public DetailsActivityFragment() {
    }

    TextView original_title, overview, vote_average, release_date;
    ListView lv_trailer;
    ListView lv_reviews;
    ImageView iv_poster;
    View rootView;
    Bitmap mIcon_val = null;
    List<String> trailer_id = new ArrayList<>();
    List<String> trailer_key = new ArrayList<>();
    List<String> trailer_name = new ArrayList<>();

    List<String> review_id = new ArrayList<>();
    List<String> review_author = new ArrayList<>();
    List<String> review_content = new ArrayList<>();
    List<String> review_url = new ArrayList<>();
    ListAdapter adapter;
    String movie_id;
    Button btn_favorite;
    List<Fields> products = new ArrayList<>();
    //Set<String> favorites_set;
    Set<String> get_favorites = new HashSet<>();
    SharedPreference sharedPreference = new SharedPreference();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        original_title = (TextView) rootView.findViewById(R.id.original_title);
        iv_poster = (ImageView) rootView.findViewById(R.id.iv_poster);
        btn_favorite = (Button) rootView.findViewById(R.id.btn_favorite);



        overview = (TextView) rootView.findViewById(R.id.overview);
        vote_average = (TextView) rootView.findViewById(R.id.vote_average);
        release_date = (TextView) rootView.findViewById(R.id.release_date);
        Intent intent = getActivity().getIntent();
        /* new GetImageTask(iv_poster).execute(intent.getStringExtra("backdrop").toString()); */
        movie_id = intent.getStringExtra("id");
        //Log.v("DeatailActivity", "movie_id " + movie_id);
        original_title.setText(intent.getStringExtra("original_title"));
        overview.setText(intent.getStringExtra("overview"));
        vote_average.setText(" "+intent.getStringExtra("vote_average"));
        release_date.setText(" " + intent.getStringExtra("release_date"));

        lv_trailer = (ListView) rootView.findViewById(R.id.lv_trailers);
        lv_reviews = (ListView) rootView.findViewById(R.id.lv_reviews);


        btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                Set<String> favorites_set = sharedPref.getStringSet("favorited", null);
                favorites_list.add(movie_id);

                if(favorites_set == null){
                    favorites_set = new HashSet<>();
                    //favorites_set.addAll(favorites_list);
                    //editor.putStringSet("favorited",favorites_set);
                    }
                else {
                    favorites_set.addAll(favorites_list);
                    Log.v("Button.OnClick", "save_favorites " + favorites_set);
                    editor.putStringSet("favorited", favorites_set);
                }
                editor.commit();
                //putStringArrayList("favorites", favorites);*/
                Fields fields = new Fields(movie_id, original_title.getText().toString(), overview.getText().toString(),
                        vote_average.getText().toString(), release_date.getText().toString());
                Log.v("Button.OnClick", "See JSON Favorites " + fields.toString());

                sharedPreference.addFavorite(getActivity(), fields);
                Toast.makeText(getActivity(),
                        "",
                        Toast.LENGTH_SHORT).show();


            }
        });


        if(savedInstanceState != null){
            mIcon_val = savedInstanceState.getParcelable("mIcon_val");
            iv_poster.setImageBitmap(mIcon_val);
        }
        else {
            new GetImageTask(iv_poster).execute(intent.getStringExtra("backdrop").toString());
            new GetTrailerTask().execute(movie_id);
            new GetReviewTask().execute(movie_id);
        }

            lv_trailer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Uri uri = Uri.parse("https://www.youtube.com/watch?v=").buildUpon()
                            .appendQueryParameter("v", trailer_key.get(position))
                            .build();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    if(intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                    else
                        Log.e("DetailsActivityFragment", "Could not resolve URI");
                }
            });


                return rootView;
    }


    class GetReviewTask extends AsyncTask<String, Void, List<String>>{



        @Override
        protected List<String> doInBackground(String... val) {
            String ret = val[0];
            final String BASE_URL = "http://api.themoviedb.org/3/movie/"+ret+"/reviews?";
            final String API_KEY = "api_key";
            final String ID = "id";
            final String AUTHOR = "author";
            final String CONTENT = "content";
            final String URL = "url";

            String review_id_string, review_author_string, review_content_string, review_url_string;


            try {
                String new_url = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY, "173bbb14e0161501f7da931746c0d538")
                        .build().toString();
                //Log.v("bitmap", new_url.toString());
                URL url = new URL(new_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                BufferedReader br;
                InputStream in;
                String review_json = null;


                in = conn.getInputStream();
                br = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = br.readLine()) !=null){
                    sb.append(line)
                            .append("\n");
                }
                review_json = sb.toString();

                JSONObject obj = new JSONObject(review_json);
                JSONArray result = obj.getJSONArray("results");
                for(int i = 0; i<result.length(); i++) {
                    JSONObject obj2 = result.getJSONObject(i);
                    review_id_string = obj2.getString(ID);
                    review_id.add(review_id_string);
                    Log.v("BackgroundTask", review_id.get(i));
                    review_author_string = obj2.getString(AUTHOR);
                    review_author.add(review_author_string);
                    Log.v("BackgroundTask", review_author.get(i));
                    review_content_string = obj2.getString(CONTENT);
                    review_content.add(review_content_string);
                    Log.v("BackgroundTask", review_content.get(i));
                    review_url_string = obj2.getString(URL);
                    review_url.add(review_url_string);
                    Log.v("BackgroundTask", review_url.get(i));
                }

            }catch(MalformedURLException e)
            {
                Log.e("Async", "MalformedURLException",e);

            } catch (IOException e) {
                Log.e("Async", "IOException",e);
            } catch (JSONException e) {
                Log.e("Async", "JSONException", e);
            } finally {
            }

            return review_content;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);

            adapter = new ArrayAdapter(getActivity(), R.layout.list_text, R.id.tv_list, strings);
            Log.v("BackgroundTask", " "+strings);
            lv_reviews.setAdapter(adapter);
        }
    }


    class GetTrailerTask extends AsyncTask<String, Void, List<String>>{



        @Override
        protected List<String> doInBackground(String... val) {
            String ret = val[0];
            final String BASE_URL = "http://api.themoviedb.org/3/movie/"+ret+"/videos?";
            final String API_KEY = "api_key";
            final String ID = "id";
            final String KEY = "key";
            final String NAME = "name";

            String id, key, name;


             try {
                String new_url = Uri.parse(BASE_URL).buildUpon()
                                .appendQueryParameter(API_KEY, "173bbb14e0161501f7da931746c0d538")
                                .build().toString();
                //Log.v("bitmap", new_url.toString());
                URL url = new URL(new_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                BufferedReader br;
                InputStream in;
                String trailer_json = null;


                in = conn.getInputStream();
                br = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = br.readLine()) !=null){
                    sb.append(line)
                            .append("\n");
                  }
                trailer_json = sb.toString();

                JSONObject obj = new JSONObject(trailer_json);
                JSONArray result = obj.getJSONArray("results");
                for(int i = 0; i<result.length(); i++) {
                    JSONObject obj2 = result.getJSONObject(i);
                    id = obj2.getString(ID);
                    trailer_id.add(id);
                    Log.v("BackgroundTask", trailer_id.get(i));
                    key = obj2.getString(KEY);
                    trailer_key.add(key);
                    Log.v("BackgroundTask", trailer_key.get(i));
                    name = obj2.getString(NAME);
                    trailer_name.add(name);
                    Log.v("BackgroundTask", trailer_name.get(i));
                  }

            }catch(MalformedURLException e)
            {
                Log.e("Async", "MalformedURLException",e);

            } catch (IOException e) {
                Log.e("Async", "IOException",e);
            } catch (JSONException e) {
                 Log.e("Async", "JSONException", e);
            } finally {
            }

            return trailer_name;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);

            adapter = new ArrayAdapter(getActivity(), R.layout.list_text, R.id.tv_list, strings);
            Log.v("BackgroundTask", " "+strings);
            lv_trailer.setAdapter(adapter);
        }
    }

    class GetImageTask extends AsyncTask<String, Void, Bitmap>{
        ImageView bmImage;

        ProgressDialog progress = new ProgressDialog(getActivity());

        public GetImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }


        @Override
        protected Bitmap doInBackground(String... val) {

            final String BASE_URL = "http://image.tmdb.org/t/p/w342/";
            String ret = val[0];
            try {
                URL new_url = new URL(BASE_URL + ret);
                //Log.v("bitmap", new_url.toString());
                HttpURLConnection conn = (HttpURLConnection) new_url.openConnection();
                InputStream in = conn.getInputStream();

                mIcon_val = BitmapFactory.decodeStream(in);

                //return mIcon_val;
            }catch(MalformedURLException e)
            {
            Log.e("Async", "MalformedURLException",e);

            } catch (IOException e) {
                Log.e("Async", "IOException",e);
            }
            finally {
            }

            //iv_poster.setImageBitmap(mIcon_val);

            return mIcon_val;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setMessage("Loading... Please wait.");
            progress.show();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progress.dismiss();
            bmImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("mIcon_val", mIcon_val);
        super.onSaveInstanceState(outState);
    }
}
