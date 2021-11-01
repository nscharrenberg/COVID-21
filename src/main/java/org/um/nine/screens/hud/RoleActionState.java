package org.um.nine.screens.hud;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.Game;
import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.roles.RoleAction;
import org.um.nine.exceptions.*;
import org.um.nine.utils.Util;

public class RoleActionState extends BaseAppState  {
    private Container window;

    private boolean heartbeat = false;

    @Inject
    private IPlayerRepository playerRepository;

    @Inject
    private IBoardRepository boardRepository;

    @Inject
    private IGameRepository gameRepository;

    @Override
    protected void initialize(Application application) {
        window = new Container();

        Label title = window.addChild(new Label("Role Actions"));
        title.setFontSize(16);
        title.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(title, 0, 0);

        Button closeBtn = new Button("Close");
        closeBtn.addClickCommands(c -> this.setEnabled(false));
        closeBtn.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(closeBtn, 0, 1);

        int btnCount = 1;

        for (RoleAction type : RoleAction.values()) {
            Button button = new Button(type.getName());
            button.setInsets(new Insets3f(10, 10, 0, 10));

            button.addClickCommands(c -> {
                boardRepository.setSelectedRoleAction(type);
                if(type.equals(RoleAction.BUILD_RESEARCH_STATION)||type.equals(RoleAction.TAKE_ANY_DISCARED_EVENT) || type.equals(RoleAction.MOVE_ANY_PAWN_TO_CITY_WITH_OTHER_PAWN)){
                    try {
                        playerRepository.action(null);
                    } catch (InvalidMoveException | NoDiseaseOrOutbreakPossibleDueToEvent | NoActionSelectedException | ResearchStationLimitException | CityAlreadyHasResearchStationException | NoCubesLeftException | GameOverException e) {
                        e.printStackTrace();
                    }
                }
                setEnabled(false);
            });

            window.addChild(button, btnCount, 0);
            btnCount++;
        }

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

    public boolean isHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }
}
