package com.livewallrcandrapp.videorangebar;

public interface onRangeSeekBarListener {

    void onError(String message);
    void onActionUp(int flag, int x);
    void onActionDown(int flag, int x);
    void onActionMove(int flag, int x);
}
