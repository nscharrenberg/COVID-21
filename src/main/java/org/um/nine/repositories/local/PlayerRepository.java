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
import org.um.nine.exceptions.*;
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
    private ICityRepository cityRepository;

    @Inject
    private ICardRepository cardRepository;

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

    public void drive(Player player, City city) throws InvalidMoveException {
        if (player.getCity().equals(city)) {
            throw new InvalidMoveException(city, player);
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

    public void direct(Player player, City city) throws InvalidMoveException {
        if (player.getCity().equals(city)) {
            throw new InvalidMoveException(city, player);
        }

        // No need to charter when neighbouring city
        if (player.getCity().getNeighbors().contains(city)) {
            drive(player, city);
        }

        // No need to charter when both cities have research station
        if (player.getCity().getResearchStation() != null && city.getResearchStation() != null) {
            drive(player, city);
        }

        PlayerCard pc = player.getHandCards().stream().filter(c -> {
            if (c instanceof CityCard cc) {
                return cc.getCity().equals(city);
            }

            return false;
        }).findFirst().orElse(null);

        // If player doesn't have the city card, it can't make this move.
        if (pc == null) {
            throw new InvalidMoveException(city, player);
        }

        player.getHandCards().remove(pc);
        drive(player, city);
    }

    public void charter(Player player, City city) throws InvalidMoveException {
        if (player.getCity().equals(city)) {
            throw new InvalidMoveException(city, player);
        }

        // No need to charter when neighbouring city
        if (player.getCity().getNeighbors().contains(city)) {
            drive(player, city);
        }

        // No need to charter when both cities have research station
        if (player.getCity().getResearchStation() != null && city.getResearchStation() != null) {
            drive(player, city);
        }

        PlayerCard pc = player.getHandCards().stream().filter(c -> {
            if (c instanceof CityCard cc) {
                return cc.getCity().equals(player.getCity());
            }

            return false;
        }).findFirst().orElse(null);

        // If player doesn't have city card of his current city, it can't make this move.
        if (pc == null) {
            throw new InvalidMoveException(city, player);
        }

        player.getHandCards().remove(pc);
        drive(player, city);
    }

    public void shuttle(Player player, City city) throws InvalidMoveException {
        if (player.getCity().equals(city)) {
            throw new InvalidMoveException(city, player);
        }

        if (player.getCity().getResearchStation() == null || city.getResearchStation() == null) {
            throw new InvalidMoveException(city, player);
        }

        drive(player, city);
    }

    @Override
    public void verifyExternalMove(Player instigator, Player target, City city, boolean accept) throws InvalidMoveException, ExternalMoveNotAcceptedException {
        if (instigator.equals(target)) {
            drive(instigator, city);
            return;
        }

        if (accept) {
            throw new ExternalMoveNotAcceptedException(instigator, target, city);
        }

        drive(target, city);
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
        nextPlayer();
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

        this.players.forEach((key, player) -> {
            int highestPopulationCount = 0;

            for (PlayerCard c : player.getHandCards()) {
                if (c instanceof CityCard tempCityCard) {
                    highestPopulationCount = Math.max(tempCityCard.getCity().getPopulation(), highestPopulationCount);
                }
            }
            highestPopulation.put(key, highestPopulationCount);
        });
        highestPopulation.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(p -> {
            this.playerOrder.push(this.players.get(p.getKey()));
        });
    }

    @Override
    public void action(ActionType type) throws InvalidMoveException, NoActionSelectedException, ResearchStationLimitException, CityAlreadyHasResearchStationException {
        if (currentRoundState == null) {
            nextState(null);
        }

        if (currentRoundState.equals(RoundState.ACTION)) {
            if (type == null) {
                throw new NoActionSelectedException();
            }

            City city = boardRepository.getSelectedCity();
            Player player = currentPlayer;

            if (type.equals(ActionType.DRIVE)) {
                drive(player, city);
            } else if (type.equals(ActionType.DIRECT_FLIGHT)) {
                direct(player, city);
            } else if (type.equals(ActionType.CHARTER_FLIGHT)) {
                charter(player, city);
            } else if (type.equals(ActionType.SHUTTLE)) {
                shuttle(player, city);
            } else if (type.equals(ActionType.BUILD_RESEARCH_STATION)) {
                cityRepository.addResearchStation(city, player);
            } else if (type.equals(ActionType.TREAT_DISEASE)) {
                // TODO: Treat disease
            } else if (type.equals(ActionType.SHARE_KNOWLEDGE)) {
                // TODO: Share knowledge
            } else if (type.equals(ActionType.DISCOVER_CURE)) {
               // TODO: Discover cure
            }
        } else if (currentRoundState.equals(RoundState.DRAW)) {
            cardRepository.drawPlayCard();

            if (drawLeft > 0) {
                action(null);
            }

            return;
        } else if (currentRoundState.equals(RoundState.INFECT)) {
            // TODO: Draw card from infection deck
            return;
        }

        nextState(currentRoundState);
    }
}

