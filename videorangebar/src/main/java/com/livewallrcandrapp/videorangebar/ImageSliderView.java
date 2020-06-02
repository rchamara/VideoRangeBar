package com.livewallrcandrapp.videorangebar;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class ImageSliderView {

    private static final String TAG = "ImageSliderView";

    /**
     * image slider parent layout
     */
    private RelativeLayout mImageSliderParent;

    /**
     * MediaMetadataRetriever object instance
     */
    private MediaMetadataRetriever mMediaMetadataRetriever;

    /**
     * video uri
     */
    private Uri mVideoUri;

    /**
     * application context
     */
    private Context mContext;

    /**
     * bitmap image collection
     */
    private ArrayList<Bitmap> mBitmapCollections;

    /**
     * FrameImageRetriever instance
     */
    private FrameImagesRetriever mFrameImageRetrieverBackGroundProcess;

    /**
     * on class object listener
     */
    private  onImageSliderListener mOnImageSliderListener;

    /**
     * bitmap collection
     */
    private ArrayList<Bitmap> mFrames;

    /**
     * bitmap image frame width
     */
    private int frame_width;

    /**
     * bitmap image frame height
     */
    private int frame_height;

    /**
     * number of images in images slider
     */
    private int threshold = 21;


    /**
     *
     * @param mVideoUri
     * @param mImageSliderParent
     * @param mContext
     */
    public void setImageSlider(Uri mVideoUri, RelativeLayout mImageSliderParent, Context mContext) {
        this.mImageSliderParent = mImageSliderParent;
        this.mVideoUri = mVideoUri;
        this.mContext = mContext;

        createBitmapCollectionObject();
        getParentWidgetRatio();

        mFrameImageRetrieverBackGroundProcess = new FrameImagesRetriever();
        mFrameImageRetrieverBackGroundProcess.execute();
    }

    /**
     * array list bitmap collection object
     */
    private void createBitmapCollectionObject() {
        mBitmapCollections = new ArrayList<>();
    }

    /**
     * set class listener
     * @param mOnImageSliderListener
     */
    public void setOnImageSliderListener(onImageSliderListener mOnImageSliderListener) {
        this.mOnImageSliderListener = mOnImageSliderListener;
    }

    /**
     * get frame image height and width
     */
    private void getParentWidgetRatio() {
        frame_width = mImageSliderParent.getWidth()/threshold;
        frame_height = mImageSliderParent.getHeight();
    }

    /**
     * start background process to get frames
     */
    private class FrameImagesRetriever extends AsyncTask<Void, ArrayList, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
           try {
               mMediaMetadataRetriever = new MediaMetadataRetriever();
               mMediaMetadataRetriever.setDataSource(mContext, mVideoUri);
               long duration = Integer.parseInt(mMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000;
               int interval = (int)duration/threshold;
               for (int i = 1; i <= threshold; i ++) {
                   Bitmap mBitmapFrameImage = mMediaMetadataRetriever.getFrameAtTime(i * interval, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                   if (mBitmapFrameImage != null ) {
                       try {
                           mBitmapFrameImage = Bitmap.createScaledBitmap(mBitmapFrameImage, frame_width, frame_height, false);
                       } catch (Exception e) {
                           Log.e(TAG, "create scaled bitmap exception: "+e.getMessage());
                       }
                       mBitmapCollections.add(mBitmapFrameImage);
                   }
               }
               publishProgress(mBitmapCollections);
           } catch (Throwable e) {
               Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
           }
            return null;
        }

        @Override
        protected void onProgressUpdate(ArrayList... values) {
            try {
                mFrames = (ArrayList<Bitmap>) values[0].clone();
                if (mOnImageSliderListener != null) mOnImageSliderListener.onBackgroundProcessFinished(mFrames, frame_width);
            } catch (Exception e) {
                Log.e(TAG,"Error in onProgressUpdate: "+e.getMessage());
                if (mOnImageSliderListener != null) mOnImageSliderListener.onError(e.getMessage());
            }
        }
    }

}
