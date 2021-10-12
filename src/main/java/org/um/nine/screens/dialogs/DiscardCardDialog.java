package org.um.nine.screens.dialogs;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;

import org.um.nine.Game;
import org.um.nine.Info;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.Player;
import org.um.nine.domain.cards.CityCard;
import org.um.nine.domain.cards.PlayerCard;
import org.um.nine.repositories.local.PlayerRepository;
import org.um.nine.screens.hud.PlayerInfoState;

public class DiscardCardDialog extends BaseAppState {
    private Container window;

    private Player currentPlayer;

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private PlayerInfoState playerInfoState;

    @Inject
    private IPlayerRepository playerRepository;

    private boolean heartbeat = false;

    private int height;

    public DiscardCardDialog() {
        this.currentPlayer = null;
    }

    public DiscardCardDialog(Player player) {
        this.currentPlayer = player;
    }

    public float getStandardScale() {
        int height = getApplication().getCamera().getHeight();
        return height / 720f;
    }

    @Override
    protected void initialize(Application application) {
        window = new Container();

        window.setBackground(new QuadBackgroundComponent(ColorRGBA.White));

        Label discardText = window.addChild(new Label("Select a card you want to discard."), 1, 0);
        discardText.setInsets(new Insets3f(10, 10, 0, 10));
        discardText.setColor(ColorRGBA.Red);

        window.addChild(discardText);

        height = application.getCamera().getHeight();

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
                if(playerRepository.getCurrentPlayer().getHandCards().size() > Info.HAND_LIMIT){
                    window.removeChild(b);
                }
                else{
                    blist.forEach(button -> {
                        window.removeChild(button);
                    });
                    this.setEnabled(false);
                }
            });
            blist.add(b);
            window.addChild(b);
        });

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
