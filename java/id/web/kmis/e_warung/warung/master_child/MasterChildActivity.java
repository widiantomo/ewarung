package id.web.kmis.e_warung.warung.master_child;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
//import android.Fragment;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.bus.ActivityResultBus;
import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.bus.ActivityResultEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import id.web.kmis.e_warung.LoginActivity;
import id.web.kmis.e_warung.R;
import id.web.kmis.e_warung.dbadapter.SetDatabaseAdapter;
import id.web.kmis.e_warung.materialnavigationdrawer.MaterialNavigationDrawer;
import id.web.kmis.e_warung.materialnavigationdrawer.elements.MaterialSection;
import id.web.kmis.e_warung.materialnavigationdrawer.elements.listeners.MaterialSectionListener;
import id.web.kmis.e_warung.warung.master_child.keanggotaan.AnggotaDetailsActivity;
import id.web.kmis.e_warung.warung.master_child.keanggotaan.AnggotaListMain;
import id.web.kmis.e_warung.warung.master_child.penjualan.JualDetailsFragment;
import id.web.kmis.e_warung.warung.master_child.penjualan.JualListMain;
import id.web.kmis.e_warung.warung.master_child.ringkasan.FragmentListTitles;
import id.web.kmis.e_warung.warung.master_child.stok.StokListMain;

/**
 * Created by neokree on 20/01/15.
 */
public class MasterChildActivity extends MaterialNavigationDrawer implements JualDetailsFragment.TextClicked {

    @Override
    public void init(Bundle savedInstanceState) {

        // todo : sync status keanggotaan check all keaktifan update check diserver


        // super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MasterChildActivity.this);
        String post_username = sp.getString("username", "");
        String kode_warung = sp.getString("warung", "");


        String lev = sp.getString("level", "");

        this.setDrawerHeaderImage(R.drawable.mat3);
        setUsername("");
        setUserEmail(post_username + " -- " + kode_warung);


        Bundle bundle = new Bundle();

        SetDatabaseAdapter lDa = SetDatabaseAdapter.getInstance(this);
        lDa.open();
             /*
        Fragment ringkas = new RingkasanFragment();
        Bundle bundle1 = new Bundle();
        bundle.putString("link", "link");
        ringkas.setArguments(bundle1);
        this.addSection(this.newSection("Ringkasan", R.drawable.portfolio, ringkas));
*/
            Fragment jual = new JualListMain();
        bundle.putString("link", "link");
            jual.setArguments(bundle);
        this.addSection(this.newSection("Penjualan", R.drawable.shopping, jual));

        if (lDa.checkkeydevexist() && lev.equals("Team Leader")) {

            Fragment stok = new StokListMain();
            Bundle bundle2 = new Bundle();
            bundle.putString("link", "link");
            stok.setArguments(bundle2);
            this.addSection(this.newSection("Stok Barang", R.drawable.box, stok));

        /*
        Fragment simpin = new SimpanPinjamFragment();
        Bundle bundle3 = new Bundle();
        bundle.putString("link", "link");
        jual.setArguments(bundle3);
        this.addSection(this.newSection("Simpan/Pinjam",R.drawable.simpan,simpin));
        */

        }
        lDa.close();

        Fragment anggota = new AnggotaListMain();
        //Bundle bundle4 = new Bundle();
        //bundle.putString("link", "link");
        //anggota.setArguments(bundle4);
        this.addSection(this.newSection("Keanggotaan", R.drawable.anggotaico, anggota));

        /*
        Fragment laporan = new LaporanFragment();
        Bundle bundle5 = new Bundle();
        bundle.putString("link", "link");
        jual.setArguments(bundle5);
        this.addSection(this.newSection("Laporan",R.drawable.print,laporan));
        *
        */
        //todo sync all pending table - anggota - stokmasuk - stok - penjualan - bukupenjualan

        // create bottom section
        this.addBottomSection(newSection("Pengaturan", R.drawable.ic_settings_black_24dp, new Intent(this, PrintDemo.class)));
        this.addBottomSection(newSection("Logout", R.drawable.ic_power_settings_new_black_24dp, new MaterialSectionListener() {

            public void onClick(MaterialSection section) {
                logout();
            }
        }));
        this.setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_ANYWHERE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // set the indicator for child fragments
        // N.B. call this method AFTER the init() to leave the time to instantiate the ActionBarDrawerToggle
        this.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    @Override
    public void onHomeAsUpSelected() {
        // when the back arrow is selected this method is called

    }

    @Override
    public void sendText(String[] text) {
        // Get Fragment B
        JualListMain frag = (JualListMain)
                getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (frag != null) {
            frag.updateText(text);
        }
    }

    public boolean isConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService
                    (Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnected()) {
                //Network is available but check if we can get access from the network.
                URL url = new URL("http://ewarong.kmis.web.id/webserv2/sync2.php");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(2000); // Timeout 2 seconds.
                urlc.connect();

                if (urlc.getResponseCode() == 200)  //Successful response.
                {
                    return true;
                } else {
                    Log.d("NO INTERNET", "NO INTERNET");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void logout() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MasterChildActivity.this);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        Intent i = new Intent(MasterChildActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResultBus.getInstance().postQueue(new ActivityResultEvent(requestCode, resultCode, data));
    }


    private void exportDB() {
        try {
            final String inFileName = "/data/data/your app package/databases/db";
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CALC/Backup";
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();
            String outFileName = path + "/filename"; // output file name
            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            Toast.makeText(this, "Backup Successfully", 2).show();
            // Close the streams
            output.flush();
            output.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







}
