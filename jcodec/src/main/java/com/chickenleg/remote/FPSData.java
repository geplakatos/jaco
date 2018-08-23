package com.chickenleg.remote;

public class FPSData {

    public static final int MIN_FPS = 5;
    public static final int MAX_FPS = 30;

    private int fps;
    private long lastframe;
    private int delay;
    private int count;

    public FPSData(int fps) {
        changeFPS(fps);
    }

    public void changeFPS(int fps) {
        fps = Math.min(MAX_FPS, Math.max(fps, MIN_FPS));
        if (this.fps != fps) {
            this.fps = fps;
            this.lastframe = -1;
            this.delay = 1000 / fps;
        }
    }
    
    public int getFrameCount() {
        return count;
    }

    public boolean isFrame() {
        long t = System.currentTimeMillis();
        if (t >= lastframe) {
            count++;
            lastframe = t + delay;
            /*long diff = lastframe - t;
            diff = diff - (((int) (diff / delay)) * delay);*/
            return true;
        }
        return false;
    }
}
