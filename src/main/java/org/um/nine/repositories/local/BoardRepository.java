package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.font.BitmapFont;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import org.um.nine.contracts.repositories.*;
import org.um.nine.domain.City;
import org.um.nine.domain.Difficulty;
import org.um.nine.domain.InfectionRateMarker;
import org.um.nine.domain.roles.RoleAction;
import org.um.nine.screens.hud.OptionHudState;
import org.um.nine.utils.managers.RenderManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BoardRepository implements IBoardRepository {
    private Geometry board;
    private City selectedCity;
    private RoleAction selectedAction = null;
    private List<RoleAction> usedActions = new ArrayList<>();
    private Difficulty difficulty;
    private InfectionRateMarker infectionRateMarker;
    private String cityCardsJSONPath = new File("").getAbsolutePath() +"src/main/resources/Cards/CityCards.json";

    private int count;

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private ICityRepository cityRepository;

    @Inject
    private IPlayerRepository playerRepository;

    @Inject
    private IDiseaseRepository diseaseRepository;

    @Inject
    private RenderManager renderManager;

    @Inject
    private OptionHudState optionHudState;

    @Inject
    private CardRepository cardRepository;

    @Override
    public void preload() {
        this.difficulty = Difficulty.NORMAL;
        playerRepository.reset();
        cityRepository.preload();
        diseaseRepository.reset();
    }

    @Override
    public void startGame() {
        renderBoard();
        //TODO: Init Game:
        //initialise city list
        cityRepository.reset();
        //Initialise outbreak and Infection markers
        //TODO: initialise outbreak marker
        infectionRateMarker = new InfectionRateMarker(1,0,true);
        //Initialise cure pieces
        diseaseRepository.reset();
        playerRepository.reset();

        renderCureSection();
        renderOutbreakSection();
        renderInfectionSection();

        gameRepository.getApp().getStateManager().attach(optionHudState);
        cardRepository.buildDecks();

    }



    private void renderCureSection() {
        renderManager.renderCureMarker(diseaseRepository.getCures().get(ColorRGBA.Red), new Vector3f(100, 0, 0), true);
        renderManager.renderCureMarker(diseaseRepository.getCures().get(ColorRGBA.Yellow), new Vector3f(50, 0, 0));
        renderManager.renderCureMarker(diseaseRepository.getCures().get(ColorRGBA.Blue), new Vector3f(0, 0, 0));
        renderManager.renderCureMarker(diseaseRepository.getCures().get(ColorRGBA.Black), new Vector3f(-50, 0, 0));
    }

    private void renderOutbreakSection() {
        BitmapFont myFont = gameRepository.getApp().getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        renderManager.renderText("Outbreaks",new Vector3f(-975, -175, 2),ColorRGBA.White,"outbreaks-title-label",20,myFont);
        renderManager.renderOutbreakStar(diseaseRepository.getOutbreakMarker().get(0), new Vector3f(0, 0, 0));
        renderManager.renderOutbreakStar(diseaseRepository.getOutbreakMarker().get(1), new Vector3f(30, -30, 0));
        renderManager.renderOutbreakStar(diseaseRepository.getOutbreakMarker().get(2), new Vector3f(0, -60, 0));
        renderManager.renderOutbreakStar(diseaseRepository.getOutbreakMarker().get(3), new Vector3f(30, -90, 0));
        renderManager.renderOutbreakStar(diseaseRepository.getOutbreakMarker().get(4), new Vector3f(0, -120, 0));
        renderManager.renderOutbreakStar(diseaseRepository.getOutbreakMarker().get(5), new Vector3f(30, -150, 0));
        renderManager.renderOutbreakStar(diseaseRepository.getOutbreakMarker().get(6), new Vector3f(0, -180, 0));
        renderManager.renderOutbreakStar(diseaseRepository.getOutbreakMarker().get(7), new Vector3f(30, -210, 0));
        renderManager.renderOutbreakStar(diseaseRepository.getOutbreakMarker().get(8), new Vector3f(0, -240, 0));
    }

    // TODO: Properly add them to a list so we can keep track of infection rate markers and their states.
    private void renderInfectionSection() {
        BitmapFont myFont = gameRepository.getApp().getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        renderManager.renderText("Infection Rate",new Vector3f(-975, -75, 2),ColorRGBA.White,"infections-title-label",20,myFont);
        renderManager.renderInfectionRateStar(diseaseRepository.getInfectionRate().get(0), new Vector3f(0, 0, 0));
        renderManager.renderInfectionRateStar(diseaseRepository.getInfectionRate().get(1), new Vector3f(30, 0, 0));
        renderManager.renderInfectionRateStar(diseaseRepository.getInfectionRate().get(2), new Vector3f(60, 0, 0));
        renderManager.renderInfectionRateStar(diseaseRepository.getInfectionRate().get(3), new Vector3f(90, 0, 0));
        renderManager.renderInfectionRateStar(diseaseRepository.getInfectionRate().get(4), new Vector3f(120, 0, 0));
        renderManager.renderInfectionRateStar(diseaseRepository.getInfectionRate().get(5), new Vector3f(150, 0, 0));
        renderManager.renderInfectionRateStar(diseaseRepository.getInfectionRate().get(6), new Vector3f(180, 0, 0));
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

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(3f));
        board.addLight(al);
        gameRepository.getApp().getRootNode().attachChild(board);
    }

    @Override
    public City getSelectedCity() {
        return selectedCity;
    }

    @Override
    public void setSelectedCity(City selectedCity) {
        this.selectedCity = selectedCity;

        String textName = "selected-city-text";

        renderManager.renderText(selectedCity != null ? selectedCity.getName() : "Nothing Selected", new Vector3f(0, 0, 5), ColorRGBA.White, textName);
    }

    @Override
    public List<RoleAction> getUsedActions() {
        return usedActions;
    }

    @Override
    public void setUsedActions(List<RoleAction> usedActions) {
        this.usedActions = usedActions;
    }

    @Override
    public RoleAction getSelectedAction() {
        return selectedAction;
    }

    @Override
    public void setSelectedAction(RoleAction selectedAction) {
        this.selectedAction = selectedAction;
    }

    @Override
    public Difficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
