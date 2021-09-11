package org.um.nine.repositories.local;

import com.google.inject.Inject;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.ISettingRepository;

public class SettingRepository implements ISettingRepository {
    private int width = 1920;
    private int height = 1080;
    private int samples = 16;
    private boolean isVsync = false;
    private boolean isFullscreen = true;
    private int frameRate = 60;
    private boolean isGammaCorrected = true;

    @Inject
    private IGameRepository gameRepository;

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getSamples() {
        return this.samples;
    }

    @Override
    public void setSamples(int samples) {
        this.samples = samples;
    }

    @Override
    public boolean isFullscreen() {
        return this.isFullscreen;
    }

    @Override
    public void setFullScreen(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
    }

    @Override
    public boolean isVsync() {
        return this.isVsync;
    }

    @Override
    public void setVSync(boolean isVsync) {
        this.isVsync = isVsync;
    }

    @Override
    public int getFrameRate() {
        return this.frameRate;
    }

    @Override
    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    @Override
    public boolean isGammaCorrected() {
        return this.isGammaCorrected;
    }

    @Override
    public void setGammaCorrected(boolean isGammaCorrected) {
        this.isGammaCorrected = isGammaCorrected;
    }

    @Override
    public void save() {
        gameRepository.getSettings().setFullscreen(this.isFullscreen);
        gameRepository.getSettings().setResolution(this.width, this.height);
        gameRepository.getSettings().setVSync(this.isVsync);
        gameRepository.getSettings().setSamples(this.samples);
        gameRepository.getSettings().setGammaCorrection(this.isGammaCorrected);
        gameRepository.getSettings().setFrameRate(this.frameRate);

        gameRepository.getApp().restart();
    }
}
