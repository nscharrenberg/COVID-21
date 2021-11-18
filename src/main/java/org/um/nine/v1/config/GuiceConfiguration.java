package org.um.nine.v1.config;

import com.google.inject.AbstractModule;
import org.um.nine.v1.agents.baseline.BaselineAgent;
import org.um.nine.v1.agents.reinforcement.ReinforcementAgent;
import org.um.nine.v1.contracts.repositories.*;
import org.um.nine.v1.repositories.local.*;
import org.um.nine.v1.screens.ConfigurationState;
import org.um.nine.v1.screens.MainMenuState;
import org.um.nine.v1.screens.PauseMenu;
import org.um.nine.v1.screens.SettingsState;
import org.um.nine.v1.screens.dialogs.*;
import org.um.nine.v1.screens.hud.*;
import org.um.nine.v1.utils.managers.RenderManager;

public class GuiceConfiguration extends AbstractModule {
    private final IGameRepository gameRepository = new GameRepository();
    private final IBoardRepository boardRepository = new BoardRepository();
    private final ICityRepository cityRepository = new CityRepository();
    private final ICardRepository cardRepository = new CardRepository();
    private final IPlayerRepository playerRepository = new PlayerRepository();
    private final IDiseaseRepository diseaseRepository = new DiseaseRepository();
    private final RenderManager renderManager = new RenderManager();
    private final IEpidemicRepository epidemicRepository = new EpidemicRepository();
    private final IAgentRepository agentRepository= new AgentRepository( new BaselineAgent(), new ReinforcementAgent());


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
        bind(IAgentRepository.class).toInstance(agentRepository);
        bind(BaselineAgent.class).toInstance(agentRepository.baselineAgent());
        bind(ReinforcementAgent.class).toInstance(agentRepository.reinforcementAgent());



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
    }
}
