package com.livewallrcandrapp.videorangebar;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class RangeSeekBarsView {

    private static final String TAG = "RangeSeekBarsView";

    /**
     * time slider parent view layout
     */
    private RelativeLayout mTimeSliderParentView;

    /**
     * left seek bar
     */
    private ImageView mLeftSeekBar;

    /**
     * right seek bar
     */
    private ImageView mRightSeekBar;

    /**
     * class event listener
     */
    private onRangeSeekBarListener mOnRangeSeekBarListener;

    /**
     * parent view width
     */
    private int mParentViewWidth;

    @SuppressLint("ClickableViewAccessibility")
    public void setRangeSeekBar(RelativeLayout mTimeSliderParentView,
                                ImageView mLeftSeekBar,
                                ImageView mRightSeekBar) {
        this.mLeftSeekBar = mLeftSeekBar;
        this.mRightSeekBar = mRightSeekBar;
        this.mTimeSliderParentView = mTimeSliderParentView;



        try {
           this.mLeftSeekBar.setOnTouchListener(onSeekLeftTouchListener());
           this.mRightSeekBar.setOnTouchListener(onSeekRightTouchListener());
        } catch (Exception exe) {
            Log.e(TAG, "Exception when touchListener: " +exe.getMessage());
            if (mOnRangeSeekBarListener != null) mOnRangeSeekBarListener.onError(exe.getMessage());
        }
    }

    /**
     * set class listener
     * @param mOnRangeSeekBarListener
     */
    public void setOnRangeSeekBarListener (onRangeSeekBarListener mOnRangeSeekBarListener) {
        this.mOnRangeSeekBarListener = mOnRangeSeekBarListener;
    }

    /**
     * get layout width
     */
    private void getParentViewWidth() {
        try {
            mParentViewWidth = mTimeSliderParentView.getWidth();
        } catch (Exception exe) {
            Log.e(TAG, "exception getWidthParent: "+exe.getMessage());
            if (mOnRangeSeekBarListener != null) mOnRangeSeekBarListener.onError(exe.getMessage());
        }
    }

    /**
     * event listener in left seek bar
     * @return
     */
    private View.OnTouchListener onSeekLeftTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int x = (int) event.getRawX();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i(TAG, "action Down: left");
                        if (mOnRangeSeekBarListener != null) mOnRangeSeekBarListener.onActionDown(1,x);
                        break;

                    case MotionEvent.ACTION_UP:
                        getParentViewWidth();
                        Log.i(TAG, "action up: left");
                        if (mOnRangeSeekBarListener != null) mOnRangeSeekBarListener.onActionUp(1,x);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        Log.i(TAG, "action move: left");
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v
                                .getLayoutParams();
                        if (x > mLeftSeekBar.getWidth()  && x < mRightSeekBar.getX()) {
                            layoutParams.leftMargin = x -mLeftSeekBar.getWidth();
                        }
                        v.setLayoutParams(layoutParams);
                        if (mOnRangeSeekBarListener != null) mOnRangeSeekBarListener.onActionMove(1,x);
                        break;

                    default:

                }
                return true;
            }
        };
    }

    /**
     * event listener in right seek bar
     * @return
     */
    private View.OnTouchListener onSeekRightTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int x = (int) event.getRawX();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i(TAG, "action Down: left");
                        getParentViewWidth();
                        if (mOnRangeSeekBarListener != null) mOnRangeSeekBarListener.onActionDown(0,x);
                        break;

                    case MotionEvent.ACTION_UP:
                        Log.i(TAG, "action up: left");
                        if (mOnRangeSeekBarListener != null) mOnRangeSeekBarListener.onActionUp(0,x);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        Log.i(TAG, "action move: left"+(x - mRightSeekBar.getWidth()));
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v
                                .getLayoutParams();
                        if (mParentViewWidth > x && x > (mLeftSeekBar.getX() + mLeftSeekBar.getWidth())) {
                            layoutParams.rightMargin = mParentViewWidth - x;
                            v.setLayoutParams(layoutParams);
                        }
                        if (mOnRangeSeekBarListener != null) mOnRangeSeekBarListener.onActionMove(0,x);
                        break;

                    default:

                }
                return true;
            }
        };
    }


}
