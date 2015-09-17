package com.abdulazeez.popularmovies;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.abdulazeez.popularmovies.data.MovieProvider;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callbacks {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    FrameLayout fl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fl = (FrameLayout) findViewById(R.id.detail_container);
        if (findViewById(R.id.detail_container) != null) {
                        // The detail container view will be present only in the large-screen layouts
                                // (res/layout-sw600dp). If this view is present, then the activity should be
                                        // in two-pane mode.
                                                mTwoPane = true;
                        // In two-pane mode, show the detail view in this activity by
                                // adding or replacing the detail fragment using a
                                        // fragment transaction.
                                                if (savedInstanceState == null) {
                                getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.detail_container, new DetailsActivityFragment(), DETAILFRAGMENT_TAG)
                                                .commit();
                            }
                    } else {
                        mTwoPane = false;

            getSupportActionBar().setElevation(0f);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        MainActivityFragment maf = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        DetailsActivityFragment daf = (DetailsActivityFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Launch the Settings Activity on Click
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(String id, String backdrop, String original_title, String overview, String vote_average, String date) {
        if (mTwoPane) {
            fl.setVisibility(View.VISIBLE);
            Bundle arguments = new Bundle();
            arguments.putString("id", id);
            arguments.putString("backdrop", backdrop);
            arguments.putString("original_title", original_title);
            arguments.putString("overview", overview);
            arguments.putString("vote_average", vote_average);
            arguments.putString("release_date", date);
            DetailsActivityFragment fragment = new DetailsActivityFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment).commit();

        } else {
            Intent intent = new Intent(this, DetailsActivity.class)
                    .putExtra("id", id)
                    .putExtra("backdrop", backdrop)
                    .putExtra("original_title", original_title)
                    .putExtra("overview", overview)
                    .putExtra("vote_average", vote_average)
                    .putExtra("release_date", date);
            startActivity(intent);
        }
    }
}
