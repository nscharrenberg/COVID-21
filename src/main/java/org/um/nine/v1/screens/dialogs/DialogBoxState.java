package org.um.nine.v1.screens.dialogs;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import org.um.nine.v1.Game;
import org.um.nine.v1.utils.Util;

public class DialogBoxState extends BaseAppState {
    private Container window;

    private final String description;

    public DialogBoxState(String description) {
        this.description = description;
    }

    @Override
    protected void initialize(Application application) {
        window = new Container();

        window.setBackground(new QuadBackgroundComponent(ColorRGBA.White));

        Label descriptionText = window.addChild(new Label(this.description), 1, 0);
        descriptionText.setInsets(new Insets3f(10, 10, 0, 10));
        descriptionText.setColor(ColorRGBA.Red);

        Button menuButton = window.addChild(new Button("Close"));
        menuButton.addClickCommands(button -> setEnabled(false));
        menuButton.setInsets(new Insets3f(10, 10, 10, 10));

        window.addChild(descriptionText);
        window.addChild(menuButton);

        Vector3f size = Util.calculateMenusize(application, window);
        size.addLocal(0, 0, 100);
        window.setLocalTranslation(size);
        window.setLocalScale(Util.getStandardScale(window));
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
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
