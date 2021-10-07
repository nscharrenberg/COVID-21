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
import org.um.nine.domain.Player;
import org.um.nine.domain.cards.CityCard;
import org.um.nine.domain.cards.PlayerCard;
import org.um.nine.exceptions.UnableToShareKnowledgeException;

public class ShareCityCardConfirmationDialogBox extends BaseAppState {
    private Container window;

    private Player currentPlayer;
    private Player otherPlayer;
    private City city;

    @Inject
    private IDiseaseRepository diseaseRepository;

    @Inject
    private IPlayerRepository playerRepository;

    public ShareCityCardConfirmationDialogBox() {
        this.currentPlayer = null;
        this.city = null;
        this.otherPlayer = null;
    }

    public ShareCityCardConfirmationDialogBox(Player player, Player otherPlayer, City city) {
        this.currentPlayer = player;
        this.otherPlayer = otherPlayer;
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

        PlayerCard pc = otherPlayer.getHandCards().stream().filter(c1 -> {
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

        Label cureText= null;

        if (currentPlayer.getHandCards().contains(cpc)) {
           cureText  = window.addChild(new Label("Does " + otherPlayer.getName() + " accept to retrieve " + city.getName() + " from " + currentPlayer.getName()), 1, 0);
        } else {
            cureText  = window.addChild(new Label("Does " + otherPlayer.getName() + " accept to give " + city.getName() + " to " + currentPlayer.getName()), 1, 0);
        }

        cureText.setInsets(new Insets3f(10, 10, 0, 10));
        cureText.setColor(ColorRGBA.Red);

        Button denyBtn = new Button("Deny");
        denyBtn.setInsets(new Insets3f(10, 10, 0, 10));

        denyBtn.addClickCommands(c -> {
            DialogBoxState dialog = new DialogBoxState(otherPlayer.getName() + "denied to share knowledge request.");
            getStateManager().attach(dialog);
            dialog.setEnabled(true);
            setEnabled(false);
        });

        window.addChild(denyBtn, 2, 0);

        Button acceptBtn = new Button("Accept");
        acceptBtn.setInsets(new Insets3f(10, 10, 0, 10));

        acceptBtn.addClickCommands(c -> {

            if (currentPlayer.getHandCards().contains(cpc)) {
                currentPlayer.getHandCards().remove(cpc);
                otherPlayer.getHandCards().add(cpc);
            } else {
                otherPlayer.getHandCards().remove(pc);
                currentPlayer.getHandCards().add(pc);
            }

            setEnabled(false);
            playerRepository.nextState(playerRepository.getCurrentRoundState());
        });

        window.addChild(acceptBtn, 2, 0);
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

    public Player getOtherPlayer() {
        return otherPlayer;
    }

    public void setOtherPlayer(Player otherPlayer) {
        this.otherPlayer = otherPlayer;
    }
}
