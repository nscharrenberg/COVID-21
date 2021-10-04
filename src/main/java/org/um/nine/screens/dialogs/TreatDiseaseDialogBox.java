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
import org.um.nine.domain.City;
import org.um.nine.domain.Disease;
import org.um.nine.domain.Player;
import org.um.nine.exceptions.NoCityCardToTreatDiseaseException;

public class TreatDiseaseDialogBox extends BaseAppState {
    private Container window;

    private City city;
    private Player player;

    @Inject
    private IDiseaseRepository diseaseRepository;

    public TreatDiseaseDialogBox() {
        this.city = null;
        this.player = null;
    }

    public TreatDiseaseDialogBox(City city, Player player) {
        this.city = city;
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

        for (Disease disease : city.getCubes()) {
            Button button = new Button(disease.getColor().toString());
            button.setInsets(new Insets3f(10, 10, 0, 10));

            button.addClickCommands(c -> {
                try {
                    diseaseRepository.treat(player, city, disease);
                } catch (NoCityCardToTreatDiseaseException e) {
                    DialogBoxState dialog = new DialogBoxState(e.getMessage());
                    getStateManager().attach(dialog);
                    dialog.setEnabled(true);
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
}
