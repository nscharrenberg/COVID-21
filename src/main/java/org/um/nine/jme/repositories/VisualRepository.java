package org.um.nine.jme.repositories;

import com.jme3.font.BitmapFont;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import org.um.nine.headless.agents.rhea.state.GameStateFactory;
import org.um.nine.headless.game.domain.*;
import org.um.nine.jme.JmeMain;
import org.um.nine.jme.utils.JmeFactory;
import org.um.nine.jme.utils.managers.RenderManager;

public class VisualRepository {
    private Geometry board;

    private final RenderManager renderManager = new RenderManager();

    private CityRepository cityRepository = JmeFactory.getCityRepository();

    public void renderCureSection() {
        renderManager.renderCureMarker(GameStateFactory.getInitialState().getDiseaseRepository().getCures().get(Color.RED), new Vector3f(100, 0, 0));
        renderManager.renderCureMarker(GameStateFactory.getInitialState().getDiseaseRepository().getCures().get(Color.YELLOW), new Vector3f(50, 0, 0));
        renderManager.renderCureMarker(GameStateFactory.getInitialState().getDiseaseRepository().getCures().get(Color.BLUE), new Vector3f(0, 0, 0));
        renderManager.renderCureMarker(GameStateFactory.getInitialState().getDiseaseRepository().getCures().get(Color.BLACK), new Vector3f(-50, 0, 0));
    }

    public void renderOutbreakSection() {
        BitmapFont myFont = JmeMain.getGameRepository().getApp().getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        renderManager.renderText("Outbreaks",new Vector3f(-975, -175, 2), ColorRGBA.White,"outbreaks-title-label",20,myFont);

        int currentHeight = 0;

        for (int i = 0; i < 9; i++) {
            int currentWidth = 30;

            if ((i % 2) == 0) {
                currentWidth = 0;
            }
            renderManager.renderOutbreakStar(GameStateFactory.getInitialState().getDiseaseRepository().getOutbreakMarkers().get(i), new Vector3f(currentWidth, currentHeight, 0));
            currentHeight = currentHeight - 30;
        }
    }

    public void renderInfectionSection() {
        BitmapFont myFont = JmeMain.getGameRepository().getApp().getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        renderManager.renderText("Infection Rate",new Vector3f(-975, -75, 2),ColorRGBA.White,"infections-title-label",20,myFont);

        int currentWidth = 0;

        for (int i = 0; i < 7; i++) {
            renderManager.renderInfectionRateStar(GameStateFactory.getInitialState().getDiseaseRepository().getInfectionRates().get(i), new Vector3f(currentWidth, 0, 0));
            currentWidth = currentWidth + 30;
        }
    }

    public Geometry getBoard() {
        if (board == null) {
            JmeMain.getGameRepository().getApp().getRootNode().detachAllChildren();
            renderBoard();
        }

        return board;
    }

    public void setBoard(Geometry board) {
        this.board = board;
    }

    public void renderBoard() {
        Box worldBox = new Box(1000, 500, 1);
        board = new Geometry("World", worldBox);
        Material mat = new Material(JmeMain.getGameRepository().getApp().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", JmeMain.getGameRepository().getApp().getAssetManager().loadTexture("images/map.jpg"));
        mat.setTexture("NormalMap", JmeMain.getGameRepository().getApp().getAssetManager().loadTexture("images/map_normal.png"));
        board.setMaterial(mat);

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(3f));
        board.addLight(al);
        JmeMain.getGameRepository().getApp().getRootNode().attachChild(board);
    }

    public void renderCities() {
        cityRepository.getCities().forEach((key, city) -> {
            city.getNeighbors().forEach(neighbor -> renderManager.renderEdge(city, neighbor));
            renderManager.renderCity(city);
        });
    }

    public void renderResearchStation(ResearchStation researchStation, Vector3f position){
        renderManager.renderResearchStation(researchStation,position);
    }

    public void renderOutbreakMarker(OutbreakMarker status) {
        renderManager.renderOutbreakMarker(status);
    }

    public void renderInfectionMarker(InfectionRateMarker status) {
        renderManager.renderInfectionMarker(status);
    }

    public void renderPlayer(Player player, Vector3f vector3f){
        renderManager.renderPlayer(player, vector3f);
    }

    public void renderDisease(Disease disease, Vector3f offset) {
        renderManager.renderDisease(disease,offset);
    }

    public void reset(){
        cityRepository = JmeFactory.getCityRepository();
    }
}
