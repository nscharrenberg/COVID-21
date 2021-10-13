package org.um.nine.screens;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.Game;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.utils.Util;
import org.um.nine.utils.managers.InputManager;

import javax.inject.Inject;

public class PauseMenu extends BaseAppState {
    private Container window;

    private boolean heartbeat = false;

    @Inject
    private SettingsState settingsState;

    @Inject
    private InputManager inputManager;

    @Inject
    private IGameRepository gameRepository;

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

        Vector3f size = Util.calculateMenusize(gameRepository.getApp(), window);
        size.addLocal(0, 0, 100);
        window.setLocalTranslation(size);
        window.setLocalScale(Util.getStandardScale(window));
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (heartbeat) {
            this.setEnabled(false);
            initialize(gameRepository.getApp());

            this.heartbeat = false;
        }
    }

    @Override
    protected void cleanup(Application application) {
        application.stop();
    }

    @Override
    protected void onEnable() {
        Vector3f size = Util.calculateMenusize(gameRepository.getApp(), window);
        size.addLocal(0, 0, 100);
        window.setLocalTranslation(size);
        window.setLocalScale(Util.getStandardScale(window));

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

    public boolean isHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }
}
