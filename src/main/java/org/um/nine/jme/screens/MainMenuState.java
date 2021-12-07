package org.um.nine.jme.screens;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.headless.game.Settings;
import org.um.nine.jme.JmeGame;
import org.um.nine.jme.JmeMain;
import org.um.nine.jme.utils.MenuUtils;

public class MainMenuState extends BaseAppState {
    private Container window;

    @Override
    protected void initialize(Application application) {
        window = new Container();

        Label title = window.addChild(new Label(Settings.APP_TITLE));
        title.setFontSize(32);
        title.setFont(application.getAssetManager().loadFont("fonts/covid2.fnt"));
        title.setInsets(new Insets3f(10, 10, 0, 10));

        playButton();
        quitButton(application);

        Vector3f size = MenuUtils.calculateMenusize(JmeMain.getGameRepository().getApp(), window);
        size.addLocal(0, 0, 100);
        window.setLocalTranslation(size);
        window.setLocalScale(MenuUtils.getStandardScale(window));
    }

    private void playButton() {
        Button menuButton = window.addChild(new Button("Play"));
        menuButton.addClickCommands(button -> {
            getStateManager().attach(new ConfigurationState());
            setEnabled(false);
        });
        menuButton.setInsets(new Insets3f(10, 10, 0, 10));
    }

    private void quitButton(Application application) {
        Button menuButton = window.addChild(new Button("Quit Game"));
        menuButton.addClickCommands(button -> application.stop());
        menuButton.setInsets(new Insets3f(10, 10, 10, 10));
    }

    @Override
    protected void cleanup(Application application) {
        application.stop();
    }

    @Override
    protected void onEnable() {
        Node gui = ((JmeGame)getApplication()).getGuiNode();
        gui.attachChild(window);
        GuiGlobals.getInstance().requestFocus(window);
    }

    @Override
    protected void onDisable() {
        window.removeFromParent();
    }
}
