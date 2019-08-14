package xyz.ichanger.coinlet;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by johannespitts on 7/23/17.
 */

public class Preferences {
    public static void setPreferences(String key, Float value, Context context){
        //When fetching info from other activites, the context MUST be the activity you are fetching from
        if (value < 0) {
            value = (float) 0;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static Float getPreferences(String key, Context context) {
        //When fetching info from other activites, the context MUST be the activity you are fetching from
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getFloat(key, (float) 0);
    }
}
