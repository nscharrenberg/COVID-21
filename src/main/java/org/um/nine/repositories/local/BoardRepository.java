package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import org.um.nine.contracts.repositories.IBoardRepository;
import org.um.nine.contracts.repositories.ICityRepository;
import org.um.nine.contracts.repositories.IGameRepository;
import org.um.nine.contracts.repositories.IPlayerRepository;
import org.um.nine.domain.City;
import org.um.nine.domain.InfectionRateMarker;
import org.um.nine.domain.Player;
import org.um.nine.domain.roles.GenericRole;
import org.um.nine.exceptions.CityAlreadyHasResearchStationException;
import org.um.nine.exceptions.PlayerLimitException;
import org.um.nine.exceptions.ResearchStationLimitException;

public class BoardRepository implements IBoardRepository {
    private Geometry board;
    private InfectionRateMarker infectionRateMarker;

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private ICityRepository cityRepository;

    @Inject
    private IPlayerRepository playerRepository;

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

        //Set up infection stack

        //Set initial infection:
        //draw 3 cards, 3 infection
        //draw 3 cards, 2 infection
        //draw 3 cards, 1 infection
        //and place cards on infection pile

        //Add players and give random roles
        int humans = 3; //Todo: get game info from setup menu
        int bots = 1;
        int players = humans+bots;
        GenericRole[] roles = new GenericRole[players]; //keep track of roles? Not sure if needed
        String[] playerNames = {"Eric", "Noah", "Kai", "Drago"};
        String[] botNames = {"Cortana", "Jarvis", "Ultron", "Dave"};
        int difficulty = 4;
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
        gameRepository.getApp().getRootNode().attachChild(board);
    }
}
