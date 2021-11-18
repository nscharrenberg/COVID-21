package org.um.nine.v1.screens.dialogs;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import org.um.nine.v1.Game;
import org.um.nine.v1.contracts.repositories.IGameRepository;
import org.um.nine.v1.utils.Util;

public class GameEndState extends BaseAppState {
    private Container window;

    private String message;
    private boolean heartbeat = false;

    @Inject
    private IGameRepository gameRepository;

    public GameEndState() {
        this.message = "Game End!";
    }

    @Override
    protected void initialize(Application application) {
        window = new Container();

        window.setBackground(new QuadBackgroundComponent(ColorRGBA.White));

        Label descriptionText = window.addChild(new Label(this.message), 1, 0);
        descriptionText.setInsets(new Insets3f(10, 10, 0, 10));
        descriptionText.setColor(ColorRGBA.Red);

        Button menuButton = window.addChild(new Button("Back to Main Menu"));
        menuButton.addClickCommands(button -> gameRepository.cleanup());
        menuButton.setInsets(new Insets3f(10, 10, 10, 10));

        Button quitButton = window.addChild(new Button("Quit Game"));
        quitButton.addClickCommands(button -> application.stop());
        quitButton.setInsets(new Insets3f(10, 10, 10, 10));

        window.addChild(descriptionText);
        window.addChild(menuButton);
        window.addChild(quitButton);

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }
}

