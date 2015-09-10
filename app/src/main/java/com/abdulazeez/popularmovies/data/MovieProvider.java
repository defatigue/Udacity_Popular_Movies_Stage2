package com.abdulazeez.popularmovies.data;

/**
 * Created by ABDULAZEEZ on 9/6/2015.
 */
import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class MovieProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;


    static final int MOVIE = 100;
    static final int TRAILER_WITH_MOVIEID = 101;
    static final int REVIEW_WITH_MOVIEID = 102;
    static final int TRAILER = 200;
    static final int REVIEW = 300;

    private static final SQLiteQueryBuilder sTrailerByMovieIdSettingQueryBuilder;
    private static final SQLiteQueryBuilder sReviewByMovieIdSettingQueryBuilder;

    static{
        sTrailerByMovieIdSettingQueryBuilder = new SQLiteQueryBuilder();



        sTrailerByMovieIdSettingQueryBuilder.setTables(
                MovieContract.TrailerEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.TrailerEntry.TABLE_NAME +
                        "." + MovieContract.TrailerEntry.COLUMN_MOV_KEY +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry._ID);
    }

    static{
        sReviewByMovieIdSettingQueryBuilder = new SQLiteQueryBuilder();


        sReviewByMovieIdSettingQueryBuilder.setTables(
                MovieContract.ReviewEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.ReviewEntry.TABLE_NAME +
                        "." + MovieContract.ReviewEntry.COLUMN_MOV_KEY +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry._ID);
    }

    //movie.movie_setting = ?
    private static final String sTrailerSettingSelection =
            MovieContract.TrailerEntry.TABLE_NAME+
                    "." + MovieContract.TrailerEntry.COLUMN_MOV_KEY + " = ? ";

   private Cursor getTrailerByMovieId(Uri uri, String[] projection, String sortOrder) {
        String trailerSetting = MovieContract.TrailerEntry.getidFromUri(uri);

        String[] selectionArgs;
        String selection;


            selection = sTrailerSettingSelection;
            selectionArgs = new String[]{trailerSetting};


        return sTrailerByMovieIdSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getReviewByMovieId(
            Uri uri, String[] projection, String sortOrder) {
        String reviewSetting = MovieContract.ReviewEntry.getidFromUri(uri);

        String[] selectionArgs;
        String selection;


        selection = sTrailerSettingSelection;
        selectionArgs = new String[]{reviewSetting};

        return sReviewByMovieIdSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher  = new UriMatcher(UriMatcher.NO_MATCH);
        final String AUTHORITY = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(AUTHORITY, MovieContract.PATH_TRAILER, TRAILER);
        matcher.addURI(AUTHORITY, MovieContract.PATH_REVIEW, REVIEW);
        matcher.addURI(AUTHORITY, MovieContract.PATH_TRAILER+"/#", TRAILER_WITH_MOVIEID);
        matcher.addURI(AUTHORITY, MovieContract.PATH_REVIEW+"/#", REVIEW_WITH_MOVIEID);

        // 3) Return the new matcher!
        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
           return true;

    }


    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {

            case TRAILER_WITH_MOVIEID:
            return MovieContract.TrailerEntry.CONTENT_ITEM_TYPE;
            case REVIEW_WITH_MOVIEID:
                return MovieContract.ReviewEntry.CONTENT_ITEM_TYPE;
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "trailer/*"
            case TRAILER_WITH_MOVIEID:
            {
                retCursor = getTrailerByMovieId(uri, projection, sortOrder);
                break;
            }
            // "Review/*"
            case REVIEW_WITH_MOVIEID: {
                retCursor = getReviewByMovieId(uri, projection, sortOrder);
                break;
            }
            // "movie"
            case MOVIE: {
                retCursor = null;
                break;
            }
            // "location"
            case REVIEW: {
                retCursor = null;
                break;
            }
            //"trailer"
            case TRAILER: {
                retCursor = null;
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {

            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                //normalizeDate(values);
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
                db.beginTransaction();
                int returnCount = 0;
                try {
                        long _id = db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
                        if (_id != -1) {
                            returnCount++;
                        }

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
        }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)) {
            long dateValue = values.getAsLong(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, MovieContract.normalizeDate(dateValue));
        }
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
//Log.v("bulkInsert", "Show Context "+mOpenHelper);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
