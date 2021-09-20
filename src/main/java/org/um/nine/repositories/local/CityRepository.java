package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Line;
import com.jme3.util.BufferUtils;
import org.um.nine.Info;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.City;
import org.um.nine.domain.Player;
import org.um.nine.domain.ResearchStation;
import org.um.nine.exceptions.CityAlreadyHasResearchStationException;
import org.um.nine.exceptions.ResearchStationLimitException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CityRepository implements ICityRepository {
    private HashMap<String, City> cities;
    private List<ResearchStation> researchStations;

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private IPlayerRepository playerRepository;

    @Override
    public HashMap<String, City> getCities() {
        return cities;
    }

    @Override
    public void addResearchStation(City city) throws ResearchStationLimitException, CityAlreadyHasResearchStationException {
        if (city.getResearchStation() != null) {
            throw new CityAlreadyHasResearchStationException();
        }

        if ((researchStations.size() + 1) > Info.RESEARCH_STATION_THRESHOLD) {
            throw new ResearchStationLimitException();
        }

        this.researchStations.add(new ResearchStation(city));

        renderResearchStation(city);
    }

    @Override
    public void reset() {
        this.researchStations = new ArrayList<>();
        this.cities = new HashMap<>();

        // TODO: Utilize a JSON file to import all the cities and it's locations.

        City atlanta = new City("Atlanta", ColorRGBA.Blue, new Vector3f(-480, 182, 1));
        City chicago = new City("Chicago", ColorRGBA.Blue, new Vector3f(-500, 240, 1));
        atlanta.addNeighbour(chicago);

        this.cities.put(atlanta.getName(), atlanta);
        this.cities.put(chicago.getName(), chicago);

        renderCities();

        try {
            addResearchStation(atlanta);
        } catch (ResearchStationLimitException e) {
            e.printStackTrace();
        } catch (CityAlreadyHasResearchStationException e) {
            e.printStackTrace();
        }

        playerRepository.reset();
    }


    public void renderResearchStation(City city) {
        float offsetX = -20;
        float offsetY = 5;

        Spatial model = gameRepository.getApp().getAssetManager().loadModel("models/research_station.j3o");
        model.setLocalScale(1);
        model.setLocalTranslation(new Vector3f(city.getLocation().getX() + offsetX, city.getLocation().getY() + offsetY, city.getLocation().getZ() + 5));
        model.rotate(45, 75, 0);
        Material mat = new Material(gameRepository.getApp().getAssetManager(),
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse", ColorRGBA.White ); // with Lighting.j3md
        mat.setColor("Ambient", ColorRGBA.White ); // with Lighting.j3md
        model.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(model);
    }

    public void renderResearchStationOld(City city) {
        Mesh mesh = new Mesh();
        float size = 10;
        float offsetX = 2.5f;
        float offsetY = 5;

        Vector3f[] vertices = new Vector3f[5];
        vertices[0] = new Vector3f(city.getLocation().getX() + offsetX, city.getLocation().getY() + offsetY, size);
        vertices[1] = new Vector3f(city.getLocation().getX() + size + offsetX,city.getLocation().getY() + offsetY,size);
        vertices[2] = new Vector3f(city.getLocation().getX() + offsetX,city.getLocation().getY() + size + offsetY,size);
        vertices[3] = new Vector3f(city.getLocation().getX() + size + offsetX, city.getLocation().getY() + size + offsetY,size);
        vertices[4] = new Vector3f(city.getLocation().getX() + (size/2) + offsetX, city.getLocation().getY() + (size + (size/2)) + offsetY,size);

        Vector2f[] texCoord = new Vector2f[4];
        texCoord[0] = new Vector2f(0,0);
        texCoord[1] = new Vector2f(1,0);
        texCoord[2] = new Vector2f(0,1);
        texCoord[3] = new Vector2f(1,1);

        int [] indexes = { 2,0,1, 1,3,2, 2,3,4 };

        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        mesh.setBuffer(VertexBuffer.Type.Index,    3, BufferUtils.createIntBuffer(indexes));
        mesh.updateBound();

        Geometry geo = new Geometry("House", mesh);
        Material mat = new Material(gameRepository.getApp().getAssetManager(),
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        geo.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(geo);
    }

    @Override
    public void renderCities() {
        getCities().forEach((key, city) -> {
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
