package com.abdulazeez.popularmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {

    public DetailsActivityFragment() {
    }

    TextView original_title, overview, vote_average, release_date;
    ImageView iv_poster;
    View rootView;
    Bitmap mIcon_val = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        original_title = (TextView) rootView.findViewById(R.id.original_title);
        iv_poster = (ImageView) rootView.findViewById(R.id.iv_poster);

        overview = (TextView) rootView.findViewById(R.id.overview);
        vote_average = (TextView) rootView.findViewById(R.id.vote_average);
        release_date = (TextView) rootView.findViewById(R.id.release_date);
        Intent intent = getActivity().getIntent();
        //new GetImageTask(iv_poster).execute(intent.getStringExtra("backdrop").toString());
        original_title.setText(intent.getStringExtra("original_title"));
        overview.setText(intent.getStringExtra("overview"));
        vote_average.setText(" "+intent.getStringExtra("vote_average"));
        release_date.setText(" " + intent.getStringExtra("release_date"));

        if(savedInstanceState != null){
            mIcon_val = savedInstanceState.getParcelable("mIcon_val");
            iv_poster.setImageBitmap(mIcon_val);
        }
        else {
            new GetImageTask(iv_poster).execute(intent.getStringExtra("backdrop").toString());
        }

                return rootView;
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
                Log.v("bitmap", new_url.toString());
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
