package id.web.kmis.e_warung.warung.master_child.penjualan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.NestedActivityResultFragment;
import com.zj.btsdk.BluetoothService;
import com.zj.btsdk.PrintPic;

import net.sqlcipher.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;

import id.web.kmis.e_warung.R;
import id.web.kmis.e_warung.dbadapter.JSONParser;
import id.web.kmis.e_warung.dbadapter.JSONParser_2;
import id.web.kmis.e_warung.dbadapter.LoginDataBaseAdapter;
import id.web.kmis.e_warung.dbadapter.Mcrypt;
import id.web.kmis.e_warung.network.SntpClient;
import id.web.kmis.e_warung.warung.master_child.DeviceListActivity;
import id.web.kmis.e_warung.warung.master_child.penjualan.ListViewAdapter_N;
import id.web.kmis.e_warung.warung.master_child.penjualan.Model_N;

public class JualListMain extends NestedActivityResultFragment {
    boolean mDualPane;
    int mCurCheckPosition = 0;
    int baru = 0;
    // String idangx = "";
    private ProgressDialog pDialog;
    private ArrayList<Model_N> productList;

    private String post_username;
    EditText inputSearch;
    private ListViewAdapter_N adapter;
    ListView lview;
    Button resetx, bayarx, connx;
    TextView totalpen;
    String timd, nokartux, notransaksi, totalharga, dateqr, timl, timo, timt;

    String tenggat;
    int totalhargaint, totalhargapen;
    boolean barubayar;

    //private ArrayList<ModelProducts> listOfObjects;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothService mService = null;
    BluetoothDevice con_dev = null;
    private static final int REQUEST_CONNECT_DEVICE = 1;  //获取设备消息
    LoginDataBaseAdapter lolo;
    SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // return inflater.inflate(R.layout.stokactivity, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.jual_main, container, false);
        timd = "";
        nokartux = "";
        notransaksi = "";
        totalharga = "";
        totalhargaint = 0;
        dateqr = "";

        productList = new ArrayList<Model_N>();
        lview = (ListView) view.findViewById(R.id.listz);
        adapter = new ListViewAdapter_N(getActivity(), productList);
        //setListAdapter(adapter);
        lview.setAdapter(adapter);

        resetx = (Button) view.findViewById(R.id.resetx);
        bayarx = (Button) view.findViewById(R.id.bayar);
        connx = (Button) view.findViewById(R.id.connx);
        totalpen = (TextView) view.findViewById(R.id.totalpenjualan);


        //listOfObjects = new ArrayList<ModelProducts>();
        mService = new BluetoothService(getActivity(), mHandler);
        //蓝牙不可用退出程序

        if (mService.isAvailable() == false) {
            Toast.makeText(getActivity(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
            //finish();
        }


        return view;
        //return super.onCreateView(inflater, container, savedInstanceState);

    }

    public void updateText(String[] text) {
        // update listadapter sini
        Model_N item;
        int post, count;

        post = getAdapterItemPosition(text[0]);

        if (post != -1) {
            if (!text[0].equals("kmis-sp")) {
                count = productList.get(post).getsJumlah();
                count = count + 1;
                productList.get(post).setJumlah(count);
            }
            if (!text[0].equals("kmis-sw-3")) {
                barubayar = true;
            }


        } else {

            item = new Model_N(text[0], text[1], Integer.parseInt(text[2]), 1);
            productList.add(item);
            if (!text[0].equals("kmis-sw-3")) {
                barubayar = true;
            }

        }
        updateTotal();
        adapter.notifyDataSetChanged();
    }

    private int getAdapterItemPosition(String id) {
        for (int position = 0; position < productList.size(); position++)
            if (productList.get(position).getsKbarang().equals(id))
                return position;
        return -1;
    }

    public void updateTotal() {
        int xx = 0;

        for (int position = 0; position < productList.size(); position++) {
            xx = xx + productList.get(position).getsTotal();
        }
        if (productList.size() != 0) {
            totalpen.setText("Total Rp. " + String.valueOf(xx));
            totalhargapen = xx;
        } else {
            totalpen.setText("Total Rp. " + "0");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCurCheckPosition = 0;


        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                int jumlah;

                jumlah = productList.get(position).getsJumlah();
                jumlah = jumlah - 1;
                if (jumlah != 0) {
                    productList.get(position).setJumlah(jumlah);
                } else {
                    productList.remove(position);
                }

                updateTotal();
                adapter.notifyDataSetChanged();
            }
        });

