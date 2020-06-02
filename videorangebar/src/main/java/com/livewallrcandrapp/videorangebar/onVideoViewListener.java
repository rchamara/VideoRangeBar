package com.livewallrcandrapp.videorangebar;

public interface onVideoViewListener {

    void onError(String message);
    void onSeeking(int flag, int time);
}
