package id.web.kmis.e_warung.dbadapter;

import java.io.IOException;
import android.util.Base64;

public class StringXORer {

    public String encode(String s, String key) {
        return base64Encode(xorWithKey(s.getBytes(), key.getBytes()));
    }

    public String decode(String s, String key) {
        return new String(xorWithKey(base64Decode(s), key.getBytes()));
    }

    private byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[i % key.length]);
        }
        return out;
    }

    private byte[] base64Decode(String s) {
        try {
            //BASE64Decoder d = new BASE64Decoder();
            byte[] data = Base64.decode(s, Base64.DEFAULT);
            String text = new String(data, "UTF-8");
            return data;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String base64Encode(byte[] bytes) {
        // Sending side
        //  byte[] data = text.getBytes("UTF-8");
        String base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        //BASE64Encoder enc = new BASE64Encoder();
        // return encode(bytes).replaceAll("\\s", "");
        return base64;
    }
}