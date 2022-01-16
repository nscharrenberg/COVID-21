package org.um.nine.headless.agents.rhea.state;

import org.um.nine.headless.game.contracts.repositories.*;
import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.Cure;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.InfectionCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static org.um.nine.headless.agents.rhea.state.StateEvaluation.abilityCure2;
import static org.um.nine.headless.game.Settings.DEFAULT_CLONER;

public interface IState extends Cloneable {

    default void reset() {
        getBoardRepository().reset();
        getPlayerRepository().reset();
        getCityRepository().reset();
        getDiseaseRepository().reset();
        getCardRepository().reset();
    }

    default void start() {
        getBoardRepository().start(this);
    }

    void setBoardRepository(IBoardRepository iBoardRepository);

    void setDiseaseRepository(IDiseaseRepository iDiseaseRepository);

    IDiseaseRepository getDiseaseRepository();

    void setPlayerRepository(IPlayerRepository iPlayerRepository);

    IPlayerRepository getPlayerRepository();

    IBoardRepository getBoardRepository();

    void setCityRepository(ICityRepository iCityRepository);

    ICityRepository getCityRepository();

    void setCardRepository(ICardRepository iCardRepository);

    ICardRepository getCardRepository();

    void setEpidemicRepository(IEpidemicRepository iEpidemicRepository);

    IEpidemicRepository getEpidemicRepository();
    default boolean isGameLost() {
        if (getDiseaseRepository().isGameOver()) return true;

        var redCubes = getDiseaseRepository().getCubes().get(Color.RED).stream().filter(c -> c.getCity() == null).count();
        if (redCubes == 0) return true;
        var blueCubes = getDiseaseRepository().getCubes().get(Color.BLUE).stream().filter(c -> c.getCity() == null).count();
        if (blueCubes == 0) return true;
        var blackCubes = getDiseaseRepository().getCubes().get(Color.BLACK).stream().filter(c -> c.getCity() == null).count();
        if (blackCubes == 0) return true;
        var yellowCubes = getDiseaseRepository().getCubes().get(Color.YELLOW).stream().filter(c -> c.getCity() == null).count();
        if (yellowCubes == 0) return true;

        return getCardRepository().getPlayerDeck().isEmpty();


        //TODO: missing some losing conditions
    }
    default boolean isGameWon() {
        return this.getDiseaseRepository().getCures().values().stream().filter(Cure::isDiscovered).count() ==4;
    }

    default IState getClonedState() {
        Stack<PlayerCard> playerDeck = (Stack<PlayerCard>) getCardRepository().getPlayerDeck().clone();
        Stack<InfectionCard> infectionDeck = (Stack<InfectionCard>) getCardRepository().getInfectionDeck().clone();
        Stack<InfectionCard> infectionDiscard = (Stack<InfectionCard>) getCardRepository().getInfectionDiscardPile().clone();
        IState clone = DEFAULT_CLONER.deepClone(this);
        clone.getCardRepository().setPlayerDeck(playerDeck);
        clone.getCardRepository().setInfectionDeck(infectionDeck);
        clone.getCardRepository().setInfectionDiscardPile(infectionDiscard);
        return clone;
    }


    IState clone();

    default PlayerCard[] getDiscardingCard() {
        PlayerCard discarding = null;
        Player player = getPlayerRepository().getCurrentPlayer();
        List<PlayerCard> pc = new ArrayList<>(player.getHand());
        for (int i = 0; i < pc.size(); i++) {
            PlayerCard card = pc.get(0);
            Color color = ((CityCard) card).getCity().getColor();
            double at = abilityCure2(this, color);
            pc.remove(card);
            if (at == abilityCure2(this, color)) {
                discarding = card;
            }
        }
        if (discarding == null) {
            Map<Color, List<PlayerCard>> cardsColors = player.getHand().stream().collect(groupingBy(c -> ((CityCard) c).getCity().getColor()));
            return new PlayerCard[]{
                    cardsColors.values().stream().min(Comparator.comparingInt(List::size)).get().get(0)
            };
        }

        return new PlayerCard[]{discarding};
    }

}
