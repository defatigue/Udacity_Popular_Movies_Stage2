package com.abdulazeez.popularmovies.data;

/**
 * Created by ABDULAZEEZ on 9/6/2015.
 */
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.abdulazeez.popularmovies.data.MovieContract.ReviewEntry;
import com.abdulazeez.popularmovies.data.MovieContract.TrailerEntry;
import com.abdulazeez.popularmovies.data.MovieContract.MovieEntry;


public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();


    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                ReviewEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                TrailerEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Review table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Trailer table during delete", 0, cursor.getCount());
        cursor.close();
    }


    public void deleteAllRecordsFromDB() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
        db.delete(MovieContract.ReviewEntry.TABLE_NAME, null, null);
        db.close();
    }


    public void deleteAllRecords() {
        deleteAllRecordsFromDB();
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }


    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();


        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {

            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: WeatherProvider registered with authority: " + providerInfo.authority +
                    " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }


    public void testGetType() {
        String type = mContext.getContentResolver().getType(ReviewEntry.CONTENT_URI);
        assertEquals("Error: the ReviewEntry CONTENT_URI should return ReviewEntry.CONTENT_TYPE",
               ReviewEntry.CONTENT_TYPE, type);

        String testMovieId = "94074";
        type = mContext.getContentResolver().getType(
                ReviewEntry.buildReviewWithId(testMovieId));
        assertEquals("Error: the ReviewEntry CONTENT_URI with location should return ReviewEntry.CONTENT_TYPE",
                ReviewEntry.CONTENT_TYPE, type);

         type = mContext.getContentResolver().getType(TrailerEntry.CONTENT_URI);
        assertEquals("Error: the ReviewEntry CONTENT_URI should return ReviewEntry.CONTENT_TYPE",
                TrailerEntry.CONTENT_TYPE, type);

         testMovieId = "94074";
        type = mContext.getContentResolver().getType(ReviewEntry.CONTENT_URI);
        assertEquals("Error: the ReviewEntry CONTENT_URI with location should return ReviewEntry.CONTENT_TYPE",
                ReviewEntry.CONTENT_TYPE, type);


        type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);
    }



    public void testBasicWeatherQuery() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
        long locationRowId = TestUtilities.insertNorthPoleLocationValues(mContext);


        ContentValues MovieValues = TestUtilities.createMovieValues();

        long weatherRowId = db.insert(MovieEntry.TABLE_NAME, null, MovieValues);
        assertTrue("Unable to Insert WeatherEntry into the Database", weatherRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor weatherCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicMovieQuery", weatherCursor, MovieValues);
    }

    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertMovieValues(long locationRowId) {
        long currentTestDate = TestUtilities.TEST_DATE;
        long millisecondsInADay = 1000*60*60*24;
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, currentTestDate+= millisecondsInADay ) {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 49026);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, currentTestDate);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 1.1);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "MADMAX");
            movieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, "There's a lot to talk about here");
            movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP, "kmgkfrhehmpethms;p");
            returnContentValues[i] = movieValues;
        }
        return returnContentValues;
    }


}

