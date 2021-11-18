package org.um.nine.v1.repositories.local;

import com.google.inject.Inject;

import org.um.nine.v1.Info;
import org.um.nine.v1.contracts.repositories.*;
import org.um.nine.v1.domain.City;
import org.um.nine.v1.domain.Disease;
import org.um.nine.v1.domain.cards.EpidemicCard;
import org.um.nine.v1.domain.cards.InfectionCard;
import org.um.nine.v1.domain.cards.PlayerCard;
import org.um.nine.v1.exceptions.GameOverException;
import org.um.nine.v1.exceptions.NoCubesLeftException;
import org.um.nine.v1.exceptions.NoDiseaseOrOutbreakPossibleDueToEvent;
import org.um.nine.v1.screens.dialogs.DiscardCardDialog;
import org.um.nine.v1.screens.dialogs.GameEndState;
import org.um.nine.v1.screens.hud.PlayerInfoState;
import org.um.nine.v1.utils.cardmanaging.CityCardReader;
import org.um.nine.v1.utils.cardmanaging.Shuffle;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;

public class CardRepository implements ICardRepository {
    @Inject
    private ICityRepository cityRepository;
    @Inject
    private IPlayerRepository playerRepository;
    @Inject
    private IDiseaseRepository diseaseRepository;

    @Inject
    private IGameRepository gameRepository;

    @Inject
    private DiscardCardDialog discardCardDialog;

    @Inject
    private IBoardRepository boardRepository;

    @Inject
    private IEpidemicRepository epidemicRepository;

    @Inject
    private PlayerInfoState playerInfoState;

    @Inject
    private GameEndState gameEndState;

    private Stack<PlayerCard> playerDeck;
    private LinkedList<PlayerCard> eventDiscardPile;
    private Stack<InfectionCard> infectionDeck;
    private Stack<InfectionCard> infectionDiscardPile;

    public CardRepository() {
    }

    @Override
    public Stack<PlayerCard> getPlayerDeck() {
        return playerDeck;
    }


    @Override
    public Stack<InfectionCard> getInfectionDeck() {
        return infectionDeck;
    }


    public void reset() {
        this.playerDeck = new Stack<>();
        this.infectionDeck = new Stack<>();
        this.infectionDiscardPile = new Stack<>();
        this.eventDiscardPile = new LinkedList<>();
    }

    @Override
    public void drawPlayCard() {
        PlayerCard drawn = this.playerDeck.pop();
        if (drawn instanceof EpidemicCard) {
            epidemicRepository.action();
            return;
        }

        playerRepository.getCurrentPlayer().addCard(drawn);
        if(playerRepository.getCurrentPlayer().getHandCards().size() > Info.HAND_LIMIT) {
            discardCardDialog.setCurrentPlayer(playerRepository.getCurrentPlayer());
            gameRepository.getApp().getStateManager().attach(discardCardDialog);
            discardCardDialog.setHeartbeat(true);
            discardCardDialog.setEnabled(true);
        }
        playerInfoState.setHeartbeat(true);
    }

    @Override
    public void drawInfectionCard() throws NoCubesLeftException, NoDiseaseOrOutbreakPossibleDueToEvent, GameOverException {
        InfectionCard infectionCard = this.infectionDeck.pop();

        diseaseRepository.infect(infectionCard.getCity().getColor(), infectionCard.getCity());
        this.infectionDiscardPile.push(infectionCard);
    }

    @Override
    public void buildDecks(){
        this.playerDeck = Shuffle.buildPlayerDeck(boardRepository.getDifficulty(), cityRepository.getCities(), playerRepository.getPlayers());
        infectionDeck = CityCardReader.generateInfectionDeck(cityRepository.getCities().values().toArray(new City[0]));
        infectionDiscardPile = new Stack<>();
        Collections.shuffle(infectionDeck);

        // Set initial infection:
        // draw 3 cards 3 cubes, 3 cards 2 cubes, 3 cards 1 cube
        // and place cards on infection discard pile
        for (int i = 3; i > 0; i--) {
            for (int j = 0; j < 3; j++) {
                InfectionCard c = infectionDeck.pop();
                infectionDiscardPile.add(c);
                Disease d = new Disease(c.getCity().getColor());
                for(int k=i;k>0;k--) {
                    try {
                        diseaseRepository.infect(d.getColor(), c.getCity());
                    } catch (NoDiseaseOrOutbreakPossibleDueToEvent e) {
                        e.printStackTrace();
                    } catch (GameOverException | NoCubesLeftException e) {
                        gameRepository.getApp().getStateManager().attach(gameEndState);
                        gameEndState.setMessage("Game Over! You Lost!");
                        gameEndState.setEnabled(true);
                    }
                }
            }
        }

    }

    @Override
    public LinkedList<PlayerCard> getEventDiscardPile() {
        return eventDiscardPile;
    }

    @Override
    public void setEventDiscardPile(LinkedList<PlayerCard> eventDiscardPile) {
        this.eventDiscardPile = eventDiscardPile;
    }

    @Override
    public Stack<InfectionCard> getInfectionDiscardPile() {
        return infectionDiscardPile;
    }

    @Override
    public void setInfectionDiscardPile(Stack<InfectionCard> newPile) {
        infectionDiscardPile = newPile;
    }

    @Override
    public void cleanup() {
        playerDeck = null;
        eventDiscardPile = null;
        infectionDeck = null;
        infectionDiscardPile = null;
    }
}
