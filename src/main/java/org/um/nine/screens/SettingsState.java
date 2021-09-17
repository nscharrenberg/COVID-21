package org.um.nine.screens;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Checkbox;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import org.checkerframework.checker.index.qual.IndexFor;
import org.um.nine.Game;
import org.um.nine.domain.Resolution;
import org.um.nine.domain.SAMPLING;

import javax.inject.Inject;
import java.awt.*;

public class SettingsState extends BaseAppState {
    private Container window;

    @Inject
    private MainMenuState mainMenuState;

    public float getStandardScale() {
        int height = getApplication().getCamera().getHeight();
        return height / 720f;
    }

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

        int height = application.getCamera().getHeight();
        Vector3f pref = window.getPreferredSize().clone();

        float standardScale = getStandardScale();
        pref.multLocal(1.5f * standardScale);

        float y = height * 0.6f + pref.y * 0.5f;

        window.setLocalTranslation(100 * standardScale, y, 0);
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
            getApplication().getContext().getSettings().setResolution(item.getSelectedItem().getHeight(), item.getSelectedItem().getHeight());
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
}
