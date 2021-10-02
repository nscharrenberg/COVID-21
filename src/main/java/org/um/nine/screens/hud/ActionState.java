package org.um.nine.screens.hud;

import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import org.um.nine.Game;
import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.ActionType;
import org.um.nine.domain.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class ActionState extends BaseAppState  {
    private Container window;

    @Inject
    private IPlayerRepository playerRepository;

    @Inject
    private IBoardRepository boardRepository;

    private float getStandardScale() {
        int height = getApplication().getCamera().getHeight();
        return height / 720f;
    }

    @Override
    protected void initialize(Application application) {
        window = new Container();

        Label title = window.addChild(new Label("Actions"));
        title.setFontSize(16);
        title.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(title, 0, 0);

        Button closeBtn = new Button("Close");
        closeBtn.addClickCommands(c -> {
            this.setEnabled(false);
        });
        closeBtn.setInsets(new Insets3f(10, 10, 0, 10));
        window.addChild(closeBtn, 0, 1);

        for (ActionType type : ActionType.values()) {
            Button button = new Button(type.getDescription());
            button.setInsets(new Insets3f(10, 10, 0, 10));

            button.addClickCommands(c -> {
                boardRepository.setSelectedPlayerAction(type);
                setEnabled(false);
            });

            window.addChild(button, 1, 0);
        }

        window.setLocalTranslation(getApplication().getCamera().getWidth() / 2f, getApplication().getCamera().getHeight() / 2f, 5);
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