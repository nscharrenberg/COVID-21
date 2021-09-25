package org.um.nine.screens.hud;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.Game;

public class OptionHudState extends BaseAppState  {
    private Container window;

    private float getStandardScale() {
        int height = getApplication().getCamera().getHeight();
        return height / 720f;
    }

    @Override
    protected void initialize(Application application) {
        window = new Container();

        Button actionButton = window.addChild(new Button("Actions"));
        actionButton.addClickCommands(button -> {
            // TODO: Open Actions Menu
            System.out.println("Action Button Clicked");
        });
        actionButton.setInsets(new Insets3f(2, 2, 0, 2));

        Button cardsButton = window.addChild(new Button("Show Cards"));
        cardsButton.addClickCommands(button -> {
            // TODO: Open Show Cards Menu
            System.out.println("Show Cards Button Clicked");
        });
        cardsButton.setInsets(new Insets3f(2, 2, 0, 2));

        Button rulesButton = window.addChild(new Button("Show Rules"));
        rulesButton.addClickCommands(button -> {
            // TODO: Open Show Rules Menu
            System.out.println("Show Rules Button Clicked");
        });
        rulesButton.setInsets(new Insets3f(2, 2, 0, 2));

        window.setLocalTranslation(getApplication().getCamera().getWidth() - 150, 150, 5);
        window.setLocalScale(1.5f);
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
}
