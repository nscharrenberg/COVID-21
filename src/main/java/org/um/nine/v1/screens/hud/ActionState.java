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
import org.um.nine.v1.domain.ActionType;
import org.um.nine.v1.utils.Util;

public class ActionState extends BaseAppState  {
    private Container window;
    private boolean heartbeat = false;

    @Inject
    private IBoardRepository boardRepository;

    @Inject
    private IGameRepository gameRepository;

    @Override
    protected void initialize(Application application) {
        window = new Container();

        Label title = window.addChild(new Label("Actions"));
        title.setFontSize(16);
        title.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(title, 0, 0);

        Button closeBtn = new Button("Close");
        closeBtn.addClickCommands(c -> this.setEnabled(false));
        closeBtn.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(closeBtn, 0, 1);

        int btnCount = 1;

        for (ActionType type : ActionType.values()) {
            Button button = new Button(type.getDescription());
            button.setInsets(new Insets3f(10, 10, 0, 10));
            button.setInsets(new Insets3f(10, 10, 0, 10));

            button.addClickCommands(c -> {
                boardRepository.setSelectedPlayerAction(type);
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
