package com.abdulazeez.popularmovies.data;

/**
 * Created by ABDULAZEEZ on 9/6/2015.
 */

        import android.content.ContentResolver;
        import android.content.ContentUris;
        import android.net.Uri;
        import android.provider.BaseColumns;
        import android.text.format.Time;

/**
 * Defines table and column names for the Movies database.
 */
public class MovieContract {


    public static final String CONTENT_AUTHORITY = "com.abdulazeez.popularmovies";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_MOVIE = "movie";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_TRAILER = "trailer";



    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class TrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String TABLE_NAME = "trailer";

        // Column with the foreign key into the Trailer table.
        public static final String COLUMN_MOV_KEY = "id";

        // Trailer ID
        public static final String COLUMN_TRAILER_ID = "trailer_id";

        // Trailer KEY
        public static final String COLUMN_TRAILER_KEY = "trailer_key";

        // Trailer NAME
        public static final String COLUMN_TRAILER_NAME = "trailer_name";

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTrailer(String trailerSetting) {
            return null;
        }

        public static Uri buildTrailerWithId(String movieid) {
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(COLUMN_MOV_KEY, movieid).build();

        }

        public static String getidFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


    }


    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String TABLE_NAME = "review";

        // Column with the foreign key into the Trailer table.
        public static final String COLUMN_MOV_KEY = "id";

        // Review ID
        public static final String COLUMN_REVIEW_ID = "review_id";

        // Review Author
        public static final String COLUMN_REVIEW_AUTHOR = "review_author";

        // Review Content
        public static final String COLUMN_REVIEW_CONTENT = "review_content";

        // Review URL
        public static final String COLUMN_REVIEW_URL = "review_url";

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildReview(String trailerSetting) {
            return null;
        }

        public static Uri buildReviewWithId(String movieid) {
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(COLUMN_MOV_KEY, movieid).build();

        }

        public static String getidFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }



    /* Inner class that defines the table contents of the Movie table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";


        // Movie id as returned by API, to identify the icon to be used
        public static final String COLUMN_MOVIE_ID = "id";

        // Title of the movie, as provided by API.
        public static final String COLUMN_MOVIE_TITLE = "movie_title";

        // Vote Average, as provided by API.
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_RELEASE_DATE = "release_date";

        // Synopsis, as provided by API.
        public static final String COLUMN_SYNOPSIS = "synopsis";

        // Synopsis, as provided by API.
        public static final String COLUMN_BACKDROP = "backdrop";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }



    }
}
