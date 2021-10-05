package org.um.nine.screens.hud;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.Game;
import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.contracts.repositories.ICardRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;

public class OptionHudState extends BaseAppState  {
    private Container window;

    @Inject
    private PlayerInfoState playerInfoState;

    @Inject
    private IPlayerRepository playerRepository;

    @Inject
    private IBoardRepository boardRepository;

    @Inject
    private ActionState actionState;

    @Inject
    private RoleActionState roleActionState;

    private float getStandardScale() {
        int height = getApplication().getCamera().getHeight();
        return height / 720f;
    }

    @Override
    protected void initialize(Application application) {
        window = new Container();

        String currentPlayerName = playerRepository.getCurrentPlayer() != null ? playerRepository.getCurrentPlayer().getName() : "Unknown";
        Label currentPlayerMoveLbl = new Label("Current Player: " + currentPlayerName);
        currentPlayerMoveLbl.setName("currentPlayerNameLbl");
        currentPlayerMoveLbl.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(currentPlayerMoveLbl, 1, 0);

        String currentActionName = boardRepository.getSelectedPlayerAction() != null ? boardRepository.getSelectedPlayerAction().toString() : "None";
        Label currentActionLbl = new Label("Selected Action: " + currentActionName);
        currentActionLbl.setName("currentActionNameLbl");
        currentActionLbl.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(currentActionLbl, 2, 0);

        String currentRoleActionName = boardRepository.getSelectedRoleAction() != null ? boardRepository.getSelectedRoleAction().getName() : "None";
        Label currentRoleActionLbl = new Label("Selected Role Action: " + currentRoleActionName);
        currentRoleActionLbl.setName("currentRoleActionNameLbl");
        currentRoleActionLbl.setInsets(new Insets3f(10, 10, 10, 10));
        window.addChild(currentRoleActionLbl, 3, 0);

        Button actionButton = window.addChild(new Button("Actions"));
        actionButton.addClickCommands(button -> {
           getStateManager().attach(actionState);
           actionState.setEnabled(true);
        });
        actionButton.setInsets(new Insets3f(2, 2, 0, 2));

        Button roleActionsButton = window.addChild(new Button("Role Actions"));
        roleActionsButton.addClickCommands(button -> {
            getStateManager().attach(roleActionState);
            roleActionState.setEnabled(true);
        });
        roleActionsButton.setInsets(new Insets3f(2, 2, 0, 2));

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

        window.setLocalTranslation(25, 350, 5);
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

    public Container getWindow() {
        return window;
    }
}
