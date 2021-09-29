package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import org.um.nine.contracts.repositories.*;
import org.um.nine.domain.*;
import org.um.nine.domain.cards.InfectionCard;
import org.um.nine.domain.roles.GenericRole;
import org.um.nine.utils.cardmanaging.CityCardReader;
import org.um.nine.utils.cardmanaging.Shuffle;
import org.um.nine.utils.managers.RenderManager;
import org.um.nine.exceptions.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BoardRepository implements IBoardRepository {
    private Geometry board;
    private City selectedCity;
    private InfectionRateMarker infectionRateMarker;
    private Stack<InfectionCard> infectionDeck;
    private Stack<Card> infectionDiscardPile;
    private String cityCardsJSONPath = new File("").getAbsolutePath() +"src/main/resources/Cards/CityCards.json";


    @Inject
    private IGameRepository gameRepository;

    @Inject
    private ICityRepository cityRepository;

    @Inject
    private IPlayerRepository playerRepository;

    @Inject
    private DiseaseRepository diseaseRepository ;

    @Inject
    private RenderManager renderManager;

    @Override
    public void startGame() {
        renderBoard();
        //TODO: Init Game:
        //initialise city list
        cityRepository.reset();

        //create Atlanta research station
        City atlanta = cityRepository.getCities().get("Atlanta");
        try {
            cityRepository.addResearchStation(atlanta);
        } catch (ResearchStationLimitException e) {
            e.printStackTrace();
        } catch (CityAlreadyHasResearchStationException e) {
            e.printStackTrace();
        }
        //Initialise outbreak and Infection markers
        infectionRateMarker = new InfectionRateMarker(1,0,true);
        //Initialise cure pieces
        renderManager.renderCureMarker(new Cure(ColorRGBA.Red), new Vector3f(200, 0, 0), true);
        renderManager.renderCureMarker(new Cure(ColorRGBA.Yellow), new Vector3f(100, 0, 0));
        renderManager.renderCureMarker(new Cure(ColorRGBA.Cyan), new Vector3f(0, 0, 0));
        renderManager.renderCureMarker(new Cure(ColorRGBA.Magenta), new Vector3f(-100, 0, 0));

        //Set up infection deck
        infectionDeck = CityCardReader.generateInfectionDeck(cityRepository.getCities().values().toArray(new City[0]));
        //Shuffle.shuffle(infectionDeck);

        //Set initial infection:
        //draw 3 cards 3 cubes, 3 cards 2 cubes, 3 cards 1 cube
        //and place cards on infection discard pile
        for(int i = 3;i>0;i--){
            for(int j = 0;j<3;j++){
                InfectionCard c = infectionDeck.pop();
                infectionDiscardPile.add(c);
                Disease d = new Disease(c.getCity().getColor());
                for(int k=i;k>0;k--)
                    c.getCity().addCube(d);
            }
        }

        //Add players and give random roles
        int difficulty = 4;//Todo: get game info from setup menu
        int humans = 3;
        int bots = 1;
        int players = humans+bots;
        GenericRole[] roles = new GenericRole[players]; //keep track of roles? Not sure if needed
        String[] playerNames = {"Eric", "Noah", "Kai", "Drago"};
        String[] botNames = {"Cortana", "Jarvis", "Ultron", "Dave"};
        try {
            for(int i=0;i<humans;i++){
                Player player = new Player(playerNames[i], atlanta,false);
                player.setRole(new GenericRole("GenericBlue", ColorRGBA.Blue)); //Todo: add role assignment
                playerRepository.addPlayer(player);
            }
            for(int i=0;i<bots;i++){
                Player player = new Player(botNames[i], atlanta,true);
                player.setRole(new GenericRole("GenericRed", ColorRGBA.Red)); //Todo: add role assignment
                playerRepository.addPlayer(player);
            }
        } catch (PlayerLimitException e) {
            e.printStackTrace();
        }
        //Draw 2 cities per person, count max population

        //get difficulty, shuffle rest of cards + epidemic cards
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

    public void infectCity(City city, Disease disease){
        if(!city.addCube(disease)) initOutbreak(city, disease);
    }

    private void initOutbreak(City city, Disease disease) {
        //TODO: increment outbreak marker
        List<City> previousOutbreaks = new ArrayList<>();
        List<City> neighbors = city.getNeighbors();
        previousOutbreaks.add(city);

        for (City c: neighbors) {
            spreadOutbreak(c,disease,previousOutbreaks);
        }
    }

    private void spreadOutbreak(City city, Disease disease, List<City> previousOutbreaks) {
        if(!city.addCube(disease)){
            //TODO: increment outbreak marker
            List<City> neighbors = city.getNeighbors();
            previousOutbreaks.add(city);

            for (City c: neighbors) {
                if(!previousOutbreaks.contains(c)) //prevent outbreaks happening twice
                    spreadOutbreak(c,disease,previousOutbreaks);
            }

        }
    }
}
