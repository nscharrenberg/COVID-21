package org.um.nine.config;

import com.google.inject.AbstractModule;
import org.um.nine.contracts.repositories.*;
import org.um.nine.domain.roles.DispatcherRole;
import org.um.nine.repositories.local.*;
import org.um.nine.screens.ConfigurationState;
import org.um.nine.screens.MainMenuState;
import org.um.nine.screens.PauseMenu;
import org.um.nine.screens.SettingsState;
import org.um.nine.screens.dialogs.*;
import org.um.nine.screens.hud.*;
import org.um.nine.utils.managers.RenderManager;

public class GuiceConfiguration extends AbstractModule {
    private final IGameRepository gameRepository = new GameRepository();
    private final IBoardRepository boardRepository = new BoardRepository();
    private final ICityRepository cityRepository = new CityRepository();
    private final ICardRepository cardRepository = new CardRepository();
    private final IPlayerRepository playerRepository = new PlayerRepository();
    private final IDiseaseRepository diseaseRepository = new DiseaseRepository();
    private final RenderManager renderManager = new RenderManager();
    private final IEpidemicRepository epidemicRepository = new EpidemicRepository();

    // Screens
    private final MainMenuState mainMenuState = new MainMenuState();
    private final SettingsState settingsState = new SettingsState();
    private final ConfigurationState configurationState = new ConfigurationState();
    private final PauseMenu pauseState = new PauseMenu();
    private final PlayerInfoState playerInfoState = new PlayerInfoState();
    private final OptionHudState optionHudState = new OptionHudState();
    private final ActionState actionState = new ActionState();
    private final TreatDiseaseDialogBox treatDiseaseDialogBox = new TreatDiseaseDialogBox();
    private final DiscoverCureDialogBox discoverCureDialogBox = new DiscoverCureDialogBox();
    private final ShareCityCardDialogBox shareCityCardDialogBox = new ShareCityCardDialogBox();
    private final ShareCityCardConfirmationDialogBox shareCityCardConfirmationDialogBox = new ShareCityCardConfirmationDialogBox();
    private final RoleActionState roleActionState = new RoleActionState();
    private final RuleState ruleState = new RuleState();
    private final DiscardCardDialog discardCardDialog = new DiscardCardDialog();
    private final ContingencyPlannerState contingencyPlannerState = new ContingencyPlannerState();
    private final GameEndState gameEndState = new GameEndState();
    private final PrognosisEventDialog prognosisEventDialog = new PrognosisEventDialog();
    private final EventState eventState = new EventState();
    private final DispatcherDialog dispatcherDialog = new DispatcherDialog();
    private final ResilientPopulationDialog resilientPopulationDialog = new ResilientPopulationDialog();

    @Override
    protected void configure() {
        bind(IGameRepository.class).toInstance(gameRepository);
        bind(IBoardRepository.class).toInstance(boardRepository);
        bind(ICityRepository.class).toInstance(cityRepository);
        bind(ICardRepository.class).toInstance(cardRepository);
        bind(IPlayerRepository.class).toInstance(playerRepository);
        bind(IDiseaseRepository.class).toInstance(diseaseRepository);
        bind(RenderManager.class).toInstance(renderManager);
        bind(IEpidemicRepository.class).toInstance(epidemicRepository);

        bind(MainMenuState.class).toInstance(mainMenuState);
        bind(SettingsState.class).toInstance(settingsState);
        bind(ConfigurationState.class).toInstance(configurationState);
        bind(PauseMenu.class).toInstance(pauseState);
        bind(OptionHudState.class).toInstance(optionHudState);
        bind(PlayerInfoState.class).toInstance(playerInfoState);
        bind(ActionState.class).toInstance(actionState);
        bind(TreatDiseaseDialogBox.class).toInstance(treatDiseaseDialogBox);
        bind(DiscoverCureDialogBox.class).toInstance(discoverCureDialogBox);
        bind(ShareCityCardDialogBox.class).toInstance(shareCityCardDialogBox);
        bind(ShareCityCardConfirmationDialogBox.class).toInstance(shareCityCardConfirmationDialogBox);
        bind(RoleActionState.class).toInstance(roleActionState);
        bind(RuleState.class).toInstance(ruleState);
        bind(DiscardCardDialog.class).toInstance(discardCardDialog);
        bind(ContingencyPlannerState.class).toInstance(contingencyPlannerState);
        bind(GameEndState.class).toInstance(gameEndState);
        bind(PrognosisEventDialog.class).toInstance(prognosisEventDialog);
        bind(EventState.class).toInstance(eventState);
        bind(DispatcherDialog.class).toInstance(dispatcherDialog);
    }
}
