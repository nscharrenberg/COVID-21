package org.um.nine.v1.screens.dialogs;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import org.um.nine.v1.Game;
import org.um.nine.v1.Info;
import org.um.nine.v1.contracts.repositories.IGameRepository;
import org.um.nine.v1.domain.Player;
import org.um.nine.v1.screens.hud.PlayerInfoState;
import org.um.nine.v1.utils.Util;

import java.util.LinkedList;

public class DiscardCardDialog extends BaseAppState {
    private Container window;

    private Player currentPlayer;

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private PlayerInfoState playerInfoState;

    private boolean heartbeat = false;

    public DiscardCardDialog() {
        this.currentPlayer = null;
    }

    @Override
    protected void initialize(Application application) {
        window = new Container();

        window.setBackground(new QuadBackgroundComponent(ColorRGBA.White));

        Label discardText = window.addChild(new Label("Select a card you want to discard."), 1, 0);
        discardText.setInsets(new Insets3f(10, 10, 0, 10));
        discardText.setColor(ColorRGBA.Red);

        window.addChild(discardText);

        Vector3f size = Util.calculateMenusize(gameRepository.getApp(), window);
        size.addLocal(0, 0, 100);
        window.setLocalTranslation(size);
        window.setLocalScale(Util.getStandardScale(window));
    }

    @Override
    public void update(float tpf){
        super.update(tpf);
        if(heartbeat){
            renderInfo();

            heartbeat=false;
        }
    }

    private void renderInfo() {
        LinkedList<Button> blist = new LinkedList<>();
        currentPlayer.getHandCards().forEach(c -> {
            Button b = new Button(c.getName());
            b.setInsets(new Insets3f(10, 10, 0, 10));

            b.addClickCommands(d -> {
                currentPlayer.discard(c);
                playerInfoState.setHeartbeat(true);
                if(currentPlayer.getHandCards().size() > Info.HAND_LIMIT){
                    window.removeChild(b);
                }
                else{
                    blist.forEach(button -> window.removeChild(button));
                    this.setEnabled(false);
                }
            });
            blist.add(b);
            window.addChild(b);
        });
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

        Node gui = ((Game) getApplication()).getGuiNode();
        gui.attachChild(window);
        GuiGlobals.getInstance().requestFocus(window);
    }

    @Override
    protected void onDisable() {
        window.removeFromParent();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setHeartbeat(boolean state){
        heartbeat=state;
    }

}
