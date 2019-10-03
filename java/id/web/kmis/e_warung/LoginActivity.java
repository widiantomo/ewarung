package id.web.kmis.e_warung;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import id.web.kmis.e_warung.dbadapter.JSONParser;
import id.web.kmis.e_warung.dbadapter.Mcrypt;
import id.web.kmis.e_warung.dbadapter.SetDatabaseAdapter;
import id.web.kmis.e_warung.dbadapter.StringXORer;
import id.web.kmis.e_warung.dbadapter.UserDatabaseAdapter;
import id.web.kmis.e_warung.network.Networkprobe;
import id.web.kmis.e_warung.dbadapter.LoginDataBaseAdapter;
import id.web.kmis.e_warung.warung.master_child.MasterChildActivity;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    // session management
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private static final int REQUEST_SIGNUP = 0;
    private static final String LOGIN_URL = "http://ewarong.kmis.web.id/webserv2/loginaktif2.php";

    // empty container
    public static final String namex = "username";
    public static final String passx = "password";
    public static final String levelx = "level";

    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;
    CheckBox rememberx;
    ProgressDialog progressDialog;

    String nwarung, iduser, posisib;
    boolean zzz, yyy;

    JSONParser jsonParser;
    UserDatabaseAdapter logindb;
    SetDatabaseAdapter dtono;

    String devkeys, warung;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //isConnected();


         devkeys = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
         progressDialog  = new ProgressDialog(LoginActivity.this);
        //setup interface
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);
        rememberx = (CheckBox) findViewById(R.id.checkBox);

        _signupLink.setEnabled(false);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                // cek device key  = jika sudah diisi sama makan cancel
                // cek unique warung = app_uq1 jika sudah diisi maka toast silahkan hubungi pendamping anda
                // cek update jika belum terisi silahkan isi biodata dulu



                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);

            }
        });


        jsonParser = new JSONParser();

        //sudahpernah()
        //test
        //logindb.insertEntry( _emailText.getText().toString(),_passwordText.getText().toString());
        // sync users table ke server
    }

    private void goToUrl (String url) {

        String uriUrl = Uri.parse(url)
                .buildUpon()
                .appendQueryParameter("param1", "param1")
                .appendQueryParameter("param2", "parma2")
                .build().toString();

        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(uriUrl));
        startActivity(launchBrowser);
    }

    public void login() {

        String username = _emailText.getText().toString();
        String passwr = _passwordText.getText().toString();

        /*
        //session in //pindahkan ke after onlogin sukses atau validate user
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(namex, username);
        edit.putString(passx, passwr);
        edit.putString(levelx, "0");
        edit.putBoolean("remember", rememberx.isChecked());
        edit.commit();
        */
        // cek server kondisi aktifasi user

        //final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
        //R.style.AppTheme_Dark_Dialog);
        //progressDialog.setIndeterminate(true);
        //progressDialog.setMessage("Authenticating...");
        //progressDialog.show();

        boolean vv = false;
        //SetDatabaseAdapter dkuro = SetDatabaseAdapter.getInstance(this);
        //dtono.open();
        vv = dtono.checkkeydevexist();
        //check teamleader 1
        //check teamleader 2


        //dtono.close();
        //  Networkprobe nn = new Networkprobe(this);
        if(validateuser(_emailText.getText().toString(), _passwordText.getText().toString())) {
            // if (!vv) {
                checkStatusAktif(); // check apakah ada perubahan lock login
            //  }else {
                //cek koneksi internet
            //   Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet", Toast.LENGTH_LONG).show();
                //onLoginSuccess();
            // }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically

                this.finish();
            }
        }
    }


    @Override
    protected void onResume() {
        sharedpreferences=getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains(namex))
        {
            if(sharedpreferences.contains(passx)){
                //Intent i = new Intent(this, ReadComments.class);
                //startActivity(i);
            }
        }
        dtono = SetDatabaseAdapter.getInstance(this);
        dtono.open();
        logindb = UserDatabaseAdapter.getInstance(this);
        logindb.open();

        boolean yy;

        boolean zz = dtono.checkkeyexisttmp();
        yy = dtono.checkkeyexisttmp2();
        //yy = dtono.checkkeyexist();

        if (zz && yy) {
            _signupLink.setVisibility(View.GONE); // sudah pernah daftar
        } else {
            _signupLink.setEnabled(true);
        }
        //set disini juga

        // SetDatabaseAdapter dtono = SetDatabaseAdapter.getInstance(this);


        //dtono.open();
        yy = dtono.checkkeyexist();
        zz = dtono.checkkeyexist2();
        // dtono.close();
        if (yy && !zz) {
            _signupLink.setEnabled(true);
        } else if (!yy && zz) {
            _signupLink.setEnabled(true);
        } else if (yy && zz) {
            _signupLink.setVisibility(View.GONE);
        } else {
            _signupLink.setEnabled(true);
        }

        super.onResume();
   /*     SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);


        if(sp.getBoolean("remember", true)==true) {
            _emailText.setText(sp.getString("username", ""));
            _passwordText.setText(sp.getString("password", ""));


            rememberx.setChecked(sp.getBoolean("remember", false));
        } else {
            _emailText.setText("");
            _passwordText.setText("");
            rememberx.setChecked(sp.getBoolean("remember", false));
        } */
    }

    public void onLoginSuccess(String leaderno) {


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(namex, _emailText.getText().toString());
        edit.putString(passx, _passwordText.getText().toString());
        edit.putString(levelx, posisib); // todo update ke posisi
        edit.putBoolean("islogin", true);
        // edit.putString("longlati", LongLat);
        edit.putString("warung", nwarung);
        edit.commit();
        //_loginButton.setEnabled(true);
        // Intent i = new Intent(LoginActivity.this, AktifitasListActivity.class);
        Intent i = new Intent(this, MasterChildActivity.class);
        startActivity(i);
        finish();

    }

    public void checkStatusAktif() {
      String name_enc = "", pass_enc = "", adv_enc = "";
        Mcrypt mcrypt = new Mcrypt();
        try {
            //name_enc = Mcrypt.bytesToHex(mcrypt.encrypt(_emailText.getText().toString()));
            pass_enc = Mcrypt.bytesToHex(mcrypt.encrypt(_passwordText.getText().toString()));
            adv_enc = Mcrypt.bytesToHex(mcrypt.encrypt(devkeys));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //logindb = UserDatabaseAdapter.getInstance(mContext);
        //dbono.open();


        //logindb = UserDatabaseAdapter.getInstance(this);
        //losa.open();
        String[] bulki = logindb.getSingleEntry(_emailText.getText().toString());
        //warung = bulki[3];
        //losa.close();


        PostAsync_aktifasi task = new PostAsync_aktifasi();
                task.execute(_emailText.getText().toString(), pass_enc, adv_enc);

    }

    public void updateappdevkey(){
        String adv_enc = "";
        Mcrypt mcrypt = new Mcrypt();
        try {
            //name_enc = Mcrypt.bytesToHex(mcrypt.encrypt(_emailText.getText().toString()));
            adv_enc = Mcrypt.bytesToHex(mcrypt.encrypt(devkeys));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //SetDatabaseAdapter ee = SetDatabaseAdapter.getInstance(this);
        //ee.open();
        String cc = dtono.getkey("app_dev");

        boolean yy;

        yy = dtono.checkkeydevexist();
        if (!yy) {
            dtono.updateAppKey(adv_enc); // sudah pernah daftar
        } else {
            if (cc == adv_enc) {

            } else {
                finish();
            }

        }
        //ee.close();


    }

    public boolean validateuser(String userName, String passw){

        boolean valid = false;
        int level;
        String[] stored;
        // check status untuk level access 2 -- stokkeeper 3 -- sales
        stored = logindb.getSingleEntry(userName);

        if(stored[0] == null) {
            //it is not null and it is not empty
            Toast.makeText(getApplicationContext(), "Username salah", Toast.LENGTH_LONG).show();
            valid = false;
            //logindb.close();
            return valid;
        }

        if(stored[0] != null && stored[1] != null) {
            // resp[1] password encrypt yang ada dan compare - kalau salah password salah
            Mcrypt mcrypt = new Mcrypt();
            String passz;
            passz = "";
            //SetDatabaseAdapter dtono = SetDatabaseAdapter.getInstance(this);
            //dtono.open();
            boolean appd = false;
            appd = dtono.checkkeydevexist();
            // dtono.close();


            try {
            passz = Mcrypt.bytesToHex(mcrypt.encrypt(_passwordText.getText().toString().trim()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(!(stored[1].equals(passz))){
                Toast.makeText(getApplicationContext(), "Password salah", Toast.LENGTH_LONG).show();
                valid = false;
                //logindb.close();
                return valid;
            }

            if(stored[2] == null && appd == false) {
                Toast.makeText(getApplicationContext(), "Tidak terdaftar", Toast.LENGTH_LONG).show();
                valid = false;
                //logindb.close();
                return valid;

            } else if (stored[2] != null && appd == false){

                valid = true;
                // logindb.close();
                return valid;

            } else if (stored[2] == null && appd == true){
                Toast.makeText(getApplicationContext(), "Unauthorized login", Toast.LENGTH_LONG).show();
                valid = false;
                // logindb.close();
                return valid;
            } else if (stored[2] != null && appd == true){

                //     Toast.makeText(getApplicationContext(), "Memasuki..", Toast.LENGTH_LONG).show();

                SharedPreferences sp = PreferenceManager
                        .getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString(namex, userName);
                edit.putString(passx, passw);
                edit.putString(levelx, "1"); // todo ganti ke posisi
                edit.putBoolean("islogin", true);
                //edit.putString("warung","");
                // edit.putString("longlati", LongLat);
                edit.commit();


                valid = true;
                // logindb.close();
                return valid;
            }


            //logindb.close();
        }


        // resp[2] ada tapi resp[3] ga ada - silahkan isi biodata atau tunggu aktifasi
        // resp[2] ga ada tapi resp[3] ada - unauthorized login
        // resp[2] ada tapi resp[3] ada - buka seluruh menu dinext activity
        // resp[2] ga ada tapi resp[3] ga ada - kelaut aje
        return valid;
    }

   private class PostAsync_aktifasi extends AsyncTask<String, String, JSONObject> {
        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        private static final String TAG_SUCCESS = "success";
        private static final String TAG_AKTIF = "darto";
        private static final String TAG_MESSAGE = "message";
        private static final String TAG_VALID = "danang";

       // variable to hold context
       private Context mContext;

       //save the context recievied via constructor in a local variable

       public void YourNonActivityClass(Context context) {
           mContext = context;
       }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                HashMap<String, String> params = new HashMap<>();
                params.put("name", args[0]);
                params.put("password", args[1]);
                params.put("adv", args[2]);
                // Log.d("text",args[0]);
                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params, mContext);
                // check aktifasi user 1 atau user 2
                if (json != null) {
                    // Log.d("JSON result", json.toString());
                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject json) {

            int success = 0;
            String message = "";
            int aktif = 0;
            int valid = 0;
            String leaderno = "";

            //get login ok
            //get aktifasi - iduser
            //getdeviceid  - unique

            if (json != null) {
                //Toast.makeText(LoginActivity.this, json.toString(),
                //       Toast.LENGTH_LONG).show();

                try {
                    success = json.getInt(TAG_SUCCESS);
                    message = json.getString(TAG_MESSAGE);
                    aktif = json.getInt(TAG_AKTIF);
                    valid = json.getInt(TAG_VALID);
                    leaderno = json.getString("tony");

                    iduser = logindb.getidusers(_emailText.getText().toString());
                    posisib = logindb.getlvusers(_emailText.getText().toString());

                    zzz = dtono.checkkeyexist();
                    yyy = dtono.checkkeyexist2();
                    nwarung = dtono.getkey("app_hst");

//                  Log.d("warung : ", leaderno);

                    if (leaderno.equals("2")) {
                        if (!yyy) {
                            dtono.updateIdKeytl2(iduser);
                        }
                    } else if (leaderno.equals("1")) {
                        if (!zzz) {
                            dtono.updateIdKeytl1(iduser);
                        }
                    } else if (leaderno.equals("null")) {
                        int ss = dtono.deleteKey(iduser);
                        logindb.deleteuser(_emailText.getText().toString());
                        Toast.makeText(getApplicationContext(), "Pergantian Team Leader, hubungi Administrator", Toast.LENGTH_LONG).show();
                        success = 0;
                        finish();
                    } else if (leaderno.equals("99")) {

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (success == 1) {
                if (success == 1 && aktif == 1 && valid == 1) {
                    updateappdevkey();
                    logindb.close();
                    dtono.close();
                    //if(validateuser(_emailText.getText().toString(), _passwordText.getText().toString())) {
                    onLoginSuccess(leaderno);
                    // calling to this function from other pleaces
                    // The notice call method of doing things
                    // }

                }

                if (success == 1 && aktif == 0 && valid == 1) {
                    logindb.close();
                    dtono.close();
                    //pindahkan ke aktifasi signup
                    //if(validateuser(_emailText.getText().toString(), _passwordText.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Daftar diri anda di menu anggota dan tunggu aktifasi", Toast.LENGTH_LONG).show();
                    onLoginSuccess(leaderno);
                    // calling to this function from other pleaces
                    // The notice call method of doing things
                    // }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Password anda salah atau akun anda disuspend", Toast.LENGTH_LONG).show();
                // Log.d("Failure", message);
            }

            String levelpendamping = "";
            //todo: set team leader designation



        }

   }

    public boolean isConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService
                    (Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnected()) {
                //Network is available but check if we can get access from the network.
                URL url = new URL("http://ewarong.kmis.web.id/webserv2/loginaktif2.php");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(2000); // Timeout 2 seconds.
                urlc.connect();

                if (urlc.getResponseCode() == 200)  //Successful response.
                {
                    return true;
                } else {
                    Toast.makeText(this, "Tidak ada sambungan internet", Toast.LENGTH_LONG).show();

                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
