package org.um.nine.repositories.local;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture2D;
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
        settings.setResolution(1920, 1080);
        settings.setSamples(16);
        settings.setVSync(false);

        // Allow for touch screen devices
        settings.setEmulateMouse(true);

        app.setSettings(settings);

        app.start();
    }

    @Override
    public void create() {
        Box b = new Box(200, 100, 1);
        Geometry geom = new Geometry("Box", b);

        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", app.getAssetManager().loadTexture("images/map.jpg"));
        geom.setMaterial(mat);

        app.getRootNode().attachChild(geom);

        app.getCamera().getLocation().setZ(275);
        app.getFlyByCamera().setZoomSpeed(50);
        app.getFlyByCamera().setMoveSpeed(50);
        app.getFlyByCamera().setRotationSpeed(0.3f);
    }

    @Override
    public void update() {

    }
}
