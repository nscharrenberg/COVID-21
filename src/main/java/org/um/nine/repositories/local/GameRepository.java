package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.app.BasicProfilerState;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.OptionPanelState;
import com.simsilica.lemur.anim.AnimationState;
import com.simsilica.lemur.style.BaseStyles;
import org.um.nine.Game;
import org.um.nine.Info;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.ISettingRepository;
import org.um.nine.screens.MainMenuState;

public class GameRepository implements IGameRepository {
    private final Game app;
    private final AppSettings settings = new AppSettings(true);
    private boolean isStarted = false;

    @Inject
    private ISettingRepository settingRepository;

    public GameRepository() {
        this.app = new Game(new StatsAppState(), new DebugKeysAppState(), new BasicProfilerState(false),
                new AnimationState(), // from Lemur
                new OptionPanelState(), // from Lemur
                new MainMenuState(),
                new ScreenshotAppState("", System.currentTimeMillis()));
    }

    @Override
    public void init() {
        settings.setTitle(Info.APP_TITLE);
        settings.setFrameRate(settingRepository.getFrameRate());

        // Set the splash screen image
        settings.setSettingsDialogImage("images/image.jpg");
        settings.setResolution(settingRepository.getWidth(), settingRepository.getHeight());
        settings.setSamples(settingRepository.getSamples());
        settings.setVSync(settingRepository.isVsync());
        settings.setFullscreen(settingRepository.isFullscreen());

        // Allow for touch screen devices
        settings.setEmulateMouse(true);

//        app.setShowSettings(false);
        app.setSettings(settings);

        app.start();
    }

    @Override
    public void create() {
        initLemur();
    }

    @Override
    public void update() {

    }

    private void initLemur() {
        GuiGlobals.initialize(app);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
    }

    @Override
    public Game getApp() {
        return app;
    }

    @Override
    public AppSettings getSettings() {
        return settings;
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }

    @Override
    public void setStarted(boolean started) {
        isStarted = started;
    }
}
