package org.um.nine.screens.dialogs;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.Game;
import org.um.nine.contracts.repositories.*;
import org.um.nine.domain.cards.InfectionCard;
import org.um.nine.utils.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;


public class PrognosisEventDialog extends BaseAppState  {
    private Container window;

    @Inject
    private ICardRepository cardRepository;

    @Inject
    private IPlayerRepository playerRepository;

    @Inject
    private IGameRepository gameRepository;

    private boolean heartbeat = false;

    LinkedList<Button> blist = new LinkedList<>();

    ArrayList<InfectionCard> cards = new ArrayList<>();
    ArrayList<InfectionCard> initialOrderedCards = new ArrayList<>();

    private Button confirm;


    @Override
    protected void initialize(Application application) {
        window = new Container();

        Label title = window.addChild(new Label("The top 6 infection cards!"));
        title.setFontSize(16);
        title.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(title, 0, 0);

        Button closeBtn = new Button("Close");
        closeBtn.addClickCommands(c -> {
            blist.forEach(b -> window.removeChild(b));
            window.removeChild(confirm);
            for(int i = initialOrderedCards.size()-1; i >= 0; i--){
                cardRepository.getInfectionDeck().add(initialOrderedCards.get(i));
            }
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
        cards = cardRepository.getTopCards();
        initialOrderedCards = (ArrayList<InfectionCard>) cards.clone();
        Stack<InfectionCard> orderedCards = new Stack<>();
        if(cards.isEmpty()){
            Label no = window.addChild(new Label("No infectioncards available"));
            no.setFontSize(12);
            no.setInsets(new Insets3f(10, 10, 0, 10));
            window.addChild(no, 0, 0);
        }else{
            cards.forEach(c ->{
                Button b = new Button(c.getName());
                b.addClickCommands(command ->{
                    window.removeChild(b);
                    orderedCards.add(c);
                });
                blist.add(b);
                window.addChild(b);
            });

            confirm = new Button("Confirm");
            confirm.addClickCommands(command ->{
                //Adds them in order if they have not been chosen yet
                blist.forEach(button -> {
                    if(window.hasChild(button)){
                        button.click();
                    }
                });
                setEnabled(false);
                blist.clear();
                int s = orderedCards.size();
                for(int i = 0; i < s; i++){
                    cardRepository.getInfectionDeck().add(orderedCards.pop());
                }
                window.removeChild(confirm);
            });
            window.addChild(confirm);
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
