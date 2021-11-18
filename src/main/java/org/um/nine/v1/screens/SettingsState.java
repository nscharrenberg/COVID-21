package org.um.nine.v1.screens;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Checkbox;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.*;
import org.um.nine.v1.Game;
import org.um.nine.v1.contracts.repositories.IGameRepository;
import org.um.nine.v1.domain.Resolution;
import org.um.nine.v1.domain.SAMPLING;
import org.um.nine.v1.utils.Util;

import javax.inject.Inject;
import java.awt.*;

public class SettingsState extends BaseAppState {
    private Container window;

    private boolean heartbeat = false;

    @Inject
    private MainMenuState mainMenuState;

    @Inject private IGameRepository gameRepository;

    @Override
    protected void initialize(Application application) {
        window = new Container();

        Label title = window.addChild(new Label("Settings"), 0, 0);
        title.setFontSize(32);
        title.setFont(application.getAssetManager().loadFont("fonts/covid2.fnt"));
        title.setInsets(new Insets3f(10, 10, 0, 10));

        fullscreenInput();
        gammaCorrectionInput();
        vsyncInput();
        resolutionInput();
        sampleInput();
        settingsButton();

        Vector3f size = Util.calculateMenusize(gameRepository.getApp(), window);
//        size.addLocal(0, 0, 100);
        System.out.println(size);
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

    private void settingsButton() {
        Button menuButton = window.addChild(new Button("Back to Main Menu"));
        menuButton.addClickCommands(button -> goToMainMenu());
        menuButton.setInsets(new Insets3f(10, 10, 10, 10));
    }

    private void save() {
        getApplication().getContext().restart();
    }

    private void goToMainMenu() {
        save();
        getStateManager().attach(mainMenuState);
        mainMenuState.setEnabled(true);
        setEnabled(false);
    }

    private void fullscreenInput() {
        Checkbox item = window.addChild(new Checkbox("Enable Fullscreen"), 1, 0);
        item.getModel().setChecked(getApplication().getContext().getSettings().isFullscreen());
        item.addClickCommands(button -> {
            getApplication().getContext().getSettings().setFullscreen(!getApplication().getContext().getSettings().isFullscreen());
            item.getModel().setChecked(getApplication().getContext().getSettings().isFullscreen());
        });
        item.setInsets(new Insets3f(0, 10, 0, 10));
    }

    private void gammaCorrectionInput() {
        Checkbox item = window.addChild(new Checkbox("Enable Gamma Correction"), 1, 1);
        item.getModel().setChecked(getApplication().getContext().getSettings().isGammaCorrection());
        item.addClickCommands(button -> {
            getApplication().getContext().getSettings().setGammaCorrection(!getApplication().getContext().getSettings().isGammaCorrection());
            item.getModel().setChecked(getApplication().getContext().getSettings().isGammaCorrection());
        });
        item.setInsets(new Insets3f(0, 10, 0, 10));
    }

    private void vsyncInput() {
        Checkbox item = window.addChild(new Checkbox("Enable VSync"), 1, 2);
        item.getModel().setChecked(getApplication().getContext().getSettings().isVSync());
        item.addClickCommands(button -> {
            getApplication().getContext().getSettings().setVSync(!getApplication().getContext().getSettings().isVSync());
            item.getModel().setChecked(getApplication().getContext().getSettings().isVSync());
        });
        item.setInsets(new Insets3f(0, 10, 0, 10));
    }

    private void resolutionInput() {
        Container subPanel = window.addChild(new Container(), 2, 0);
        subPanel.addChild(new Label("Resolution"));
        ListBox<Resolution> item = subPanel.addChild(new ListBox<>());

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        int index = 0;
        for(DisplayMode mode : gd.getDisplayModes()) {
            Resolution res = new Resolution(index, mode.getWidth(), mode.getHeight());
            item.getModel().add(index, res);
            index++;

            if (mode.getHeight() == getApplication().getContext().getSettings().getHeight() &&
                    mode.getWidth() == getApplication().getContext().getSettings().getWidth()) {
                item.getSelectionModel().setSelection(res.getId());
            }
        }

        if (item.getSelectedItem() == null) {
            item.getSelectionModel().setSelection(0);
        }

        item.addClickCommands(listBox -> {
            getApplication().getContext().getSettings().setWidth(item.getSelectedItem().getWidth());
            getApplication().getContext().getSettings().setHeight(item.getSelectedItem().getHeight());
        });

        subPanel.setInsets(new Insets3f(10, 10, 0, 10));
    }

    private void sampleInput() {
        Container subPanel = window.addChild(new Container(), 2, 1);
        subPanel.addChild(new Label("Anti-Alias"));
        ListBox<SAMPLING> item = subPanel.addChild(new ListBox<>());
        // TODO: Just Iterate over the enum instead of manually doing it.
        item.getModel().add(SAMPLING.DISABLED.getId(), SAMPLING.DISABLED);
        item.getModel().add(SAMPLING.X2.getId(), SAMPLING.X2);
        item.getModel().add(SAMPLING.X4.getId(), SAMPLING.X4);
        item.getModel().add(SAMPLING.X6.getId(), SAMPLING.X6);
        item.getModel().add(SAMPLING.X8.getId(), SAMPLING.X8);
        item.getModel().add(SAMPLING.X16.getId(), SAMPLING.X16);

        SAMPLING selected = SAMPLING.findByValue(getApplication().getContext().getSettings().getSamples());
        item.getSelectionModel().setSelection(selected != null ? selected.getId() : SAMPLING.X4.getId());
        item.addClickCommands(listBox -> {
            getApplication().getContext().getSettings().setSamples(item.getSelectedItem().getValue());
        });
        subPanel.setInsets(new Insets3f(10, 10, 0, 10));
    }

    public boolean isHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }
}
