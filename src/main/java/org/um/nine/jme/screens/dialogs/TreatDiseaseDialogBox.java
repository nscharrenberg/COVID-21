package org.um.nine.jme.screens.dialogs;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Disease;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.exceptions.NoCityCardToTreatDiseaseException;
import org.um.nine.jme.JmeGame;
import org.um.nine.jme.repositories.DiseaseRepository;
import org.um.nine.jme.repositories.GameRepository;
import org.um.nine.jme.repositories.PlayerRepository;
import org.um.nine.jme.screens.DialogBoxState;
import org.um.nine.jme.utils.JmeFactory;
import org.um.nine.jme.utils.MenuUtils;


public class TreatDiseaseDialogBox extends BaseAppState {
    private Container window;

    private City city;
    private Player player;

    private boolean heartbeat = false;

    private DiseaseRepository diseaseRepository = JmeFactory.getDiseaseRepository();

    private PlayerRepository playerRepository = JmeFactory.getPlayerRepository();

    private GameRepository gameRepository = JmeFactory.getGameRepository();

    public TreatDiseaseDialogBox() {
        this.city = null;
        this.player = null;
    }

    @Override
    protected void initialize(Application application) {
        window = new Container();

        window.setBackground(new QuadBackgroundComponent(ColorRGBA.White));

        Label cureText = window.addChild(new Label("Select Disease to treat:"), 1, 0);
        cureText.setInsets(new Insets3f(10, 10, 0, 10));
        cureText.setColor(ColorRGBA.Red);

        renderItems();

        window.addChild(cureText);

        Vector3f size = MenuUtils.calculateMenusize(gameRepository.getApp(), window);
        size.addLocal(0, 0, 100);
        window.setLocalTranslation(size);
        window.setLocalScale(MenuUtils.getStandardScale(window));
    }

    private void renderItems() {
        int btnCount = 1;

        for (Disease disease : city.getCubes()) {
            Button button = new Button(disease.getColor().getColor().toString());
            button.setInsets(new Insets3f(10, 10, 0, 10));

            button.addClickCommands(c -> {
                diseaseRepository.treat(player, city, disease.getColor());
                playerRepository.nextState(playerRepository.getCurrentRoundState());
                setEnabled(false);
            });

            window.addChild(button, btnCount, 0);
            btnCount++;
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (heartbeat) {
            this.setEnabled(false);
            initialize(gameRepository.getApp());
            this.setEnabled(true);
            heartbeat = false;
        }
    }

    @Override
    protected void cleanup(Application application) {
        application.stop();
    }

    @Override
    protected void onEnable() {
        Vector3f size = MenuUtils.calculateMenusize(gameRepository.getApp(), window);
        size.addLocal(0, 0, 100);
        window.setLocalTranslation(size);
        window.setLocalScale(MenuUtils.getStandardScale(window));

        Node gui = ((JmeGame)getApplication()).getGuiNode();
        gui.attachChild(window);
        GuiGlobals.getInstance().requestFocus(window);
    }

    @Override
    protected void onDisable() {
        window.removeFromParent();
    }

    public City getCity() {
        return city;
    }

    public Player getPlayer() {
        return player;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }
}
