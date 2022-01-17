package org.um.nine.headless.agents.rhea.state;

import org.um.nine.headless.game.contracts.repositories.*;
import org.um.nine.headless.game.domain.Color;
import org.um.nine.headless.game.domain.Cure;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.cards.CityCard;
import org.um.nine.headless.game.domain.cards.PlayerCard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static org.um.nine.headless.agents.rhea.state.StateEvaluation.abilityCure2;

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

        if (
                getDiseaseRepository().getCubes().get(Color.RED).stream().noneMatch(c -> c.getCity() == null) ||
                        getDiseaseRepository().getCubes().get(Color.BLUE).stream().noneMatch(c -> c.getCity() == null) ||
                        getDiseaseRepository().getCubes().get(Color.BLACK).stream().noneMatch(c -> c.getCity() == null) ||
                        getDiseaseRepository().getCubes().get(Color.YELLOW).stream().noneMatch(c -> c.getCity() == null)
        ) return true;


        return getCardRepository().getPlayerDeck().isEmpty();
        //TODO: missing some losing conditions
    }

    default boolean isGameWon() {
        return this.getDiseaseRepository().getCures().values().stream().allMatch(Cure::isDiscovered);
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
