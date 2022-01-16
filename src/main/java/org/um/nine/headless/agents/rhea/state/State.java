package org.um.nine.headless.agents.rhea.state;

import org.um.nine.headless.game.contracts.repositories.*;
import org.um.nine.headless.game.domain.City;
import org.um.nine.headless.game.domain.Disease;
import org.um.nine.headless.game.domain.Player;
import org.um.nine.headless.game.domain.RoundState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class State implements IState {

    public static void main(String[] args) {
        IState original = (GameStateFactory.createInitialState());
        IState cloned = original.clone();

        System.out.println(original.equals(cloned));


        original.getPlayerRepository().setCurrentRoundState(RoundState.DRAW);
        try {
            original.getPlayerRepository().playerAction(null, original);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(original.equals(cloned));

        debugState(original);
        System.out.println("=========================================================================================================================");
        debugState(cloned);

    }

    public static void debugState(IState state) {

        state.getCityRepository().getCities().values().stream().map(City::getCubes).filter(list -> !list.isEmpty()).forEach(System.out::println);
        System.out.println();
        state.getDiseaseRepository().getCubes().forEach((color, list) -> {
            System.out.println(color + " -> " + list.stream().filter(disease -> disease.getCity() != null).map(disease -> disease.getCity().getName() + " " + disease.getColor()).collect(Collectors.toList()));
        });


    }

    private IDiseaseRepository iDiseaseRepository;
    private IPlayerRepository iPlayerRepository;
    private IEpidemicRepository iEpidemicRepository;
    private ICityRepository iCityRepository;
    private ICardRepository iCardRepository;
    private IBoardRepository iBoardRepository;

    public State() {
    }

    public State(IDiseaseRepository iDiseaseRepository,
                  IPlayerRepository iPlayerRepository,
                  ICardRepository iCardRepository,
                  ICityRepository iCityRepository,
                  IEpidemicRepository iEpidemicRepository,
                  IBoardRepository iBoardRepository
    ) {
        this.setDiseaseRepository(iDiseaseRepository);
        this.setPlayerRepository(iPlayerRepository);
        this.setCardRepository(iCardRepository);
        this.setCityRepository(iCityRepository);
        this.setEpidemicRepository(iEpidemicRepository);
        this.setBoardRepository(iBoardRepository);
    }


    @Override
    public void setDiseaseRepository(IDiseaseRepository iDiseaseRepository) {
        this.iDiseaseRepository = iDiseaseRepository;
    }
    @Override
    public IDiseaseRepository getDiseaseRepository() {
        return this.iDiseaseRepository;
    }

    @Override
    public void setPlayerRepository(IPlayerRepository iPlayerRepository) {
        this.iPlayerRepository = iPlayerRepository;
    }
    @Override
    public IPlayerRepository getPlayerRepository() {
        return this.iPlayerRepository;
    }
    @Override
    public void setBoardRepository(IBoardRepository iBoardRepository) {
        this.iBoardRepository = iBoardRepository;
    }
    @Override
    public IBoardRepository getBoardRepository() {
        return this.iBoardRepository;
    }

    @Override
    public void setCityRepository(ICityRepository iCityRepository) {
        this.iCityRepository = iCityRepository;
    }

    @Override
    public ICityRepository getCityRepository() {
        return this.iCityRepository;
    }

    @Override
    public void setCardRepository(ICardRepository iCardRepository) {
        this.iCardRepository = iCardRepository;
    }
    @Override
    public ICardRepository getCardRepository() {
        return this.iCardRepository;
    }


    @Override
    public void setEpidemicRepository(IEpidemicRepository iEpidemicRepository) {
        this.iEpidemicRepository = iEpidemicRepository;
    }

    @Override
    public IEpidemicRepository getEpidemicRepository() {
        return this.iEpidemicRepository;
    }

    @Override
    public IState clone() {
        try {
            IState clone = (IState) super.clone();
            clone.setPlayerRepository(this.getPlayerRepository().clone());
            clone.setBoardRepository(this.getBoardRepository().clone());
            clone.setCardRepository(this.getCardRepository().clone());
            clone.setCityRepository(this.getCityRepository().clone());
            clone.setEpidemicRepository(this.getEpidemicRepository().clone());
            clone.setDiseaseRepository(this.getDiseaseRepository().clone());


            clone.getCityRepository().getCities().forEach(
                    (s, city) -> {
                        City thisCity = this.getCityRepository().getCities().get(s);
                        city.setPawns(thisCity.getPawns().stream().map(player -> clone.getPlayerRepository().getPlayers().get(player.getName())).collect(Collectors.toCollection(ArrayList::new)));
                        List<Disease> diseases = thisCity.
                                getCubes().
                                stream().
                                map(disease -> clone.getDiseaseRepository().getCubes().get(disease.getColor())).
                                map(allCubesSameColor -> allCubesSameColor.
                                        stream().
                                        filter(disease1 -> disease1.getCity() != null && disease1.getCity().equals(thisCity)).
                                        peek(disease -> disease.setCity(city)).
                                        collect(Collectors.toList())).
                                findFirst().
                                orElse(new ArrayList<>());
                        city.setCubes(diseases);
                        List<Player> pawns = thisCity.
                                getPawns().
                                stream().
                                map(player -> clone.getPlayerRepository().getPlayers().get(player.getName())).
                                peek(player -> player.setCityField(city)).
                                collect(Collectors.toList());
                        city.setPawns(pawns);

                        List<City> neighbours = thisCity.
                                getNeighbors().
                                stream().
                                map(city1 -> clone.getCityRepository().getCities().get(city1.getName())).
                                collect(Collectors.toList());
                        city.setNeighbors(neighbours);
                    }
            );
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Objects.equals(iDiseaseRepository, state.iDiseaseRepository) &&
                Objects.equals(iPlayerRepository, state.iPlayerRepository) &&
                Objects.equals(iEpidemicRepository, state.iEpidemicRepository) &&
                Objects.equals(iCityRepository, state.iCityRepository) &&
                Objects.equals(iCardRepository, state.iCardRepository) &&
                Objects.equals(iBoardRepository, state.iBoardRepository);
    }

}
