package com.livewallrcandrapp.videorangebar;

import android.graphics.Bitmap;

import java.util.ArrayList;

public interface onImageSliderListener {

    void onBackgroundProcessFinished (ArrayList<Bitmap> mFrames, int frameWidth);
    void onError(String message);
}
