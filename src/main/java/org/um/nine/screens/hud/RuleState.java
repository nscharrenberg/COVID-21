package org.um.nine.screens.hud;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.*;
import org.um.nine.Game;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.utils.Util;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class RuleState extends BaseAppState {
    private Container window;

    private boolean heartbeat = false;

    @Inject
    private IGameRepository gameRepository;

    @Override
    protected void initialize(Application application) {
        window = new Container();

        Label title = window.addChild(new Label("Rules"));
        title.setFontSize(16);
        title.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(title, 0, 0);

        Button closeBtn = new Button("Close");
        closeBtn.addClickCommands(c -> {
            this.setEnabled(false);
        });
        closeBtn.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(closeBtn, 0, 1);

        Label flowTitle = window.addChild(new Label("Gameflow: "));
        flowTitle.setFontSize(12);
        flowTitle.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(flowTitle, 1, 0);

        Label flowtext1 = window.addChild(new Label("1. Actions: The first step of your turn consists 4 actions. \nThe possible actions can be found in the actions tab."));
        flowtext1.setFontSize(8);
        flowtext1.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(flowtext1, 2, 0);

        Label flowtext2 = window.addChild(new Label("2. Draw: After you finished your actions you will draw two cards. \nIn the case that you draw an epidemic card it will be \nplayed immediately."));
        flowtext2.setFontSize(8);
        flowtext2.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(flowtext2, 3, 0);

        Label flowtext3 = window.addChild(new Label("3. Infect: After every turn a few cities will be infected. \nThe amount is defined by the infection rate."));
        flowtext3.setFontSize(8);
        flowtext3.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(flowtext3, 4, 0);

        Label gameOver = window.addChild(new Label("Game ending scenarios: "));
        gameOver.setFontSize(12);
        gameOver.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(gameOver, 5, 0);

        Label gameOverText = window.addChild(new Label("To win the game all diseases have to be cured (not eradicated).\nThere are multiple game over conditions:\n 1. If the player deck or the infection deck runs out of cards \n 2. If The outbreak marker reaches the last space\n 3. If there are not enough disease cubes left to infect a city"));
        gameOverText.setFontSize(8);
        gameOverText.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(gameOverText, 6, 0);

        Label generalInfo = window.addChild(new Label("General hints / mechanics: "));
        generalInfo.setFontSize(12);
        generalInfo.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(generalInfo, 7, 0);

        Label generalInfoText = window.addChild(new Label("1. Event cards can be played at any time \n2. You can give city cards to other players if both players are \nin the city of the city card\n3. It is a team game! Players should help each other! \n"));
        generalInfoText.setFontSize(8);
        generalInfoText.setInsets(new Insets3f(10, 10, 0, 10));
        window.setLocalScale(15f);
        window.addChild(generalInfoText, 8, 0);

        Button readMoreBtn = new Button("Read More");
        readMoreBtn.addClickCommands(b -> {
            try {
                Desktop.getDesktop().browse(new URI("https://images.zmangames.com/filer_public/53/ed/53edbee8-adfb-4715-899f-dd381e1420d7/zm7101_rules_web.pdf"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        readMoreBtn.setInsets(new Insets3f(10, 10, 10, 10));
        window.addChild(readMoreBtn, 9, 0);

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
