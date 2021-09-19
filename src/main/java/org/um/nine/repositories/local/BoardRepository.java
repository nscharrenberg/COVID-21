package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Line;
import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.domain.City;
import org.um.nine.domain.InfectionRateMarker;
import org.um.nine.domain.Marker;

import java.util.ArrayList;
import java.util.List;

public class BoardRepository implements IBoardRepository {
    private Geometry board;
    private List<InfectionRateMarker> infectionRate;
    private List<Marker> outbreakMarker;

    public BoardRepository() {
        this.infectionRate = new ArrayList<>();
        this.outbreakMarker = new ArrayList<>();

        initMarkers();
    }

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private ICityRepository cityRepository;

    @Override
    public void startGame() {
        renderBoard();
        renderCities();
    }

    @Override
    public Geometry getBoard() {
        if (board == null) {
            gameRepository.getApp().getRootNode().detachAllChildren();
            renderBoard();
        }

        return this.board;
    }

    private void renderBoard() {
        Box worldBox = new Box(1000, 500, 1);
        board = new Geometry("World", worldBox);
        Material mat = new Material(gameRepository.getApp().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", gameRepository.getApp().getAssetManager().loadTexture("images/map.jpg"));
        mat.setTexture("NormalMap", gameRepository.getApp().getAssetManager().loadTexture("images/map_normal.png"));
        board.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(board);
    }

    private void renderCities() {
        cityRepository.getCities().forEach(city -> {
            city.getNeighbors().forEach(neighbor -> renderEdge(city, neighbor));

            renderCity(city);
        });
    }

    private void renderCity(City city) {
        Cylinder plateShape = new Cylinder(5, 10, 12.5f, 2, true);
        Geometry plate = new Geometry(city.getName(), plateShape);
        Material mat = new Material(gameRepository.getApp().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", city.getColor());
        mat.setColor("GlowColor", city.getColor());
        gameRepository.refreshFpp();
        plate.setMaterial(mat);
        plate.setLocalTranslation(city.getLocation());
        gameRepository.getApp().getRootNode().attachChild(plate);
    }

    private void renderEdge(City city1, City city2) {
        Line lineShape = new Line(city1.getLocation(), city2.getLocation());
        Geometry plate = new Geometry(city1.getName() + "->" + city2.getName(), lineShape);
        Material mat = new Material(gameRepository.getApp().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        mat.setColor("GlowColor", ColorRGBA.White);
        mat.getAdditionalRenderState().setLineWidth(1);
        mat.getAdditionalRenderState().setWireframe(true);
        plate.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(plate);
    }

    private void initMarkers() {
        this.infectionRate.add(new InfectionRateMarker(0, 2, true));
        this.infectionRate.add(new InfectionRateMarker(1, 2));
        this.infectionRate.add(new InfectionRateMarker(2, 2));
        this.infectionRate.add(new InfectionRateMarker(3, 3));
        this.infectionRate.add(new InfectionRateMarker(4, 3));
        this.infectionRate.add(new InfectionRateMarker(5, 4));
        this.infectionRate.add(new InfectionRateMarker(6, 4));

        this.outbreakMarker.add(new Marker(0, true));

        for (int i = 1; i <= 8; i++) {
            this.outbreakMarker.add(new Marker(i));
        }
    }
}
