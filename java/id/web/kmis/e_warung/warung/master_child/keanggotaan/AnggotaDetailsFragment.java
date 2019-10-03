package id.web.kmis.e_warung.warung.master_child.keanggotaan;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebHistoryItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TextView;

import android.widget.Toast;

import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.NestedActivityResultFragment;
import com.zj.btsdk.BluetoothService;

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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import id.web.kmis.e_warung.R;
import id.web.kmis.e_warung.dbadapter.JSONParser;
import id.web.kmis.e_warung.dbadapter.LoginDataBaseAdapter;
import id.web.kmis.e_warung.dbadapter.Mcrypt;
import id.web.kmis.e_warung.dbadapter.SetDatabaseAdapter;
import id.web.kmis.e_warung.network.AndroidMultiPartEntity;
import id.web.kmis.e_warung.network.Config;
import id.web.kmis.e_warung.sensor.AndroidCameraApi;
import id.web.kmis.e_warung.sensor.Custom_CameraActivity;
import id.web.kmis.e_warung.sensor.SDImageLoader;

/**
 * Created by js on 8/21/2016.
 */
public class AnggotaDetailsFragment extends NestedActivityResultFragment {

    private static final String TAG = "ee";

    Button btnTackPic, buttonktp, buttonfoto, simpandatap, printidbar;

    EditText editNama, editNoAnggota, editAlamat, EditTempat, EditTgl,
            editTelepon, editNoKtp, editNoKartu, editNIbuKandung;
    Spinner spinPosisi, spinPedidikan, spinPekerjaan;

    ArrayAdapter<CharSequence> adapter3, adapter, adapter2;
    ImageView ivThumbnailPhoto, Imagefoto, Imagektp;
    String posisi, pendidikan, pekerjaan;
    CheckBox chkWindows;
    String fotopath, ktppath, formpath, unique;
    Calendar myCalendar;
    private SDImageLoader mImageLoader;
    Uri imageUri = null;
    ProgressBar progressBar;
    int baru, ganti;
    String field2;
    String muke;
    long totalSize = 0;
    String momo;

    //private ArrayList<ModelProducts> listOfObjects;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothService mService = null;
    BluetoothDevice con_dev = null;
    private static final int REQUEST_CONNECT_DEVICE = 1;  //获取设备消息


    SetDatabaseAdapter dkuro;
    LoginDataBaseAdapter loginDataBaseAdapter;
    String userbaru, password, posisiskrg;
    boolean fff = false;

    private File output = null;

    public static AnggotaDetailsFragment newInstance(int idang, String index, int baru) {
        AnggotaDetailsFragment f = new AnggotaDetailsFragment();


        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("idang", idang);
        args.putString("index", index);
        args.putInt("baru", baru);
        //args.putInt("barupor",barux );
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.camgps, container, false);

        loginDataBaseAdapter = new LoginDataBaseAdapter(getActivity());
        loginDataBaseAdapter.open();
        dkuro = SetDatabaseAdapter.getInstance(getActivity());
        dkuro.open();
        // jnj = null;
        //
        field2 = "";
        fotopath = "";
        ktppath = "";
        formpath = "";
        userbaru = "";
        password = "";
        ganti = 0;
        posisiskrg = "";

        editNama = (EditText) view.findViewById(R.id.editNama);
        editNoAnggota = (EditText) view.findViewById(R.id.editNoAnggota);
        editAlamat = (EditText) view.findViewById(R.id.editAlamat);
        EditTempat = (EditText) view.findViewById(R.id.EditTempat);
        EditTgl = (EditText) view.findViewById(R.id.EditTgl);
        editTelepon = (EditText) view.findViewById(R.id.editTelepon);
        editNoKtp = (EditText) view.findViewById(R.id.editNoKtp);
        editNoKartu = (EditText) view.findViewById(R.id.editNoKartu);
        editNIbuKandung = (EditText) view.findViewById(R.id.editIbu);

        chkWindows = (CheckBox) view.findViewById(R.id.chkWindows);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        //spinPosisi = (Spinner) view.findViewById(R.id.editPosisiAng);
        //spinPedidikan = (Spinner) view.findViewById(R.id.editPendidikan);
        spinPekerjaan = (Spinner) view.findViewById(R.id.editPekerjaan);

