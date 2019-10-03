package id.web.kmis.e_warung.dbadapter;


import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


import android.content.ContentValues;
import android.content.Context;


//import android.database.Cursor;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
//import android.database.sqlite.SQLiteDatabase;

import net.sqlcipher.database.SQLiteOpenHelper;

//import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class LoginDataBaseAdapter {

	// TODO: Create public field for each column in your table.

	public SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;
    private static LoginDataBaseAdapter instance;

    Calendar c = Calendar.getInstance();
    DateFormat df = new SimpleDateFormat("d MMM yyyy");
    DateFormat dt = new SimpleDateFormat("HH:mm");

    String date = df.format(Calendar.getInstance().getTime());


    public LoginDataBaseAdapter(Context _context) {
        // SQLiteDatabase.loadLibs(_context);
        this.dbHelper = new DatabaseOpenHelper(_context);
	}

	public void open(){

        db = dbHelper.getWritableDatabase("passworddb");

    }

	public void close() {
        if (db != null) {
            db.close();
        }
	}

    public static LoginDataBaseAdapter getInstance(Context context) {
        if (instance == null) {
            instance = new LoginDataBaseAdapter(context);
        }
        return instance;
    }

	public void insertEntry(String userName, String password) {
		ContentValues newValues = new ContentValues();
		// Assign values for each row.
		newValues.put("username", userName);
		newValues.put("password", password);

		db.insert("users", null, newValues);

	}

    public String[] encryptcredential(String... args) {
        String[] resp = new String[2];
        Mcrypt mmm = new Mcrypt();
        try {
            resp[0] = Mcrypt.bytesToHex(mmm.encrypt(args[0].trim()));
            resp[1] = Mcrypt.bytesToHex(mmm.encrypt(args[1].trim()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resp;
    }

    public Cursor insertRaw(String ttx) {

        return db.rawQuery(ttx, null);

    }

	public int deleteEntry(String UserName) {
		// String id=String.valueOf(ID);
		String where = "username=?";
		int numberOFEntriesDeleted = db.delete("users", where,
				new String[] { UserName });
		// Toast.makeText(context,
		// "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted,
		// Toast.LENGTH_LONG).show();
		return numberOFEntriesDeleted;
	}

    public void updateEntry(String userName, String password) {
        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
		updatedValues.put("username", userName);
		updatedValues.put("password", password);

		String where = "username = ?";
		db.update("users", updatedValues, where, new String[] { userName });
	}

    public void updateSite(String site_id, String locationName, String Address, String gps_long, String gps_lati, String operator, String owner, String comment, String filektp, String filefoto, String fileform, String nokartu, String ibukan, String posisix, String pendidikanx, String pekerjaanx, String usernamex) {
        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put("posisi", posisix); //posisi
        updatedValues.put("nama", locationName); //nama
        //updatedValues.put("idanggota", Address); //idanggota
        updatedValues.put("alamat", gps_long); //alamat
        updatedValues.put("telepon", gps_lati); //telepon
        updatedValues.put("tl", operator); //tempat lahir
        updatedValues.put("ttl", owner); //tanggal lahir
        updatedValues.put("noktp", comment); //no ktp
        updatedValues.put("nokartu", nokartu);
        updatedValues.put("namaibu", ibukan);
        updatedValues.put("pekerjaan", pekerjaanx);
        updatedValues.put("pendidikan", pendidikanx);
        updatedValues.put("Username", usernamex);
        //updatedValues.put("aktif", 0); //no ktp

        updatedValues.put("filektp", filektp); // lokasi file ktp
        updatedValues.put("filefoto", filefoto); // lokasi file foto
        updatedValues.put("fileform", fileform); // lokasi file form


        String where = "idanggota = ?";
        db.update("anggota", updatedValues, where, new String[]{Address});
    }

    public boolean SiteUi(String site_id, String locationName, String Address, String gps_long, String gps_lati, String operator, String owner, String comment, String filektp, String filefoto, String fileform, String nokartu, String ibukan, String posisix, String pendidikanx, String pekerjaanx, String usernamex) {
        //Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);

            Mcrypt mmm = new Mcrypt();
            try {
                Address = Mcrypt.bytesToHex(mmm.encrypt(Address.trim()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //encrypt dulu  address
            Cursor cursor = db.query("anggota", null, " idanggota=?",
                    new String[]{Address}, null, null, null);
            if (cursor.getCount() < 1) // location Not Exist
			{

                insertSiteEntry(site_id, locationName, Address, gps_long, gps_lati, operator, owner, comment, filektp, filefoto, fileform, nokartu, ibukan, posisix, pendidikanx, pekerjaanx, usernamex);

				cursor.close();
				return false;
			}
			
			cursor.close();
			//update user and password for particular user;
        updateSite(site_id, locationName, Address, gps_long, gps_lati, operator, owner, comment, filektp, filefoto, fileform, nokartu, ibukan, posisix, pendidikanx, pekerjaanx, usernamex);
        //""String passWord = cursor.getString(cursor.getColumnIndex("password"));
			return true;
		}

    public boolean SiteUistatusupdate(String Address, int success) {
        //Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);

        Mcrypt mmm = new Mcrypt();
        try {
            Address = Mcrypt.bytesToHex(mmm.encrypt(Address.trim()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Cursor cursor = db.query("anggota", null, " idanggota=?",
                new String[]{Address}, null, null, null);
        if (cursor.getCount() < 1) // location Not Exist
        {
            return false;
        }

        cursor.close();
        //update user and password for particular user;
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("success", success); // lokasi file form
        updatedValues.put("updated_data", 1); // lokasi file form

        String where = "idanggota = ?";
        db.update("anggota", updatedValues, where, new String[]{Address});
        return true;
    }

    public boolean SiteUistatusgambar(String Address, int success) {
        //Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);

        Mcrypt mmm = new Mcrypt();
        try {
            Address = Mcrypt.bytesToHex(mmm.encrypt(Address.trim()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Cursor cursor = db.query("anggota", null, " idanggota=?",
                new String[]{Address}, null, null, null);
        if (cursor.getCount() < 1) // location Not Exist
        {
            return false;
        }

        cursor.close();
        //update user and password for particular user;
        ContentValues updatedValues = new ContentValues();
        //updatedValues.put("success", success); // lokasi file form
        updatedValues.put("updated_gambar", 1); // lokasi file form
        updatedValues.put("success", success); // lokasi file form
        String where = "idanggota = ?";
        db.update("anggota", updatedValues, where, new String[]{Address});
        return true;
    }

    // insertto site
    public void insertSiteEntry(String site_id, String locationName, String Address, String gps_long, String gps_lati, String operator, String owner, String comment, String filektp, String filefoto, String fileform, String nokartu, String ibukan, String posisix, String pendidikanx, String pekerjaanx, String usernamex) {
            ContentValues newValues = new ContentValues();
			// Assign values for each row.
        newValues.put("posisi", posisix);
            newValues.put("nama", locationName);
            //encrypt dulu
            newValues.put("idanggota", Address);
            newValues.put("alamat", gps_long);
            newValues.put("telepon", gps_lati);
            newValues.put("noktp", comment);
            newValues.put("tl", operator); //tempat lahir
            newValues.put("ttl", owner); //tanggal lahir

        newValues.put("nokartu", nokartu);
        newValues.put("namaibu", ibukan);
        newValues.put("pekerjaan", pekerjaanx);
        newValues.put("pendidikan", pendidikanx);
        newValues.put("Username", usernamex);

            newValues.put("filektp", filektp); // lokasi file ktp
            newValues.put("filefoto", filefoto); // lokasi file foto
            newValues.put("fileform", fileform); // lokasi file form

            newValues.put("aktif", 0); //no ktp

            //username pendaftar
            //newValues.put("field3", "operator"); //
            // Insert the row into your table
            db.insert("anggota", null, newValues);
            // /Toast.makeText(context, "Reminder Is Successfully Saved",
            // Toast.LENGTH_LONG).show();
		}
		
    public String composeJSONfromSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM users where success = '"+ 1 +"'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("username", cursor.getString(1));
                map.put("password", cursor.getString(2));
                map.put("kodewarung", cursor.getString(3));
                map.put("unique", cursor.getString(4));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        db.close();
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }

    public void updateSyncStatus(String id, String status){
        String updateQuery = "Update users set updateStatus = '"+ status +"' where username="+"'"+ id +"'";
        Log.d("query",updateQuery);
        db.execSQL(updateQuery);
        db.close();
    }

    //check password is it correct?
    public long CheckAccess() {
        String SQL_GET_ALL_TABLES = "SELECT count(*) FROM sqlite_master WHERE type = 'table' AND name != 'android_metadata'";
			return db.compileStatement(SQL_GET_ALL_TABLES).simpleQueryForLong();
		}

    //insert appdev_ appuq1_
    public boolean updateAppKey(String... args) {
        //Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);
        Cursor cursor = db.query("setting", null, "value=?",
                new String[] { args[1] }, null, null, null);
        if (cursor.getCount() < 1) // belum pernah registrasi
        {
            ContentValues updatedValues = new ContentValues();
            updatedValues.put("value", args[1]);

            String where = " name = ?";
            db.update("setting", updatedValues, where, new String[] { "app_uq1" });
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
        db.update("setting", updatedValues, where, new String[] { names });
    }

    /***
     * management data anggota
     *
     *
     */
    //check if user available if yes update if no insert
    public boolean anggotapendamping(String... args ) {
        //Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM anggota", null);
        Cursor cursor = db.query("anggota", null, " idanggota=?",
                new String[] { args[0] }, null, null, null);
        if (cursor.getCount() < 1) // anggota belum ada
        {
            ContentValues newValues = new ContentValues();
            // Assign values for each row.
            newValues.put("idanggota", args[0]);
            newValues.put("field3", args[1]);
            newValues.put("telepon", args[2]);
            newValues.put("email", args[3]);
            newValues.put("nama", args[4]);
            newValues.put("posisi", "Team Leader");

            newValues.put("Username", args[3]);
            newValues.put("Tgl_daftar", df.format(Calendar.getInstance().getTime())); //tempat lahir
            newValues.put("Wkt_daftar", dt.format(Calendar.getInstance().getTime())); //tanggal lahir

            //newValues.put("Submitted", 0); //no ktp
            //newValues.put("user_assign", "operator"); //

            // Insert the row into your table
            db.insert("anggota", null, newValues);
            cursor.close();
            return false;
        }
        cursor.close();
        //update data anggota
        /* untuk anggota baru ksm
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put("siteID", site_id); //posisi
        updatedValues.put("locationName", locationName); //nama
        updatedValues.put("Address", Address); //idanggota
        updatedValues.put("gps_long", gps_long); //alamat
        updatedValues.put("gps_lati", gps_lati); //telepon
        updatedValues.put("operator", operator); //tempat lahir
        updatedValues.put("owner", owner); //tanggal lahir
        updatedValues.put("comment", comment); //no ktp

        updatedValues.put("Submitted", 0); //no ktp

        updatedValues.put("gpslong_act", filektp); // lokasi file ktp
        updatedValues.put("gpslati_act", filefoto); // lokasi file foto
        updatedValues.put("gpskore", fileform); // lokasi file form




        String where = "Address = ?";
        db.update("site", updatedValues, where, new String[] { Address });
        */
        //""String passWord = cursor.getString(cursor.getColumnIndex("password"));
        return true;
    }

    /***
     * manajemen pembaharuan stok
     *
     * @param names
     * @param appk
     */

    public void updatestokquery(String names, int appk, int jum, int coun) {
        Cursor cursor = db.query("syncstok", null, " queryid=?",
                new String[]{names}, null, null, null);
        if (cursor.getCount() < 1) // anggota belum ada
        {
            ContentValues values = new ContentValues();
            values.put("queryid", names);
            values.put("success", appk);
            values.put("jumlahserver", jum);
            values.put("countclient", coun);

            db.insert("syncstok", null, values);
            //cursor.close();

        }
        cursor.close();
    }

    public int updatestokquery_jawab(String names, int coun) {
// TODO DIKODE updatesyncstok masi ngaco
        int total = 0, hitunganskrg = 0;

        Cursor cursor = db.query("syncstok", null, " queryid = ?", new String[]{names}, null, null, null);
        if (cursor.getCount() < 1) // anggota belum ada
        {
            cursor.close();
        }

        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("jumlahserver"));
            int ketotal = cursor.getInt(cursor.getColumnIndex("countclient"));
            hitunganskrg = coun + ketotal;
            cursor.close();

            ContentValues updatedValues = new ContentValues();
            updatedValues.put("countclient", hitunganskrg);
            String where = "queryid" + " = ?";
            db.update("syncstok", updatedValues, where, new String[]{names});

        }
        if (hitunganskrg == total) {
            int numberOFEntriesDeleted;
            String where = "queryid = ?";
            numberOFEntriesDeleted = db.delete("syncstok", where, new String[]{names});
            Log.d("names", names + " sudah didelete");
            where = "Field3 = ?";
            numberOFEntriesDeleted = db.delete("stokmasuk", where, new String[]{names});

            return numberOFEntriesDeleted;
        }



        return 0;
    }

    public void insertstokmasuk(String[] arr, String id, int count, Context context) {

        File mediaStorageDir;
        String pathToExternalStorage;

        pathToExternalStorage = context.getFilesDir().toString();
        mediaStorageDir = new File(pathToExternalStorage + "/" + "eKMIS" + "/barang");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

            }
        }


        Cursor cursor = db.query("stokmasuk", null, " Id_stokmasuk=?",
                new String[]{arr[0]}, null, null, null);
        if (cursor.getCount() < 1) // anggota belum ada
        {
            cursor.close();

            ContentValues values = new ContentValues();
            values.put("Id_stokmasuk", arr[0]);
            values.put("Nopo", arr[1]);
            values.put("Supplier", arr[2]);
            values.put("Kodebarang", arr[3]);
            values.put("Namabarang", arr[4]);
            values.put("Hargajual", arr[5]);
            values.put("Qtymasuk", Integer.parseInt(arr[6]));
            values.put("Tgl_masuk", arr[7]);
            values.put("Gambar", mediaStorageDir.getPath() + "/" + arr[8]);
            //tambahkan Field2 untuk no_DO
            values.put("Field2", arr[9]);
            values.put("Field3", id);
            values.put("updated_data", 0); // lokasi file form
            values.put("Status", 0);
            values.put("Success", 1);
            db.insert("stokmasuk", null, values);
            count = count - 1;


        }
        cursor.close();
        //db.close();
    }

    public String getqueryid(int posisi) {
        String queyr = "";
        Cursor cursor = db.rawQuery("select Field3 from stokmasuk where ID = " + posisi, null);
        if (cursor.getCount() < 1) {
            cursor.close();
        }
        if (cursor.moveToFirst()) {
            queyr = cursor.getString(cursor.getColumnIndex("Field3"));
            // queyr[1] = cursor.getString(cursor.getColumnIndex("Gambar"));
        }
        cursor.close();

        return queyr;
    }

    public String getquerygambar(int posisi) {
        String queyr = "";
        Cursor cursor = db.rawQuery("select Gambar from stokmasuk where ID = " + posisi, null);
        if (cursor.getCount() < 1) {
            cursor.close();
        }
        if (cursor.moveToFirst()) {
            //queyr = cursor.getString(cursor.getColumnIndex("Field3"));
            queyr = cursor.getString(cursor.getColumnIndex("Gambar"));
        }
        cursor.close();

        return queyr;
    }

    public void tambahstok(String[] args) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String tanggal = dateFormat.format(date);

        Cursor cursor = db.query("stok", null, " Kodebarang = ? ",
                new String[]{args[0]}, null, null, null);
        if (cursor.getCount() < 1) // anggota belum ada
        {
            cursor.close();
            ContentValues values = new ContentValues();
            values.put("Kodebarang", args[0]);
            values.put("Namabarang", args[1]);
            values.put("Qty", Integer.parseInt(args[2]));
            values.put("Satuan", "unit");
            values.put("Hargajual", Integer.parseInt(args[3]));
            values.put("Field3", args[4]);
            values.put("Field1", args[5]);

            values.put("LastUpdate", tanggal);
            db.insert("stok", null, values);

        } else { // update nilai stok

            if (cursor.moveToFirst()) {
                int stkskrg;

                stkskrg = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Qty")));
                stkskrg = stkskrg + Integer.parseInt(args[2]);

                ContentValues updatedValues = new ContentValues();
                updatedValues.put("Qty", stkskrg);
                String where = "Kodebarang" + " = ?";
                db.update("stok", updatedValues, where, new String[]{args[0]});

            }
            cursor.close();

        }

        //   cursor.close();
        //db.close();
    }

    public void updatestokmasuk_meta(int air) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String tanggal = dateFormat.format(date);

        Cursor cursor = db.rawQuery("select Status, Diterima, Tglpesan from stokmasuk where ID = " + air, null);

        // Cursor cursor = db.query("stokmasuk", null, " ID=?",
        //        new String[]{air}, null, null, null);
        if (cursor.getCount() < 1) // stokmasuktidakada
        {
            cursor.close();
        } else {
            cursor.close();
            ContentValues updatedValues = new ContentValues();
            updatedValues.put("Status", 1);
            updatedValues.put("Diterima", 1);
            updatedValues.put("updated_data", 0); // lokasi file form
            //updatedValues.put("Tglpesan", tanggal);
            updatedValues.put("Timestamp", tanggal); //tanggal terima

            String where = "ID" + " = ?";
            db.update("stokmasuk", updatedValues, where, new String[]{String.valueOf(air)});
        }
    }

    public void updatestokmasuk_metatolak(int air) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String tanggal = dateFormat.format(date);

        Cursor cursor = db.rawQuery("select Status, Diterima, Tglpesan from stokmasuk where ID = " + air, null);

        // Cursor cursor = db.query("stokmasuk", null, " ID=?",
        //        new String[]{air}, null, null, null);
        if (cursor.getCount() < 1) // stokmasuktidakada
        {
            cursor.close();
        } else {
            cursor.close();
            ContentValues updatedValues = new ContentValues();
            updatedValues.put("Status", 1);
            updatedValues.put("Ditolak", 1);
            updatedValues.put("updated_data", 0); // lokasi file form
            //updatedValues.put("Tglpesan", tanggal);
            updatedValues.put("Timestamp", tanggal); //tanggal terima

            String where = "ID" + " = ?";
            db.update("stokmasuk", updatedValues, where, new String[]{String.valueOf(air)});
        }
    }


    public int getjmlstoker() {
        String queyr = "Stok Keeper";
        Cursor cursor = db.rawQuery("select nama from anggota where posisi = '" + queyr + "'", null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return cursor.getCount();
        } else {
            cursor.close();
            return cursor.getCount();
        }
    }

    public int getjmlsales() {
        String queyr = "Sales";
        Cursor cursor = db.rawQuery("select nama from anggota where posisi = '" + queyr + "'", null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return cursor.getCount();
        } else {
            cursor.close();
            return cursor.getCount();
        }
    }

    public boolean stok(String kode, String nama, int harga, int jumlah) {

        ContentValues updatedValues = new ContentValues();
        updatedValues.put("Qty", jumlah); //no ktp


        String where = "Kodebarang = ?";
        db.update("stok", updatedValues, where, new String[]{kode});
        return true;

    }

    public boolean stokskrg(String kode, int jumlah) {

        ContentValues updatedValues = new ContentValues();
        updatedValues.put("Qty", jumlah); //no ktp


        String where = "Kodebarang = ?";
        db.update("stok", updatedValues, where, new String[]{kode});
        return true;

    }


    public void tambahsimpanan(String[] args, int kredi) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String tanggal = dateFormat.format(date);

        ContentValues values = new ContentValues();
        values.put("Idanggota", args[0]);
        values.put("Kode_sp", args[1]);
        values.put("Tgl_simpanan", args[2]);
        values.put("Wkt_simpanan", args[3]);
        values.put("Noref", args[4]);
        values.put("Noedc", args[5]);
        values.put("Kredit", kredi);
        values.put("Username", args[6]);
        values.put("Updated_Status", 1);
        db.insert("bukusimpanan", null, values);

        //   cursor.close();
        //db.close();
    }

    public void tambahjual(String[] args, int[] kredi, String nbrc) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String tanggal = dateFormat.format(date);

        ContentValues values = new ContentValues();
        values.put("Nomornota", args[4]);
        values.put("Kodebarang", args[1]);
        values.put("Field1", nbrc);
        values.put("Qty", kredi[0]);
        values.put("Hargajual", kredi[1]);
        values.put("Subtotal", kredi[2]);
        values.put("Field2", args[2]);
        values.put("Field3", args[3]);
        values.put("Username", args[6]);
        values.put("Updated_Status", 1);
        db.insert("penjualan", null, values);

        //   cursor.close();
        //db.close();
    }

    public void tambahpenjualan(String[] args, int kredi) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String tanggal = dateFormat.format(date);

        ContentValues values = new ContentValues();
        values.put("Nomor_jb", args[4]);
        values.put("Idanggota", args[0]);
        values.put("Tgl_penjualan", args[2]);
        values.put("Wkt_penjualan", args[3]);
        values.put("Refno", args[4]);
        values.put("Refedc", args[5]);
        values.put("Kredit", kredi);
        values.put("Timestamp", tanggal);
        values.put("Username", args[6]);
        values.put("Updated_Status", 1);
        db.insert("bukupenjualan", null, values);

    }

    public String getilduserskartu(String nokartu) {
        String mm = "";
        nokartu = nokartu.trim();
        Cursor cursor = db.query("anggota", null, " nokartu=?",
                new String[]{nokartu}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "";
        }
        if (cursor.moveToFirst()) {
            mm = cursor.getString(cursor.getColumnIndex("idanggota"));
        }
        cursor.close();
        return mm;
    }

    public String getstatuskartu(String nokartu) {
        String mm = "";
        nokartu = nokartu.trim();
        Cursor cursor = db.query("anggota", null, " nokartu=?",
                new String[]{nokartu}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "";
        }
        if (cursor.moveToFirst()) {
            mm = cursor.getString(cursor.getColumnIndex("aktif"));
        }
        cursor.close();
        return mm;
    }

    public String getfield1kartu(String nokartu) {
        String mm = "";
        nokartu = nokartu.trim();
        Cursor cursor = db.query("anggota", null, " nokartu=?",
                new String[]{nokartu}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "";
        }
        if (cursor.moveToFirst()) {

            if (cursor.getString(cursor.getColumnIndex("field1")) != null) {
                mm = cursor.getString(cursor.getColumnIndex("field1"));
            } else {
                mm = "2011-06-26";
            }

        }   //tanggaltenggat
        cursor.close();
        return mm;
    }

    public boolean getnokartu(String nokartu) {
        boolean nnn = false;
        Cursor cursor = db.query("anggota", null, " nokartu=?",
                new String[]{nokartu}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            nnn = false;
            return nnn;
        }
        if (cursor.moveToFirst()) {
            cursor.close();
            nnn = true;
        }
        return nnn;
    }

    public boolean getnoreff(String nokartu) {
        boolean nnn = false;
        Cursor cursor = db.query("bukupenjualan", null, " Nomor_jb=?",
                new String[]{nokartu}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            nnn = false;
            return nnn;
        }
        if (cursor.moveToFirst()) {
            cursor.close();
            nnn = true;
        }
        return nnn;
    }

    public void settenggatanggota(String idang, String teng) {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("field1", teng);
        String where = "idanggota = ?";
        db.update("anggota", updatedValues, where, new String[]{idang});
    }

    public void setaktifanggota(String idang) {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("aktif", 1);
        String where = "idanggota = ?";
        db.update("anggota", updatedValues, where, new String[]{idang});
    }

    public String[] getsaldoanggota(String idanggota) {
        String[] mm = new String[3];
        int simpokk = 0;
        int simwjkk = 0;
        int simsuka = 0;
        idanggota = idanggota.trim();
        Cursor cursor = db.query("bukusimpanan", null, " Noedc=?",
                new String[]{idanggota}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return null;
        }
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                if (cursor.getString(cursor.getColumnIndex("Kode_sp")).equals("kmis-sp")) {

                    simpokk = simpokk + (cursor.getInt(cursor.getColumnIndex("Kredit")));

                }
                if (cursor.getString(cursor.getColumnIndex("Kode_sp")).equals("kmis-sw-3")) {
                    simwjkk = simwjkk + (cursor.getInt(cursor.getColumnIndex("Kredit")));
                }
                if (cursor.getString(cursor.getColumnIndex("Kode_sp")).equals("kmis-sr")) {
                    simsuka = simsuka + (cursor.getInt(cursor.getColumnIndex("Kredit")));
                }
                cursor.moveToNext();
            }
        }
        if (String.valueOf(simpokk) != null) {
            mm[0] = String.valueOf(simpokk);
        } else {
            mm[0] = "";
        }
        if (String.valueOf(simwjkk) != null) {
            mm[1] = String.valueOf(simwjkk);
        } else {
            mm[1] = "";
        }

        if (String.valueOf(simsuka) != null) {
            mm[2] = String.valueOf(simsuka);
        } else {
            mm[2] = "";
        }

        cursor.close();
        return mm;
    }

}
	
	

