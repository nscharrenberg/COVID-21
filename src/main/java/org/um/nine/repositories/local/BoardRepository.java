package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Line;
import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.domain.City;

import java.util.ArrayList;
import java.util.List;

public class BoardRepository implements IBoardRepository {
    private Geometry board;
    private List<City> cities;

    public BoardRepository() {
        this.cities = new ArrayList<>();

        // TODO: Utilize a JSON file to import all the cities and it's locations.

        City atlanta = new City("Atlanta", ColorRGBA.Blue, new Vector3f(-480, 192, 1));
        City chicago = new City("Chicago", ColorRGBA.Blue, new Vector3f(-500, 250, 1));
        atlanta.addNeighbour(chicago);

        this.cities.add(atlanta);
        this.cities.add(chicago);
    }

    @Inject
    private IGameRepository gameRepository;


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
        this.cities.forEach(city -> {
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
}
