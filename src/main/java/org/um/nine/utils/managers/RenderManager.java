package org.um.nine.utils.managers;

import com.google.inject.Inject;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Line;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.domain.City;
import org.um.nine.domain.Disease;
import org.um.nine.domain.Player;
import org.um.nine.domain.ResearchStation;

public class RenderManager {
    @Inject
    private IGameRepository gameRepository;

    public void renderPlayer(Player player) {
        float offsetX = 20;
        float offsetY = 10;

        Spatial model = gameRepository.getApp().getAssetManager().loadModel("models/pawn.j3o");
        model.rotate(45, 0, 0);
        model.setLocalTranslation(new Vector3f(player.getCity().getLocation().getX() + offsetX, player.getCity().getLocation().getY() + offsetY, player.getCity().getLocation().getZ() + 1));
        model.setLocalScale(.75f);
        Material mat = new Material(gameRepository.getApp().getAssetManager(),
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse", player.getRole().getColor() );
        mat.setColor("Ambient", player.getRole().getColor() );
        model.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(model);
    }

    public void renderResearchStation(ResearchStation researchStation) {
        float offsetX = -20;
        float offsetY = 5;

        Spatial model = gameRepository.getApp().getAssetManager().loadModel("models/research_station.j3o");
        model.setLocalScale(1);
        model.setLocalTranslation(new Vector3f(researchStation.getCity().getLocation().getX() + offsetX, researchStation.getCity().getLocation().getY() + offsetY, researchStation.getCity().getLocation().getZ() + 5));
        model.rotate(45, 75, 0);
        Material mat = new Material(gameRepository.getApp().getAssetManager(),
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse", ColorRGBA.White ); // with Lighting.j3md
        mat.setColor("Ambient", ColorRGBA.White ); // with Lighting.j3md
        model.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(model);
    }

    public void renderCity(City city) {
        Cylinder plateShape = new Cylinder(5, 10, 12.5f, 2, true);
        Geometry plate = new Geometry(city.getName(), plateShape);
        Material mat = new Material(gameRepository.getApp().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", city.getColor());
        mat.setColor("GlowColor", city.getColor());
        plate.setMaterial(mat);
        plate.setLocalTranslation(city.getLocation());
        gameRepository.getApp().getRootNode().attachChild(plate);
    }

    public void renderEdge(City from, City to) {
        Line lineShape = new Line(from.getLocation(), to.getLocation());
        Geometry plate = new Geometry(from.getName() + "->" + to.getName(), lineShape);
        Material mat = new Material(gameRepository.getApp().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        mat.setColor("GlowColor", ColorRGBA.White);
        mat.getAdditionalRenderState().setLineWidth(1);
        mat.getAdditionalRenderState().setWireframe(true);
        plate.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(plate);
    }

    public void renderDisease(Disease disease) {
        float offsetX = -15;
        float offsetY = -15;

        Spatial model = gameRepository.getApp().getAssetManager().loadModel("models/cube.j3o");
        model.rotate(45, 0, 0);
        model.setLocalTranslation(new Vector3f(disease.getCity().getLocation().getX() + offsetX, disease.getCity().getLocation().getY() + offsetY, disease.getCity().getLocation().getZ() + 1));
        model.setLocalScale(1.25f);
        Material mat = new Material(gameRepository.getApp().getAssetManager(),
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse", disease.getColor() );
        mat.setColor("Ambient", disease.getColor() );
        model.setMaterial(mat);
        gameRepository.getApp().getRootNode().attachChild(model);
    }
}
