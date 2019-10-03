package id.web.kmis.e_warung.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by js on 8/17/2016.
 */
public class Networkprobe {


    Context mContext;
    public Networkprobe(Context mContext) {
        this.mContext = mContext;
    }

    //network check
    public boolean isConnected(String alamat) {
        try {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnected()) {
                // Network is available but check if we can get access from the
                // network.
                URL url = new URL(
                        alamat);
                HttpURLConnection urlc = (HttpURLConnection) url
                        .openConnection();
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(10000); // Timeout 2 seconds.
                urlc.connect();

                if (urlc.getResponseCode() == 200) // Successful response.
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


}
