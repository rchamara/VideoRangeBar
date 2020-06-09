package com.livewallrcandrapp.videorangebar;

import android.media.MediaPlayer;

public interface onVideoMediaPlayerListener {

     void onPrepared(MediaPlayer mp);
     boolean onError(MediaPlayer mp, int what, int extra);
     void onCompletion(MediaPlayer mp);
     void onException(Exception exc);
     void onRangeSeekBarMove(int sb_flag, int time);
     void onBackButtonTouch(boolean value);
     void onDoneButtonTouch(boolean value);
}
