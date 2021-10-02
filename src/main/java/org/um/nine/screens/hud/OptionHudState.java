package org.um.nine.screens.hud;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.lwjgl.Sys;
import org.um.nine.Game;
import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.contracts.repositories.ICardRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.Card;
import org.um.nine.domain.Player;
import org.um.nine.exceptions.GameOverException;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class OptionHudState extends BaseAppState  {
    private Container window;

    @Inject
    private PlayerInfoState playerInfoState;

    @Inject
    private ICardRepository cardRepository;

    @Inject
    private IPlayerRepository playerRepository;

    private float getStandardScale() {
        int height = getApplication().getCamera().getHeight();
        return height / 720f;
    }

    @Override
    protected void initialize(Application application) {
        window = new Container();

        Button actionButton = window.addChild(new Button("Actions"));
        actionButton.addClickCommands(button -> {
            // TODO: Open Actions Menu
            //TODO: remove from here
            ArrayList<Player> pl = new ArrayList<>();
            for(Player p: playerRepository.getPlayers().values()){
                pl.add(p);
            }
            Player p = pl.get(0);
            try{
                cardRepository.drawPlayCard(p);
            }
            catch(GameOverException e){
                e.printStackTrace();
                System.exit(0);
            }
            //TODO: to here!
        });
        actionButton.setInsets(new Insets3f(2, 2, 0, 2));

        Button cardsButton = window.addChild(new Button("Show Players Info (Cards, Roles)"));
        cardsButton.addClickCommands(button -> {
            getStateManager().attach(playerInfoState);
            playerInfoState.setEnabled(true);
        });
        cardsButton.setInsets(new Insets3f(2, 2, 0, 2));

        Button rulesButton = window.addChild(new Button("Show Rules"));
        rulesButton.addClickCommands(button -> {
            // TODO: Open Show Rules Menu
            System.out.println("Show Rules Button Clicked");
        });
        rulesButton.setInsets(new Insets3f(2, 2, 0, 2));

        window.setLocalTranslation(getApplication().getCamera().getWidth() - 150, 150, 5);
        window.setLocalScale(1.5f);
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
