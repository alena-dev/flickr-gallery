package com.g.e.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ThumbnailDownloader<T> extends HandlerThread{
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private static final int MESSAGE_PREDOWNLOAD = 1;

    private boolean mHasQuit = false;
    private Handler mRequestHandler;
    private ConcurrentMap <T, String> mRequestMap = new ConcurrentHashMap<>();
    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;
    private LruCache <String, Bitmap> mBitmapCache = new LruCache<>(50);

    public  interface ThumbnailDownloadListener<T>{
        void  onThumbnailDownloaded(T target, Bitmap thumbnail);
    }

    public  void setThumbnailDownloaderListener(ThumbnailDownloadListener<T> listener){
        mThumbnailDownloadListener = listener;
    }


    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MESSAGE_DOWNLOAD){
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL: "
                            +mRequestMap.get(target));
                    handleRequest(target);
                }
                if(msg.what == MESSAGE_PREDOWNLOAD){
                    String url = msg.obj.toString();
                    Log.i(TAG, "Got a request of predownload for URL: " + url);
                    try{
                        if(url == null) return;

                        if (mBitmapCache.get(url)!=null) return;

                        final Bitmap bitmap = downloadBitmap(url);

                        putToCache(url, bitmap);

                    } catch (IOException ioe){
                        Log.e(TAG, "Error downloading image", ioe);
                    }
                }
            }
        };
    }

    private void handleRequest(final T target) {
        try {
            final String url = mRequestMap.get(target);

            if(url == null) return;

            Bitmap bitmap = mBitmapCache.get(url);
            if (bitmap ==null) {
                bitmap = downloadBitmap(url);
                putToCache(url, bitmap);
            }
            final Bitmap resultBitmap = bitmap;
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mRequestMap.get(target) != url || mHasQuit)
                        return;

                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target, resultBitmap);
                }
            });

        } catch (IOException ioe){
            Log.e(TAG, "Error downloading image", ioe);
        }
    }

    private Bitmap downloadBitmap(String url) throws IOException {
            if (url == null)return null;

        byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes,
                    0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");

            return bitmap;
    }

    private void putToCache(String url, Bitmap bitmap) {
        mBitmapCache.put(url, bitmap);
        Log.i(TAG, "Bitmap has been cached");
    }

    @Override
    public boolean quit() {
        mHasQuit=true;
        return super.quit();
    }

    public void queueThumbnail(T target, String url){
        Log.i(TAG, "Got a URL: " + url);

        if(url==null){
            mRequestMap.remove(target);
        } else {
            Bitmap bitmap = mBitmapCache.get(url);

            if(bitmap==null) {
                mRequestMap.put(target, url);
                mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                        .sendToTarget();
            } else {
                mRequestMap.remove(target);
                mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
            }
        }
    }

    public void clearQueue (){
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear();
    }

    public void preDownload (String url){
        Log.i(TAG, "Got a URL for predownloading: " + url);

        if (url == null) return;

//        1. Check image in cach
        Bitmap bitmap = mBitmapCache.get(url);

        if(bitmap == null) {
            mRequestHandler.obtainMessage(MESSAGE_PREDOWNLOAD, url)
                    .sendToTarget();
//        2. Download image
//        3. Put image in cach
        }
    }
}
