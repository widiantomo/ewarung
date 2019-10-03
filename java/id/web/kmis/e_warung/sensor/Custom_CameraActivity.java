package id.web.kmis.e_warung.sensor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import id.web.kmis.e_warung.R;
import id.web.kmis.e_warung.R.id;
import id.web.kmis.e_warung.R.layout;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class Custom_CameraActivity extends Activity {
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private Context mContext = this;
    private String looc;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_cameraactivity);
        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);

        Bundle extras = getIntent().getExtras();
        looc = extras.getString("coor");

        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
                // havent try to use doinbackgroud test first

            }
        });
    }

    /**
     * Helper method to access the camera returns null if it cannot get the
     * camera or does not exist
     *
     * @return
     */
    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        Camera.Parameters params = camera.getParameters();

        List<Size> sizes = params.getSupportedPictureSizes();
        int w = 0, h = 0;
        for (Size size : sizes) {
            if (size.width > w || size.height > h) {
                w = size.width;
                h = size.height;
            }

        }
        params.setPictureSize(w, h);


        if (params.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        params.setPictureFormat(ImageFormat.JPEG);
        params.setJpegQuality(100);
        camera.setParameters(params);


        return camera;
    }

    PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            // add text overlay
            Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);
            picture = decodeBitmapSize(picture, 1280);
            //picture = bitmap_resize(picture, 960, 540);
            String timeStamp = new SimpleDateFormat("dd-MM-yyyy-hh.mm.ss")
                    .format(new Date());
            timeStamp = timeStamp + "_" + looc;
            Log.d("nama file", timeStamp);
            // picture = mark(picture,timeStamp);
            // Compress image
            data = codec(picture, Bitmap.CompressFormat.JPEG, 60);

            File pictureFile = getOutputMediaFile(looc, mContext);

            if (pictureFile == null) {

                return;
            }
            try {
                //	Log.d("io", pictureFile.toString());
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("io", "ga ada filenya coy");
            } catch (IOException e) {
                Log.d("io", "error io");
            }

            Intent intent = new Intent();
            //intent.putExtra("data",data);  
            intent.putExtra("imageUri", pictureFile.toString());
            setResult(RESULT_OK, intent);
            //Log.d("Lokasi FIlenya", pictureFile.toString());
            finish(); //finishing activity 

        }
    };

    private static File getOutputMediaFile(String namex, Context context) {
        //String pathToExternalStorage = Environment.getExternalStorageDirectory().toString();
        //File mediaStorageDir = new File(
        //        Environment
        //                .getExternalStoragePublicDirectory(Environment.),
        //        "e-KMIS"); 

        String pathToExternalStorage = context.getFilesDir().toString();
        File mediaStorageDir = new File(pathToExternalStorage + "/" + "eKMIS");

        //if (!appDirectory.isDirectory() || !appDirectory.exists()) //Checks if the directory exists
        //    appDirectory.mkdir();

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                //Log.d("surevy", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        //mediaFile = new File(mediaStorageDir.getPath() + File.separator
        //        + "IMG_" + timeStamp + ".jpg");

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + namex + ".jpg");


        Log.d("Camera activty", mediaStorageDir.getPath());

        return mediaFile;
    }


    public static Bitmap rotate(Bitmap in, int angle) {
        Matrix mat = new Matrix();
        mat.postRotate(angle);
        return Bitmap.createBitmap(in, 0, 0, in.getWidth(), in.getHeight(), mat, true);
    }

    private static byte[] codec(Bitmap src, Bitmap.CompressFormat format,
                                int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);

        byte[] array = os.toByteArray();

        return array;
    }

    public Bitmap mark(Bitmap src, String watermark) {
        int w = src.getWidth();
        int h = src.getHeight();

        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#4F000000")); // transparent
        // black,change opacity
        // by changing hex value
        // "AA" between "00" and
        // "FF"
/**
 100% � FF
 95% � F2
 90% � E6
 85% � D9
 80% � CC
 75% � BF
 70% � B3
 65% � A6
 60% � 99
 55% � 8C
 50% � 80
 45% � 73
 40% � 66
 35% � 59
 30% � 4D
 25% � 40
 20% � 33
 15% � 26
 10% � 1A
 5% � 0D
 0% � 00
 **/

        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(10);
        paint.setAntiAlias(true);
        paint.setUnderlineText(false);

        // should draw background first,order is important
        int left = 0;
        int right = w;
        int bottom = h;
        int top = (int) (bottom - (h * .25));
        canvas.drawRect(left, top, right, bottom, bgPaint);

        canvas.drawText(watermark, 10, h - 15, paint);

        return result;
    }

    public static Bitmap decodeBitmapSize(Bitmap bm, int IMAGE_BIGGER_SIDE_SIZE) {

        //we will return this Bitmap  
        Bitmap b = null;


        //convert Bitmap to byte[]
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        //We need to know image width and height, 
        //for it we create BitmapFactory.Options object  and do BitmapFactory.decodeByteArray
        //inJustDecodeBounds = true - means that we do not need load Bitmap to memory
        //but we need just know width and height of it
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, opt);
        int CurrentWidth = opt.outWidth;
        int CurrentHeight = opt.outHeight;


        //there is function that can quick scale images
        //but we need to give in scale parameter, and scale - it is power of 2
        //for example 0,1,2,4,8,16...
        //what scale we need? for example our image 1000x1000 and we want it will be 100x100
        //we need scale image as match as possible but should leave it more then required size
        //in our case scale=8, we receive image 1000/8 = 125 so  125x125, 
        //scale = 16 is incorrect in our case, because we receive 1000/16 = 63 so 63x63 image 
        //and it is less then 100X100
        //this block of code calculate scale(we can do it another way, but this way it more clear to read)
        int scale = 1;
        int PowerOf2 = 0;
        int ResW = CurrentWidth;
        int ResH = CurrentHeight;
        if (ResW > IMAGE_BIGGER_SIDE_SIZE || ResH > IMAGE_BIGGER_SIDE_SIZE) {
            while (1 == 1) {
                PowerOf2++;
                scale = (int) Math.pow(2, PowerOf2);
                ResW = (int) ((double) opt.outWidth / (double) scale);
                ResH = (int) ((double) opt.outHeight / (double) scale);
                if (Math.max(ResW, ResH) < IMAGE_BIGGER_SIDE_SIZE) {
                    PowerOf2--;
                    scale = (int) Math.pow(2, PowerOf2);
                    ResW = (int) ((double) opt.outWidth / (double) scale);
                    ResH = (int) ((double) opt.outHeight / (double) scale);
                    break;
                }

            }
        }


        //Decode our image using scale that we calculated
        BitmapFactory.Options opt2 = new BitmapFactory.Options();
        opt2.inSampleSize = scale;
        //opt2.inScaled = false;
        b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, opt2);


        //calculating new width and height
        int w = b.getWidth();
        int h = b.getHeight();
        if (w >= h) {
            w = IMAGE_BIGGER_SIDE_SIZE;
            h = (int) ((double) b.getHeight() * ((double) w / b.getWidth()));
        } else {
            h = IMAGE_BIGGER_SIDE_SIZE;
            w = (int) ((double) b.getWidth() * ((double) h / b.getHeight()));
        }


        //if we lucky and image already has correct sizes after quick scaling - return result
        if (opt2.outHeight == h && opt2.outWidth == w) {
            return b;
        }


        //we scaled our image as match as possible using quick method
        //and now we need to scale image to exactly size
        b = Bitmap.createScaledBitmap(b, w, h, true);


        return b;
    }

    public Bitmap bitmap_resize(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Config.ARGB_8888);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }

}