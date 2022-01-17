package org.um.nine.experiments.rhea.graph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;
import org.um.nine.headless.agents.rhea.state.GameStateFactory;
import org.um.nine.headless.game.contracts.repositories.IAnalyticsRepository;
import org.um.nine.headless.game.domain.analytics.GameAnalytics;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

import static org.um.nine.headless.game.Settings.DEFAULT_RUNNING_GAME;

public class StatGraph extends ApplicationFrame {
    // Actions
    private JFreeChart actionChart;
    private DefaultCategoryDataset actionDataset = new DefaultCategoryDataset();
    private ChartPanel actionChartPanel;

    // Macro Actions
    private JFreeChart macroActionChart;
    private DefaultCategoryDataset macroActionDataset = new DefaultCategoryDataset();
    private ChartPanel macroActionChartPanel;

    // Win Loss Ratio
    private JFreeChart winLossChart;
    private DefaultCategoryDataset winLossDataset = new DefaultCategoryDataset();
    private ChartPanel winLossChartPanel;

    // Treat Diseases
    private JFreeChart treatDiseaseChart;
    private DefaultCategoryDataset treatDiseaseDataset = new DefaultCategoryDataset();
    private ChartPanel treatDiseaseChartPanel;

    // Cure Diseases
    private JFreeChart cureDiseaseChart;
    private DefaultCategoryDataset cureDiseaseDataset = new DefaultCategoryDataset();
    private ChartPanel cureDiseaseChartPanel;

    // Research Station
    private JFreeChart buildResearchStationChart;
    private DefaultCategoryDataset buildResearchStationDataset = new DefaultCategoryDataset();
    private ChartPanel buildResearchStationChartPanel;

    // Visited Cities
    private JFreeChart cityVisitedChart;
    private DefaultCategoryDataset cityVisitedDataset = new DefaultCategoryDataset();
    private ChartPanel cityVisitedChartPanel;

    private Timer timer = new Timer();

    public StatGraph(String title) {
        super(title);
        GridLayout layout = new GridLayout(3, 12);
        setLayout(layout);

        renderWinLossChart();
        renderActionChart();
        renderMacroActionChart();
        renderTreatedDiseasesChart();
        renderCuredDiseasesChart();
        renderBuildResearchStationChart();
        renderCitiesVisited();

        scheduler();
    }

    private void renderWinLossChart() {
        this.winLossChart = ChartFactory.createBarChart(
                "Win / Loss Ratio",
                "Win or Loss", "Amount",
                winLossDataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        winLossChartPanel = new ChartPanel(winLossChart);
        winLossChartPanel.setPreferredSize(new Dimension(560, 367));
        add(winLossChartPanel);
    }

    private void renderBuildResearchStationChart() {
        this.buildResearchStationChart = ChartFactory.createBarChart(
                "Research Stations Build",
                "City", "Build",
                buildResearchStationDataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        buildResearchStationChartPanel = new ChartPanel(buildResearchStationChart);
        buildResearchStationChartPanel.setPreferredSize(new Dimension(560, 367));
        add(buildResearchStationChartPanel);
    }

    private void renderCuredDiseasesChart() {
        this.cureDiseaseChart = ChartFactory.createBarChart(
                "Cure Diseases",
                "Diseases", "Cured",
                cureDiseaseDataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        cureDiseaseChartPanel = new ChartPanel(cureDiseaseChart);
        cureDiseaseChartPanel.setPreferredSize(new Dimension(560, 367));
        add(cureDiseaseChartPanel);
    }

    private void renderCitiesVisited() {
        this.cityVisitedChart = ChartFactory.createBarChart(
                "Cities Visited",
                "City", "Visited Count",
                cityVisitedDataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        cityVisitedChartPanel = new ChartPanel(cityVisitedChart);
        cityVisitedChartPanel.setPreferredSize(new Dimension(560, 367));
        add(cityVisitedChartPanel);
    }

    private void renderTreatedDiseasesChart() {
        this.treatDiseaseChart = ChartFactory.createBarChart(
                "Treat Diseases",
                "Diseases", "Treated",
                treatDiseaseDataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        treatDiseaseChartPanel = new ChartPanel(treatDiseaseChart);
        treatDiseaseChartPanel.setPreferredSize(new Dimension(560, 367));
        add(treatDiseaseChartPanel);
    }

    private void renderActionChart() {
        this.actionChart = ChartFactory.createBarChart(
                "Action Types",
                "ActionType", "Amount",
                actionDataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        actionChartPanel = new ChartPanel(actionChart);
        actionChartPanel.setPreferredSize(new Dimension(560, 367));
        add(actionChartPanel);
    }

    private void renderMacroActionChart() {
        this.macroActionChart = ChartFactory.createBarChart(
                "Macro Action Types",
                "Macro Action", "Amount",
                macroActionDataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );


        macroActionChartPanel = new ChartPanel(macroActionChart);
        macroActionChartPanel.setPreferredSize(new Dimension(1900, 367));
        add(macroActionChartPanel);
    }

    private void scheduler() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                IAnalyticsRepository repository = GameStateFactory.getAnalyticsRepository();
                GameAnalytics game = repository.getCurrentGameAnalytics(DEFAULT_RUNNING_GAME.getCurrentState());

                winLossDataset.setValue(repository.winCount(), "Total", "Won");
                winLossDataset.setValue(repository.lossCount(), "Total", "Loss");
                winLossDataset.setValue(repository.winLossRatio(), "Total", "Ratio");

                game.getPlayerAnalytics().forEach((k, v) -> {
                    v.getActionsUsed().forEach((kp, vp) -> {
                        actionDataset.addValue(vp, kp, k);
                    });

                    v.getMacroActionsUsed().forEach((kp, vp) -> {
                        macroActionDataset.addValue(vp, kp, k);
                    });

                    v.getDiseasesTreatedCount().forEach((kp, vp) -> {
                        treatDiseaseDataset.addValue(vp, kp, k);
                    });

                    v.getDiseasesCuredCount().forEach((kp, vp) -> {
                        cureDiseaseDataset.addValue(vp, kp, kp);
                    });

                    v.getCityVisitedCount().forEach((kp, vp) -> {
                        cityVisitedDataset.addValue(vp, kp, k);
                    });

                    buildResearchStationDataset.addValue(v.getResearchStationBuild().size(), "Build", k);
                });
            }
        }, 5000, 1000);
    }

    public JFreeChart getActionChart() {
        return actionChart;
    }

    public ChartPanel getActionChartPanel() {
        return actionChartPanel;
    }

    public DefaultCategoryDataset getActionDataset() {
        return actionDataset;
    }
}