        bayarx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Holo_Dialog_NoActionBar);
                alertDialogBuilder //kebalik cuy
                        .setTitle("Total Belanja " + totalpen.getText())
                        .setMessage("1. Bayar sejumlah diatas melalui mesin EDC yang disediakan. \n\n2. Siapkan printout EDC untuk dipindai \n" +
                                "\n" +
                                "3. Tekan SUDAH untuk memindai")
                        .setCancelable(true)
                        .setNeutralButton("", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .setPositiveButton("Sudah", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                scanqrcode();
                                //data kembalian :  23082016,16 digit no kartu,12 digit no referensi,12 digit total belanja
                                // contoh :  "23082016,1946900600000083,000000015400,000000001202"
                            }

                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        resetx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                barubayar = false;
                productList.clear();
                adapter.notifyDataSetChanged();
                totalpen.setText("Total Rp. " + "0");
            }
        });

        connx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);      //运行另外一个类的活动
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);

            }
        });


        // ListView lv = getListView();
        View detailsFrame = getActivity().findViewById(R.id.details);

        mDualPane = detailsFrame != null
                && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("index", 0);
            //   idangx = savedInstanceState.getString("idang", "");
            // baru = savedInstanceState.getInt("baru", 0);
        }

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected
            // item.
            // getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            //  Make sure our UI is in the correct state.
            showDetails(mCurCheckPosition);
        } else {
            // We also highlight in uni-pane just for fun
            // getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // getListView().setItemChecked(mCurCheckPosition, true);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    private void scanqrcode() {
        bayarx.setEnabled(false);
        bayarx.setFocusable(false);

        // int requestCode = 778;
        //IntentIntegrator intentintegrator= new IntentIntegrator (this);
        //
        //intentintegrator.initiateScan(ZxingIntent.QR_CODE_TYPES);
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        Intent intent = integrator.createScanIntent();
        startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);
        //integrator.initiateScan();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index", mCurCheckPosition);
    }

    void showDetails(int index) {
        mCurCheckPosition = index;
        //    idangx = locnm[index];

        if (mDualPane) {
            //getListView().setItemChecked(index, true);


            Fragment details = getActivity().getSupportFragmentManager().findFragmentById(R.id.details);
            JualDetailsFragment detailx = JualDetailsFragment.newInstance(mCurCheckPosition);

            if (details != null && !(details.getClass().equals(detailx.getClass()))) {
                //StokDetailsFragment
                //       detailx = StokDetailsFragment.newInstance(mCurCheckPosition);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.details, detailx);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                baru = 0;

            } else {
                JualDetailsFragment detailv = (JualDetailsFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.details);
                if (details == null || (detailv.getShownIndex() != index)) {
                    detailv = JualDetailsFragment.newInstance(mCurCheckPosition);
                    FragmentTransaction ftx = getActivity().getSupportFragmentManager().beginTransaction();
                    ftx.replace(R.id.details, detailv);
                    ftx.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ftx.commit();
                    baru = 0;
                }

            }

        } else {

            Intent intent = new Intent();
            intent.setClass(getActivity(), JualDetailsActivity.class);
            intent.putExtra("index", mCurCheckPosition);
            startActivity(intent);
            baru = 0;
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // retrieve scan result

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, intent);

        switch (requestCode) {
            case REQUEST_ENABLE_BT:      //请求打开蓝牙
                if (resultCode == Activity.RESULT_OK) {   //蓝牙已经打开
                    Toast.makeText(getActivity(), "Bluetooth open successful", Toast.LENGTH_LONG).show();
                } else {                 //用户不允许打开蓝牙
                    // finish();
                }
                break;
            case REQUEST_CONNECT_DEVICE:     //请求连接某一蓝牙设备
                if (resultCode == Activity.RESULT_OK) {   //已点击搜索列表中的某个设备项
                    String address = intent.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);  //获取列表项中设备的mac地址
                    con_dev = mService.getDevByMac(address);

                    mService.connect(con_dev);
                }
                break;
            case IntentIntegrator.REQUEST_CODE:     //请求连接某一蓝牙设备
                Log.d("canceledbro", String.valueOf(resultCode));
                if (resultCode != 0) {
                    if (scanningResult != null) {
                        // we have a result
                        String scanContent = scanningResult.getContents();
                        validator(scanContent);
                        //  Toast.makeText(getActivity(),
                        //        scanContent, Toast.LENGTH_SHORT).show();
                        // IDEditText .setText("CONTENT: " + scanContent);
                    } else {
                        Toast.makeText(getActivity(),
                                "QRcode Salah", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                //Toast.makeText(getActivity(),
                //        "", Toast.LENGTH_SHORT).show();
        }
    }


    private void validator(String hasil) {
        List<String> items = Arrays.asList(hasil.split("\\s*,\\s*"));
        dateqr = items.get(0);
        nokartux = items.get(1);
        notransaksi = items.get(2);
        totalharga = items.get(3);
        totalhargaint = Integer.parseInt(totalharga);

        //cek no kartu masi dalam masa tenggat
        //fiel1


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
                //Calendar cal = Calendar.getInstance();
                //TimeZone timeZoneInDevice = cal.getTimeZone();

                //int differentialOfTimeZones = timeZoneInDevice.getOffset(System.currentTimeMillis());
                //nowAsPerDeviceTimeZone -= differentialOfTimeZones;
                //String myFormat = "yyyy/MM/dd"; // In which you need put here
                //sdf = new SimpleDateFormat(myFormat, Locale.US);
                String myFormat = "ddMMyyyy"; // In which you need put here
                SimpleDateFormat edf = new SimpleDateFormat("dd MMM yyyy hh:mm");
                SimpleDateFormat fdf = new SimpleDateFormat("hh:mm");
                SimpleDateFormat adf = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf;
                sdf = new SimpleDateFormat(myFormat, Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
                edf.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
                fdf.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
                adf.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
                timt = adf.format(nowAsPerDeviceTimeZone);
                //timt = adf.parse(nowAsPerDeviceTimeZone);
                timd = sdf.format(nowAsPerDeviceTimeZone);
                timl = edf.format(nowAsPerDeviceTimeZone);
                timo = fdf.format(nowAsPerDeviceTimeZone);

                local_date = true;

            }
            //local_date = false;
            return local_date;
        }

        @Override
        protected void onPostExecute(Boolean local_date) {
            boolean datevalid, totalh, nokar, fiedl, ref, aktifas;
            if (timd.equals(dateqr)) {
                datevalid = true;
            } else {
                datevalid = false;
                Toast.makeText(getActivity(),
                        dateqr + "vs" + timd + " valid : " + String.valueOf(datevalid), Toast.LENGTH_SHORT).show();

            }

            if (totalhargaint == totalhargapen) {
                totalh = true;
            } else {
                totalh = false;
                Toast.makeText(getActivity(),
                        " nilai pembayaran yang dimasukan salah ", Toast.LENGTH_SHORT).show();

            }
            lolo = LoginDataBaseAdapter.getInstance(getActivity());
            lolo.open();
            // todo check juga nokartu
            if (lolo.getnokartu(nokartux)) {
                nokar = true;
                Toast.makeText(getActivity(),
                        " anda sudah daftar ", Toast.LENGTH_SHORT).show();

            } else {
                nokar = false;
                Toast.makeText(getActivity(),
                        " kartu tidak ada / anda belum daftar ", Toast.LENGTH_SHORT).show();

            }

            // todo check tanggal tenggat
            SimpleDateFormat adf = new SimpleDateFormat("yyyy-MM-dd");
            //String lbl_date="2011-06-26";
            Date dteng = new Date();
            Date timte = new Date();
            try {
                dteng = adf.parse(lolo.getfield1kartu(nokartux));
                timte = adf.parse(timt);
                //     Log.d("datetenggat",dteng.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //         Log.d("datetenggat",timt.toString());
            if (timte.before(dteng) || (barubayar == true)) {
                fiedl = true;

                //todo : sw sudah dibayar cek apakah sp sudah dibayar
                Toast.makeText(getActivity(),
                        " Anda sudah membayar simpanan ", Toast.LENGTH_SHORT).show();

                String vv = lolo.getstatuskartu(nokartux);
                if (vv == "1") {
                    Toast.makeText(getActivity(),
                            " Status keanggotaan warung aktif", Toast.LENGTH_SHORT).show();
                }


            } else {
                fiedl = false;
                Toast.makeText(getActivity(),
                        " Anda belum membayar simpanan wajib ", Toast.LENGTH_SHORT).show();
            }


            // check no kartu
            if (lolo.getnoreff(notransaksi)) {
                ref = false;
                Toast.makeText(getActivity(),
                        " transaksi sudah pernah dilakukan ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(),
                        " transaksi belum pernah dilakukan ", Toast.LENGTH_SHORT).show();
                ref = true;
            }

            // new async task to check transaction validity online
            if (local_date = true) {
                if (datevalid && totalh && fiedl && nokar && ref) {
                    //Toast.makeText(getActivity(),
                    //        " transaksi terverifikasi ", Toast.LENGTH_SHORT).show();


                    new AsyncCaller().execute();
                } else {
                    barubayar = false;
                    //       productList.clear();
                    //       adapter.notifyDataSetChanged();
                    //       totalpen.setText("Total Rp. " + "0");
                    Toast.makeText(getActivity(),
                            " transaksi gagal ", Toast.LENGTH_SHORT).show();

                }
            }

            // create new post execute to server for transaction approval

            bayarx.setEnabled(true);
            bayarx.setFocusable(true);
        }

    }

    private class AsyncCaller extends AsyncTask<String, String, JSONObject> {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        JSONArray array;
        StringBuilder sbParams, sbParams2;
        String charset = "UTF-8";
        String paramsString;
        //HashMap<String, String> params;
        String[] aa;
        String unique;

        JSONParser_2 jsonParser = new JSONParser_2();
        private static final String LOGIN_URL = "http://ewarong.kmis.web.id/webserv2/penjualan.php";


        // variable to hold context
        private Context mContext;

        //save the context recievied via constructor in a local variable

        public void YourNonActivityClass(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            unique = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
            Mcrypt mcrypt = new Mcrypt();
            try {
                unique = Mcrypt.bytesToHex(mcrypt.encrypt(unique));
            } catch (Exception e) {
                e.printStackTrace();
            }


            JSONArray array = new JSONArray();
            //this method will be running on UI thread
            //pdLoading.setMessage("\tLoading...");
            //pdLoading.show();
        }

        @Override
        protected JSONObject doInBackground(String... paramsx) {
            //sbParams = new StringBuilder();
            sbParams2 = new StringBuilder();
            String kmiskode = "kmis";


            aa = lolo.encryptcredential(preferences.getString("username", ""), preferences.getString("password", ""));


            HashMap<String, String> params2 = new HashMap<>();
            params2.put("username", preferences.getString("username", ""));
            params2.put("password", aa[1]);
            params2.put("unique", unique);
            params2.put("tanggal", dateqr);
            params2.put("nokartu", nokartux);
            params2.put("noreferensi", notransaksi);
            params2.put("totalpembayaran", String.valueOf(totalhargaint));
            params2.put("arrayno", String.valueOf(productList.size()));
            // create hashmap json
            for (int position = 0; position < productList.size(); position++) {
                String namajson = "barang" + String.valueOf(position);
                JSONObject params = new JSONObject();
                try {
                    //HashMap<String, String> params = new HashMap<>();
                    params.put("sKbarang", productList.get(position).getsKbarang());
                    params.put("sNbarang", productList.get(position).getsNbarang());
                    params.put("sHarga", String.valueOf(productList.get(position).getsHarga()));
                    params.put("sJumlah", String.valueOf(productList.get(position).getsJumlah()));
                    params.put("sTotal", String.valueOf(productList.get(position).getsTotal()));

                    if (productList.get(position).getsKbarang().toLowerCase().contains(kmiskode.toLowerCase())) {
                        params.put("kategori", "1");
                    } else {
                        params.put("kategori", "2");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //paramsString = sbParams.toString();
                params2.put(namajson, params.toString());
            }

            int i = 0;
            for (String key : params2.keySet()) {
                try {
                    if (i != 0) {
                        sbParams2.append("&");
                    }
                    sbParams2.append(key).append("=")
                            .append(URLEncoder.encode(params2.get(key), charset));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                i++;
            }
            paramsString = sbParams2.toString();
            Log.d("json kiriman :", paramsString);
            //Toast.makeText(getActivity(),
            //         paramsString, Toast.LENGTH_SHORT).show();
            JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", paramsString, mContext);
            Log.d("request", "starting");
            if (json != null) {
                Log.d("JSON result", json.toString());
                return json;
            }


            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            //super.onPostExecute(result);
            int success;
            success = 0;
            String message = "";
            tenggat = "";
            int aktifasi = 0;
            if (json != null) {
                try {
                    success = json.getInt("success");
                    message = json.getString("message");
                    tenggat = json.getString("datetenggat");
                    aktifasi = json.getInt("aktifasi");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (success == 1) {
                Log.d("sukses", "sukses");
                Toast.makeText(getActivity(),
                        message, Toast.LENGTH_SHORT).show();

                // todo data anggota - tanggaltenggat
                // todo update buku penjualan
                // todo update penjualan
                // todo update buku simpanan - jangan lupa buat loop iterasi untuk cek simpanan pokok & simpanan wajib

                String noot;
                noot = buatnota();
                transaksiberhasil(noot);
                // alert dialog nota pembelian
                // print barcode


            } else {
                barubayar = false;
                productList.clear();
                adapter.notifyDataSetChanged();
                //notatrx ="";
                totalpen.setText("Total Rp. " + "0");
            }




            lolo.close();
            //this method will be running on UI thread

            //pdLoading.dismiss();
        }

    }

    private String buatnota() {

        String notatrx = preferences.getString("warung", "") + "\nUser Kasir: " + preferences.getString("username", "") + "\nTanggal: " + timl;
        String kmiskode = "kmis";
        if (productList.size() > 0) {
            for (int i = 0; i < productList.size(); i++) {

                notatrx += "\n \nKode: " + productList.get(i).getsKbarang() + "\n" + productList.get(i).getsNbarang() + "\n" +
                        "Harga: @Rp." + productList.get(i).getsHarga() + " * " + productList.get(i).getsJumlah() + " EA" +
                        "\n Subtotal: Rp." + productList.get(i).getsTotal();

            }
            notatrx += "\n -----------------------------";
            notatrx += "\nTOTAL = Rp." + totalhargapen + "\n ";
            notatrx += "\nNo.Ref:" + notransaksi;
            notatrx += "\nNo.Kartu:" + nokartux;
            notatrx += "\nTenggat:" + tenggat;

            // if (productList.get(i).getsKbarang().toLowerCase().contains(kmiskode.toLowerCase())) {
            // params.put("kategori", "1");
            //  } else {
            // params.put("kategori", "2");
            //  }


        } else
            notatrx = "\n\nShopping cart is empty.\n\n";


        return notatrx;
    }

    private void transaksiberhasil(final String notac) {
        //save all to db
        saveDB();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        alertDialogBuilder
                .setTitle("Transaksi Berhasil - Print Nota Transaksi?")
                .setMessage(notac)
                .setCancelable(true)
                .setNeutralButton("", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setPositiveButton("Print", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        printnota(notac);


                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        barubayar = false;
                        productList.clear();
                        adapter.notifyDataSetChanged();
                        //notatrx ="";
                        totalpen.setText("Total Rp. " + "0");
                        dialog.cancel();

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        textView.setMaxLines(8);
        textView.setScroller(new Scroller(getActivity()));
        textView.setVerticalScrollBarEnabled(true);
        textView.setMovementMethod(new ScrollingMovementMethod());


    }

    public void printnota(String notax) {
        printImage();
        String msg = "";
        String lang = getString(R.string.strLang);
        printImage();

        byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;

        //倍宽、倍高模式
        cmd[2] &= 0xEF;
        mService.write(cmd);
        msg = notax;

        mService.sendMessage(msg, "GBK");
        cmd[2] |= 0x10;
        mService.write(cmd);
        mService.sendMessage("Terima Kasih!\n", "GBK");
        //取消倍高、倍宽模式

        barubayar = false;
        productList.clear();
        adapter.notifyDataSetChanged();
        //notatrx ="";
        totalpen.setText("Total Rp. " + "0");
    }

    private void updateDB() {
        LoginDataBaseAdapter loginDataBaseAdapter = new LoginDataBaseAdapter(getActivity());
        loginDataBaseAdapter.open();
        int cc = productList.size();
        int qtynow, qtythen, x, y;

        Cursor c;
        if (cc > 0) {
            for (int i = 0; i < cc; i++) {

                c = loginDataBaseAdapter.db.rawQuery("select * from stok where Kodebarang = '" + productList.get(i).getsKbarang() + "'", null);
                String pname = productList.get(i).getsKbarang();
                if (!(c.moveToFirst()) || c.getCount() == 0) {
                    c.close();
                } else {

                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                        //populate table here

                        qtynow = c.getInt(c.getColumnIndex("Qty"));
                        y = productList.get(i).getsJumlah();
                        qtythen = qtynow - y;
                        loginDataBaseAdapter.stok(pname, "", 0, qtythen);

                    }
                }
                c.close();
            }

        }

        loginDataBaseAdapter.close();

    }

    @Override
    public void onStart() {
        super.onStart();
        //蓝牙未打开，打开蓝牙

        try {
            if (mService.isBTopen() == false) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
                /*btnSendDraw = (Button) this.findViewById(R.id.btn_test);
				btnSendDraw.setOnClickListener(new ClickEvent());
				btnSearch = (Button) this.findViewById(R.id.btnSearch);
				btnSearch.setOnClickListener(new ClickEvent());
				btnSend = (Button) this.findViewById(R.id.btnSend);
				btnSend.setOnClickListener(new ClickEvent());
				btnClose = (Button) this.findViewById(R.id.btnClose);
				btnClose.setOnClickListener(new ClickEvent());
				edtContext = (EditText) findViewById(R.id.txt_content);
				btnClose.setEnabled(false);
				btnSend.setEnabled(false);
				btnSendDraw.setEnabled(false);*/
            //post_comment.setEnabled(false);
        } catch (Exception ex) {
            Log.e("出错信息", ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null)
            mService.stop();
        mService = null;
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:   //已连接
                            Toast.makeText(getActivity(), "Connect successful",
                                    Toast.LENGTH_SHORT).show();
                            //ee_printer.setEnabled(true);
                            //post_comment.setEnabled(true);
                            //btnSendDraw.setEnabled(true);
                            break;
                        case BluetoothService.STATE_CONNECTING:  //正在连接
                            Log.d("蓝牙调试", "正在连接.....");
                            break;
                        case BluetoothService.STATE_LISTEN:     //监听连接的到来
                        case BluetoothService.STATE_NONE:
                            Log.d("蓝牙调试", "等待连接.....");
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:    //蓝牙已断开连接
                    //    Toast.makeText(getActivity(), "Device connection was lost",
                    //            Toast.LENGTH_SHORT).show();
                    //ee_printer.setEnabled(false);
                    //post_comment.setEnabled(false);
                    //btnSendDraw.setEnabled(false);
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:     //无法连接设备
                    Toast.makeText(getActivity(), "Unable to connect device",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };

    private void printImage() {
        byte[] sendData = null;
        String imageUri = "drawable://" + R.drawable.ic_launcher;
        PrintPic pg = new PrintPic();
        pg.initCanvas(384);
        pg.initPaint();
        pg.drawImage(0, 0, imageUri);
        sendData = pg.printDraw();
        mService.write(sendData);   //打印byte流数据
    }

    private boolean saveDB() {
        String[] aar = new String[7];
        int[] aax = new int[3];
        String notatrx = preferences.getString("warung", "") + "\nUser Kasir: " + preferences.getString("username", "") + "\nTanggal: " + timl;
        String kmiskode = "kmis";
        String idangg = "";
        idangg = lolo.getilduserskartu(nokartux);


        aar[6] = preferences.getString("username", "");
        aar[0] = idangg;
        aar[1] = "";
        aar[2] = timd;
        aar[3] = timo;
        aar[4] = notransaksi;
        aar[5] = nokartux;
        lolo.tambahpenjualan(aar, totalhargaint);

        if (productList.size() > 0) {
            for (int i = 0; i < productList.size(); i++) {
                aar[1] = productList.get(i).getsKbarang();
                if (productList.get(i).getsKbarang().toLowerCase().contains(kmiskode.toLowerCase())) {
                    // buku simpanan
                    lolo.tambahsimpanan(aar, productList.get(i).getsTotal());

                    if (productList.get(i).getsKbarang().toLowerCase().equals("kmis-sw-3")) {
                        // set tanggal tenggat
                        lolo.settenggatanggota(idangg, tenggat);
                    }

                    if (productList.get(i).getsKbarang().toLowerCase().equals("kmis-sp")) {
                        // set aktif
                        lolo.setaktifanggota(idangg);
                    }


                } else {
                    // buku penjualan
                    String nbr = productList.get(i).getsNbarang();
                    aax[0] = productList.get(i).getsJumlah();
                    aax[1] = productList.get(i).getsHarga();
                    aax[2] = productList.get(i).getsTotal();
                    lolo.tambahjual(aar, aax, nbr);

                    //kurangi stok
                    Cursor c = lolo.db.rawQuery("select * from stok where Kodebarang = '" + productList.get(i).getsKbarang() + "'", null);
                    String pname = productList.get(i).getsKbarang();
                    if (!(c.moveToFirst()) || c.getCount() == 0) {
                        c.close();
                    } else {
                        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                            //populate table here
                            int qtynow = c.getInt(c.getColumnIndex("Qty"));
                            int y = productList.get(i).getsJumlah();
                            int qtythen = qtynow - y;
                            lolo.stok(pname, "", 0, qtythen);
                        }
                    }
                    c.close();

                }

            }

        }

        return false;
    }


}



