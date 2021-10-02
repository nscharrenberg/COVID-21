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
import com.simsilica.lemur.Label;
import org.um.nine.domain.ActionType;
import org.um.nine.domain.City;
import org.um.nine.domain.Difficulty;
import org.um.nine.domain.InfectionRateMarker;
import org.um.nine.domain.roles.RoleAction;
import org.um.nine.exceptions.*;
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
    private ICardRepository cardRepository;

    @Override
    public void preload() {
        this.difficulty = Difficulty.NORMAL;
        playerRepository.reset();
        cityRepository.preload();
        diseaseRepository.reset();
        cardRepository.reset();
    }

    @Override
    public void startGame() {
        cityRepository.renderCities();

        City atlanta = cityRepository.getCities().get("Atlanta");
        playerRepository.getPlayers().forEach((key, player) -> {
            playerRepository.assignRoleToPlayer(player);

            atlanta.addPawn(player);
            renderManager.renderPlayer(player, atlanta.getPawnPosition(player));
        });

        try {
            cityRepository.addResearchStation(atlanta, null);
        } catch (ResearchStationLimitException | CityAlreadyHasResearchStationException e) {
            e.printStackTrace();
        }

        renderBoard();

        renderCureSection();
        renderOutbreakSection();
        renderInfectionSection();

        try {
            cardRepository.buildDecks();
        } catch (NoCubesLeftException e) {
            e.printStackTrace();
        } catch (NoDiseaseOrOutbreakPossibleDueToEvent noDiseaseOrOutbreakPossibleDueToEvent) {
            noDiseaseOrOutbreakPossibleDueToEvent.printStackTrace();
        } catch (GameOverException e) {
            e.printStackTrace();
        } catch (OutbreakException e) {
            e.printStackTrace();
        }
        gameRepository.getApp().getStateManager().attach(optionHudState);
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

        int currentHeight = 0;

        for (int i = 0; i < 9; i++) {
            int currentWidth = 30;

            if ((i % 2) == 0) {
                currentWidth = 0;
            }
            renderManager.renderOutbreakStar(diseaseRepository.getOutbreakMarker().get(i), new Vector3f(currentWidth, currentHeight, 0));
            currentHeight = currentHeight - 30;
        }
    }

    private void renderInfectionSection() {
        BitmapFont myFont = gameRepository.getApp().getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        renderManager.renderText("Infection Rate",new Vector3f(-975, -75, 2),ColorRGBA.White,"infections-title-label",20,myFont);

        int currentWidth = 0;

        for (int i = 0; i < 7; i++) {
            renderManager.renderInfectionRateStar(diseaseRepository.getInfectionRate().get(i), new Vector3f(currentWidth, 0, 0));
            currentWidth = currentWidth + 30;
        }
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
    public RoleAction getSelectedRoleAction() {
        return selectedRoleAction;
    }

    @Override
    public void setSelectedRoleAction(RoleAction selectedRoleAction) {
        this.selectedRoleAction = selectedRoleAction;

        if (optionHudState != null) {
            Label tempLbl = (Label) optionHudState.getWindow().getChild("currentRoleActionNameLbl");

            if (tempLbl != null) {
                tempLbl.setText("Selected Role Action: " + selectedRoleAction);
            }
        }
    }

    @Override
    public Difficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }


    @Override
    public ActionType getSelectedPlayerAction() {
        return selectedPlayerAction;
    }

    @Override
    public void setSelectedPlayerAction(ActionType selectedPlayerAction) {
        this.selectedPlayerAction = selectedPlayerAction;

        if (optionHudState != null) {
            Label tempLbl = (Label) optionHudState.getWindow().getChild("currentActionNameLbl");

            if (tempLbl != null) {
                tempLbl.setText("Selected Action: " + selectedPlayerAction);
            }
        }
    }

    @Override
    public InfectionRateMarker getInfectionRateMarker() {
        return infectionRateMarker;
    }

}