        spinPosisi = (Spinner) view.findViewById(R.id.editPosisiAng);
        adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.PosisiAng, R.layout.simple_spinner);
        adapter.setDropDownViewResource(R.layout.simple_spinner_drop);
        spinPosisi.setAdapter(adapter);
        posisi = "anggota";
        spinPosisi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                posisi = spinPosisi.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        spinPedidikan = (Spinner) view.findViewById(R.id.editPendidikan);
        adapter2 = ArrayAdapter.createFromResource(getActivity(),
                R.array.Pendidikan, R.layout.simple_spinner);
        adapter2.setDropDownViewResource(R.layout.simple_spinner_drop);
        spinPedidikan.setAdapter(adapter2);
        pendidikan = "Tdk";
        spinPedidikan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                pendidikan = spinPedidikan.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        spinPekerjaan = (Spinner) view.findViewById(R.id.editPekerjaan);
        adapter3 = ArrayAdapter.createFromResource(getActivity(),
                R.array.Pekerjaan, R.layout.simple_spinner);
        adapter3.setDropDownViewResource(R.layout.simple_spinner_drop);
        spinPekerjaan.setAdapter(adapter3);
        pekerjaan = "Tdk Bekerja";
        spinPekerjaan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                pekerjaan = spinPekerjaan.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        editNama.setFocusable(false);
        EditTempat.setFocusable(false);
        editNoAnggota.setFocusable(false);
        editNoKtp.setFocusable(false);
        editNoKartu.setFocusable(false);
        editNIbuKandung.setFocusable(false);
        EditTgl.setEnabled(false);
        EditTgl.setFocusable(false);
        editNoKtp.setFocusable(false);
        spinPekerjaan.setFocusable(false);
        spinPosisi.setFocusable(false);
        spinPedidikan.setFocusable(false);

        //todo get data kode anggota from selection through in getIndex or cara lain
        // editNoAnggota.setText(locname);
        // post_lokor = locname;
        simpandatap = (Button) view.findViewById(R.id.simpandata);
        printidbar = (Button) view.findViewById(R.id.printIDBard);
        printidbar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // todo query to simpanan peridanggota
                //  handleIntent(getIntent());
                ceksaldoangg();

            }
        });
        baru = getArguments().getInt("baru", 0);
        // SharedPreferences preferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        if (baru == 0) {
            momo = getArguments().getString("index", "");
            Log.d("baru", "tidak");

        } else {
            Log.d("baru", "iya");
            Random rngg = new Random();
            momo = generateString(rngg, "1234567890", 16);
            editNoAnggota.setText(momo);
        }

        unique = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        Mcrypt mcrypt = new Mcrypt();
        try {
            muke = Mcrypt.bytesToHex(mcrypt.encrypt(momo));
            unique = Mcrypt.bytesToHex(mcrypt.encrypt(unique));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ivThumbnailPhoto = (ImageView) view.findViewById(R.id.ivThumbnailPhoto);
        // int id = getResources().getIdentifier("@drawable/foto","drawable",getActivity().getPackageName());
        // ivThumbnailPhoto.setImageResource(id);
        btnTackPic = (Button) view.findViewById(R.id.btnTakePic);
        btnTackPic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here
                Intent i = new Intent();
                i.setClass(getActivity(), AndroidCameraApi.class);
                //i.setClass(getActivity(), Custom_CameraActivity.class);
                i.putExtra("coor", muke + "_form");
                getActivity().startActivityForResult(i, 232);// Activity is started with
                // requestCode 2

            }
        });

        Imagefoto = (ImageView) view.findViewById(R.id.Imagefoto);
        // id = getResources().getIdentifier( "@drawable/ktp","drawable",getActivity().getPackageName());
        //  Imagefoto.setImageResource(id);
        buttonfoto = (Button) view.findViewById(R.id.buttonfoto);
        buttonfoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here
                Intent i = new Intent();
                i.setClass(getActivity(), AndroidCameraApi.class);
                //i.setClass(getActivity(), Custom_CameraActivity.class);
                i.putExtra("coor", muke + "_foto");
                getActivity().startActivityForResult(i, 212);// Activity is started with


            }
        });

        Imagektp = (ImageView) view.findViewById(R.id.Imagektp);
        //  id = getResources().getIdentifier( "@drawable/form","drawable",getActivity().getPackageName());
        // Imagektp.setImageResource(id);

        buttonktp = (Button) view.findViewById(R.id.buttonktp);
        buttonktp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here
                // Intent i = new Intent(getActivity(), Custom_CameraActivity.class);
                // startActivity(i);
                //  i.putExtra("coor", post_lokor + "_ktp");
                Intent i = new Intent();
                //i.setClass(getActivity(), Custom_CameraActivity.class);
                i.setClass(getActivity(), AndroidCameraApi.class);

                i.putExtra("coor", muke + "_ktp");
                getActivity().startActivityForResult(i, 222);// Activity is started with


            }
        });


        //listOfObjects = new ArrayList<ModelProducts>();
        mService = new BluetoothService(getActivity(), mHandler);
        //蓝牙不可用退出程序

        if (mService.isAvailable() == false) {
            Toast.makeText(getActivity(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
            //finish();
            fff = true;
        }

        return view;

        // return super.onCreateView(inflater, container, savedInstanceState);
/*
        ScrollView scroller = new ScrollView(getActivity());
        TextView text = new TextView(getActivity());
        int padding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, getActivity()
                        .getResources().getDisplayMetrics());
        text.setPadding(padding, padding, padding, padding);
        scroller.addView(text);
        text.setText(Shakespeare.DIALOGUE[getShownIndex()]);
    return scroller; */
        // return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void ceksaldoangg() {
        String notac = "";
        loginDataBaseAdapter.open();

        String[] jnj = loginDataBaseAdapter.getsaldoanggota(editNoKartu.getText().toString());

//        String[] jnj =  loginDataBaseAdapter.getsaldoanggota(momo);
        if (jnj != null) {
            Log.d("simpanan pokok", jnj[0]);
            Log.d("simpanan wajib", jnj[0]);
            loginDataBaseAdapter.close();

            notac = "Nama: " + editNama.getText() + "\n" + "No Kartu: \n" + editNoKartu.getText() + "\nSimpanan Pokok: Rp." + jnj[0] + "\n" +
                    "Simpanan Wajib: Rp." + jnj[1] + "\n" +
                    "Simpanan Sukarela: Rp." + jnj[2];
            // build text saldo
            final String notaz = notac;

            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity(), android.R.style.Theme_Holo_Dialog_NoActionBar);
            alertDialogBuilder
                    .setTitle("Total Saldo Simpanan")
                    .setMessage(notac)
                    .setCancelable(true)
                    .setNeutralButton("", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    })
                    .setPositiveButton("Print", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (fff) {
                                printnota(notaz);
                            } else {
                                Toast.makeText(getActivity(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
                            }

                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
            textView.setMaxLines(8);
            textView.setScroller(new Scroller(getActivity()));
            textView.setVerticalScrollBarEnabled(true);
            textView.setMovementMethod(new ScrollingMovementMethod());
        } else {
            Toast.makeText(getActivity(), "Saldo Kosong", Toast.LENGTH_LONG).show();

        }
    }

    private void updateLabel() {

        String myFormat = "yyyy/MM/dd"; // In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        EditTgl.setText(sdf.format(myCalendar.getTime()));
    }

    private void isibiodata() {
        boolean vv;


        Log.d("idanggota", momo);

        if (momo != null && baru == 0) {

            vv = dkuro.checkkeydevexist();
            dkuro.close();

            String kore;
            //load data anggota
            kore = "";
            Mcrypt mcrypt = new Mcrypt();
            try {
                kore = Mcrypt.bytesToHex(mcrypt.encrypt(momo));
            } catch (Exception e) {
                e.printStackTrace();
            }


            Cursor c = loginDataBaseAdapter.db.rawQuery(
                    "select * from anggota where idanggota = '" + kore + "'", null);
            //||
            if (!(c.moveToFirst()) || c.getCount() == 0) { //masuk if firsttime check app_dev exist or not
                buttonktp.setEnabled(true);
                buttonktp.setClickable(true);
                btnTackPic.setEnabled(true);
                btnTackPic.setClickable(true);
                buttonfoto.setEnabled(true);
                buttonfoto.setClickable(true);
                editNama.setInputType(InputType.TYPE_CLASS_TEXT);
                editNama.setFocusableInTouchMode(true);
                editNama.setFocusable(true);
                editNoAnggota.requestFocus();
                EditTempat.setInputType(InputType.TYPE_CLASS_TEXT);
                EditTempat.setFocusableInTouchMode(true);
                EditTempat.setFocusable(true);
                EditTgl.setEnabled(true);
                EditTgl.setFocusable(true);
                EditTgl.setFocusableInTouchMode(true);
                editNoKtp.setFocusable(true);
                editNoKtp.setFocusableInTouchMode(true);
                editNIbuKandung.setFocusable(true);
                editNIbuKandung.setFocusableInTouchMode(true);
                editNoKartu.setFocusable(true);
                editNoKartu.setFocusableInTouchMode(true);
                spinPedidikan.setFocusable(true);
                spinPekerjaan.setFocusable(true);
                spinPosisi.setFocusable(true);


                //     Log.d("idanggota", String.valueOf(baru));
           /*     if (c.getCount() != 0) {
                    editNama.setText(c.getString(c.getColumnIndex("nama")));
                    editTelepon.setText(c.getString(c.getColumnIndex("telepon")));
                    editAlamat.setText(c.getString(c.getColumnIndex("alamat")));
                    editNoKtp.setText(c.getString(c.getColumnIndex("noktp")));
                    editNoAnggota.setText(momo);
                    EditTempat.setText(c.getString(c.getColumnIndex("tl")));
                    EditTgl.setText(c.getString(c.getColumnIndex("ttl")));
                    fotopath = c.getString(c.getColumnIndex("filefoto"));
                    ktppath = c.getString(c.getColumnIndex("filektp"));
                    formpath = c.getString(c.getColumnIndex("fileform"));
                    //editNoAnggota.setText(momo);
                    if (c.getColumnIndex("aktif") == 1) {

                        chkWindows.setChecked(true);
                    }
                }
*/
            } else {
                c.moveToFirst();

                buttonktp.setEnabled(true);
                buttonktp.setClickable(true);
                btnTackPic.setEnabled(true);
                btnTackPic.setClickable(true);
                buttonfoto.setEnabled(true);
                buttonfoto.setClickable(true);
                editNama.setInputType(InputType.TYPE_CLASS_TEXT);
                editNama.setFocusableInTouchMode(true);
                editNama.setFocusable(true);
                editNoAnggota.requestFocus();
                EditTempat.setInputType(InputType.TYPE_CLASS_TEXT);
                EditTempat.setFocusableInTouchMode(true);
                EditTempat.setFocusable(true);
                EditTgl.setEnabled(true);
                EditTgl.setFocusable(true);
                EditTgl.setFocusableInTouchMode(true);
                editNoKtp.setFocusable(true);
                editNoKtp.setFocusableInTouchMode(true);
                editNIbuKandung.setFocusable(true);
                editNIbuKandung.setFocusableInTouchMode(true);
                editNoKartu.setFocusable(true);
                editNoKartu.setFocusableInTouchMode(true);
                spinPedidikan.setFocusable(true);
                spinPekerjaan.setFocusable(true);
                spinPosisi.setFocusable(true);

                editNama.setText(c.getString(c.getColumnIndex("nama")));
                editAlamat.setText(c.getString(c.getColumnIndex("alamat")));
                editNoAnggota.setText(momo);
                EditTempat.setText(c.getString(c.getColumnIndex("tl")));
                EditTgl.setText(c.getString(c.getColumnIndex("ttl")));
                editTelepon.setText(c.getString(c.getColumnIndex("telepon")));
                editNoKtp.setText(c.getString(c.getColumnIndex("noktp")));

                editNIbuKandung.setText(c.getString(c.getColumnIndex("namaibu")));
                editNoKartu.setText(c.getString(c.getColumnIndex("nokartu")));

                posisiskrg = c.getString(c.getColumnIndex("posisi"));
                spinPosisi.setSelection(adapter.getPosition(posisiskrg));

                spinPedidikan.setSelection(adapter2.getPosition(c.getString(c.getColumnIndex("pendidikan"))));
                spinPekerjaan.setSelection(adapter3.getPosition(c.getString(c.getColumnIndex("pekerjaan"))));

                fotopath = c.getString(c.getColumnIndex("filefoto"));
                ktppath = c.getString(c.getColumnIndex("filektp"));
                formpath = c.getString(c.getColumnIndex("fileform"));


                if (c.getInt(c.getColumnIndex("aktif")) == 1) {
                    // if nfc or swipe code exist then set aktif
                    //todo ganti ke tanggal tenggat
                    chkWindows.setChecked(true);
                }
            }


            c.close();


        } else {

            editNama.setText("");
            editAlamat.setText("");
            EditTempat.setText("");
            EditTgl.setText("");
            editTelepon.setText("");
            editNoKtp.setText("");

            buttonktp.setEnabled(true);
            buttonktp.setClickable(true);
            btnTackPic.setEnabled(true);
            btnTackPic.setClickable(true);
            buttonfoto.setEnabled(true);
            buttonfoto.setClickable(true);
            simpandatap.setEnabled(true);
            simpandatap.setClickable(true);

            editNama.setInputType(InputType.TYPE_CLASS_TEXT);
            editNama.setFocusableInTouchMode(true);
            editNama.setFocusable(true);
            //editNoAnggota.requestFocus();
            //editNoAnggota.setInputType(InputType.TYPE_CLASS_TEXT);
            //editNoAnggota.setFocusableInTouchMode(true);
            //editNoAnggota.setFocusable(true);
            EditTempat.setInputType(InputType.TYPE_CLASS_TEXT);
            EditTempat.setFocusableInTouchMode(true);
            EditTempat.setFocusable(true);
            EditTgl.setEnabled(true);
            EditTgl.setFocusable(true);
            EditTgl.setFocusableInTouchMode(true);
            editNoKtp.setFocusable(true);
            editNoKtp.setFocusableInTouchMode(true);
            editNIbuKandung.setFocusable(true);
            editNIbuKandung.setFocusableInTouchMode(true);
            editNoKartu.setFocusable(true);
            editNoKartu.setFocusableInTouchMode(true);
            spinPedidikan.setFocusable(true);
            spinPekerjaan.setFocusable(true);
            spinPosisi.setFocusable(true);

            // baru = 1;
            // create new idanggota random string 20


        }

        loginDataBaseAdapter.close();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("baru", String.valueOf(baru));

        isibiodata();

        if (fotopath != "") {
            mImageLoader = new SDImageLoader();
            mImageLoader.load(fotopath, Imagefoto);

            Imagefoto.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    File externalFile = new File(fotopath);
                    Uri external = Uri.fromFile(externalFile);
                    intent.setDataAndType(external, "image/*");
                    startActivity(intent);

                }
            });
        }

        if (ktppath != "") {
            mImageLoader = new SDImageLoader();
            mImageLoader.load(ktppath, Imagektp);

            Imagektp.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    File externalFile = new File(ktppath);
                    Uri external = Uri.fromFile(externalFile);
                    intent.setDataAndType(external, "image/*");
                    startActivity(intent);
                }
            });


        }
        if (formpath != "") {
            mImageLoader = new SDImageLoader();
            mImageLoader.load(formpath, ivThumbnailPhoto);

            ivThumbnailPhoto.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    File externalFile = new File(formpath);
                    Uri external = Uri.fromFile(externalFile);
                    intent.setDataAndType(external, "image/*");
                    startActivity(intent);
                    // v.getId() will give you the image id

                }
            });

        } else {



        }
        // Do you have Camera Apps?
        if (hasDefualtCameraApp(MediaStore.ACTION_IMAGE_CAPTURE)) {

        }

        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };


        EditTgl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), android.app.AlertDialog.THEME_HOLO_DARK, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        simpandatap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here
                // validate user

                if (validate()) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    //check dulu posisi yg dipilih
                    //jika anggota lanjut
                    //jika yang lain mesti update ke table users
                    simpandatap.setClickable(false);
                    simpandatap.setEnabled(false);
                    LoginDataBaseAdapter doma = new LoginDataBaseAdapter(getActivity());
                    doma.open();
                    doma.SiteUi("Anggota", editNama.getText().toString(), editNoAnggota.getText().toString(), editAlamat.getText().toString(), editTelepon.getText().toString(), EditTempat.getText().toString(), EditTgl.getText().toString(), editNoKtp.getText().toString(), ktppath, fotopath, formpath, editNoKartu.getText().toString(), editNIbuKandung.getText().toString(), posisi, pendidikan, pekerjaan, preferences.getString("username", ""));
                    doma.close();
                    // Toast.makeText(getActivity(), "Berhasil disimpan", Toast.LENGTH_LONG).show();
                    new PostAsync().execute(editNama.getText().toString(), editNoAnggota.getText().toString(), editAlamat.getText().toString(), EditTempat.getText().toString(), EditTgl.getText().toString(), editTelepon.getText().toString(), editNoKtp.getText().toString(), ktppath, fotopath, formpath, editNoKartu.getText().toString(), editNIbuKandung.getText().toString(), posisi, pendidikan, pekerjaan);
                    // new UploadFileToServer().execute();
                    // Log.d("validated", "validated");
                }


            }
        });
    }

    private boolean validate() {
        boolean valid = true;

        String name = editNama.getText().toString();
        String nik = editNoKtp.getText().toString();
        String nokartu = editNoKartu.getText().toString();
        String alamat = editAlamat.getText().toString();
        String tempat = EditTempat.getText().toString();
        String tanggal = EditTgl.getText().toString();
        String telepon = editTelepon.getText().toString();
        String ibuk = editNIbuKandung.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            //      editNama.setError("Sedikitnya 3 karakter");
            editNama.setError(Html.fromHtml("<font color='#ffffff'>Sedikitnya 3 karakter</font>"));

            valid = false;
        } else {
            editNama.setError(null);
        }

        if (nik.isEmpty() || nokartu.length() < 16 || nokartu.length() > 16) {
            //editNoKtp.setError("isi 16 digit nomor KTP");
            editNoKtp.setError(Html.fromHtml("<font color='#ffffff'>isi 16 digit nomor KTP</font>"));

            valid = false;
        } else {
            editNoKtp.setError(null);
        }

        if (nokartu.isEmpty() || nokartu.length() < 16 || nokartu.length() > 16) {
            //editNoKtp.setError("isi 16 digit nomor kartu");
            editNoKartu.setError(Html.fromHtml("<font color='#ffffff'>isi 16 digit nomor Kartu</font>"));

            valid = false;
        } else {
            editNoKartu.setError(null);
        }

        if (telepon.isEmpty() || telepon.length() < 3 || !(telepon.matches("^[0-9]*$"))) {
            //editTelepon.setError("Nomor hp dimulai 08");
            editTelepon.setError(Html.fromHtml("<font color='#ffffff'>Nomor hp dimulai 08</font>"));

            valid = false;
        } else {
            editTelepon.setError(null);
        }

        if (alamat.isEmpty() || alamat.length() < 10) {
            //editAlamat.setError("isi alamat dengan lengkap");
            // editAlamat.setError(Html.fromHtml("<font color="#000000">isi alamat dengan lengkap</font>"));
            editAlamat.setError(Html.fromHtml("<font color='#ffffff'>isi alamat dengan lengkap</font>"));
            valid = false;
        } else {
            editAlamat.setError(null);
        }

        if (tempat.isEmpty() || tempat.length() < 3) {
            // EditTempat.setError("isi tempat lahir dengan benar");
            EditTempat.setError(Html.fromHtml("<font color='#ffffff'>isi tempat lahir dengan benar</font>"));
            valid = false;
        } else {
            EditTempat.setError(null);
        }

       /* if (tanggal.isEmpty() || tanggal.length() < 3 ) {
            EditTgl.setError("gunakan pemilih tanggal klik 2x pada kolom");
            valid = false;
        } else {
            EditTgl.setError(null);
        }*/

        if (ibuk.isEmpty() || ibuk.length() < 3) {
            //editNIbuKandung.setError("isi nama ibu kandung dengan lengkap");
            editNIbuKandung.setError(Html.fromHtml("<font color='#ffffff'>isi nama ibu kandung dengan lengkap</font>"));

            valid = false;
        } else {
            editNIbuKandung.setError(null);
        }


        if (fotopath != null) {
            // buttonfoto.setError("foto");
            //valid = false;
        } else {
            //  buttonfoto.setError(null);
        }

        if (fotopath != null) {
            //buttonktp.setError("pindai ktp");
            //valid = false;
        } else {
            //  buttonktp.setError(null);
        }

        if (fotopath != null) {
            //btnTackPic.setError("pindai formulir");
            //valid = false;
        } else {
            // btnTackPic.setError(null);
        }

        loginDataBaseAdapter.open();
        // check new user name dan password jika dipilih stok keeper atau sales
        int jmlstoker, jmlsales;
        jmlstoker = loginDataBaseAdapter.getjmlstoker();
        jmlsales = loginDataBaseAdapter.getjmlsales();
        if (posisi.equals("Sales")) {
            if (jmlsales == 2) {
                Toast.makeText(getActivity(), "Jumlah sales person sudah 2. Jika ingin mengganti sales, ganti sales yang sekarang menjadi anggota", Toast.LENGTH_LONG).show();
                valid = false;
            }
        }

        if (posisi.equals("Stok Keeper")) {
            if (jmlstoker == 2) {
                Toast.makeText(getActivity(), "Jumlah stok keeper sudah 2. Jika ingin mengganti stokkeeper, ganti stokkeeper yang sekarang menjadi anggota", Toast.LENGTH_LONG).show();
                valid = false;
            }
        }

        loginDataBaseAdapter.close();
        return valid;
    }

    private boolean hasDefualtCameraApp(String action) {
        final PackageManager packageManager = getActivity().getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);

        return list.size() > 0;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {

        if (requestCode == 232) { // change to 212 to take from Custom_Camera
            // Activity before::: TAKE_PICTURE

            if (resultCode == getActivity().RESULT_OK) {

                final String photoFilePath = intent.getExtras().getString(
                        "imageUri");
                Log.d("CamGps", photoFilePath);
                // Load using class load image
                formpath = photoFilePath;
                //  Toast.makeText(getActivity(), "lokasi form" + formpath,
                //            Toast.LENGTH_LONG).show();

                mImageLoader = new SDImageLoader();
                mImageLoader.load(photoFilePath, ivThumbnailPhoto);

                ivThumbnailPhoto.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        File externalFile = new File(formpath);
                        Uri external = Uri.fromFile(externalFile);
                        intent.setDataAndType(external, "image/*");

                        startActivity(intent);
                        // v.getId() will give you the image id

                    }
                });

            } else if (resultCode == getActivity().RESULT_CANCELED) {


            } else {


            }
        } else if (requestCode == 222) { // change to 212 to take from
            // Custom_Camera
            // Activity before::: TAKE_PICTURE

            if (resultCode == getActivity().RESULT_OK) {

                final String photoFilePath = intent.getExtras().getString(
                        "imageUri");
                Log.d("CamGps", photoFilePath);
                // Load using class load image
                ktppath = photoFilePath;

                mImageLoader = new SDImageLoader();
                mImageLoader.load(photoFilePath, Imagektp);

                Imagektp.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        File externalFile = new File(ktppath);
                        Uri external = Uri.fromFile(externalFile);
                        intent.setDataAndType(external, "image/*");
                        startActivity(intent);
                        // v.getId() will give you the image id

                    }
                });

            } else if (resultCode == getActivity().RESULT_CANCELED) {


            } else {


            }
        } else if (requestCode == 212) { // change to 212 to take from
            // Custom_Camera
            // Activity before::: TAKE_PICTURE

            if (resultCode == getActivity().RESULT_OK) {

                final String photoFilePath = intent.getExtras().getString(
                        "imageUri");
                Log.d("CamGps", photoFilePath);
                // Load using class load image
                fotopath = photoFilePath;

                mImageLoader = new SDImageLoader();
                mImageLoader.load(photoFilePath, Imagefoto);

                Imagefoto.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        File externalFile = new File(fotopath);
                        Uri external = Uri.fromFile(externalFile);
                        intent.setDataAndType(external, "image/*");
                        startActivity(intent);
                        // v.getId() will give you the image id

                    }
                });

            } else if (resultCode == getActivity().RESULT_CANCELED) {

            } else {

            }
        }

    }

    public static String generateString(Random rng, String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

    class PostAsync extends AsyncTask<String, String, JSONObject> {
        JSONParser jsonParser = new JSONParser();
        private ProgressDialog pDialog;

        private static final String LOGIN_URL = "http://ewarong.kmis.web.id/webserv2/anggota2.php";
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
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Menghubungi Server...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
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

                //, editNoKartu.getText().toString(), editNIbuKandung.getText().toString(), posisi, pendidikan, pekerjaan, preferences.getString("username", "")

                HashMap<String, String> params = new HashMap<>();
                params.put("nama", args[0]);
                params.put("idanggota", args[1]);
                params.put("alamat", args[2]);
                params.put("tempat", args[3]);
                params.put("tanggallahir", args[4]);
                params.put("telepon", args[5]);
                params.put("noktp", args[6]);
                params.put("username", preferences.getString("username", ""));
                // Log.v("user", preferences.getString("username", ""));
                params.put("password", aa[1]);
                params.put("posisi", args[12]);

                params.put("nokartu", args[10]);
                params.put("ibukandung", args[11]);
                //params.put("posisi", args[9]);
                params.put("pendidikan", args[13]);
                params.put("pekerjaan", args[14]);

                //todo : keaktifan check box dan data nfc /kartu swipe

                params.put("warung", preferences.getString("warung", ""));
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

            // save ke database via logindatabaseadapter atau entah lewat mana
            // Toast.makeText(getActivity(), json.toString(),
            //        Toast.LENGTH_LONG).show();
            int success;
            success = 0;
            String message = "";


            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (json != null) {
                try {
                    success = json.getInt(TAG_SUCCESS);
                    Log.d("sukses", "sukses");
                    message = json.getString(TAG_MESSAGE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Log.d("fotopath:",fotopath);
            if (success == 1) {
                // update anggota updatedstatus to 1
                LoginDataBaseAdapter doma = new LoginDataBaseAdapter(getActivity());
                doma.open();
                doma.SiteUistatusupdate(editNoAnggota.getText().toString(), success);
                doma.close();

                Log.d("sukses", "sukses2");
                if (fotopath != "" && fotopath != null && ktppath != "" && ktppath != null && formpath != "" && formpath != null) {

                    new UploadFileToServer().execute();

                    simpandatap.setClickable(true);
                    simpandatap.setEnabled(true);
                } else {

                    if (getResources().getConfiguration().orientation != 1) {
                        if (baru == 1) {
                            AnggotaListMain detailx = new AnggotaListMain();
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.frame_container, detailx);
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                            ft.commit();
                        }
                    } else {
                        getActivity().finish();
                    }

                    simpandatap.setClickable(true);
                    simpandatap.setEnabled(true);
                }
                // if posisi = stok keeper / sales upload data registration to signup




            } else {
                Log.d("Failure", message);
            }


        }

    }

    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);
            // updating progress bar value
            progressBar.setProgress(progress[0]);
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
            HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                Log.d("tae", fotopath);
                File sourceFile1 = new File(fotopath);
                File sourceFile2 = new File(ktppath);
                File sourceFile3 = new File(formpath);

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
                // Adding file data to http body
                entity.addPart("ktpfoto", new FileBody(sourceFile2));
                // Adding file data to http body
                entity.addPart("formfoto", new FileBody(sourceFile3));

                // Extra parameters if you want to pass to server  ///todo test dulu ma parameter bawaan
                //entity.addPart("website",
                //        new StringBody("www.androidhive.info"));
                //entity.addPart("email", new StringBody("abc@gmail.com"));

                // Extra parameters if you want to pass to server
                entity.addPart("username", new StringBody(preferences.getString("username", "")));
                entity.addPart("password", new StringBody(aa[1]));
                entity.addPart("idanggota", new StringBody(editNoAnggota.getText().toString()));

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
            LoginDataBaseAdapter doma = new LoginDataBaseAdapter(getActivity());
            doma.open();
            doma.SiteUistatusgambar(editNoAnggota.getText().toString(), 1);
            doma.close();
            // showing the server response in an alert dialog
            //showAlert(result);
            Toast.makeText(getActivity(), "Berhasil disimpan", Toast.LENGTH_LONG).show();

            if (getResources().getConfiguration().orientation != 1) {
                if (baru == 1) {
                    AnggotaListMain detailx = new AnggotaListMain();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frame_container, detailx);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }
            } else {
                getActivity().finish();
            }

            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public int getShownIndex() {
        return getArguments().getInt("idang", 0);
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

    private void printnota(String notax) {
        //printImage();
        String msg = "";
        String lang = getString(R.string.strLang);
        //printImage();

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

        //barubayar = false;
        //productList.clear();
        adapter.notifyDataSetChanged();
        //notatrx ="";
        //totalpen.setText("Total Rp. " + "0");
    }
}