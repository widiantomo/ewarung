package id.web.kmis.e_warung.warung.master_child.penjualan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import id.web.kmis.e_warung.R;
import id.web.kmis.e_warung.dbadapter.JSONParser;
import id.web.kmis.e_warung.dbadapter.LoginDataBaseAdapter;
import id.web.kmis.e_warung.dbadapter.Mcrypt;
import id.web.kmis.e_warung.network.Downloader;
import id.web.kmis.e_warung.warung.master_child.stok.ListViewAdapter_M;
import id.web.kmis.e_warung.warung.master_child.stok.Model2;

public class JualDetailsFragment extends Fragment {
    private ArrayList<Model_J> productList;
    LoginDataBaseAdapter loginDataBaseAdapter;

    ListViewAdapter_J adapter;
    GridView lview;

    TextClicked mCallback;

    public interface TextClicked {
        public void sendText(String[] text);
    }

    @Override
    public void onDetach() {
        mCallback = null; // => avoid leaking, thanks @Deepscorn
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a;

        if (context instanceof Activity) {
            a = (Activity) context;
            try {
                mCallback = (TextClicked) a;
            } catch (ClassCastException e) {
                throw new ClassCastException(a.toString()
                        + " must implement TextClicked");
            }
        }
    }


    public static JualDetailsFragment newInstance(int index) {
        JualDetailsFragment f = new JualDetailsFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }


    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.jual_samping, container, false);
        productList = new ArrayList<Model_J>();
        lview = (GridView) view.findViewById(R.id.gridView1);
        adapter = new ListViewAdapter_J(getActivity(), productList);
        lview.setAdapter(adapter);
        //setListAdapter(adapter);
        populateList();
        adapter.notifyDataSetChanged();

        return view;
        // return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                String[] ffg = new String[3];
                ffg[0] = productList.get(position).getjKode();
                ffg[1] = productList.get(position).getjNama();
                ffg[2] = productList.get(position).getjHjual();
                mCallback.sendText(ffg);


            }
        });

    }


    private void populateList() {

        Model_J item1, item2, item3, item4, item5;


        LoginDataBaseAdapter loginDataBaseAdapter = new LoginDataBaseAdapter(getActivity());
        loginDataBaseAdapter.open();

        Cursor c = loginDataBaseAdapter.db.rawQuery("select * from stok", null);
        int x = 0;
        if (!(c.moveToFirst()) || c.getCount() == 0) {
            c.close();
        } else {

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                //populate table here

                item1 = new Model_J(c.getString(c.getColumnIndex("Field1")), c.getString(c.getColumnIndex("Kodebarang")), c.getString(c.getColumnIndex("Namabarang")), c.getString(c.getColumnIndex("Hargajual")), c.getString(c.getColumnIndex("Qty")));
                productList.add(item1);

                x++;
            }
        }
        c.close();
        loginDataBaseAdapter.close();

        item2 = new Model_J("simpokok", "kmis-sp", "Simpanan Pokok", "10000", "1");
        productList.add(item2);
        item3 = new Model_J("simwajib3", "kmis-sw-3", "Simpanan Wajib 3 Bulan", "15000", "1");
        productList.add(item3);
        item4 = new Model_J("simsukarela10", "kmis-sr", "Simpanan Sukarela Rp 10000", "10000", "1");
        productList.add(item4);
    }

}