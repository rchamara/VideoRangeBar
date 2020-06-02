package com.livewallrcandrapp.videorangebar;

public interface onTimeLineListeners {

    void onTimeLineSlidePressDown(int x);
    void onTimeLineSlidePressMove(int x);
    void onTimeLineSlidePressUp(int x);
    void onTimeLineSlideComeEnd(boolean flag);
}
