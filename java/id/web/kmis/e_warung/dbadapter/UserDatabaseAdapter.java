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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class UserDatabaseAdapter {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static UserDatabaseAdapter instance;

    Calendar c = Calendar.getInstance();
    DateFormat df = new SimpleDateFormat("d MMM yyyy");
    DateFormat dt = new SimpleDateFormat("HH:mm");

    String date = df.format(Calendar.getInstance().getTime());

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    private UserDatabaseAdapter(Context context) {
        //SQLiteDatabase.loadLibs(context);
        this.openHelper = new DatabaseOpenHelperUser(context);
    }

    /**
     * Return a singleton instance of SetDatabaseAdapter.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static UserDatabaseAdapter getInstance(Context context) {
        if (instance == null) {
            instance = new UserDatabaseAdapter(context);
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


    //insert  appuq1
    public boolean updateIdKey(String id) {
        //Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);
        Cursor cursor = database.query("setting", null, "value=?",
                new String[]{id}, null, null, null);
        if (cursor.getCount() < 1) // belum pernah registrasi
        {
            ContentValues updatedValues = new ContentValues();
            updatedValues.put("name", "app_uq1");
            updatedValues.put("value", id);

            String where = " name = ?";
            database.update("setting", updatedValues, where, new String[]{"app_uq1"});
            cursor.close();
            return true;
        }
        return false;
    }

    //insert appdev__
    public boolean updateAppKey(String id) {
        //Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);
        Cursor cursor = database.query("setting", null, "value=?",
                new String[]{id}, null, null, null);
        if (cursor.getCount() < 1) // belum pernah registrasi
        {
            ContentValues updatedValues = new ContentValues();
            updatedValues.put("name", "app_dev");
            updatedValues.put("value", id);
            String where = " name = ?";
            database.update("setting", updatedValues, where, new String[]{"app_dev"});
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
        database.update("setting", updatedValues, where, new String[]{names});
    }

    public void insertEntry(String userName, String password) {
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("username", userName);
        newValues.put("password", password);

        database.insert("users", null, newValues);

    }

    //check appuq1
    public boolean checkkeydevexist() {
        Cursor cursor = database.query("setting", null, " name=?",
                new String[]{"app_dev"}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return false;
        }

        cursor.moveToFirst();
        String xx = cursor.getString(2);

        if (xx == null || xx.length() == 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }


    public String getkey(String name, String key) {
        String keys;
        keys = "";
        Cursor cursor = database.query("setting", null, " " + name + "=?",
                new String[]{key}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            if (cursor.moveToFirst()) {
                do {
                    keys = cursor.getString(1);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return keys;
        }
        cursor.close();
        return keys;
    }

    public String[] getSingleEntry(String userName) {
        Cursor cursor = database.query("users", null, " username = ?", new String[]{userName}, null, null, null);
        String[] resp = new String[4];
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            resp[0] = null;
            return resp;
        }
        resp[0] = "ok";
        if (cursor.moveToFirst()) {
            do {
                resp[1] = cursor.getString(cursor.getColumnIndex("password"));
                resp[2] = cursor.getString(cursor.getColumnIndex("uq1"));
                resp[3] = cursor.getString(cursor.getColumnIndex("ex1"));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return resp;
    }

    //check if user available if yes update if no insert
    public boolean UserUi(String userName, String PassWord) {
        //Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);
        Cursor cursor = database.query("users", null, " username=?",
                new String[]{userName}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            insertEntry(userName, PassWord);
            cursor.close();
            return false;
        }
        cursor.close();
        //update user and password for particular user;
        updateEntry(userName, PassWord);
        //""String passWord = cursor.getString(cursor.getColumnIndex("password"));
        return true;
    }


    public void insertUsers(HashMap<String, String> queryValues) {
        // get current time

        Cursor cursor = database.query("users", null, " username=?",
                new String[]{queryValues.get("userName")}, null, null, null);
        if (cursor.getCount() < 1) // anggota belum ada
        {
            ContentValues values = new ContentValues();
            values.put("username", queryValues.get("userName"));
            values.put("password", queryValues.get("passWord"));
            values.put("ex1", queryValues.get("namawarung"));
            values.put("uq1", queryValues.get("idanggota"));
            values.put("nw1", queryValues.get("level"));
            values.put("Tgl_lastlogin", df.format(Calendar.getInstance().getTime()));
            values.put("Wkt_lastlogin", dt.format(Calendar.getInstance().getTime()));
            values.put("updateStatus", "no");

            values.put("success", "1");
            database.insert("users", null, values);
            cursor.close();

        }
        cursor.close();
        //db.close();
    }

    public String getidusers(String email) {
        String mm = "";
        email = email.trim();
        Cursor cursor = database.query("users", null, " username=?",
                new String[]{email}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {

            cursor.close();
            return "";

        }
        if (cursor.moveToFirst()) {
            mm = cursor.getString(cursor.getColumnIndex("uq1"));

            //if (mm == null || mm.length() == 0) {
            //    cursor.close();
            //    return "";
            // }


        }
        cursor.close();
        return mm;
    }

    public String getlvusers(String email) {
        String mm = "";
        email = email.trim();
        Cursor cursor = database.query("users", null, " username=?",
                new String[]{email}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {

            cursor.close();
            return "";

        }
        if (cursor.moveToFirst()) {
            mm = cursor.getString(cursor.getColumnIndex("nw1"));

            //if (mm == null || mm.length() == 0) {
            //    cursor.close();
            //    return "";
            // }


        }
        cursor.close();
        return mm;
    }

    public void updateEntry(String userName, String password) {
        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put("username", userName);
        updatedValues.put("password", password);

        String where = "username = ?";
        database.update("users", updatedValues, where, new String[]{userName});
    }

    public void deleteuser(String user) {
        String where = " username =?";
        int numberOFEntriesDeleted = database.delete("users", where,
                new String[]{user});
    }

}