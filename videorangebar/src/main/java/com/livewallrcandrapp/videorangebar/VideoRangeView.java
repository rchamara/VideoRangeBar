package com.livewallrcandrapp.videorangebar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.util.ArrayList;

public class VideoRangeView extends FrameLayout {

    private static final String TAG = "VideoRangeView";

    private VideoView mVideoView;
    private ImageView mPlayButton;
    private ImageView mLeftSeekBar;
    private ImageView mRightSeekBar;
    private RelativeLayout mSeekBarParent;
    private RelativeLayout mTimeLineParent;
    private RelativeLayout mImageSliderContainer;
    private ImageView mSeekLineSlide;
    private ImageView mDone;
    private ImageView mBack;

    private RelativeLayout mLeftSeekOverlay;
    private RelativeLayout mRightSeekOverlay;

    private RangeSeekBarsView mRangeSeekBarView;
    private TimeLineView mTimeLineView;
    private ImageSliderView mImageSliderView;

    private Context mContext;

    private int leftSeekPosition;
    private int rightSeekPosition;


    /**
     * instance of media player listener
     */
    private onVideoMediaPlayerListener mOnVideoMediaPlayerListener;

    /**
     * constructor for class initializer
     * @param context
     * @param attrs
     */
    public VideoRangeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context);
    }

    /**
     * constructor for xml initializer
     * @param context
     */
    public VideoRangeView(Context context) {
        super(context);
        initControl(context);
    }


    /**
     * init with layout inflate services
     * @param context
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.video_view, this, true);
        Log.i(TAG, "Init control in VideoRangeView");

        this.mContext = context;

        mVideoView = (VideoView) findViewById(R.id.videoLoader);
        mPlayButton = (ImageView) findViewById(R.id.icon_video_play);
        mLeftSeekBar = (ImageView) findViewById(R.id.seek_bar_left);
        mRightSeekBar = (ImageView) findViewById(R.id.seek_bar_right);
        mSeekBarParent = (RelativeLayout) findViewById(R.id.rangeBar);
        mSeekLineSlide = (ImageView) findViewById(R.id.seek_line);
        mTimeLineParent = (RelativeLayout) findViewById(R.id.timeLineBar);
        mImageSliderContainer = (RelativeLayout) findViewById(R.id.image_slider);
        mDone = (ImageView) findViewById(R.id.video_edit_done);
        mBack = (ImageView) findViewById(R.id.video_edit_back);
        onBackButtonTouchListener();
        onDoneButtonTouchListener();

        mRangeSeekBarView = new RangeSeekBarsView();
        mRangeSeekBarView.setOnRangeSeekBarListener(onSeekBarListener());
        mRangeSeekBarView.setRangeSeekBar(mTimeLineParent,mLeftSeekBar, mRightSeekBar);

        mLeftSeekOverlay = (RelativeLayout) findViewById(R.id.leftSeekOverlay);
        mRightSeekOverlay = (RelativeLayout) findViewById(R.id.rightSeekOverlay);

        mTimeLineView = new TimeLineView();
        mTimeLineView.setTimeLineProperties(mVideoView, mSeekLineSlide, mLeftSeekBar, mRightSeekBar, mTimeLineParent);
        mTimeLineView.setOnTimeLineListeners(onTimeLineViewListeners());

        mImageSliderView = new ImageSliderView();

    }

    /**
     * fire when back image touch
     */
    @SuppressLint("ClickableViewAccessibility")
    private void onBackButtonTouchListener() {
        if (mBack != null) {
            mBack.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mOnVideoMediaPlayerListener != null) mOnVideoMediaPlayerListener.onBackButtonTouch(true);
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Animation animFadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
                        mBack.startAnimation(animFadeIn);
                    }
                    return true;
                }
            });
        } else {
            Log.e(TAG, "[onBackButtonTouchListener] back button null");
        }
    }

    /**
     * fire when done image touch
     */
    @SuppressLint("ClickableViewAccessibility")
    private void onDoneButtonTouchListener() {
        if (mDone != null) {
            mDone.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (mOnVideoMediaPlayerListener != null) mOnVideoMediaPlayerListener.onDoneButtonTouch(true);
                        Animation animFadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
                        mDone.startAnimation(animFadeIn);
                    }
                    return true;
                }
            });
        } else {
            Log.e(TAG, "[onDoneButtonTouchListener] done button is null");
        }
    }

    private onTimeLineListeners onTimeLineViewListeners() {
        return new onTimeLineListeners() {
            @Override
            public void onTimeLineSlidePressDown(int x) {

            }

            @Override
            public void onTimeLineSlidePressMove(int x) {

            }

            @Override
            public void onTimeLineSlidePressUp(int x) {
                mPlayButton.setVisibility(INVISIBLE);
            }

            @Override
            public void onTimeLineSlideComeEnd(boolean flag) {
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    mVideoView.seekTo(leftSeekPosition);
                    mPlayButton.setVisibility(VISIBLE);
                }
            }
        };
    }

    private onImageSliderListener onImageSliderViewListener() {
        return new onImageSliderListener() {
            @Override
            public void onBackgroundProcessFinished(ArrayList<Bitmap> mFrames, int frameWidth) {
                Log.i(TAG, "in process end called");
                FrameImageView mFrameImageView = new FrameImageView(mContext);
                mFrameImageView.setFrameBitmaps(mFrames);
                mFrameImageView.setFrameWidth(frameWidth);
                mImageSliderContainer.addView(mFrameImageView);
            }

            @Override
            public void onError(String message) {

            }
        };
    }

    private onRangeSeekBarListener onSeekBarListener() {
        return new onRangeSeekBarListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onActionUp(int flag, int x) {

            }

            @Override
            public void onActionDown(int flag, int x) {
                mVideoView.pause();
                mPlayButton.setVisibility(VISIBLE);

                switch (flag) {
                    case 1:
                        mLeftSeekOverlay.setVisibility(VISIBLE);
                        break;
                    case 0:
                        mRightSeekOverlay.setVisibility(VISIBLE);
                        break;
                }
            }

            @Override
            public void onActionMove(int flag, int x) {
                switch (flag) {
                    case 1:
                        float leftSeekPercentage = getRangeSeekBarPercentage(mLeftSeekBar.getX());
                        leftSeekPosition = getPosition(leftSeekPercentage);
                        mVideoView.seekTo(leftSeekPosition);
                        RelativeLayout.LayoutParams layoutParamsLeft = (RelativeLayout.LayoutParams) mLeftSeekOverlay
                                .getLayoutParams();
                        layoutParamsLeft.width = (int) (mLeftSeekBar.getX() + mLeftSeekBar.getWidth());
                        mLeftSeekOverlay.setLayoutParams(layoutParamsLeft);
                        if (mOnVideoMediaPlayerListener != null) mOnVideoMediaPlayerListener.onRangeSeekBarMove(1,leftSeekPosition);
                        break;
                    case 0:
                        float rightSeekPercentage = getRangeSeekBarPercentage(mRightSeekBar.getX());
                        rightSeekPosition = getPosition(rightSeekPercentage);
                        RelativeLayout.LayoutParams layoutParamsRight = (RelativeLayout.LayoutParams) mRightSeekOverlay
                                .getLayoutParams();
                        layoutParamsRight.width = (int) (mSeekBarParent.getWidth() - mRightSeekBar.getX());
                        mRightSeekOverlay.setLayoutParams(layoutParamsRight);
                        if (mOnVideoMediaPlayerListener != null) mOnVideoMediaPlayerListener.onRangeSeekBarMove(0,rightSeekPosition);
                        break;
                }

            }

        };
    }

    /**
     *
     * @param mOnVideoMediaPlayerListener
     */
    public void setOnVideoMediaPlayerListener(onVideoMediaPlayerListener mOnVideoMediaPlayerListener) {
        this.mOnVideoMediaPlayerListener = mOnVideoMediaPlayerListener;
    }

    /**
     * set url of video pass by application
     * @param uri
     */
    @SuppressLint("ClickableViewAccessibility")
    public void setUrl(final Uri uri) {
        if (uri == null) {
            Log.e(TAG, "Uri is null");
        } else {
            if (mVideoView != null && mVideoView.isPlaying()) {
                mVideoView.stopPlayback();
                mVideoView.resume();
            }
            try {
                mVideoView.setVideoURI(uri);

                /**
                 * run only player is ready
                 */
                mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        Log.i(TAG, "onPrepared");
                        if (mOnVideoMediaPlayerListener != null ) mOnVideoMediaPlayerListener.onPrepared(mp);
                        mVideoView.start();
                        mVideoView.requestFocus();
                        mVideoView.pause();
                        onPlayButtonClickListener();
                        mImageSliderView.setImageSlider(uri, mImageSliderContainer, mContext);
                        mImageSliderView.setOnImageSliderListener(onImageSliderViewListener());
                    }
                });

                /**
                 * get errors
                 */
                mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.e(TAG, "onError: what: "+what+" extra: "+extra);
                        if (mOnVideoMediaPlayerListener != null ) mOnVideoMediaPlayerListener.onError(mp, what, extra);
                        return false;
                    }
                });

                /**
                 * set when video play is end
                 */
                mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.i(TAG, "onComplete");
                        if (mOnVideoMediaPlayerListener != null ) mOnVideoMediaPlayerListener.onCompletion(mp);
                        mp.seekTo(0);
                        mPlayButton.setVisibility(VISIBLE);
                        mSeekLineSlide.setVisibility(INVISIBLE);
                    }
                });


                /**
                 * run on video view touch event
                 */
                mVideoView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        Log.i(TAG, "onTouchEvent");
                        if (mVideoView != null) {
                            if (mVideoView.isPlaying()) {
                                mVideoView.pause();
                                mPlayButton.setVisibility(VISIBLE);
                            }
                        }
                        return true;
                    }
                });

            } catch (NullPointerException exc) {
                Log.e(TAG,"NullPointerException in setUrl: "+exc.getMessage());
                if (mOnVideoMediaPlayerListener != null ) mOnVideoMediaPlayerListener.onException(exc);
            }
        }
    }

    /**
     * run when play button touch
     */
    private void onPlayButtonClickListener() {
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVideoView != null && !mVideoView.isPlaying()) {
                    if (leftSeekPosition > 0) {
                        mVideoView.seekTo(leftSeekPosition);
                    }
                    mVideoView.start();
                    v.setVisibility(INVISIBLE);
                    mSeekLineSlide.setVisibility(VISIBLE);
                    mTimeLineView.ExecuteThread();
                }
            }
        });
    }

    private int getPosition (float percentage)  {
        int duration = mVideoView.getDuration();
        return (int) (percentage * duration / 100);
    }

    private float getRangeSeekBarPercentage (float X_POS) {
        int timeLineParentWidth = mTimeLineParent.getWidth();
        return  X_POS * 100/timeLineParentWidth;
    }


}
