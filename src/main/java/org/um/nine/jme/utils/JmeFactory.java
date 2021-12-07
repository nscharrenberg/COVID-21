package org.um.nine.jme.utils;


import org.um.nine.headless.agents.state.GameStateFactory;
import org.um.nine.jme.repositories.*;
import org.um.nine.jme.screens.dialogs.*;
import org.um.nine.jme.screens.hud.*;
import org.um.nine.jme.utils.managers.InputManager;


public class JmeFactory {
    private static GameRepository gameRepository;
    private static BoardRepository boardRepository;
    private static CardRepository cardRepository;
    private static CityRepository cityRepository;
    private static DiseaseRepository diseaseRepository;
    private static EpidemicRepository epidemicRepository;
    private static PlayerRepository playerRepository;
    private static VisualRepository visualRepository;

    private static DiscardCardDialog discardCardDialog;
    private static DiscoverCureDialogBox discoverCureDialogBox;
    private static GameEndState gameEndState;
    private static ShareCityCardConfirmationDialogBox shareCityCardConfirmationDialogBox;
    private static ShareCityCardDialogBox shareCityCardDialogBox;
    private static TreatDiseaseDialogBox treatDiseaseDialogBox;

    private static ActionState actionState;
    private static ContingencyPlannerState contingencyPlannerState;
    private static OptionHudState optionHudState;
    private static PlayerInfoState playerInfoState;
    private static RoleActionState roleActionState;
    private static RuleState ruleState;
    private static InputManager inputManager;

    public static void init(GameRepository gr){
        //initializing GameStateFactory in case it has not been initialized yet
        GameStateFactory.getInitialState();
        gameRepository = gr;
        boardRepository = new BoardRepository();
        cardRepository = new CardRepository();
        cityRepository = new CityRepository();
        diseaseRepository = new DiseaseRepository();
        epidemicRepository =  new EpidemicRepository();
        playerRepository = new PlayerRepository();
        visualRepository = new VisualRepository();

        discardCardDialog = new DiscardCardDialog();
        discoverCureDialogBox = new DiscoverCureDialogBox();
        gameEndState = new GameEndState();
        shareCityCardConfirmationDialogBox = new ShareCityCardConfirmationDialogBox();
        shareCityCardDialogBox = new ShareCityCardDialogBox();
        treatDiseaseDialogBox = new TreatDiseaseDialogBox();

        actionState = new ActionState();
        contingencyPlannerState = new ContingencyPlannerState();
        optionHudState = new OptionHudState();
        playerInfoState = new PlayerInfoState();
        roleActionState = new RoleActionState();
        ruleState = new RuleState();
        inputManager = new InputManager();

        boardRepository.reset();
        playerRepository.reset();
        visualRepository.reset();
        discardCardDialog.reset();
        cityRepository.reset();
        diseaseRepository.reset();
        roleActionState.reset();
        optionHudState.reset();
    }

    public static GameRepository getGameRepository() {
        return gameRepository;
    }

    public static InputManager getInputManager() {
        return inputManager;
    }

    public static BoardRepository getBoardRepository() {
        return boardRepository;
    }

    public static CardRepository getCardRepository() {
        return cardRepository;
    }

    public static CityRepository getCityRepository() {
        return cityRepository;
    }

    public static DiseaseRepository getDiseaseRepository() {
        return diseaseRepository;
    }

    public static EpidemicRepository getEpidemicRepository() {
        return epidemicRepository;
    }

    public static PlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    public static VisualRepository getVisualRepository() {
        return visualRepository;
    }

    public static DiscardCardDialog getDiscardCardDialog() {
        return discardCardDialog;
    }

    public static DiscoverCureDialogBox getDiscoverCureDialogBox() {
        return discoverCureDialogBox;
    }

    public static GameEndState getGameEndState() {
        return gameEndState;
    }

    public static ShareCityCardConfirmationDialogBox getShareCityCardConfirmationDialogBox() {
        return shareCityCardConfirmationDialogBox;
    }

    public static ShareCityCardDialogBox getShareCityCardDialogBox() {
        return shareCityCardDialogBox;
    }

    public static TreatDiseaseDialogBox getTreatDiseaseDialogBox() {
        return treatDiseaseDialogBox;
    }

    public static ActionState getActionState() {
        return actionState;
    }

    public static ContingencyPlannerState getContingencyPlannerState() {
        return contingencyPlannerState;
    }

    public static OptionHudState getOptionHudState() {
        return optionHudState;
    }

    public static PlayerInfoState getPlayerInfoState() {
        return playerInfoState;
    }

    public static RoleActionState getRoleActionState() {
        return roleActionState;
    }

    public static RuleState getRuleState() {
        return ruleState;
    }
}
