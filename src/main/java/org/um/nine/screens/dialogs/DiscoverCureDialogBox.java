package org.um.nine.screens.dialogs;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import org.um.nine.Game;
import org.um.nine.contracts.repositories.IDiseaseRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.City;
import org.um.nine.domain.Cure;
import org.um.nine.domain.Disease;
import org.um.nine.domain.Player;
import org.um.nine.exceptions.NoCityCardToTreatDiseaseException;
import org.um.nine.exceptions.UnableToDiscoverCureException;

import java.util.Map;

public class DiscoverCureDialogBox extends BaseAppState {
    private Container window;

    private Player player;

    @Inject
    private IDiseaseRepository diseaseRepository;

    @Inject
    private IPlayerRepository playerRepository;

    public DiscoverCureDialogBox() {
        this.player = null;
    }

    public DiscoverCureDialogBox(Player player) {
        this.player = player;
    }

    public float getStandardScale() {
        int height = getApplication().getCamera().getHeight();
        return height / 720f;
    }

    @Override
    protected void initialize(Application application) {
        window = new Container();

        window.setBackground(new QuadBackgroundComponent(ColorRGBA.White));

        Label cureText = window.addChild(new Label("Select Disease to treat:"), 1, 0);
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

        int height = application.getCamera().getHeight();
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
