package org.um.nine.screens.hud;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.Game;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.Player;
import org.um.nine.utils.Util;

import java.util.concurrent.atomic.AtomicInteger;

public class PlayerInfoState extends BaseAppState  {
    private Container window;

    @Inject
    private IPlayerRepository playerRepository;

    @Inject
    private IGameRepository gameRepository;

    private boolean heartbeat = false;

    @Override
    protected void initialize(Application application) {
        window = new Container();

        Label title = window.addChild(new Label("Players Information"));
        title.setFontSize(16);
        title.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(title, 0, 0);

        Button closeBtn = new Button("Close");
        closeBtn.addClickCommands(c -> this.setEnabled(false));
        closeBtn.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(closeBtn, 0, 1);

        AtomicInteger i = new AtomicInteger(0);

        playerRepository.getPlayers().forEach((key, player) -> {
            renderPlayerInfo(player, i.get());
            i.getAndIncrement();
        });


        Vector3f size = Util.calculateMenusize(gameRepository.getApp(), window);
        size.addLocal(0, 0, 100);
        window.setLocalTranslation(size);
        window.setLocalScale(Util.getStandardScale(window));
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (heartbeat) {
            initialize(gameRepository.getApp());

            heartbeat = false;
        }
    }

    private void renderPlayerInfo(Player player, int i) {
        Label title = window.addChild(new Label(player.getName()));
        title.setFontSize(16);
        title.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(title, 1, i);

        Label role = window.addChild(new Label("Role: " + player.getRole().getName()));
        role.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(role, 2, i);

        Label cardTxt = window.addChild(new Label("Cards: "));
        cardTxt.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(cardTxt, 3, i);

        ListBox<String> cards = new ListBox<>();

        AtomicInteger cardIndex = new AtomicInteger();

        player.getHandCards().forEach(c -> {
            cards.getModel().add(cardIndex.get(), c.getName());

            cardIndex.getAndIncrement();
        });

        window.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(cards, 4, i);
    }

    @Override
    protected void cleanup(Application application) {
        application.stop();
    }

    @Override
    protected void onEnable() {
        Vector3f size = Util.calculateMenusize(gameRepository.getApp(), window);
        size.addLocal(0, -300, 100);
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
