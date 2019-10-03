package id.web.kmis.e_warung.network;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;


/**
 * Created by js on 9/1/2016.
 */
public class Util {

    static JSONObject jObj = null;
    static String json = "";

    public Util() {
    }

    public JSONObject GetJSON(String URL) throws JSONException {
/*
        try {
            final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

           //URL url = new URL(URL);
            HttpsURLConnection httpURLConnection  = (HttpsURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type","0");
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setAllowUserInteraction(false);
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setHostnameVerifier(DO_NOT_VERIFY);
            httpURLConnection.connect();
            int status = httpURLConnection.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    json = sb.toString();
                    jObj = new JSONObject(json);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
*/
        return jObj;
    }
}