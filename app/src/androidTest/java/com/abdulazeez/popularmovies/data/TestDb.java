package com.abdulazeez.popularmovies.data;

/**
 * Created by ABDULAZEEZ on 9/6/2015.
 */
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }


    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
       do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );


        assertTrue("Error: Your database was created without entry tables",
                tableNameHashSet.isEmpty());


        db.close();
    }


    public void testReviewTable() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.ReviewEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );


        assertTrue("Error: Your database was created without entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" +MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> MovieColumnHashSet = new HashSet<String>();
        MovieColumnHashSet.add(MovieContract.MovieEntry._ID);
        MovieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        MovieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_BACKDROP);
        MovieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
        MovieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        MovieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_SYNOPSIS);
        MovieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
           String columnName = c.getString(columnNameIndex);
            MovieColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required entry columns",
                MovieColumnHashSet.isEmpty());
        db.close();
    }


    public void testTrailerTable() {


    }


    public void testMovieTable() {

    }



    public long insertLocation() {
        return -1L;
    }
}
