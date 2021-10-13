package org.um.nine.screens;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.Game;
import org.um.nine.Info;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.screens.dialogs.*;
import org.um.nine.screens.hud.*;
import org.um.nine.utils.Util;

import javax.inject.Inject;

public class MainMenuState extends BaseAppState {
    private Container window;

    private boolean heartbeat = false;

    @Inject
    private SettingsState settingsState;

    @Inject
    private ConfigurationState configurationState;

    @Inject
    private IGameRepository gameRepository;


    @Inject private DiscardCardDialog discardCardDialog;
    @Inject private DiscoverCureDialogBox discoverCureDialogBox;
    @Inject private GameEndState gameEndState;
    @Inject private ShareCityCardConfirmationDialogBox shareCityCardConfirmationDialogBox;
    @Inject private ShareCityCardDialogBox shareCityCardDialogBox;
    @Inject private TreatDiseaseDialogBox treatDiseaseDialogBox;
    @Inject private ActionState actionState;
    @Inject private ContingencyPlannerState contingencyPlannerState;
    @Inject private OptionHudState optionHudState;
    @Inject private PlayerInfoState playerInfoState;
    @Inject private RoleActionState roleActionState;
    @Inject private RuleState ruleState;
    @Inject private PauseMenu pauseMenu;

    @Override
    protected void initialize(Application application) {
        window = new Container();

        Label title = window.addChild(new Label(Info.APP_TITLE));
        title.setFontSize(32);
        title.setFont(application.getAssetManager().loadFont("fonts/covid2.fnt"));
        title.setInsets(new Insets3f(10, 10, 0, 10));

        playButton();
        settingsButton();
        quitButton(application);

        Vector3f size = Util.calculateMenusize(gameRepository.getApp(), window);
        size.addLocal(0, 0, 100);
        window.setLocalTranslation(size);
        window.setLocalScale(Util.getStandardScale(window));
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (heartbeat) {
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

    private void playButton() {
        Button menuButton = window.addChild(new Button("Play"));
        menuButton.addClickCommands(button -> {
            getStateManager().attach(configurationState);
            configurationState.setEnabled(true);
            setEnabled(false);
        });
        menuButton.setInsets(new Insets3f(10, 10, 0, 10));
    }

    private void settingsButton() {
        Button menuButton = window.addChild(new Button("Settings"));
        menuButton.addClickCommands(button -> goToSettings());
        menuButton.setInsets(new Insets3f(10, 10, 0, 10));
    }

    private void quitButton(Application application) {
        Button menuButton = window.addChild(new Button("Quit Game"));
        menuButton.addClickCommands(button -> application.stop());
        menuButton.setInsets(new Insets3f(10, 10, 10, 10));
    }

    protected void goToSettings() {
        getStateManager().attach(settingsState);
        settingsState.setEnabled(true);
        setEnabled(false);
    }

    public boolean isHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }
}
