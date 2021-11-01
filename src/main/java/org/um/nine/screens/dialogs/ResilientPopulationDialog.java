package org.um.nine.screens.dialogs;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.Game;
import org.um.nine.contracts.repositories.ICardRepository;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.cards.InfectionCard;
import org.um.nine.utils.Util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class ResilientPopulationDialog extends BaseAppState {
    private Container window;

    @Inject
    private ICardRepository cardRepository;

    @Inject
    private IPlayerRepository playerRepository;

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private ICityRepository cityRepository;

    private boolean heartbeat = false;

    @Override
    protected void initialize(Application application) {
        window = new Container();

        Label title = window.addChild(new Label("Choose the infection card from the discarded pile to be removed!"));
        title.setFontSize(16);
        title.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(title, 0, 0);

        Button closeBtn = new Button("Close");
        closeBtn.addClickCommands(c -> {
            this.setEnabled(false);
        });
        closeBtn.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(closeBtn, 0, 1);

    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (heartbeat) {
            renderInfo();
            heartbeat = false;
        }
    }

    private void renderInfo(){
        ListBox<String> discardedCards = new ListBox();
        AtomicInteger cIndex = new AtomicInteger();
        cardRepository.getInfectionDiscardPile().forEach(c -> {
            discardedCards.getModel().add(cIndex.get(), c.getName());
            cIndex.getAndIncrement();
        });
        discardedCards.addClickCommands(listCommand -> {
            setEnabled(false);
            AtomicReference<InfectionCard> icard = new AtomicReference<InfectionCard>();
            cardRepository.getInfectionDiscardPile().forEach(c -> {
                if(c.getName().equals(discardedCards.getSelectedItem())){
                    icard.set(c);
                }
            });
            cardRepository.getInfectionDiscardPile().remove(icard.get());
            window.removeChild(discardedCards);
        });
        window.addChild(discardedCards);

        Vector3f size = Util.calculateMenusize(gameRepository.getApp(), window);
        size.addLocal(0, 0, 100);
        window.setLocalTranslation(size);
        window.setLocalScale(Util.getStandardScale(window));
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

    public void setHeartbeat(boolean b) {
        heartbeat = b;
    }

}
