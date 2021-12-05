package org.um.nine.jme.screens;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.headless.game.GameStateFactory;
import org.um.nine.headless.game.domain.Difficulty;
import org.um.nine.headless.game.exceptions.PlayerLimitException;
import org.um.nine.jme.JmeGame;
import org.um.nine.jme.JmeMain;
import org.um.nine.jme.utils.MenuUtils;

public class ConfigurationState extends BaseAppState {
    private Container window;

    @Override
    protected void initialize(Application application) {
        window = new Container();
        GameStateFactory.getInitialState().getBoardRepository().preload();

        Label title = window.addChild(new Label("Game Configuration"), 0, 0);
        title.setFontSize(32);
        title.setFont(application.getAssetManager().loadFont("fonts/covid2.fnt"));
        title.setInsets(new Insets3f(10, 10, 0, 10));

        difficultyLevel();
        renderFields();

        Vector3f size = MenuUtils.calculateMenusize(JmeMain.getGameRepository().getApp(), window);
        size.addLocal(0, 0, 100);
        window.setLocalTranslation(size);
        window.setLocalScale(MenuUtils.getStandardScale(window));
    }

    private void renderFields() {
        startGame();
        createPlayer(0);
    }

    private void startGame() {
        Button menuButton = window.addChild(new Button("Start Game"),0, 0);
        menuButton.addClickCommands(button -> {
            if (GameStateFactory.getInitialState().getPlayerRepository().getPlayers().size() < 2) {
                DialogBoxState dialog = new DialogBoxState("The game can only start when there are at least 2 players.");
                getStateManager().attach(dialog);
                dialog.setEnabled(true);
                return;
            }

            JmeMain.getGameRepository().start();
            setEnabled(false);
        });
        menuButton.setInsets(new Insets3f(10, 10, 0, 10));
    }

    int rowCount = 1;
    int count = 1;

    private void createPlayer(int i) {
        Container subPanel = window.addChild(new Container(), rowCount, count);
        subPanel.addChild(new Label("Player " + (i+1)), 1, 0);

        Checkbox botEnabled = isBot(i);
        subPanel.addChild(botEnabled, 2, 0);

        TextField item = new TextField("Player " + i);
        item.setPreferredWidth(150);
        subPanel.addChild(item, 3, 0);

        Button addBtn = new Button("Add");
        addBtn.addClickCommands(c -> addPlayer(item.getText(), botEnabled.isChecked()));
        subPanel.addChild(addBtn, 1, 1);
        if (count == 2) {
            count = 0;
            rowCount++;
        } else {
            count++;
        }
    }

    private Checkbox isBot(int i) {
        return window.addChild(new Checkbox("is AI"));
    }

    private void addPlayer(String name, boolean isBot) {
        try {
            if (GameStateFactory.getInitialState().getPlayerRepository().getPlayers().get(name) != null) {
                DialogBoxState dialog = new DialogBoxState("The name of the player must be unique");
                getStateManager().attach(dialog);
                dialog.setEnabled(true);
                return;
            }
            //TODO: in headless players are added by default settings
            GameStateFactory.getInitialState().getPlayerRepository().createPlayer(name, isBot);

            if(GameStateFactory.getInitialState().getPlayerRepository().getPlayers().size()<4) {
                createPlayer(GameStateFactory.getInitialState().getPlayerRepository().getPlayers().size());
            }
        } catch (PlayerLimitException e) {
            DialogBoxState dialog = new DialogBoxState(e.getMessage());
            getStateManager().attach(dialog);
            dialog.setEnabled(true);
        }
    }

    private void difficultyLevel() {
        Container subPanel = window.addChild(new Container(), 0, 1);
        subPanel.addChild(new Label("Difficulty"));
        ListBox<Difficulty> item = subPanel.addChild(new ListBox<>());

        for (Difficulty difficulty : Difficulty.values()) {
            item.getModel().add(difficulty.getId(), difficulty);
        }

        subPanel.setInsets(new Insets3f(10, 10, 0, 10));
        subPanel.addChild(item);

        item.getSelectionModel().setSelection(GameStateFactory.getInitialState().getBoardRepository().getDifficulty() != null ? GameStateFactory.getInitialState().getBoardRepository().getDifficulty().getId() : Difficulty.NORMAL.getId());
        item.addClickCommands(c -> {
            GameStateFactory.getInitialState().getBoardRepository().setDifficulty(item.getSelectedItem());
        });
    }

    @Override
    protected void cleanup(Application application) {
        application.stop();
    }

    @Override
    protected void onEnable() {
        Node gui = ((JmeGame)getApplication()).getGuiNode();
        gui.attachChild(window);
        GuiGlobals.getInstance().requestFocus(window);
    }

    @Override
    protected void onDisable() {
        window.removeFromParent();
    }
}
