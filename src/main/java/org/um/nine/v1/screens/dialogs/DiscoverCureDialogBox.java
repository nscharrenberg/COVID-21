package org.um.nine.v1.screens.dialogs;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import org.um.nine.jme.screens.DialogBoxState;
import org.um.nine.v1.Game;
import org.um.nine.v1.contracts.repositories.IDiseaseRepository;
import org.um.nine.v1.contracts.repositories.IGameRepository;
import org.um.nine.v1.contracts.repositories.IPlayerRepository;
import org.um.nine.v1.domain.Cure;
import org.um.nine.v1.domain.Player;
import org.um.nine.v1.exceptions.UnableToDiscoverCureException;
import org.um.nine.v1.utils.Util;

import java.util.Map;

public class DiscoverCureDialogBox extends BaseAppState {
    private Container window;

    private Player player;
    private boolean heartbeat = false;

    public boolean isHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }

    @Inject
    private IDiseaseRepository diseaseRepository;

    @Inject
    private IPlayerRepository playerRepository;

    @Inject
    private IGameRepository gameRepository;

    public DiscoverCureDialogBox() {
        this.player = null;
    }

    @Override
    protected void initialize(Application application) {
        window = new Container();

        window.setBackground(new QuadBackgroundComponent(ColorRGBA.White));

        Label cureText = window.addChild(new Label("Select disease to CureMacro:"), 1, 0);
        cureText.setInsets(new Insets3f(10, 10, 0, 10));
        cureText.setColor(ColorRGBA.Red);

        int btnCount = 1;

        for (Map.Entry<ColorRGBA, Cure> entry : diseaseRepository.getCures().entrySet()) {
            Cure cure = entry.getValue();

            Button button = new Button(cure.getColor().toString());
            button.setInsets(new Insets3f(10, 10, 0, 10));

            button.addClickCommands(c -> {
                try {
                    diseaseRepository.discoverCure(player, cure);
                    playerRepository.nextState(playerRepository.getCurrentRoundState());
                } catch (UnableToDiscoverCureException e) {
                    DialogBoxState dialog = new DialogBoxState(e.getMessage());
                    getStateManager().attach(dialog);
                    dialog.setEnabled(true);
                    setEnabled(false);
                    return;
                }
                setEnabled(false);
            });

            window.addChild(button, btnCount, 0);
            btnCount++;
        }

        window.addChild(cureText);

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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
