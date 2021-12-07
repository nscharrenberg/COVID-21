package org.um.nine.jme.screens.hud;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.headless.game.domain.roles.RoleAction;
import org.um.nine.jme.repositories.BoardRepository;
import org.um.nine.jme.repositories.GameRepository;
import org.um.nine.jme.repositories.PlayerRepository;
import org.um.nine.jme.utils.JmeFactory;

public class RoleActionState extends BaseAppState  {
    private Container window;

    private boolean heartbeat = false;

    private PlayerRepository playerRepository = JmeFactory.getPlayerRepository();

    private BoardRepository boardRepository = JmeFactory.getBoardRepository();

    private GameRepository gameRepository = JmeFactory.getGameRepository();

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
                if(type.equals(RoleAction.BUILD_RESEARCH_STATION)||type.equals(RoleAction.TAKE_ANY_DISCARED_EVENT)){
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
