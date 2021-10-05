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
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.domain.City;
import org.um.nine.domain.Player;
import org.um.nine.domain.cards.CityCard;
import org.um.nine.domain.cards.PlayerCard;
import org.um.nine.exceptions.NoCityCardToTreatDiseaseException;
import org.um.nine.exceptions.UnableToShareKnowledgeException;

public class ShareCityCardDialogBox extends BaseAppState {
    private Container window;

    private Player currentPlayer;
    private City city;

    @Inject
    private IDiseaseRepository diseaseRepository;

    @Inject
    private ShareCityCardConfirmationDialogBox shareCityCardConfirmationDialogBox;

    @Inject
    private IGameRepository gameRepository;

    public ShareCityCardDialogBox() {
        this.currentPlayer = null;
        this.city = null;
    }

    public ShareCityCardDialogBox(Player player, City city) {
        this.currentPlayer = player;
        this.city = city;
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

        for (Player pawn : city.getPawns()) {
            Button button = new Button(pawn.getName());
            button.setInsets(new Insets3f(10, 10, 0, 10));

            button.addClickCommands(c -> {

                PlayerCard pc = pawn.getHandCards().stream().filter(c1 -> {
                    if (c1 instanceof CityCard cc) {
                        return cc.getCity().equals(city);
                    }

                    return false;
                }).findFirst().orElse(null);

                PlayerCard cpc = currentPlayer.getHandCards().stream().filter(c1 -> {
                    if (c1 instanceof CityCard cc) {
                        return cc.getCity().equals(city);
                    }

                    return false;
                }).findFirst().orElse(null);

                if (!pawn.getHandCards().contains(pc) && !currentPlayer.getHandCards().contains(cpc)) {
                    try {
                        throw new UnableToShareKnowledgeException(city, currentPlayer, pawn);
                    } catch (UnableToShareKnowledgeException e) {
                        DialogBoxState dialog = new DialogBoxState(e.getMessage());
                        getStateManager().attach(dialog);
                        dialog.setEnabled(true);
                        setEnabled(false);
                        return;
                    }
                }

                gameRepository.getApp().getStateManager().attach(shareCityCardConfirmationDialogBox);
                shareCityCardConfirmationDialogBox.setCity(city);
                shareCityCardConfirmationDialogBox.setCurrentPlayer(currentPlayer);
                shareCityCardConfirmationDialogBox.setOtherPlayer(pawn);
                shareCityCardConfirmationDialogBox.setEnabled(true);
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

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
