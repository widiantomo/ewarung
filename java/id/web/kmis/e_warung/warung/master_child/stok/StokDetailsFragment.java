package id.web.kmis.e_warung.warung.master_child.stok;

import android.app.AlertDialog;

import android.app.ProgressDialog;

import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.provider.Settings;

import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.NestedActivityResultFragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import java.util.Random;
import java.util.TimeZone;

import id.web.kmis.e_warung.R;
import id.web.kmis.e_warung.dbadapter.JSONParser;
import id.web.kmis.e_warung.dbadapter.LoginDataBaseAdapter;
import id.web.kmis.e_warung.dbadapter.Mcrypt;
import id.web.kmis.e_warung.network.AndroidMultiPartEntity;
import id.web.kmis.e_warung.network.Config;
import id.web.kmis.e_warung.network.Config2;
import id.web.kmis.e_warung.network.Downloader;
import id.web.kmis.e_warung.network.SntpClient;
import id.web.kmis.e_warung.sensor.AndroidCameraApi;
import id.web.kmis.e_warung.sensor.SDImageLoader;
import id.web.kmis.e_warung.warung.master_child.keanggotaan.AnggotaListMain;

public class StokDetailsFragment extends NestedActivityResultFragment implements ListViewAdapter_M.EditPlayerAdapterCallback {
    private ArrayList<Model> productList;
    private ArrayList<Model2> productList2;
    LoginDataBaseAdapter loginDataBaseAdapter;
    private String unique, queryid;

    String flag1 = "0", flag2 = "0", alasankurang = "";
    String timl, fotopath, mmmmj;
    int hhh, jmlopname;

    private static final String TAG_POSTS = "posts";
    private static final String TAG_IDSTOCK = "id_stokmasuk";
    private static final String TAG_NOPO = "nopo";
    private static final String TAG_NODO = "nodo";

    private static final String TAG_SUPPLIER = "supplier";
    private static final String TAG_KODE = "kodebarang";
    private static final String TAG_NAMA = "namabarang";
    private static final String TAG_HJUAL = "hjual";
    private static final String TAG_JML = "jumlah";
    private static final String TAG_TGLMASUK = "tanggalin";
    private static final String TAG_GAMBAR = "gambarlok";

    private static final String LOGIN_URL = "http://ewarong.kmis.web.id/webserv2/stokbaru2.php";

    TextView kodew, nbarw, pow, tpesanw, suppw;
    EditText alasw;
    ImageView imagebutton;

    Button refreshx, simpanx, btnTackPic;
    private int menux;
    int x;
    ListViewAdapter_M adapter;
    ListViewAdapter adapterx;
    LinearLayout bottomlayer;

    private SDImageLoader mImageLoader;

    public static StokDetailsFragment newInstance(int index) {
        StokDetailsFragment f = new StokDetailsFragment();

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
        View view;
        menux = getArguments().getInt("index", 0);
        hhh = -1;
        flag1 = "0";

        unique = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        Mcrypt mcrypt = new Mcrypt();
        try {
            unique = Mcrypt.bytesToHex(mcrypt.encrypt(unique));
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (menux == 0) {
            view = inflater.inflate(R.layout.inventori, container, false);
            productList = new ArrayList<Model>();

            LinearLayout priksa = (LinearLayout) view.findViewById(R.id.periksablok);
            priksa.setVisibility(View.GONE);


            ListView lview = (ListView) view.findViewById(android.R.id.list);
            ListViewAdapter adapter = new ListViewAdapter(getActivity(), productList);
            lview.setAdapter(adapter);
            populateList();
            adapter.notifyDataSetChanged();
        } else if (menux == 1) {
            view = inflater.inflate(R.layout.inventori_m, container, false);
            productList2 = new ArrayList<Model2>();
            ListView lview = (ListView) view.findViewById(R.id.list);
            adapter = new ListViewAdapter_M(getActivity(), productList2);
            lview.setAdapter(adapter);
            adapter.setCallback(this);

            bottomlayer = (LinearLayout) view.findViewById(R.id.form_penolakan);

            kodew = (TextView) view.findViewById(R.id.textkodebarang);
            nbarw = (TextView) view.findViewById(R.id.textnamabarang);
            pow = (TextView) view.findViewById(R.id.textpo);
            tpesanw = (TextView) view.findViewById(R.id.texttglpesan);
            suppw = (TextView) view.findViewById(R.id.textsupp);

            imagebutton = (ImageView) view.findViewById(R.id.Imagefoto);
            // int id = getResources().getIdentifier("@drawable/foto","drawable",getActivity().getPackageName());
            // ivThumbnailPhoto.setImageResource(id);
            btnTackPic = (Button) view.findViewById(R.id.buttonfoto);
            btnTackPic.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Random rngg = new Random();
                    String momo = generateString(rngg, "1234567890Abcdefghijklmnop", 12);
                    //String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    Intent i = new Intent();
                    i.setClass(getActivity(), AndroidCameraApi.class);
                    i.putExtra("coor", momo + "_alasan");
                    getActivity().startActivityForResult(i, 289);// Activity is started with

                }
            });


