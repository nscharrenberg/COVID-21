package org.um.nine.repositories.local;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import org.um.nine.Game;
import org.um.nine.Info;
import org.um.nine.contracts.repositories.IGameRepository;

public class GameRepository implements IGameRepository {
    private final Game app;
    private final AppSettings settings = new AppSettings(true);

    public GameRepository() {
        this.app = new Game();
    }

    @Override
    public void init() {
        settings.setTitle(Info.APP_TITLE);
        settings.setFrameRate(60);

        // Set the splash screen image
        settings.setSettingsDialogImage("images/image.jpg");

        // Allow for touch screen devices
        settings.setEmulateMouse(true);

        app.setSettings(settings);

        app.start();
    }

    @Override
    public void create() {
        Box b = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", b);

        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);

        app.getRootNode().attachChild(geom);
    }

    @Override
    public void update() {

    }
}
