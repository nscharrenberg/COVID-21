package org.um.nine.screens.dialogs;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import org.um.nine.Game;
import org.um.nine.contracts.repositories.IGameRepository;

public class GameEndState extends BaseAppState {
    private Container window;

    private String message;

    @Inject
    private IGameRepository gameRepository;

    public GameEndState() {
        this.message = "Game End!";
    }

    public float getStandardScale() {
        int height = getApplication().getCamera().getHeight();
        return height / 720f;
    }

    @Override
    protected void initialize(Application application) {
        window = new Container();

        window.setBackground(new QuadBackgroundComponent(ColorRGBA.White));

        Label descriptionText = window.addChild(new Label(this.message), 1, 0);
        descriptionText.setInsets(new Insets3f(10, 10, 0, 10));
        descriptionText.setColor(ColorRGBA.Red);

        Button menuButton = window.addChild(new Button("Back to Main Menu"));
        menuButton.addClickCommands(button -> {
            gameRepository.cleanup();
        });
        menuButton.setInsets(new Insets3f(10, 10, 10, 10));

        Button quitButton = window.addChild(new Button("Quit Game"));
        quitButton.addClickCommands(button -> {
            application.stop();
        });
        quitButton.setInsets(new Insets3f(10, 10, 10, 10));

        window.addChild(descriptionText);
        window.addChild(menuButton);
        window.addChild(quitButton);

        int height = application.getCamera().getHeight();
        Vector3f pref = window.getPreferredSize().clone();

        float standardScale = getStandardScale();
        pref.multLocal(1.5f * standardScale);

        float y = height * 0.6f + pref.y * 0.5f;

        window.setLocalTranslation(100 * standardScale, y, 100);
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
