package com.livewallrcandrapp.videorangebar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class FrameImageView extends View {

    private static final String TAG = "FrameImageView";

    /**
     * collection of bitmap images in video
     */
    private ArrayList<Bitmap> mFrameBitmaps;

    /**
     * Image width
     */
    private int frame_width;

    /**
     * Set frame image
     * @param context
     */
    public FrameImageView(Context context) {
        super(context);
    }

    /**
     * set frame image array list
     * @param mFrameBitmaps
     */
    public void setFrameBitmaps (ArrayList<Bitmap> mFrameBitmaps) {
        this.mFrameBitmaps = mFrameBitmaps;
    }

    /**
     * set frame width
     * @param frame_width
     */
    public void setFrameWidth (int  frame_width) {
        this.frame_width = frame_width;
    }

    /**
     * onDraw methods
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mFrameBitmaps != null && mFrameBitmaps.size() > 0) {
           try {
               for (int i = 0; i < mFrameBitmaps.size(); i++) {
                canvas.drawBitmap(mFrameBitmaps.get(i), i* frame_width, 0f,null);
               }
           } catch (Exception e) {
               Log.e(TAG, "Error in onDraw "+e.getMessage());
           }
        } else {
            Log.e(TAG, "frame list null or empty");
        }
    }
}
