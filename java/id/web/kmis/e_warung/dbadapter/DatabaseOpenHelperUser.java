package id.web.kmis.e_warung.dbadapter;

/**
 * Created by js on 8/18/2016.
 */

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseOpenHelperUser extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "log.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseOpenHelperUser(Context context) {
        //super(context, DATABASE_NAME, null, DATABASE_VERSION);
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}