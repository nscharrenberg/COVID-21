package org.um.nine.v1.screens.hud;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.v1.Game;
import org.um.nine.v1.contracts.repositories.IBoardRepository;
import org.um.nine.v1.contracts.repositories.IGameRepository;
import org.um.nine.v1.contracts.repositories.IPlayerRepository;
import org.um.nine.v1.utils.Util;

public class OptionHudState extends BaseAppState  {
    private Container window;
    private boolean heartbeat = false;

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

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private RuleState ruleState;

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
            getStateManager().attach(ruleState);
            ruleState.setEnabled(true);
        });
        rulesButton.setInsets(new Insets3f(2, 2, 0, 2));

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
        window.setLocalTranslation(window.getWorldScale().getX(), 200, 100);
        window.setLocalScale(Util.getStandardScale(window));

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

    public boolean isHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }
}
