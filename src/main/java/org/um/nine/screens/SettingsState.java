package org.um.nine.screens;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import org.um.nine.Game;
import org.um.nine.Main;
import org.um.nine.domain.RESOLUTION;
import org.um.nine.domain.SAMPLING;

public class SettingsState extends BaseAppState {
    private Container window;

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
        getStateManager().attach(new MainMenuState());
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
        ListBox<RESOLUTION> item = subPanel.addChild(new ListBox<>());
        // TODO: Just Iterate over the enum instead of manually doing it.
        item.getModel().add(RESOLUTION.RES_4K.getId(), RESOLUTION.RES_4K);
        item.getModel().add(RESOLUTION.RES_3K.getId(), RESOLUTION.RES_3K);
        item.getModel().add(RESOLUTION.RES_2K.getId(), RESOLUTION.RES_2K);
        item.getModel().add(RESOLUTION.RES_1080.getId(), RESOLUTION.RES_1080);
        item.getModel().add(RESOLUTION.RES_1360.getId(), RESOLUTION.RES_1360);
        item.getModel().add(RESOLUTION.RES_720.getId(), RESOLUTION.RES_720);
        item.getModel().add(RESOLUTION.RES_480.getId(), RESOLUTION.RES_480);

        RESOLUTION selected = RESOLUTION.findByResolution(getApplication().getContext().getSettings().getWidth(), getApplication().getContext().getSettings().getHeight());
        item.getSelectionModel().setSelection(selected != null ? selected.getId() : RESOLUTION.RES_1080.getId());
        item.addClickCommands(listBox -> {
            getApplication().getContext().getSettings().setHeight(item.getSelectedItem().getHeight());
            getApplication().getContext().getSettings().setWidth(item.getSelectedItem().getWidth());
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
