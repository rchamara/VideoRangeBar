package com.livewallrcandrapp.videorangebar;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;


public class TimeLineView {

    private static final String TAG = "TimeLineView";

    /**
     * video view instance
     */
    private VideoView mVideoView;

    /**
     * seek bar slide instance
     */
    private ImageView mSeekSlide;

    /**
     *
     */
    private ImageView mLeftSlide;

    private ImageView mRightSlide;

    /**
     * parent layout of seek bar slide
     */
    private RelativeLayout mTimeLineSlideParent;

    /**
     * on time line listeners instances
     */
    private onTimeLineListeners mOnTimeLineListeners;

    private RunSeekBar mRunSeekBarBackGroundWork;

    /**
     * set event listeners instance
     * @param mOnTimeLineListeners
     */
    public void setOnTimeLineListeners(onTimeLineListeners mOnTimeLineListeners) {
        this.mOnTimeLineListeners = mOnTimeLineListeners;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setTimeLineProperties(VideoView mVideoView,
                                      ImageView mSeekSlide,
                                      ImageView mLeftSlide,
                                      ImageView mRightSlide,
                                      RelativeLayout mTimeLineSlideParent) {
        this.mSeekSlide = mSeekSlide;
        this.mVideoView = mVideoView;
        this.mLeftSlide = mLeftSlide;
        this.mRightSlide = mRightSlide;
        this.mTimeLineSlideParent = mTimeLineSlideParent;
        this.mSeekSlide.setOnTouchListener(onSeekSlideTouchListener());

    }


    public void ExecuteThread() {
        mRunSeekBarBackGroundWork = new RunSeekBar();
        mRunSeekBarBackGroundWork.execute();
    }


    private View.OnTouchListener  onSeekSlideTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch(event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i(TAG, "ACTION DOWN:");
                        if (mOnTimeLineListeners != null) mOnTimeLineListeners.onTimeLineSlidePressDown(x);
                        mVideoView.pause();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i(TAG, "ACTION UP");
                        if (mOnTimeLineListeners != null) mOnTimeLineListeners.onTimeLineSlidePressUp(x);
                        mVideoView.start();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.i(TAG, "ACTION MOVE");
                        if (mOnTimeLineListeners != null) mOnTimeLineListeners.onTimeLineSlidePressMove(x);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                        layoutParams.leftMargin = measurePositions(layoutParams.leftMargin,x);
                        v.setLayoutParams(layoutParams);
                        float getPercentage = getPositionPercentage(x);
                        final int seekPosition = getSeekPosition(getPercentage);
                        mVideoView.seekTo(seekPosition);
                        break;
                }
                /**
                 * redraw the view
                 */
                mTimeLineSlideParent.invalidate();
                return true;
            }
        };
    }

    /**
     * measured the pixel position
     * @param current_left_margin
     * @param new_x_position
     * @return
     */
    private int measurePositions(int current_left_margin, int new_x_position) {
        int measure_value;
        int delta_x = new_x_position - current_left_margin;
        measure_value = (delta_x + current_left_margin) - mSeekSlide.getWidth()/2;
        if (new_x_position > (mTimeLineSlideParent.getWidth()-mSeekSlide.getWidth()/2)) {
            measure_value = current_left_margin;
        }
        return measure_value;
    }

    private float getPositionPercentage(int x) {
        return ((float) (x) *100) /mTimeLineSlideParent.getWidth();
    }

    private int getSeekPosition(float percentage) {
        return (int)( percentage * mVideoView.getDuration()/100);
    }

    public class RunSeekBar extends AsyncTask<Void, Integer, Void> {

        int duration = mVideoView.getDuration();
        int parent_width = (int) (mTimeLineSlideParent.getWidth());
        int current = 0;
        float percentage = 0f;

        @Override
        protected Void doInBackground(Void... params) {

            do {
                current = mVideoView.getCurrentPosition();
                percentage = getPositionsPercentage(duration, current);
                int x_CR = getSeekEstimatePosition(parent_width, percentage);
                try {
                    publishProgress(x_CR);
                    if(percentage >= 100){
                        break;
                    }
                } catch (Exception e) {
                }
            } while (percentage <= 100);

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mSeekSlide.getLayoutParams();
            if (values[0] < mRightSlide.getX() - mRightSlide.getWidth()) {
                layoutParams.leftMargin = values[0];
                mSeekSlide.setLayoutParams(layoutParams);
            } else {
                if (mOnTimeLineListeners != null) mOnTimeLineListeners.onTimeLineSlideComeEnd(true);
            }


        }
    }

    /**
     *
     * @param videoDuration
     * @param currentPositions
     * @return
     */
    private float getPositionsPercentage(int videoDuration, int currentPositions) {
        return ((float) currentPositions/videoDuration) * 100;
    }

    /**
     *
     * @param parentWidth
     * @param percentage
     * @return
     */
    private int getSeekEstimatePosition(int parentWidth, float percentage) {
        return (int) ((parentWidth) * percentage)/100;
    }
}
