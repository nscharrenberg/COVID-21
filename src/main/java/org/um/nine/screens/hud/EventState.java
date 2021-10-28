package org.um.nine.screens.hud;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import org.um.nine.Game;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.cards.EventCard;
import org.um.nine.utils.Util;

import java.util.LinkedList;

public class EventState extends BaseAppState {
    private Container window;

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private IPlayerRepository playerRepository;

    private boolean heartbeat = false;


    @Override
    protected void initialize(Application application) {
        window = new Container();

        Label eventText = window.addChild(new Label("Available event cards: "), 1, 0);
        eventText.setInsets(new Insets3f(10, 10, 0, 10));

        window.addChild(eventText);

        Button closeBtn = new Button("Close");
        closeBtn.addClickCommands(c -> {
            this.setEnabled(false);
        });
        closeBtn.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(closeBtn, 0, 1);

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
        playerRepository.getPlayers().values().forEach(player -> {
            player.getHandCards().forEach(c -> {
                if(c instanceof EventCard){
                    Button b = new Button(c.getName());
                    b.addClickCommands(command -> {
                        ((EventCard) c).event();
                        player.getHandCards().remove(c);
                        blist.forEach(button -> window.removeChild(b));
                        setEnabled(false);
                    });
                    blist.add(b);
                    window.addChild(b);
                }
            });
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

    public void setHeartbeat(boolean state){
        heartbeat=state;
    }

}
