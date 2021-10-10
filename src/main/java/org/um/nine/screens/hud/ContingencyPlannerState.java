package org.um.nine.screens.hud;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.Game;
import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.contracts.repositories.ICardRepository;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.roles.RoleAction;

public class ContingencyPlannerState extends BaseAppState  {
    private Container window;

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

        Label title = window.addChild(new Label("Discarded Event cards!"));
        title.setFontSize(16);
        title.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(title, 0, 0);

        Button closeBtn = new Button("Close");
        closeBtn.addClickCommands(c -> {
            this.setEnabled(false);
        });
        closeBtn.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(closeBtn, 0, 1);

        if(cardRepository.getEventDiscardPile().isEmpty()){
            Label no = window.addChild(new Label("No Event cards have been used!"));
            no.setFontSize(12);
            no.setInsets(new Insets3f(10, 10, 0, 10));
            window.addChild(no, 0, 0);
        }else{
            cardRepository.getEventDiscardPile().forEach(c ->{
                Button b = new Button(c.getName());
                b.addClickCommands(command ->{
                    playerRepository.getCurrentPlayer().addCard(c);
                    cardRepository.getEventDiscardPile().remove(c);
                    setEnabled(false);
                });
                window.addChild(b);
            });
        }

        window.setLocalTranslation(25, 1000, 5);
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