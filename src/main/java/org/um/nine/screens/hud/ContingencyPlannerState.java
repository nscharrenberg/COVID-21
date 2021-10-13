package org.um.nine.screens.hud;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.Game;
import org.um.nine.contracts.repositories.*;
import org.um.nine.domain.roles.RoleAction;
import org.um.nine.utils.Util;

import java.util.LinkedList;

public class ContingencyPlannerState extends BaseAppState  {
    private Container window;

    @Inject
    private ICardRepository cardRepository;

    @Inject
    private IPlayerRepository playerRepository;

    @Inject
    private PlayerInfoState playerInfoState;

    @Inject
    private IGameRepository gameRepository;

    private boolean heartbeat = false;

    LinkedList<Button> blist = new LinkedList<>();

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
            blist.forEach(button -> window.removeChild(button));
            blist.clear();
        });
        closeBtn.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(closeBtn, 0, 1);

    }

    @Override
    public void update(float tpf){
        super.update(tpf);
        if(heartbeat){
            info();
            heartbeat=false;
        }
    }

    private void info(){
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
                    playerInfoState.setHeartbeat(true);
                    setEnabled(false);
                    blist.forEach(button -> window.removeChild(button));
                    blist.clear();
                });
                blist.add(b);
                window.addChild(b);
            });
        }

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
