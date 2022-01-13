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
import org.um.nine.headless.game.repositories.AnalyticsRepository;

import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ActionTypeGraph extends ApplicationFrame {
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private DefaultCategoryDataset dataset;
    private Timer timer = new Timer();

    public ActionTypeGraph(String title) {
        super(title);

        this.chart = ChartFactory.createBarChart(
                "Action Types",
                "ActionType", "Amount",
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false
        );

        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(560, 367));
        setContentPane(chartPanel);
    }

    private DefaultCategoryDataset createDataset() {
        dataset = new DefaultCategoryDataset();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                IAnalyticsRepository repository = GameStateFactory.getAnalyticsRepository();
                List<GameAnalytics> gameAnalytics = repository.getGameAnalytics();

                if (gameAnalytics.size() >= 1) {
                    GameAnalytics game = gameAnalytics.get(gameAnalytics.size() - 1);
                    game.getActionsUsed().forEach((k, v) -> {
                        dataset.setValue(v, k, k);
                    });
                }
            }
        }, 5000, 1000);

        return dataset;
    }

    public JFreeChart getChart() {
        return chart;
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public DefaultCategoryDataset getDataset() {
        return dataset;
    }
}
