package tr.mht.wallpaper.activity;

import android.app.IntentService;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.EditText;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import tr.mht.wallpaper.R;

/**
 * Created by abhishek on 15/10/16.
 */

public class WallPaperRefresh extends Service{

    static int interval=1,i=1;
    private static Handler mHandler = new Handler();


    private static final class StaticRunnable implements Runnable{
        private final WallPaperRefresh mActivity;

        protected StaticRunnable(WallPaperRefresh activity){
            mActivity=activity;
        }
        @Override
        public void run() {
            setWallpaperFromURLString("http://www.keepinspiring.me/wp-content/uploads/2015/11/"+String.valueOf(i)+".jpg",mActivity);
            i++;
            if(i==30)
                i=1;
//            mHandler.postDelayed(this,interval*60*1000);
//            mActivity.clear();
        }
    }


    WallPaperRefresh w;
    public WallPaperRefresh() {
        super();
    }
    private static int mWallpaperWidth, mWallpaperHeight;

    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent


    }


    private static void setWallpaperFromURLString(final String urlString,final WallPaperRefresh wallPaperRefresh) {
        AsyncTask<String, String, Object> task = new AsyncTask<String, String, Object>() {
            @Override
            protected Object doInBackground(String... params) {
            URL url = null;
                Resources resources=wallPaperRefresh.getResources();
                try {
                    url = new URL(urlString);
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                try {
                    if (url != null) {
                       Bitmap bitmap = resizeBitmap(resources, mWallpaperWidth-300, mWallpaperHeight,
                                url.openConnection().getInputStream()
                                );

                        WallpaperManager.getInstance(wallPaperRefresh).setBitmap(
                                bitmap);

                        if (bitmap != null && !bitmap.isRecycled())
                            bitmap.recycle();
                    resources=null;
                    url=null;

                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
    }
}.execute(urlString);
    }
    public static Bitmap resizeBitmap(Resources res, int reqWidth, int reqHeight,
                               InputStream inputStream) {
        Bitmap bitmap = null;
        InputStream in = null;
        InputStream in2 = null;
        InputStream in3 = null;

        try {
            in3 = inputStream;

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayOutputStream out2 = new ByteArrayOutputStream();

            copy(in3,out);
            out2 = out;
            in2 = new ByteArrayInputStream(out.toByteArray());
            in = new ByteArrayInputStream(out2.toByteArray());

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);

            if(options.outHeight == -1 || options.outWidth == 1 || options.outMimeType == null){
                return null;
            }

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            options.inJustDecodeBounds = false;

            bitmap = BitmapFactory.decodeStream(in2, null, options);

            if(bitmap != null){
                bitmap = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, false);
            }
            in.close();
            in2.close();
            in3.close();
            out.close();
            out2.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return bitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    public static int copy(InputStream input, OutputStream output) throws IOException{
        byte[] buffer = new byte[8*1024];
        int count = 0;
        int n = 0;

        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
//            publishProgress((int) (count * 100 / fileLength));
        }

        return count;
    }



     @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        onHandleIntent(intent);
         w=this;
         String dataString;
         if(intent!=null) {
             dataString = intent.getDataString();
         }else {
             dataString = String.valueOf(interval);
         }
//         SharedPreferences sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
//         dataString=sharedpreferences.getString("interval","1");

         interval=Integer.parseInt(dataString);
         mWallpaperWidth = WallpaperManager.getInstance(WallPaperRefresh.this).getDesiredMinimumWidth();
         mWallpaperHeight =WallpaperManager.getInstance(WallPaperRefresh.this).getDesiredMinimumHeight();
         StaticRunnable runnable=new StaticRunnable(this);

//        interval=interval*60*1000;
//        mHandler.postDelayed(runnable,interval);
         ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
         executor.scheduleAtFixedRate(runnable,0,(long) interval, TimeUnit.MINUTES);
        return START_REDELIVER_INTENT;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
