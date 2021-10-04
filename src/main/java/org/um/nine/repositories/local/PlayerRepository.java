package org.um.nine.repositories.local;

import com.google.inject.Inject;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Label;
import org.um.nine.Info;
import org.um.nine.contracts.repositories.*;
import org.um.nine.domain.*;
import org.um.nine.domain.cards.CityCard;
import org.um.nine.domain.cards.PlayerCard;
import org.um.nine.domain.roles.*;
import org.um.nine.exceptions.ExternalMoveNotAcceptedException;
import org.um.nine.exceptions.InvalidMoveException;
import org.um.nine.exceptions.PlayerLimitException;
import org.um.nine.screens.hud.OptionHudState;
import org.um.nine.utils.managers.RenderManager;

import java.util.*;

public class PlayerRepository implements IPlayerRepository {
    private HashMap<String, Player> players;
    private Stack<Role> availableRoles;
    private Player currentPlayer = null;
    private Stack<Player> playerOrder;

    private RoundState currentRoundState = null;

    private int actionsLeft = 4;
    private int drawLeft = 2;
    private int infectionLeft = 2;

    @Inject
    private IBoardRepository boardRepository;

    @Inject
    private IDiseaseRepository diseaseRepository;

    @Inject
    private OptionHudState optionHudState;

    @Inject
    private RenderManager renderManager;

    public HashMap<String, Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) throws PlayerLimitException {
        if (this.players.size() + 1 > Info.PLAYER_THRESHOLD) {
            throw new PlayerLimitException();
        }

        this.players.put(player.getName(), player);
    }

    @Override
    public void reset() {
        this.players = new HashMap<>();

        this.availableRoles = new Stack<>();
        availableRoles.add(new ContingencyPlannerRole());
        availableRoles.add(new DispatcherRole());
        availableRoles.add(new MedicRole());
        availableRoles.add(new OperationsExpertRole());
        availableRoles.add(new QuarantineSpecialistRole());
        availableRoles.add(new ResearcherRole());
        availableRoles.add(new ScientistRole());

        Collections.shuffle(availableRoles);
    }

    @Override
    public void move(Player player, City city) throws InvalidMoveException {
        if (player.getCity().equals(city)) {
            throw new InvalidMoveException(city, player);
        }

        if(player.getCity().getResearchStation() != null && city.getResearchStation() != null) {
            // TODO: Allow Move normally
        } else if (!player.getCity().getNeighbors().contains(city)) {
            // TODO: allow player to discard a card or not
        }

        city.addPawn(player);

        if (player.getRole().events(RoleEvent.AUTO_REMOVE_CUBES_OF_CURED_DISEASE)) {
            city.getCubes().forEach(c -> {
                Cure found = diseaseRepository.getCures().get(c.getColor());

                if (found != null) {
                    if (found.isDiscovered()) {
                        city.getCubes().removeIf(cb -> cb.getColor().equals(found.getColor()));
                    }
                }
            });
        }

        renderManager.renderPlayer(player, city.getPawnPosition(player));
    }

    @Override
    public void verifyExternalMove(Player instigator, Player target, City city, boolean accept) throws InvalidMoveException, ExternalMoveNotAcceptedException {
        if (instigator.equals(target)) {
            move(instigator, city);
            return;
        }

        if (accept) {
            throw new ExternalMoveNotAcceptedException(instigator, target, city);
        }

        move(target, city);
    }

    /**
     * @return null if the turn has ended otherwise the RoundState of the turn
     */
    @Override
    public RoundState nextState(RoundState currentState){
        if (currentState == null) {
            this.currentRoundState = RoundState.ACTION;
            return RoundState.ACTION;
        } else if (currentState == RoundState.ACTION){
            actionsLeft--;
            if (actionsLeft == 0) {
                actionsLeft = 4;
                this.currentRoundState = RoundState.DRAW;
                return RoundState.DRAW;
            }

            this.currentRoundState = RoundState.ACTION;
            return RoundState.ACTION;
        } else if (currentState == RoundState.DRAW){
            drawLeft--;
            if (drawLeft == 0){
                drawLeft = 2;
                this.currentRoundState = RoundState.INFECT;
                return RoundState.INFECT;
            }

            this.currentRoundState = RoundState.DRAW;
            return RoundState.DRAW;
        } else if (currentState == RoundState.INFECT){
            infectionLeft--;
            if (infectionLeft == 0){
                infectionLeft = Objects.requireNonNull(diseaseRepository.getInfectionRate().stream().filter(Marker::isCurrent).findFirst().orElse(null)).getCount();
                this.currentRoundState = null;
                return null;
            }

            this.currentRoundState = RoundState.INFECT;
            return RoundState.INFECT;
        }

        this.currentRoundState = null;
        throw new IllegalStateException();
    }

    @Override
    public RoundState getCurrentRoundState() {
        return currentRoundState;
    }

    @Override
    public void setCurrentRoundState(RoundState currentRoundState) {
        this.currentRoundState = currentRoundState;
    }

    @Override
    public void assignRoleToPlayer(Player player) {
        Role role = availableRoles.pop();
        player.setRole(role);
    }

    @Override
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;

        if (optionHudState != null) {
            if (optionHudState.getWindow() == null) {
                return;
            }

            Spatial tempSpat = optionHudState.getWindow().getChild("currentPlayerNameLbl");

            if (tempSpat == null) {
                return;
            }

            Label tempLbl = (Label) tempSpat;
            tempLbl.setText("Current Player: " + currentPlayer);
        }
    }

    @Override
    public void nextPlayer() {
        Player newPlayer = this.playerOrder.pop();
        this.playerOrder.push(newPlayer);

        setCurrentPlayer(newPlayer);

        boardRepository.resetRound();
    }

    @Override
    public void resetRound() {
        actionsLeft = 4;
        drawLeft = 2;
        infectionLeft = 2;
        currentRoundState = null;
    }

    @Override
    public void decidePlayerOrder() {
        this.playerOrder = new Stack<>();

        HashMap<String, Integer> highestPopulation = new HashMap<>();

        System.out.println("Player Population Counting START:");

        this.players.forEach((key, player) -> {

            System.out.println("START GOING THROUGH CITIES OF : " + key);

            int highestPopulationCount = 0;

            for (PlayerCard c : player.getHandCards()) {
                if (c instanceof CityCard tempCityCard) {
                    highestPopulationCount = Math.max(tempCityCard.getCity().getPopulation(), highestPopulationCount);
                }
            }

            System.out.println("Highest Population number: " + highestPopulationCount);

            highestPopulation.put(key, highestPopulationCount);

            System.out.println("END GOING THROUGH CITIES OF : " + key);
        });

        System.out.println("Player Population Counting END:");

        System.out.println("Final Player Order START");

        highestPopulation.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(p -> {
            System.out.println("Player: " + this.players.get(p.getKey()) + " - " + p.getValue());
            this.playerOrder.push(this.players.get(p.getKey()));
        });

        System.out.println("Final Player Order START");
    }
}

