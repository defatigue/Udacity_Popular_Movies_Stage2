package com.abdulazeez.popularmovies;

/**
 * Created by ABDULAZEEZ on 9/8/2015.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.gson.Gson;

public class SharedPreference {

    public static final String PREFS_NAME = "PRODUCT_APP";
    public static final String FAVORITES = "Product_Favorite";

    public SharedPreference() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveFavorites(Context context, List<Fields> favorites) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.commit();
    }

    public void addFavorite(Context context, Fields product) {
        List<Fields> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<Fields>();
        favorites.add(product);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, Fields product) {
        ArrayList<Fields> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(product);
            saveFavorites(context, favorites);
        }
    }

    public ArrayList<Fields> getFavorites(Context context) {
        SharedPreferences settings;
        List<Fields> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            Fields[] favoriteItems = gson.fromJson(jsonFavorites,
                    Fields[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<Fields>(favorites);
        } else
            return null;

        return (ArrayList<Fields>) favorites;
    }
}