package id.web.kmis.e_warung.dbadapter;

/**
 * Created by js on 8/19/2016.
 */

import android.content.ContentValues;
import android.content.Context;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
//import android.database.sqlite.SQLiteDatabase;

import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SetDatabaseAdapter {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static SetDatabaseAdapter instance;

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    private SetDatabaseAdapter(Context context) {
        SQLiteDatabase.loadLibs(context);

        this.openHelper = new DatabaseOpenHelperSet(context);
    }

    /**
     * Return a singleton instance of SetDatabaseAdapter.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static SetDatabaseAdapter getInstance(Context context) {
        if (instance == null) {
            instance = new SetDatabaseAdapter(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase("passworddb");

    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    /**
     * Read all quotes from the database.
     *
     * @return a List of quotes
     */
    public List<String> getQuotes() {
        List<String> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM quotes", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public boolean updateHostKeytl1(String id) {
        //Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);
        Cursor cursor = database.query("setting", null, "value=?",
                new String[]{id}, null, null, null);
        if (cursor.getCount() < 1) // belum pernah registrasi
        {
            ContentValues updatedValues = new ContentValues();
            updatedValues.put("name", "app_hst");
            updatedValues.put("value", id);

            String where = " name = ?";
            database.update("setting", updatedValues, where, new String[]{"app_hst"});
            cursor.close();
            return true;
        }
        return false;

    }


    //insert  appuq1
    public boolean updateIdKeytl1(String id) {
        //Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);
        Cursor cursor = database.query("setting", null, "value=?",
                new String[] { id }, null, null, null);
        if (cursor.getCount() < 1) // belum pernah registrasi
        {
            ContentValues updatedValues = new ContentValues();
            updatedValues.put("name", "app_uq1");
            updatedValues.put("value", id);

            String where = " name = ?";
            database.update("setting", updatedValues, where, new String[] { "app_uq1" });
            cursor.close();
            return true;
        }
        return false;

    }

    //insert  appuq1
    public boolean updateIdKeytl2(String id) {
        //Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);
        Cursor cursor = database.query("setting", null, "value=?",
                new String[]{id}, null, null, null);
        if (cursor.getCount() < 1) // belum pernah registrasi
        {
            ContentValues updatedValues = new ContentValues();
            updatedValues.put("name", "app_dt_1");
            updatedValues.put("value", id);

            String where = " name = ?";
            database.update("setting", updatedValues, where, new String[]{"app_dt_1"});
            cursor.close();
            return true;
        }
        return false;

    }


    //insert  appuq1
    public boolean updateIdKeytmp1(String id) {
        //Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);
        Cursor cursor = database.query("setting", null, "value2=?",
                new String[]{id}, null, null, null);
        if (cursor.getCount() < 1) // belum pernah registrasi
        {
            ContentValues updatedValues = new ContentValues();
            updatedValues.put("name", "app_uq1");
            updatedValues.put("value2", id);

            String where = " name = ?";
            database.update("setting", updatedValues, where, new String[]{"app_uq1"});
            cursor.close();
            return true;
        }
        return false;

    }

    //insert  appuq1
    public boolean updateIdKeytmp2(String id) {
        //Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);
        Cursor cursor = database.query("setting", null, "value2=?",
                new String[]{id}, null, null, null);
        if (cursor.getCount() < 1) // belum pernah registrasi
        {
            ContentValues updatedValues = new ContentValues();
            updatedValues.put("name", "app_dt_1");
            updatedValues.put("value2", id);

            String where = " name = ?";
            database.update("setting", updatedValues, where, new String[]{"app_dt_1"});
            cursor.close();
            return true;
        }
        return false;

    }

    public int deleteKey(String iduser) {
        String name = "";
        Cursor cursor = database.query("setting", null, "value2 =?",
                new String[]{iduser}, null, null, null);
        if (cursor.getCount() < 1) // belum pernah registrasi
        {

        } else {
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex("name"));
                cursor.close();

                ContentValues updatedValues = new ContentValues();
                //updatedValues.put("name", "app_dev");
                updatedValues.put("value", "");
                updatedValues.put("value2", "");
                String where = " name = ?";
                database.update("setting", updatedValues, where, new String[]{name});

                //where = " uq1 =?";
                //int numberOFEntriesDeleted = database.delete("setting", where,
                //        new String[]{iduser});

                return 1;
            }
        }
        // Toast.makeText(context,
        // "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted,
        // Toast.LENGTH_LONG).show();
        return 0;
    }


    //insert appdev__
    public boolean updateAppKey(String id) {
        //Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);
        Cursor cursor = database.query("setting", null, "value=?",
                new String[] { id }, null, null, null);
        if (cursor.getCount() < 1) // belum pernah registrasi
        {
            ContentValues updatedValues = new ContentValues();
            updatedValues.put("name", "app_dev");
            updatedValues.put("value", id);
             String where = " name = ?";
            database.update("setting", updatedValues, where, new String[] { "app_dev" });
            cursor.close();
            return true;
        }
        return false;
    }

    public void updateEntrykey(String names, String appk) {
        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put("name", names);
        updatedValues.put("value", appk);

        String where = names + " = ?";
        database.update("setting", updatedValues, where, new String[] { names });
    }


    //check appuq1  todo teamleader 1
    public boolean checkkeyexist() {
        Cursor cursor = database.query("setting", null, " name=?",
                new String[] { "app_uq1" }, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {

            cursor.close();
            return false;

        }
        if (cursor.moveToFirst()) {
            String xx = cursor.getString(cursor.getColumnIndex("value"));

            if (xx == null || xx.length() == 0) {
                cursor.close();
                return false;
            } else {
                cursor.close();
                return true;
            }
        }
        return false;
    }

    //check appuq1  todo teamleader 1
    public boolean checkkeyexist2() {
        Cursor cursor = database.query("setting", null, " name=?",
                new String[]{"app_dt_1"}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {

            cursor.close();
            return false;

        }
        if (cursor.moveToFirst()) {
            String xx = cursor.getString(2);

            if (xx == null || xx.length() == 0) {
                cursor.close();
                return false;
            } else {
                cursor.close();
                return true;
            }
        }
        return false;
    }

    //check appuq1  todo teamleader 1
    public boolean checkkeyexisttmp() {
        Cursor cursor = database.query("setting", null, " name=?",
                new String[]{"app_uq1"}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {

            cursor.close();
            return false;

        }
        if (cursor.moveToFirst()) {
            String xx = cursor.getString(3);

            if (xx == null || xx.length() == 0) {
                cursor.close();
                return false;
            } else {
                cursor.close();
                return true;
            }
        }
        return false;
    }

    //check appuq1  todo teamleader 1
    public boolean checkkeyexisttmp2() {
        Cursor cursor = database.query("setting", null, " name=?",
                new String[]{"app_dt_1"}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {

            cursor.close();
            return false;

        }
        if (cursor.moveToFirst()) {
            String xx = cursor.getString(3);

            if (xx == null || xx.length() == 0) {
                cursor.close();
                return false;
            } else {
                cursor.close();
                return true;
            }
        }
        return false;
    }

    //check appuq1
    public boolean checkkeydevexist() {
        Cursor cursor = database.query("setting", null, " name=?",
                new String[] { "app_dev" }, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return false;
        }

        if (cursor.moveToFirst()) {
            String xx = cursor.getString(2);

            if (xx == null || xx.length() == 0) {
                cursor.close();
                return false;
            } else {
                cursor.close();
                return true;
            }
        }
        return false;
    }


    public String getkey(String key) {
        String keys;
        keys = "";
        Cursor cursor = database.query("setting", null, " name=?",
                new String[] { key }, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "";
        }
        if (cursor.moveToFirst()) {
            keys = cursor.getString(cursor.getColumnIndex("value"));
        }
        cursor.close();
        return keys;
    }


}