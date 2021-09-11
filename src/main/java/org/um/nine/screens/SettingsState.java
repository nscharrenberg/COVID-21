package org.um.nine.screens;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.Game;

public class SettingsState extends BaseAppState {
    private Container window;

    public float getStandardScale() {
        int height = getApplication().getCamera().getHeight();
        return height / 720f;
    }

    protected void showError(String title, String error) {
        getState(OptionPanelState.class).show(title, error);
    }

    @Override
    protected void initialize(Application application) {
        window = new Container();

        Label title = window.addChild(new Label("Settings"));
        title.setFontSize(32);
        title.setFont(application.getAssetManager().loadFont("fonts/covid2.fnt"));
        title.setInsets(new Insets3f(10, 10, 0, 10));

        mainMenuButton();

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

    private void mainMenuButton() {
        Button settings = window.addChild(new Button("Back to Main Menu"));
        settings.addClickCommands(button -> goToMainMenu());
        settings.setInsets(new Insets3f(10, 10, 10, 10));
    }

    protected void goToMainMenu() {
        getStateManager().attach(new MainMenuState());

        setEnabled(false);
    }
}
