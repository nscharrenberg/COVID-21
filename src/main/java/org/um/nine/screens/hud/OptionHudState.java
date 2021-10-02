package org.um.nine.screens.hud;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.Game;
import org.um.nine.contracts.repositories.IPlayerRepository;

public class OptionHudState extends BaseAppState  {
    private Container window;

    @Inject
    private PlayerInfoState playerInfoState;

    @Inject
    private IPlayerRepository playerRepository;

    private float getStandardScale() {
        int height = getApplication().getCamera().getHeight();
        return height / 720f;
    }

    @Override
    protected void initialize(Application application) {
        window = new Container();

        String currentPlayerName = playerRepository.getCurrentPlayer() != null ? playerRepository.getCurrentPlayer().getName() : "Unknown";
        Label currentPlayerMoveLbl = new Label("Current Player: " + currentPlayerName, "currentPlayerName");
        currentPlayerMoveLbl.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(currentPlayerMoveLbl, 1, 0);

        Button actionButton = window.addChild(new Button("Actions"));
        actionButton.addClickCommands(button -> {
            // TODO: Open Actions Menu
            System.out.println("Action Button Clicked");
        });
        actionButton.setInsets(new Insets3f(2, 2, 0, 2));

        Button cardsButton = window.addChild(new Button("Cards & Roles"));
        cardsButton.addClickCommands(button -> {
            getStateManager().attach(playerInfoState);
            playerInfoState.setEnabled(true);
        });
        cardsButton.setInsets(new Insets3f(2, 2, 0, 2));

        Button rulesButton = window.addChild(new Button("Show Rules"));
        rulesButton.addClickCommands(button -> {
            // TODO: Open Show Rules Menu
            System.out.println("Show Rules Button Clicked");
        });
        rulesButton.setInsets(new Insets3f(2, 2, 0, 2));

        window.setLocalTranslation(25, 200, 5);
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
