package org.um.nine.screens.hud;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import org.um.nine.Game;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.Player;
import org.um.nine.domain.cards.PlayerCard;

import java.util.concurrent.atomic.AtomicInteger;

public class PlayerInfoState extends BaseAppState  {
    private Container window;

    @Inject
    private IPlayerRepository playerRepository;

    private float getStandardScale() {
        int height = getApplication().getCamera().getHeight();
        return height / 720f;
    }

    @Override
    protected void initialize(Application application) {
        window = new Container();
        window.setBackground(new QuadBackgroundComponent(ColorRGBA.White));

        Label title = window.addChild(new Label("Players Information"));
        title.setFontSize(16);
        title.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(title, 0, 0);

        Button closeBtn = new Button("Close");
        closeBtn.addClickCommands(c -> {
            this.setEnabled(false);
        });
        closeBtn.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(closeBtn, 0, 1);

        AtomicInteger i = new AtomicInteger(0);

        playerRepository.getPlayers().forEach((key, player) -> {
            renderPlayerInfo(player, i.get());
            i.getAndIncrement();
        });


        window.setLocalTranslation(getApplication().getCamera().getWidth() / 2f, getApplication().getCamera().getHeight() / 2f, 5);
        window.setLocalScale(1.5f);
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

        ListBox<PlayerCard> cards = new ListBox<>();

        AtomicInteger cardIndex = new AtomicInteger();

        player.getHandCards().forEach(c -> {
            cards.getModel().add(cardIndex.get(), c);

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
        Node gui = ((Game)getApplication()).getGuiNode();
        gui.attachChild(window);
        GuiGlobals.getInstance().requestFocus(window);
    }

    @Override
    protected void onDisable() {
        window.removeFromParent();
    }
}
