package id.web.kmis.e_warung;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings.Secure;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;

import id.web.kmis.e_warung.dbadapter.JSONParser;
import id.web.kmis.e_warung.dbadapter.LoginDataBaseAdapter;
import id.web.kmis.e_warung.dbadapter.Mcrypt;
import id.web.kmis.e_warung.dbadapter.RandomString;
import id.web.kmis.e_warung.dbadapter.SetDatabaseAdapter;
import id.web.kmis.e_warung.dbadapter.UserDatabaseAdapter;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    EditText _nameText;
    EditText _emailText;
    EditText _passwordText;
    EditText _hpText;
    EditText _namaWarung;
    TextView _teamleader;
    int warung;

    Button _signupButton;
    TextView _loginLink;
    boolean zz, yy;
    int success = 0;
    String awal = "pendamping";
    SetDatabaseAdapter dtono;
    UserDatabaseAdapter dbini;
    LoginDataBaseAdapter dbinix;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //cek app_dt_1


        _nameText = (EditText) findViewById(R.id.input_name);
        _namaWarung = (EditText) findViewById(R.id.namawarung);
        _hpText = (EditText) findViewById(R.id.handphone);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        //String decrypted = new String(MCrypt.decrypt(encrypted), "UTF-8");
        _teamleader = (TextView) findViewById(R.id.teamleader);


        dtono = SetDatabaseAdapter.getInstance(this);
        dtono.open();
        zz = dtono.checkkeyexisttmp();
        yy = dtono.checkkeyexisttmp2();
        //dtono.close();
        warung = 0;
        if (zz && !yy) {
            _teamleader.setText("Pendaftaran - Team Leader 2");
            _namaWarung.setFocusable(false);
            _namaWarung.setVisibility(View.GONE);
            warung = 2;

        } else if (!zz && yy) {
            _teamleader.setText("Pendaftaran - Team Leader 1");
            warung = 1;

        } else {
            _teamleader.setText("Pendaftaran - Team Leader 1");
            warung = 1;
        }


        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                onBackPressed();
                finish();
            }
        });


    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }



        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String namawarung = "";
        if (warung == 1) {
            namawarung = _namaWarung.getText().toString();
        } else {
            namawarung = dtono.getkey("app_hst");
            ;
        }


        String nohp = _hpText.getText().toString();
        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();
        String id_anggota = "";
        String android_id;
        android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

        // create new idanggota random string 20
        Random rngg = new Random();
        id_anggota = generateString(rngg, "1234567890", 12);

        //encrypt text
        String name_enc = "", namawr_enc= "", nohp_enc= "", email_enc= "", pass_enc = "";
        Mcrypt mcrypt = new Mcrypt();
        try {
            //name_enc = Mcrypt.bytesToHex(mcrypt.encrypt(name));
           // namawr_enc = Mcrypt.bytesToHex(mcrypt.encrypt(namawarung));
            //nohp_enc = Mcrypt.bytesToHex(mcrypt.encrypt(nohp));
            //email_enc = Mcrypt.bytesToHex(mcrypt.encrypt(email));
            pass_enc = Mcrypt.bytesToHex(mcrypt.encrypt(password));
            id_anggota = Mcrypt.bytesToHex(mcrypt.encrypt(id_anggota));
            android_id = Mcrypt.bytesToHex(mcrypt.encrypt(android_id));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // JSON SEND
        PostAsync post = new PostAsync(getApplicationContext());
        post.execute(name, namawarung, nohp, email, pass_enc, id_anggota, android_id);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success

                       // onSignupFailed();
                        progressDialog.dismiss();
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 8000);

    }


    public void onSignupSuccess() {
//        _signupButton.setEnabled(true);
       // Toast.makeText(getBaseContext(), "Silahkan login, isi biodata keanggotaan dan tunggu aktifasi 1x24 jam", Toast.LENGTH_LONG).show();

        //setResult(RESULT_OK, null);


    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Maaf pendaftaran belum berhasil. Silahkan hubungi KMIS", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean isAlphaNumeric(String s){
        String pattern= "^[a-zA-Z0-9]*$";
        return s.matches(pattern);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String namawarung = _namaWarung.getText().toString();
        String nohp = _hpText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("Sedikitnya 3 karakter");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (warung != 2) {
        if (namawarung.isEmpty() || namawarung.length() < 3 ) {
            _namaWarung.setError("Dimulai dengan kata Warung");
            valid = false;
        } else {
            _namaWarung.setError(null);
        }
        }

        if (nohp.isEmpty() || nohp.length() < 3  || !(nohp.matches("^[0-9]*$"))) {
            _hpText.setError("Nomor hp dimulai 08");
            valid = false;
        } else {
            _hpText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Masukan alamat email yang benar");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || !(isAlphaNumeric(password)) || password.length() < 10) {
            _passwordText.setError("Hanya karakter alfabet (A-Z,a-z,0) dan minimal 10 karakter");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void signupandcheck(String Name, String Namewar, String tele, String usrx, String passx, String ida) {

        //assign temp
        // dtono = SetDatabaseAdapter.getInstance(this);
        //dtono.open();
        //set nama warung



        if (zz && !yy) {
            //params.put("leaderno", "2");
            dtono.updateIdKeytmp2(ida);
            Namewar = dtono.getkey("app_hst");
        } else if (!zz && yy) {
            //params.put("leaderno", "1");
            dtono.updateIdKeytmp1(ida);
            dtono.updateHostKeytl1(Namewar);
        } else if (!zz && !yy) {
            // params.put("leaderno", "1");
            dtono.updateIdKeytmp1(ida);
            dtono.updateHostKeytl1(Namewar);
        } else {
            //params.put("leaderno", "");
        }

        dtono.close();

        dbini = UserDatabaseAdapter.getInstance(this);
        dbini.open();

        // update data awal ke users
        HashMap<String, String> queryValues = new HashMap<String, String>();
        queryValues.put("userName",  usrx);
        queryValues.put("passWord", passx);
        queryValues.put("namawarung", Namewar);
        queryValues.put("idanggota", ida);
        queryValues.put("level", "Team Leader");  // ganti pas release ke 0 - calon anggota
        dbini.insertUsers(queryValues);
        dbini.close();

        LoginDataBaseAdapter dbinix = LoginDataBaseAdapter.getInstance(this);
        dbinix.open();

        Log.d("dipanggil", Name);
        // update dataawal ke anggota
        dbinix.anggotapendamping(ida, Namewar, tele, usrx, Name);
        //Toast.makeText(getApplicationContext(), "Silahkan login dan isi biodata keanggotaan untuk login", Toast.LENGTH_LONG).show();
        dbinix.close();

        //
       // onSignupSuccess();
    }

    class PostAsync extends AsyncTask<String, String, JSONObject> {
        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        private static final String LOGIN_URL = "http://ewarong.kmis.web.id/webserv2/signup2.php";
        //139.59.246.250
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";

        // variable to hold context
        private Context mContext;

        //save the context recievied via constructor in a local variable

        public PostAsync(Context context) {
            this.mContext = context;
        }


        @Override
        protected void onPreExecute() {
        //    pDialog = new ProgressDialog(SignupActivity.this);
        //    pDialog.setMessage("Menghubungi Server...");
         //   pDialog.setIndeterminate(false);
         //   pDialog.setCancelable(true);
         //   pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {
                //(name_enc, namawr_enc,nohp_enc,email_enc,pass_enc,id_anggota);

                HashMap<String, String> params = new HashMap<>();
                params.put("nama", args[0]);
                params.put("namawarung", args[1]);
                params.put("nohp", args[2]);
                params.put("email", args[3]);
                params.put("password", args[4]);
                params.put("idanggota", args[5]);
                params.put("androidid", args[6]);

                if (zz && !yy) {
                    params.put("leaderno", "2");
                } else if (!zz && yy) {
                    params.put("leaderno", "1");
                } else if (!zz && !yy) {
                    params.put("leaderno", "1");
                } else {
                    params.put("leaderno", "");
                }

                // todo params level 0

                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params, mContext);
                Log.d("request", "starting");
                if (json != null) {
                    Log.d("JSON result", json.toString());

                    signupandcheck(args[0], args[1], args[2], args[3], args[4], args[5]);
                    return json;
                }

                // update device key
                // unique warung


            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d("user", args[0]);
            return null;
        }

        protected void onPostExecute(JSONObject json, String... args) {

            // save ke database via logindatabaseadapter atau entah lewat mana
            Toast.makeText(SignupActivity.this, json.toString(),
                    Toast.LENGTH_LONG).show();

            int success = 1;
            String message = "";


           // if (pDialog != null && pDialog.isShowing()) {
           //     pDialog.dismiss();
           // }
            //success = 1;
            if (json != null) {

                success = 0;

                try {

                    message = json.getString(TAG_MESSAGE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (success == 1) {



            }else{
                Log.d("Failure", message);
            }

        }

    }



    public static String generateString(Random rng, String characters, int length)
    {
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }
    }
