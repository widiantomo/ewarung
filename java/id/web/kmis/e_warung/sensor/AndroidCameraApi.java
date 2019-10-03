package id.web.kmis.e_warung.sensor;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import id.web.kmis.e_warung.R;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AndroidCameraApi extends AppCompatActivity {
    private static final String TAG = "AndroidCameraApi";
    private Button takePictureButton;
    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    String looc;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_camera_api);
        textureView = (TextureView) findViewById(R.id.texture);
        assert textureView != null;

        Bundle extras = getIntent().getExtras();
        looc = extras.getString("coor");
        mContext = getApplicationContext();
        textureView.setSurfaceTextureListener(textureListener);
        takePictureButton = (Button) findViewById(R.id.btn_takepicture);
        assert takePictureButton != null;
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
//            cameraDevice.close();
            cameraDevice = null;
        }
    };
    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);

            Intent intent = new Intent();
            //intent.putExtra("data",data);
            intent.putExtra("imageUri", file.toString());
            setResult(RESULT_OK, intent);
            //Log.d("Lokasi FIlenya", pictureFile.toString());
            closeCamera();
            finish(); //finishing activity

            //Toast.makeText(AndroidCameraApi.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
            //createCameraPreview();
        }
    };

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    protected void takePicture() {
        if (null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 1280;
            int height = 768;
            //if (jpegSizes != null && 0 < jpegSizes.length) {
            //    width = jpegSizes[0].getWidth();
            //    height = jpegSizes[0].getHeight();
            //}

            ImageReader reader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            //Set the JPEG quality here like so
            //captureBuilder.set(CaptureRequest.JPEG_QUALITY, (byte)40);

            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            //final File file = new File(Environment.getExternalStorageDirectory()+"/pic.jpg");
            file = getOutputMediaFile(looc, mContext);

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    FileOutputStream fos = null;
                    Bitmap bitmap = null;
                    //Image img = null;
                    try {
                        image = reader.acquireLatestImage();
                        if (image != null) {
                            //ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                            //byte[] bytes = new byte[buffer.capacity()];
                            //buffer.get(bytes);

                            Image.Plane[] planes = image.getPlanes();
                            if (planes[0].getBuffer() == null) {
                                return;
                            }
                            int width = image.getWidth();
                            int height = image.getHeight();
                            int pixelStride = planes[0].getPixelStride();
                            int rowStride = planes[0].getRowStride();
                            int rowPadding = rowStride - pixelStride * width;
                            byte[] newData = new byte[width * height * 4];
                            DisplayMetrics metrics = new DisplayMetrics();
                            int offset = 0;
                            bitmap = Bitmap.createBitmap(metrics, width, height, Bitmap.Config.ARGB_8888);
                            ByteBuffer buffer = planes[0].getBuffer();
                            for (int i = 0; i < height; ++i) {
                                for (int j = 0; j < width; ++j) {
                                    int pixel = 0;
                                    pixel |= (buffer.get(offset) & 0xff) << 16;     // R
                                    pixel |= (buffer.get(offset + 1) & 0xff) << 8;  // G
                                    pixel |= (buffer.get(offset + 2) & 0xff);       // B
                                    pixel |= (buffer.get(offset + 3) & 0xff) << 24; // A
                                    bitmap.setPixel(j, i, pixel);
                                    offset += pixelStride;
                                }
                                offset += rowPadding;
                            }

                            String timeStamp = new SimpleDateFormat("dd-MM-yyyy-hh.mm.ss")
                                    .format(new Date());
                            timeStamp = timeStamp + "_" + looc;
                            //Log.d("nama file", timeStamp);

                            fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos);

                            //save(bytes);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                        if (null != fos) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (null != bitmap) {
                            bitmap.recycle();
                        }
                    }
                }

                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }


            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);

                    Intent intent = new Intent();
                    //intent.putExtra("data",data);
                    intent.putExtra("imageUri", file.toString());
                    setResult(RESULT_OK, intent);
                    //Log.d("Lokasi FIlenya", pictureFile.toString());
                    closeCamera();
                    finish(); //finishing activity


                    // Toast.makeText(AndroidCameraApi.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
                    //createCameraPreview();
                }
            };
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());

            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    configureTransform(textureView.getWidth(), textureView.getHeight());
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    // Toast.makeText(AndroidCameraApi.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];

            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AndroidCameraApi.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    protected void updatePreview() {
        if (null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }

    private void configureTransform(int viewWidth, int viewHeight) {
        //Activity activity = getActivity();
        if (null == textureView || null == imageDimension) {
            return;
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, imageDimension.getHeight(), imageDimension.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / imageDimension.getHeight(),
                    (float) viewWidth / imageDimension.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(AndroidCameraApi.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        //closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

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
}