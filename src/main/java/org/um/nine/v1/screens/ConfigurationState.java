package org.um.nine.v1.screens;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.v1.Game;
import org.um.nine.v1.contracts.repositories.IBoardRepository;
import org.um.nine.v1.contracts.repositories.IGameRepository;
import org.um.nine.v1.contracts.repositories.IPlayerRepository;
import org.um.nine.v1.domain.Difficulty;
import org.um.nine.v1.domain.Player;
import org.um.nine.v1.exceptions.PlayerLimitException;
import org.um.nine.jme.screens.DialogBoxState;
import org.um.nine.v1.utils.Util;

import javax.inject.Inject;

public class ConfigurationState extends BaseAppState {
    private Container window;

    private boolean heartbeat = false;

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private IPlayerRepository playerRepository;

    @Inject
    private IBoardRepository boardRepository;

    public float getStandardScale() {
        int height = getApplication().getCamera().getHeight();
        return height / 720f;
    }

    @Override
    protected void initialize(Application application) {
        boardRepository.preload();
        window = new Container();

        Label title = window.addChild(new Label("Game Configuration"), 0, 0);
        title.setFontSize(32);
        title.setFont(application.getAssetManager().loadFont("fonts/covid2.fnt"));
        title.setInsets(new Insets3f(10, 10, 0, 10));

        difficultyLevel();
        renderFields();

        Vector3f size = Util.calculateMenusize(gameRepository.getApp(), window);
        size.addLocal(0, 0, 100);
        window.setLocalTranslation(size);
        window.setLocalScale(Util.getStandardScale(window));
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (heartbeat) {
            this.setEnabled(false);
            initialize(gameRepository.getApp());

            this.heartbeat = false;
        }
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

    private void renderFields() {
        startGame();
        createPlayer(0);
    }

    private void startGame() {
        Button menuButton = window.addChild(new Button("Start Game"),0, 0);
        menuButton.addClickCommands(button -> {
            if (playerRepository.getPlayers().size() < 2) {
                DialogBoxState dialog = new DialogBoxState("The game can only start when there are at least 2 players.");
                getStateManager().attach(dialog);
                dialog.setEnabled(true);
                return;
            }

            gameRepository.start();
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
        Checkbox item = window.addChild(new Checkbox("is AI"));
        return item;
    }

    private void addPlayer(String name, boolean isBot) {
        try {
            if (playerRepository.getPlayers().get(name) != null) {
                DialogBoxState dialog = new DialogBoxState("The name of the player must be unique");
                getStateManager().attach(dialog);
                dialog.setEnabled(true);
                return;
            }

            playerRepository.addPlayer(new Player(name, isBot));

            if(playerRepository.getPlayers().size()<4) {
                createPlayer(playerRepository.getPlayers().size());
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

        item.getSelectionModel().setSelection(boardRepository.getDifficulty() != null ? boardRepository.getDifficulty().getId() : Difficulty.NORMAL.getId());
        item.addClickCommands(c -> {
            boardRepository.setDifficulty(item.getSelectedItem());
        });
    }

    public boolean isHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }
}
