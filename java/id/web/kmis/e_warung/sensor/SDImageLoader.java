package id.web.kmis.e_warung.sensor;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;
 
/*
 *Original code i found from the below given link
 *---------------------------------------
 *http://www.samcoles.co.uk/tag/listview/
 *
 *How to use
 *----------
 *private final SDImageLoader mImageLoader = new SDImageLoader();
 *
 *mImageLoader.load(photoFilePath, photoView);
 *
 */

public class SDImageLoader {

    public void load(String filePath, ImageView v) {
        if (cancelPotentialSDLoad(filePath, v)) {
            SDLoadImageTask task = new SDLoadImageTask(v);
            SDLoadDrawable sdDrawable = new SDLoadDrawable(task);
            v.setImageDrawable(sdDrawable);
            task.execute(filePath);
        }
    }

    private Bitmap loadImageFromSDCard(String filePath) {

        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 2;
        bfo.outWidth = 400;
        bfo.outHeight = 240;
        Bitmap photo = BitmapFactory.decodeFile(filePath, bfo);
        return photo;
    }

    private static boolean cancelPotentialSDLoad(String filePath, ImageView v) {
        SDLoadImageTask sdLoadTask = getAsyncSDLoadImageTask(v);

        if (sdLoadTask != null) {
            String path = sdLoadTask.getFilePath();
            if ((path == null) || (!path.equals(filePath))) {
                sdLoadTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private static SDLoadImageTask getAsyncSDLoadImageTask(ImageView v) {
        if (v != null) {
            Drawable drawable = v.getDrawable();
            if (drawable instanceof SDLoadDrawable) {
                SDLoadDrawable asyncLoadedDrawable = (SDLoadDrawable) drawable;
                return asyncLoadedDrawable.getAsyncSDLoadTask();
            }
        }
        return null;
    }

    private class SDLoadImageTask extends AsyncTask<String, Void, Bitmap> {

        private String mFilePath;
        private final WeakReference<ImageView> mImageViewReference;

        public String getFilePath() {
            return mFilePath;
        }

        public SDLoadImageTask(ImageView v) {
            mImageViewReference = new WeakReference<ImageView>(v);
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            if (mImageViewReference != null) {
                ImageView v = mImageViewReference.get();
                SDLoadImageTask sdLoadTask = getAsyncSDLoadImageTask(v);
                // Change bitmap only if this process is still associated with it


                if (this == sdLoadTask) {
                    v.setImageBitmap(bmp);
                }
            }
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            mFilePath = params[0];
            return loadImageFromSDCard(mFilePath);
        }
    }

    private class SDLoadDrawable extends ColorDrawable {
        private final WeakReference<SDLoadImageTask> asyncSDLoadTaskReference;

        public SDLoadDrawable(SDLoadImageTask asyncSDLoadTask) {
            super(Color.BLACK);
            asyncSDLoadTaskReference = new WeakReference<SDLoadImageTask>(asyncSDLoadTask);
        }

        public SDLoadImageTask getAsyncSDLoadTask() {
            return asyncSDLoadTaskReference.get();
        }

    }
}