            alasw = (EditText) view.findViewById(R.id.editalasan);

            bottomlayer.setVisibility(View.GONE);

            simpanx = (Button) view.findViewById(R.id.Simpan);
            simpanx.setVisibility(View.INVISIBLE);


            simpanx.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Holo_Dialog_NoActionBar);
                    alertDialogBuilder //kebalik cuy
                            .setTitle("Tolak Barang")
                            .setMessage("Apa anda yakin?")
                            .setCancelable(true)
                            .setNeutralButton("", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            })
                            .setPositiveButton("Tidak", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }

                            })
                            .setNegativeButton("Ya", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    tolakbarang(hhh);

                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();


                }
            });

            refreshx = (Button) view.findViewById(R.id.Refreshx);





            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent ev) {
                    bottomlayer.setVisibility(View.GONE);
                    simpanx.setVisibility(View.INVISIBLE);
                    return false;
                }
            });

        } else {
            view = inflater.inflate(R.layout.inventori, container, false);

            LinearLayout priksa = (LinearLayout) view.findViewById(R.id.periksablok);
            priksa.setVisibility(View.GONE);

            productList = new ArrayList<Model>();

            TextView jul = (TextView) view.findViewById(R.id.juddul);
            jul.setText("Stok Opname");

            TextView jjs = (TextView) view.findViewById(R.id.qtyak);
            jjs.setText("Qty Aktual");

            TextView jjw = (TextView) view.findViewById(R.id.tglperi);
            jjw.setText("Tgl Periksa");

            ListView lview = (ListView) view.findViewById(android.R.id.list);
            adapterx = new ListViewAdapter(getActivity(), productList);
            lview.setAdapter(adapterx);
            populateListopn();
            adapterx.notifyDataSetChanged();

            lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View view, final int position, long arg) {

                    LayoutInflater li = LayoutInflater.from(getActivity());
                    View promptsView = li.inflate(R.layout.prompts, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            getActivity(), android.R.style.Theme_Holo_Dialog_NoActionBar);
                    alertDialogBuilder.setView(promptsView);

                    // final EditText userInput = (EditText) promptsView
                    //         .findViewById(R.id.editTextDialogUserInput);
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());


                    TextView tglopname = (TextView) promptsView.findViewById(R.id.tglper1);
                    TextView kodeopname = (TextView) promptsView.findViewById(R.id.kodbar1);
                    TextView namopname = (TextView) promptsView.findViewById(R.id.nambar1);
                    final TextView qtykiniopn = (TextView) promptsView.findViewById(R.id.qtykini);

                    tglopname.setText("Tanggal : " + currentDateTimeString);
                    kodeopname.setText("Kode : " + productList.get(position).getsNo());
                    namopname.setText("Nama : " + productList.get(position).getProduct());
                    qtykiniopn.setText(productList.get(position).getCategory());

                    final EditText qtyaktu = (EditText) promptsView.findViewById(R.id.editQtyaktual);
                    final EditText alaselisih = (EditText) promptsView.findViewById(R.id.alasanperbedaan);

                    final int posii = position;
                    //int jumlahterbaru;
                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            //todo validator angka quantity opname < jumlah sekarang
                                            //alasan > 5 karakter
                                            boolean qtybenar = false, alabenar = false;
                                            if (Integer.parseInt(qtyaktu.getText().toString()) > Integer.parseInt(qtykiniopn.getText().toString())) {
                                                //qtyaktu.setError("Jumlah mesti dibawah nilai terkini");
                                                Toast.makeText(getActivity(), "Jumlah mesti dibawah nilai terkini", Toast.LENGTH_LONG).show();
                                                qtybenar = false;

                                            } else {
                                                jmlopname = Integer.parseInt(qtyaktu.getText().toString());
                                                qtybenar = true;
                                            }

                                            if (alaselisih.getText().length() < 5) {
                                                //alaselisih.setError("Tulis alasan selisih jumlah secara lengkap");
                                                Toast.makeText(getActivity(), "Tulis alasan selisih jumlah secara lengkap", Toast.LENGTH_LONG).show();
                                                alabenar = false;
                                            } else {
                                                alabenar = true;
                                            }

                                            if (qtybenar && alabenar) {
                                                // update stok item server dan lokal
                                                alasankurang = alaselisih.getText().toString();
                                                opnamebarang(posii, qtyaktu.getText().toString());
                                            }
                                        }
                                    })
                            .setNegativeButton("Batal",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                }
            });

        }

        return view;
        // return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (menux == 1) {
            Log.d("dipencet", "menu ke 1");
            populateListallbarudb();
            adapter.notifyDataSetChanged();
            refreshx.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //post_async
                    //  if(isConnected()){
                    refreshx.setEnabled(false);
                    refreshx.setClickable(false);

                    new PostAsync().execute();

                    //   }

                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        if (requestCode == 289) { // change to 212 to take from Custom_Camera
            // Activity before::: TAKE_PICTURE

            if (resultCode == getActivity().RESULT_OK) {

                final String photoFilePath = intent.getExtras().getString(
                        "imageUri");
                fotopath = photoFilePath;
                //  Toast.makeText(getActivity(), "lokasi form" + formpath,
                //            Toast.LENGTH_LONG).show();

                mImageLoader = new SDImageLoader();
                mImageLoader.load(photoFilePath, imagebutton);

            } else if (resultCode == getActivity().RESULT_CANCELED) {


            } else {


            }
        }

    }

    @Override
    public void tolakpressed(int position) {
        tolakplayer(position);
    }

    private void tolakplayer(final int position) {

        bottomlayer.setVisibility(View.VISIBLE);

        hhh = position;
        //TextView kodew, nbarw, pow, tpesanw, suppw;
        //EditText alasw;
        String temp = (productList2.get(position).getmsKode());
        kodew.setText("Kode Barang:\n" + temp);
        temp = (productList2.get(position).getmsNama());
        nbarw.setText("Nama Barang:\n" + temp);
        temp = (productList2.get(position).getmsNopo());
        pow.setText("No. PO:\n" + temp);
        temp = (productList2.get(position).getmsTglp());
        tpesanw.setText("Tgl Pesan:\n" + temp);
        temp = (productList2.get(position).getmsSupp());
        suppw.setText("Supplier:\n" + temp);

        alasw.setText("Alasan pengiriman ditolak : ");
        simpanx.setVisibility(View.VISIBLE);

    }

    @Override
    public void deletePressed(int position) {
        deletePlayer(position);
    }

    private void deletePlayer(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Holo_Dialog_NoActionBar);
        alertDialogBuilder //kebalik cuy
                .setTitle("Terima Barang")
                .setMessage("Apa anda yakin?")
                .setCancelable(true)
                .setNeutralButton("", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setPositiveButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }

                })
                .setNegativeButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        flag1 = "H1";
                        hhh = position;

                        String[] bbb = new String[8];

                        bbb[0] = productList2.get(position).getmsKode();
                        bbb[1] = productList2.get(position).getmsNama();
                        bbb[2] = productList2.get(position).getmsJumlah();
                        //Log.d("jumlah ", "productList2.get(posisi).getmsJumlah()");
                        bbb[3] = productList2.get(position).getmsHjual();
                        bbb[4] = productList2.get(position).getmsSupp();
                        bbb[5] = productList2.get(position).getmsSatuan();
                        bbb[6] = "";
                        bbb[7] = "";
                        new postStok().execute(bbb);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void terimabarang(int posisi) {

        String qid, gambarx;
        String[] aaa = new String[6];
        int ok;

        loginDataBaseAdapter = LoginDataBaseAdapter.getInstance(getActivity());
        loginDataBaseAdapter.open();
        // get queryid dari stokmasuk
        qid = loginDataBaseAdapter.getqueryid(Integer.parseInt(productList2.get(posisi).getmsNo()));
        gambarx = loginDataBaseAdapter.getquerygambar(Integer.parseInt(productList2.get(posisi).getmsNo()));

        // update synctock
        ok = loginDataBaseAdapter.updatestokquery_jawab(qid, 1); //kode ini masi ngaco

        // update stok dari data stokmasuk
        // siapkan data
        aaa[0] = productList2.get(posisi).getmsKode();
        aaa[1] = productList2.get(posisi).getmsNama();
        aaa[2] = productList2.get(posisi).getmsJumlah();
        Log.d("jumlah ", "productList2.get(posisi).getmsJumlah()");
        aaa[3] = productList2.get(posisi).getmsHjual();
        aaa[4] = qid;
        aaa[5] = gambarx;
        //tambahkan ke stok
        loginDataBaseAdapter.tambahstok(aaa);

        // flag stokmasuk status = 1; Diterima = 1; tanggalpesan = current date ; usernamepenerima /mestinya
        loginDataBaseAdapter.updatestokmasuk_meta(Integer.parseInt(productList2.get(posisi).getmsNo()));

        // remove
        productList2.remove(posisi);
        adapter.notifyDataSetChanged();
        loginDataBaseAdapter.close();

    }

    private void tolakbarang(int posisi) {


        flag1 = "HX";
        // update server
        //new postStok().execute()
        String[] bbb = new String[8];

        mmmmj = productList2.get(posisi).getmsKode();
        bbb[0] = productList2.get(posisi).getmsKode();
        bbb[1] = productList2.get(posisi).getmsNama();
        bbb[2] = productList2.get(posisi).getmsJumlah();
        //Log.d("jumlah ", "productList2.get(posisi).getmsJumlah()");
        bbb[3] = productList2.get(posisi).getmsHjual();
        bbb[4] = productList2.get(posisi).getmsSupp();
        bbb[5] = productList2.get(posisi).getmsSatuan();
        bbb[6] = alasw.getText().toString();
        bbb[7] = "";
        new postStok().execute(bbb);

    }

    private void opnamebarang(int posisi, String jumak) {
        flag1 = "H8";
        hhh = posisi;

        String[] bbb = new String[8];

        bbb[0] = productList.get(posisi).getsNo();
        bbb[1] = productList.get(posisi).getProduct();
        bbb[2] = productList.get(posisi).getCategory();
        bbb[3] = "";
        bbb[4] = "";
        bbb[5] = "unit";
        bbb[6] = alasankurang;
        bbb[7] = jumak;

        new postStok().execute(bbb);
    }

    private void opnamebaranglagi(int posisi) {

        loginDataBaseAdapter = LoginDataBaseAdapter.getInstance(getActivity());
        loginDataBaseAdapter.open();

        loginDataBaseAdapter.stokskrg(productList.get(posisi).getsNo(), jmlopname);
        loginDataBaseAdapter.close();

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        productList.get(posisi).setPrice(String.valueOf(jmlopname));
        productList.get(posisi).setHjual(currentDateTimeString);

        //productList.remove(posisi);
        adapterx.notifyDataSetChanged();
    }


    private void tolakbaranglagi(int posisi) {
        String qid, gambarx;
        String[] aaa = new String[6];
        int ok;

        loginDataBaseAdapter = LoginDataBaseAdapter.getInstance(getActivity());
        loginDataBaseAdapter.open();
        // get queryid dari stokmasuk
        qid = loginDataBaseAdapter.getqueryid(Integer.parseInt(productList2.get(posisi).getmsNo()));

        // update synctock
        ok = loginDataBaseAdapter.updatestokquery_jawab(qid, 1); //kode ini masi ngaco

        // flag stokmasuk status = 1; Diterima = 1; tanggalpesan = current date ; usernamepenerima /mestinya
        loginDataBaseAdapter.updatestokmasuk_metatolak(Integer.parseInt(productList2.get(posisi).getmsNo()));
        loginDataBaseAdapter.close();

        if (fotopath != null) {
            new UploadFileToServer().execute();
        }

        simpanx.setVisibility(View.INVISIBLE);

        productList2.remove(posisi);
        adapter.notifyDataSetChanged();
    }


    private void populateList() {

        Model item1, item2, item3, item4, item5;

        LoginDataBaseAdapter loginDataBaseAdapter = new LoginDataBaseAdapter(getActivity());
        loginDataBaseAdapter.open();

        Cursor c = loginDataBaseAdapter.db.rawQuery("select * from stok", null);
        int x = 0;
        if (!(c.moveToFirst()) || c.getCount() == 0) {
            c.close();
        } else {

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                //populate table here

                item1 = new Model(c.getString(c.getColumnIndex("Kodebarang")), c.getString(c.getColumnIndex("Namabarang")), c.getString(c.getColumnIndex("Qty")), c.getString(c.getColumnIndex("Satuan")), c.getString(c.getColumnIndex("Hargajual")));
                productList.add(item1);

                x++;
            }
        }
        c.close();
        loginDataBaseAdapter.close();
    }

    private void populateListopn() {

        Model item1, item2, item3, item4, item5;

        LoginDataBaseAdapter loginDataBaseAdapter = new LoginDataBaseAdapter(getActivity());
        loginDataBaseAdapter.open();

        Cursor c = loginDataBaseAdapter.db.rawQuery("select * from stok", null);
        int x = 0;
        if (!(c.moveToFirst()) || c.getCount() == 0) {
            c.close();
        } else {

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                //populate table here

                item1 = new Model(c.getString(c.getColumnIndex("Kodebarang")), c.getString(c.getColumnIndex("Namabarang")), c.getString(c.getColumnIndex("Qty")), "", "");
                productList.add(item1);

                x++;
            }
        }
        c.close();
        loginDataBaseAdapter.close();
    }

    private void populateListbarudb() {
        //if possible connect to internet
        Model2 item1;

        LoginDataBaseAdapter loginDataBaseAdapter = new LoginDataBaseAdapter(getActivity());
        loginDataBaseAdapter.open();


        Cursor c = loginDataBaseAdapter.db.rawQuery("select * from stokmasuk  where Status = 0", null);
        int x = 0;
        if (!(c.moveToFirst()) || c.getCount() == 0) {
            c.close();
        } else {

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                //populate table here

                item1 = new Model2(c.getString(c.getColumnIndex("ID")), c.getString(c.getColumnIndex("Tgl_masuk")), c.getString(c.getColumnIndex("Supplier")), c.getString(c.getColumnIndex("Nopo")), c.getString(c.getColumnIndex("Kodebarang")), c.getString(c.getColumnIndex("Namabarang")), c.getString(c.getColumnIndex("Qtymasuk")), "unit", c.getString(c.getColumnIndex("Hargajual")));
                productList2.add(item1);

                x++;
            }
        }
        c.close();
        loginDataBaseAdapter.close();
    }

    private void populateListallbarudb() {

        if (menux == 1) {

            populateListbarudb();
        }
        // postasync dari button refresh

    }

    class PostAsync extends AsyncTask<String, String, JSONObject> {
        JSONParser jsonParser = new JSONParser();
        private ProgressDialog pDialog;
        File mediaStorageDir;
        String pathToExternalStorage;

        HashMap<String, String> map;

        private JSONArray mComments = null;
        private ArrayList<HashMap<String, String>> mCommentList;

        //139.59.246.250
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";


        // variable to hold context
        private Context mContext;

        //save the context recievied via constructor in a local variable

        public void YourNonActivityClass(Context context) {
            mContext = context;
        }


        @Override
        protected void onPreExecute() {

            pathToExternalStorage = getActivity().getFilesDir().toString();
            mediaStorageDir = new File(pathToExternalStorage + "/" + "eKMIS" + "/barang");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {

                }
            }
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {
                //(name_enc, namawr_enc,nohp_enc,email_enc,pass_enc,id_anggota);
                // encrypt dulu idanggota
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                LoginDataBaseAdapter lolo = LoginDataBaseAdapter.getInstance(getActivity());
                lolo.open();
                String[] aa = lolo.encryptcredential(preferences.getString("username", ""), preferences.getString("password", ""));
                lolo.close();

                Random rngg = new Random();
                queryid = generateString(rngg, "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ", 15);

                HashMap<String, String> params = new HashMap<>();
                params.put("username", preferences.getString("username", ""));
                params.put("password", aa[1]);
                params.put("warung", preferences.getString("warung", ""));
                params.put("unique", unique);
                params.put("queryid", queryid);

                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params, mContext);

                if (json != null) {
                    try {
                        mComments = json.getJSONArray(TAG_POSTS); //???
                        String xx = "";

                        // looping through all posts according to the json object returned
                        for (int i = 0; i < mComments.length(); i++) {
                            JSONObject c = mComments.getJSONObject(i);
                            xx = c.getString(TAG_GAMBAR);
                            // download gambar dan taro ditempat nyimpen gambar foto
                            Downloader dl = new Downloader(mediaStorageDir.getPath(), "http://ewarong.kmis.web.id/kmis/datain/" + xx, xx);
                            dl.start();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Log.d("JSON result", json.toString());
                    return json;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            // save ke database via logindatabaseadapter atau entah lewat mana
            // Toast.makeText(getActivity(), json.toString(),
            //        Toast.LENGTH_LONG).show();

            int success = 0;
            String message = "";

            // put query id and data number on syncstock


            if (json != null) {
                try {
                    success = json.getInt(TAG_SUCCESS);
                    message = json.getString(TAG_MESSAGE);
                    Log.d("success", success + "--" + message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (success == 1) {
                loginDataBaseAdapter = LoginDataBaseAdapter.getInstance(getActivity());
                loginDataBaseAdapter.open();

                String[] hh = new String[10];

                try {
                    mComments = json.getJSONArray(TAG_POSTS); //???
                    x = 0;
                    // looping through all posts according to the json object returned
                    for (int i = 0; i < mComments.length(); i++) {
                        JSONObject c = mComments.getJSONObject(i);

                        //  creating new HashMap
                        //map = new HashMap<String, String>();

                        hh[0] = c.getString(TAG_IDSTOCK);
                        hh[1] = c.getString(TAG_NOPO);
                        hh[2] = c.getString(TAG_SUPPLIER);
                        hh[3] = c.getString(TAG_KODE);
                        hh[4] = c.getString(TAG_NAMA);
                        hh[5] = c.getString(TAG_HJUAL);
                        hh[6] = c.getString(TAG_JML);

                        hh[7] = c.getString(TAG_TGLMASUK);
                        hh[8] = c.getString(TAG_GAMBAR);
                        hh[9] = c.getString(TAG_NODO);

                        loginDataBaseAdapter.insertstokmasuk(hh, queryid, mComments.length(), getActivity()); //update table stokmasuk
                        // mCommentList = new ArrayList<HashMap<String, String>>();
                        // adding HashList to ArrayList
                        //mCommentList.add(map);

                        x = x + 1;
                    }
                    loginDataBaseAdapter.updatestokquery(queryid, success, mComments.length(), 0);
                    loginDataBaseAdapter.close();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //update list stok masuk
                populateListallbarudb();
                adapter.notifyDataSetChanged();


            } else {
                Log.d("Failure", message);
            }

            refreshx.setEnabled(true);
            refreshx.setClickable(true);
            refreshx.setVisibility(View.GONE);
        }

    }

    public boolean isConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService
                    (Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnected()) {
                //Network is available but check if we can get access from the network.
                URL url = new URL("http://ewarong.kmis.web.id/webserv2/stokbaru2.php");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(2000); // Timeout 2 seconds.
                urlc.connect();

                if (urlc.getResponseCode() == 200)  //Successful response.
                {
                    return true;
                } else {
                    Toast.makeText(getActivity(), "Tidak ada sambungan internet", Toast.LENGTH_LONG).show();

                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String generateString(Random rng, String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

    private void validator(String hasil) {

        new GetNTPAsynctask().execute();
        //String currentdate = getUTCTime();

    }

    class GetNTPAsynctask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            //private SntpClient sntpClient = new SntpClient();
            //return sntpClient.requestTime("pool.ntp.org", 30000);
            boolean local_date = false;
            long nowAsPerDeviceTimeZone = 0;
            SntpClient sntpClient = new SntpClient();
            //String timex ="";

            if (sntpClient.requestTime("ntp.kim.lipi.go.id", 30000)) {
                nowAsPerDeviceTimeZone = sntpClient.getNtpTime();

                String myFormat = "ddMMyyyy"; // In which you need put here
                SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                edf.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
                timl = edf.format(nowAsPerDeviceTimeZone);

                local_date = true;

            }
            //local_date = false;
            return local_date;
        }

        @Override
        protected void onPostExecute(Boolean local_date) {

        }

    }

    // update stok client saat penerimaan
    class postStok extends AsyncTask<String, String, JSONObject> {
        JSONParser jsonParser = new JSONParser();
        private ProgressDialog pDialog;

        private static final String LOGIN_URL = "http://ewarong.kmis.web.id/webserv2/stok2.php";
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";

        private Context mContext;

        public void YourNonActivityClass(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Menghubungi Server...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String[] args) {

            try {
                //(name_enc, namawr_enc,nohp_enc,email_enc,pass_enc,id_anggota);
                // encrypt dulu idanggota
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                LoginDataBaseAdapter lolo = LoginDataBaseAdapter.getInstance(getActivity());
                lolo.open();
                String[] aa = lolo.encryptcredential(preferences.getString("username", ""), preferences.getString("password", ""));
                lolo.close();

                HashMap<String, String> params = new HashMap<>();
                params.put("kodewarung", unique);
                params.put("supplier", args[4]);
                params.put("kodebarang", args[0]);
                params.put("namabarang", args[1]);
                params.put("satuan", args[5]);
                params.put("het", args[3]);
                params.put("jumlah", args[2]);
                params.put("username", preferences.getString("username", ""));
                params.put("flag1", flag1); // flag opsi untuk update table pemesanan terima, tolak, opname
                params.put("flag2", args[6]);
                params.put("flag3", args[7]);

                params.put("password", aa[1]);
                //params.put("warung", preferences.getString("warung", ""));
                params.put("unique", unique);

                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params, mContext);
                Log.d("request", "starting");
                if (json != null) {
                    Log.d("JSON result", json.toString());

                    return json;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            int success;
            success = 0;
            String message = "";


            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (json != null) {
                try {
                    success = json.getInt(TAG_SUCCESS);
                    message = json.getString(TAG_MESSAGE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Log.d("fotopath:",fotopath);
            if (success == 1) {

                if (flag1 == "H1") {
                    terimabarang(hhh);
                    hhh = -1;
                    flag1 = "xx";
                }

            } else if (success == 2) {
                tolakbaranglagi(hhh);
                hhh = -1;
                flag1 = "xx";
                flag2 = "";

            } else if (success == 3) {
                opnamebaranglagi(hhh);
                hhh = -1;
                flag1 = "xx";
                flag2 = "";

            } else {
                Log.d("Failure", message);
            }


        }

    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        long totalSize = 0;

        //ProgressBar progressBar;
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            //progressBar.setProgress(0);
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            //progressBar.setVisibility(View.VISIBLE);
            // updating progress bar value
            //progressBar.setProgress(progress[0]);
            // updating percentage value
            //EditTempat.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Config2.FILE_UPLOAD_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                // Log.d("tae", fotopath);
                File sourceFile1 = new File(fotopath);

                // todo check shared preference fragment  --- ga keluar ini username passwd nya
                //SharedPreferences preferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                //  Log.d("fusername",preferences.getString("username", ""));
                LoginDataBaseAdapter lolo = LoginDataBaseAdapter.getInstance(getActivity());
                lolo.open();
                String[] aa = lolo.encryptcredential(preferences.getString("username", ""), preferences.getString("password", ""));
                lolo.close();
                // Adding file data to http body
                //   entity.addPart("imagefoto", new FileBody(sourceFile1));
                //todo pake bawaan dulu --- ganti line diatas
                entity.addPart("fotopath", new FileBody(sourceFile1));


                // Extra parameters if you want to pass to server  ///todo test dulu ma parameter bawaan
                //entity.addPart("website",
                //        new StringBody("www.androidhive.info"));
                //entity.addPart("email", new StringBody("abc@gmail.com"));

                // Extra parameters if you want to pass to server
                entity.addPart("username", new StringBody(preferences.getString("username", "")));
                entity.addPart("password", new StringBody(aa[1]));
                entity.addPart("kodebarang", new StringBody(mmmmj));
                entity.addPart("unique", new StringBody(unique));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            //Log.e(TAG, "Response from server: " + result);

            // update anggota updatedstatus to 1

            // showing the server response in an alert dialog
            //showAlert(result);
            Toast.makeText(getActivity(), "Berhasil disimpan", Toast.LENGTH_LONG).show();
            bottomlayer.setVisibility(View.GONE);


            super.onPostExecute(result);
        }

    }

}