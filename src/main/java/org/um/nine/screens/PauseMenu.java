package org.um.nine.screens;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.Game;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.utils.managers.InputManager;

import javax.inject.Inject;

public class PauseMenu extends BaseAppState {
    private Container window;

    @Inject
    private SettingsState settingsState;

    @Inject
    private InputManager inputManager;

    public float getStandardScale() {
        int height = getApplication().getCamera().getHeight();
        return height / 720f;
    }

    @Override
    protected void initialize(Application application) {
        inputManager.clear();

        window = new Container();

        Label title = window.addChild(new Label("Paused"));
        title.setFontSize(32);
        title.setFont(application.getAssetManager().loadFont("fonts/covid2.fnt"));
        title.setInsets(new Insets3f(10, 10, 0, 10));

        playButton();
        quitButton(application);

        int height = application.getCamera().getHeight();
        Vector3f pref = window.getPreferredSize().clone();

        float standardScale = getStandardScale();
        pref.multLocal(1.5f * standardScale);

        float y = height * 0.6f + pref.y * 0.5f;

        window.setLocalTranslation(100 * standardScale, y, 0);
        window.setLocalScale(1.5f * standardScale);
    }

    @Override
    protected void cleanup(Application application) {
        application.stop();
    }

    @Override
    protected void onEnable() {
        Node gui = ((Game)getApplication()).getGuiNode();
        gui.attachChild(window);
        GuiGlobals.getInstance().requestFocus(window);
    }

    @Override
    protected void onDisable() {
        window.removeFromParent();
    }

    private void playButton() {
        Button menuButton = window.addChild(new Button("Continue"));
        menuButton.addClickCommands(button -> {
            inputManager.init();
            setEnabled(false);

        });
        menuButton.setInsets(new Insets3f(10, 10, 0, 10));
    }

    private void quitButton(Application application) {
        Button menuButton = window.addChild(new Button("Quit Game"));
        menuButton.addClickCommands(button -> application.stop());
        menuButton.setInsets(new Insets3f(10, 10, 10, 10));
    }

    protected void goToSettings() {
        getStateManager().attach(settingsState);
        settingsState.setEnabled(true);
        setEnabled(false);
    }
}