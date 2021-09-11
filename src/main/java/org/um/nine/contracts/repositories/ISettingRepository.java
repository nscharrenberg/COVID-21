package org.um.nine.contracts.repositories;

public interface ISettingRepository {
    int getHeight();
    int getWidth();
    void setHeight(int height);
    void setWidth(int width);
    int getSamples();
    void setSamples(int samples);
    boolean isFullscreen();
    void setFullScreen(boolean isFullscreen);
    boolean isVsync();
    void setVSync(boolean isVsync);
    int getFrameRate();
    void setFrameRate(int frameRate);
    boolean isGammaCorrected();
    void setGammaCorrected(boolean isGammaCorrected);
    void save();
}
